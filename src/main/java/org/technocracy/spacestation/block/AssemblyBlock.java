package org.technocracy.spacestation.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.technocracy.spacestation.item.components.ChargeData;
import org.technocracy.spacestation.item.components.Utils;
import org.technocracy.spacestation.registry.ModComponents;
import org.technocracy.spacestation.item.components.ToolIngredient;
import org.technocracy.spacestation.item.components.ItemTool;
import org.technocracy.spacestation.system.ActionTimer;

import java.util.*;

public class AssemblyBlock extends Block {
    record Upgrade(Block result, float cost, float assemblyTime, float fuelCost, float disassemblyTime, ToolIngredient tools) {}
    public record AssemblyRecipe(Block source, Block result, float cost, float assemblyTime,
                                 float fuelCost, float disassemblyTime,
                                 ToolIngredient assemblyTool, ToolIngredient disassemblyTool) {}
    // source + material -> upgrade
    private static final Map<Block, Map<ToolIngredient, AssemblyBlock.Upgrade>> ASSEMBLY_REGISTRY = new HashMap<>();
    // assembled block -> source (для разбора)
    private static final Map<Block, AssemblyBlock.Upgrade> DISASSEMBLY_REGISTRY = new HashMap<>();
    private static final List<AssemblyRecipe> RECIPES = new ArrayList<>();

    public static void registerUpgrade(Block source, Block result,
                                       float cost, float assemblyTime, float fuelCost, float disassemblyTime,
                                       ToolIngredient assembly, ToolIngredient disassembly) {
        Upgrade upgrade = new Upgrade(result, cost, assemblyTime, fuelCost, disassemblyTime, assembly);

        RECIPES.add(new AssemblyRecipe(source, result, cost, assemblyTime, fuelCost,
            disassemblyTime, assembly, disassembly));

        ASSEMBLY_REGISTRY.computeIfAbsent(source, k -> new HashMap<>())
                .put(assembly, upgrade);

        if (!disassembly.isEmpty()) {
            DISASSEMBLY_REGISTRY.put(result, new Upgrade(source, cost, assemblyTime, fuelCost, disassemblyTime, disassembly));
        }
    }

    public static List<AssemblyRecipe> getRecipes() {
        return List.copyOf(RECIPES);
    }

    public static void clearRecipes() {
        ASSEMBLY_REGISTRY.clear();
        DISASSEMBLY_REGISTRY.clear();
        RECIPES.clear();
    }
    public static void registerUpgrade(Block source, Block result,
                                       float cost, float assemblyTime,
                                       ToolIngredient assembly) {
        registerUpgrade(source, result, cost, assemblyTime, 0f, 0f, assembly, ToolIngredient.empty());
    }

    public static void registerUpgrade(Block source, Block result,
                                       float cost, float assemblyTime,
                                       float disassemblyTime, ToolIngredient assembly, ToolIngredient disassembly) {
        registerUpgrade(source, result, cost, assemblyTime, 0f, disassemblyTime, assembly, disassembly);
    }

