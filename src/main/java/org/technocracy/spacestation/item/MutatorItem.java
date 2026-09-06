package org.technocracy.spacestation.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.technocracy.spacestation.mutation.Mutation;
import org.technocracy.spacestation.mutation.MutationRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MutatorItem extends Item {

    private final double negativeMultiplier;
    private final SoundEvent defaultMutationSound;
    private final Map<String, SoundEvent> mutationSounds;

    public MutatorItem(Settings settings) {
        super(settings);

        if (settings instanceof MutatorSettings mutatorSettings) {
            this.negativeMultiplier = mutatorSettings.negativeMultiplier;
            this.defaultMutationSound = mutatorSettings.defaultMutationSound;
            this.mutationSounds = Map.copyOf(mutatorSettings.mutationSounds);
        } else {
            this.negativeMultiplier = 1.0;
            this.defaultMutationSound = SoundEvents.ITEM_BONE_MEAL_USE;
            this.mutationSounds = Map.of();
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient()) {
            return ActionResult.PASS;
        }

        BlockPos pos = context.getBlockPos();
        Block block = context.getWorld().getBlockState(pos).getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);

        Optional<MutationRegistry.MutationRecipe> recipe = MutationRegistry.get(blockId);

        if (recipe.isEmpty()) {
            return ActionResult.PASS;
        }

        Mutation.MutationContext mutationContext = new Mutation.MutationContext(
                context.getWorld(),
                pos
        );

        List<MutationRegistry.MutationEntry> applicableMutations = recipe.get()
                .mutations()
                .stream()
                .filter(entry -> entry.mutation().canApply(mutationContext))
                .toList();

        if (applicableMutations.isEmpty()) {
            return ActionResult.PASS;
        }

        Random random = context.getWorld().random;

        MutationRegistry.MutationEntry entry = chooseMutation(
                applicableMutations,
                random
        );

        if (entry == null) {
            return ActionResult.PASS;
        }

        Mutation mutation = entry.mutation();

        boolean applied = mutation.apply(mutationContext);

        if (!applied) {
            return ActionResult.PASS;
        }

        context.getStack().decrement(1);

        mutation.spawnParticles(mutationContext);

        SoundEvent sound = this.mutationSounds.getOrDefault(entry.type(), this.defaultMutationSound);

        context.getWorld().playSound(
                null,
                pos,
                sound,
                SoundCategory.BLOCKS,
                1.0F,
                1.0F
        );

        return ActionResult.SUCCESS;
    }

    private MutationRegistry.MutationEntry chooseMutation(List<MutationRegistry.MutationEntry> mutations, Random random) {
        double totalWeight = 0;

        for (MutationRegistry.MutationEntry entry : mutations) {
            double weight = entry.weight();

            if (entry.isNegative()) {
                weight *= this.negativeMultiplier;
            }

            if (weight > 0) {
                totalWeight += weight;
            }
        }

        if (totalWeight <= 0) {
            return null;
        }

        // roll in [0, totalWeight)
        double roll = random.nextDouble() * totalWeight;

        for (MutationRegistry.MutationEntry entry : mutations) {
            double weight = entry.weight();

            if (entry.isNegative()) {
                weight *= this.negativeMultiplier;
            }

            if (weight <= 0) {
                continue;
            }

            roll -= weight;

            if (roll < 0) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Настройки MutatorItem: множитель веса негативных мутаций +
     * звук по умолчанию и опциональные звуки под конкретные типы мутаций
     * ("harvest", "nothing", "death", "transform" — то, что стоит
     * в поле "type" в mutations/*.json).
     */
    public static class MutatorSettings extends Item.Settings {
        private double negativeMultiplier = 1.0;
        private SoundEvent defaultMutationSound = SoundEvents.ITEM_BONE_MEAL_USE;
        private final Map<String, SoundEvent> mutationSounds = new HashMap<>();

        public MutatorSettings negativeMultiplier(double negativeMultiplier) {
            this.negativeMultiplier = negativeMultiplier;
            return this;
        }

        /**
         * Звук по умолчанию играет, если для сработавшего типа мутации
         * не задан отдельный звук через {@link #mutationSound(String, SoundEvent)}.
         */
        public MutatorSettings mutationSound(SoundEvent sound) {
            this.defaultMutationSound = sound;
            return this;
        }

        /**
         * Звук под конкретный тип мутации. Можно передать как ванильный
         * SoundEvents.XXX, так и свой кастомный SoundEvent.
         *
         * @param mutationType значение поля "type" из mutations/*.json,
         *                     например "death" или "transform"
         */
        public MutatorSettings mutationSound(String mutationType, SoundEvent sound) {
            this.mutationSounds.put(mutationType, sound);
            return this;
        }
    }
}