    public AssemblyBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world,
                                             BlockPos pos, PlayerEntity player, Hand hand,
                                             BlockHitResult hit) {
        Item heldItem = stack.getItem();

        boolean canToggle = stack.contains(ModComponents.ITEM_TOGGLE_COMPONENT);
        boolean isActivated = stack.getOrDefault(ModComponents.ITEM_TOGGLE_COMPONENT, true);

        Map<ToolIngredient, Upgrade> upgrades = ASSEMBLY_REGISTRY.get(this);
        Optional<Map.Entry<ToolIngredient, Upgrade>> match = upgrades == null
                ? Optional.empty()
                : upgrades.entrySet().stream()
                .filter(e -> e.getKey().contains(stack))
                .findFirst();
        boolean canAssemble = match.isPresent();

        @Nullable
        Upgrade disassembly = DISASSEMBLY_REGISTRY.get(this);
        boolean canDisassemble = disassembly != null && disassembly.tools().contains(stack);

        if (!canAssemble && !canDisassemble) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (canToggle && !isActivated) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (world.isClient()) return ItemActionResult.SUCCESS;

        @Nullable
        ChargeData data = stack.get(ModComponents.CHARGE_COMPONENT);
        float speed = heldItem instanceof ItemTool tool ? tool.SPEED : 1f;

        // Assemble
        if (canAssemble) {
            Upgrade upgrade = match.get().getValue();
            if (stack.getCount() < upgrade.cost() && Utils.isTool(stack)) return ItemActionResult.FAIL;
            if (ActionTimer.isActive((ServerPlayerEntity) player, pos)) return ItemActionResult.SUCCESS;

            ActionTimer.start((ServerPlayerEntity) player, pos, upgrade.assemblyTime() / speed, false, p -> {
                if (data != null) {
                    stack.set(ModComponents.CHARGE_COMPONENT, data.withCharge(data.charge() - upgrade.fuelCost()));
                }

                if (p.getMainHandStack().getItem() == heldItem &&
                        (p.getMainHandStack().getCount() >= upgrade.cost() || p.getMainHandStack().getMaxCount() == 1)) {
                    world.setBlockState(pos, copyFacing(state, upgrade.result().getDefaultState()));
                    if (!p.getAbilities().creativeMode && data == null) {
                        p.getMainHandStack().decrement((int) upgrade.cost());
                    }
                    spawnAssemblyEffects(world, pos);
                }
            },
                    p -> {
                        ItemStack curStack = p.getStackInHand(hand);
                        boolean rightTool = upgrade.tools.contains(curStack);
                        boolean isNowActivated = curStack.getOrDefault(ModComponents.ITEM_TOGGLE_COMPONENT, true);
                        ChargeData chargeData = curStack.get(ModComponents.CHARGE_COMPONENT);
                        boolean hasFuel = chargeData == null
                                || chargeData.charge() > 0f;
                        return rightTool && isNowActivated && hasFuel;
            });
            return ItemActionResult.SUCCESS;
        }

        // Disassemble
        if (ActionTimer.isActive((ServerPlayerEntity) player, pos)) return ItemActionResult.SUCCESS;

        ActionTimer.start((ServerPlayerEntity) player, pos, disassembly.disassemblyTime() / speed, true, p -> {
            Optional<Map.Entry<ToolIngredient, Upgrade>> disMatch = ASSEMBLY_REGISTRY.getOrDefault(disassembly.result(), Map.of())
                    .entrySet().stream()
                    .filter(e -> e.getValue().result().equals(this))
                    .findFirst();
            if (disMatch.isEmpty()) return;

            Map.Entry<ToolIngredient, Upgrade> e = disMatch.get();

            if (!e.getKey().needItems().isEmpty()) {
                p.dropItem(new ItemStack(e.getKey().needItems().iterator().next(), (int) e.getValue().cost()), false);
            }

            world.setBlockState(pos, copyFacing(state, disassembly.result().getDefaultState()));
            if (data != null) {
                stack.set(ModComponents.CHARGE_COMPONENT, data.withCharge(data.charge() - e.getValue().fuelCost()));
            }

            if (!p.getAbilities().creativeMode && data == null) {
                stack.damage(1, p, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }

            spawnDisassemblyEffects(world, pos);
        },
                p -> {
                    ItemStack curStack = p.getStackInHand(hand);
                    boolean rightTool = disassembly.tools.contains(curStack);
                    boolean isNowActivated = curStack.getOrDefault(ModComponents.ITEM_TOGGLE_COMPONENT, true);
                    ChargeData chargeData = curStack.get(ModComponents.CHARGE_COMPONENT);
                    boolean hasFuel = chargeData == null
                            || chargeData.charge() > 0f;
                    return rightTool && isNowActivated && hasFuel;
                });
        return ItemActionResult.SUCCESS;

    }

    private void spawnAssemblyEffects(World world, BlockPos pos) {
        double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
        ServerWorld sw = (ServerWorld) world;
        sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0.2, 0.2, 0.2, 0.05);
        sw.spawnParticles(ParticleTypes.SMOKE, x, y + 0.3, z, 8, 0.1, 0.1, 0.1, 0.02);
        sw.spawnParticles(ParticleTypes.ASH, x, y, z, 20, 10, 0.2, 0.2, 0);
        world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private static BlockState copyFacing(BlockState source, BlockState target) {
        if (source.contains(Properties.HORIZONTAL_FACING)
                && target.contains(Properties.HORIZONTAL_FACING)) {
            return target.with(Properties.HORIZONTAL_FACING,
                    source.get(Properties.HORIZONTAL_FACING));
        }
        return target;
    }

    private void spawnDisassemblyEffects(World world, BlockPos pos) {
        double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
        ServerWorld sw = (ServerWorld) world;
        sw.spawnParticles(ParticleTypes.CRIT, x, y, z, 10, 0.2, 0.2, 0.2, 0.05);
        sw.spawnParticles(ParticleTypes.SMOKE, x, y + 0.3, z, 5, 0.1, 0.1, 0.1, 0.02);
        world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, 0.8f);
    }
}
