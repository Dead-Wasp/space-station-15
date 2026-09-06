package org.technocracy.spacestation.client.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.technocracy.spacestation.SpaceStation;
import org.technocracy.spacestation.block.AssemblyBlock;
import org.technocracy.spacestation.chemistry.ChemData;
import org.technocracy.spacestation.chemistry.ChemRegistry;
import org.technocracy.spacestation.chemistry.sublimator.SublimationRecipe;
import org.technocracy.spacestation.client.integration.assembly.AssemblyJeiCategory;
import org.technocracy.spacestation.client.integration.assembly.AssemblyJeiRecipe;
import org.technocracy.spacestation.client.integration.chemmaster.ChemMasterJeiCategory;
import org.technocracy.spacestation.client.integration.chemmaster.ChemMasterJeiRecipe;
import org.technocracy.spacestation.client.integration.mutation.MutationJeiCategory;
import org.technocracy.spacestation.client.integration.mutation.MutationJeiRecipe;
import org.technocracy.spacestation.client.integration.sublimator.SublimationJeiCategory;
import org.technocracy.spacestation.client.integration.sublimator.SublimationJeiRecipe;
import org.technocracy.spacestation.item.components.ToolIngredient;
import org.technocracy.spacestation.item.components.ToolQuality;
import org.technocracy.spacestation.mutation.MutationRegistry;
import org.technocracy.spacestation.mutation.mutations.MutationTransform;
import org.technocracy.spacestation.registry.ModBlocks;
import org.technocracy.spacestation.registry.ModComponents;
import org.technocracy.spacestation.registry.items.ChemItems;
import org.technocracy.spacestation.registry.items.ToolItems;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    public static final RecipeType<SublimationJeiRecipe> SUBLIMATION =
            RecipeType.create(SpaceStation.MOD_ID, "sublimation", SublimationJeiRecipe.class);
    public static final RecipeType<ChemMasterJeiRecipe> GRINDING =
            RecipeType.create(SpaceStation.MOD_ID, "grinding", ChemMasterJeiRecipe.class);
    public static final RecipeType<ChemMasterJeiRecipe> REACTIONS =
            RecipeType.create(SpaceStation.MOD_ID, "reactions", ChemMasterJeiRecipe.class);
    public static final RecipeType<AssemblyJeiRecipe> ASSEMBLY =
            RecipeType.create(SpaceStation.MOD_ID, "assembly", AssemblyJeiRecipe.class);
    public static final RecipeType<AssemblyJeiRecipe> DISASSEMBLY_SCREWING =
            RecipeType.create(SpaceStation.MOD_ID, "disassembly_screwing", AssemblyJeiRecipe.class);
    public static final RecipeType<AssemblyJeiRecipe> DISASSEMBLY_WELDING =
            RecipeType.create(SpaceStation.MOD_ID, "disassembly_welding", AssemblyJeiRecipe.class);
    public static final RecipeType<AssemblyJeiRecipe> DISASSEMBLY_PRYING =
            RecipeType.create(SpaceStation.MOD_ID, "disassembly_prying", AssemblyJeiRecipe.class);
    public static final RecipeType<AssemblyJeiRecipe> DISASSEMBLY_ANCHORING =
            RecipeType.create(SpaceStation.MOD_ID, "disassembly_anchoring", AssemblyJeiRecipe.class);
    public static final RecipeType<AssemblyJeiRecipe> DISASSEMBLY_IGNITION =
            RecipeType.create(SpaceStation.MOD_ID, "disassembly_ignition", AssemblyJeiRecipe.class);
    public static final RecipeType<AssemblyJeiRecipe> DISASSEMBLY_GENERIC =
            RecipeType.create(SpaceStation.MOD_ID, "disassembly", AssemblyJeiRecipe.class);
    public static final RecipeType<MutationJeiRecipe> MUTATION =
            RecipeType.create(SpaceStation.MOD_ID, "mutation", MutationJeiRecipe.class
            );

    @Override
    public Identifier getPluginUid() {
        return Identifier.of(SpaceStation.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new SublimationJeiCategory(registration.getJeiHelpers().getGuiHelper()),
                new ChemMasterJeiCategory(registration.getJeiHelpers().getGuiHelper(), GRINDING,
                        "jei.spacestation.grinding"),
                new ChemMasterJeiCategory(registration.getJeiHelpers().getGuiHelper(), REACTIONS,
                        "jei.spacestation.reactions"),
                new AssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), ASSEMBLY,
                        "jei.spacestation.assembly", ModBlocks.STEEL_WALL.asItem()),
                new AssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), DISASSEMBLY_SCREWING,
                        "jei.spacestation.disassembly.screwing", ToolItems.SCREWDRIVER),
                new AssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), DISASSEMBLY_WELDING,
                        "jei.spacestation.disassembly.welding", ToolItems.WELDER),
                new AssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), DISASSEMBLY_PRYING,
                        "jei.spacestation.disassembly.prying", ToolItems.CROWBAR),
                new AssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), DISASSEMBLY_ANCHORING,
                        "jei.spacestation.disassembly.anchoring", ToolItems.WRENCH),
                new AssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), DISASSEMBLY_IGNITION,
                        "jei.spacestation.disassembly.ignition", ToolItems.LIGHTER),
                new AssemblyJeiCategory(registration.getJeiHelpers().getGuiHelper(), DISASSEMBLY_GENERIC,
                        "jei.spacestation.disassembly", ModBlocks.STEEL_WALL.asItem()),
                new MutationJeiCategory(registration.getJeiHelpers().getGuiHelper(), MUTATION,
                        "jei.spacestation.mutation", ChemItems.UNSTABLE_MUTAGEN_POWDER.asItem())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<SublimationJeiRecipe> recipes = new ArrayList<>();
        for (SublimationRecipe recipe : ChemRegistry.getSublimationRecipes()) {
            ItemStack input = new ItemStack(ChemItems.BEAKER);
            input.set(ModComponents.CHEM_DATA, new ChemData(Map.of(recipe.chemical(), recipe.units()), ChemData.EMPTY_BEAKER.capacity()));
            recipes.add(new SublimationJeiRecipe(input, new ItemStack(Registries.ITEM.get(recipe.output())), recipe.chemical()));
        }
        registration.addRecipes(SUBLIMATION, recipes);

        List<ChemMasterJeiRecipe> grinding = new ArrayList<>();
        for (ChemRegistry.GrindingRecipe recipe : ChemRegistry.getGrindingRecipes()) {
            ItemStack input = new ItemStack(Registries.ITEM.get(recipe.ingredient()));
            List<ItemStack> outputs = recipe.results().entrySet().stream()
                    .map(entry -> beaker(entry.getKey(), entry.getValue() * 25.0))
                    .toList();
            grinding.add(new ChemMasterJeiRecipe(List.of(input), outputs));
        }
        registration.addRecipes(GRINDING, grinding);

        List<ChemMasterJeiRecipe> reactions = new ArrayList<>();
        for (ChemRegistry.ReactionRecipe recipe : ChemRegistry.getReactions()) {
            List<ItemStack> inputs = recipe.reagents().entrySet().stream()
                    .map(entry -> beaker(entry.getKey(), entry.getValue() * 25.0))
                    .toList();
            List<ItemStack> outputs = recipe.results().entrySet().stream()
                    .map(entry -> beaker(entry.getKey(), entry.getValue() * 25.0))
                    .toList();
            reactions.add(new ChemMasterJeiRecipe(inputs, outputs));
        }
        registration.addRecipes(REACTIONS, reactions);

        List<AssemblyJeiRecipe> assemblyRecipes = new ArrayList<>();
        Map<RecipeType<AssemblyJeiRecipe>, List<AssemblyJeiRecipe>> disassemblyRecipes = new LinkedHashMap<>();
        disassemblyRecipes.put(DISASSEMBLY_SCREWING, new ArrayList<>());
        disassemblyRecipes.put(DISASSEMBLY_WELDING, new ArrayList<>());
        disassemblyRecipes.put(DISASSEMBLY_PRYING, new ArrayList<>());
        disassemblyRecipes.put(DISASSEMBLY_ANCHORING, new ArrayList<>());
        disassemblyRecipes.put(DISASSEMBLY_IGNITION, new ArrayList<>());
        disassemblyRecipes.put(DISASSEMBLY_GENERIC, new ArrayList<>());

        for (AssemblyBlock.AssemblyRecipe recipe : AssemblyBlock.getRecipes()) {
            ItemStack source = new ItemStack(recipe.source().asItem());
            ItemStack result = new ItemStack(recipe.result().asItem());
            List<ItemStack> assemblyInputs = new ArrayList<>(List.of(source));
            recipe.assemblyTool().needItems().stream().findFirst().ifPresent(item ->
                    assemblyInputs.add(new ItemStack(item, Math.max(1, (int) recipe.cost()))));
            assemblyRecipes.add(new AssemblyJeiRecipe(assemblyInputs, List.of(result), false,
                    recipe.assemblyTime(), recipe.fuelCost()));

            if (!recipe.disassemblyTool().isEmpty() && recipe.disassemblyTime() > 0.0f) {
                List<ItemStack> disassemblyInputs = List.of(new ItemStack(recipe.result().asItem()));
                List<ItemStack> disassemblyOutputs = new ArrayList<>(List.of(new ItemStack(recipe.source().asItem())));
                recipe.assemblyTool().needItems().stream().findFirst().ifPresent(item ->
                        disassemblyOutputs.add(new ItemStack(item, Math.max(1, (int) recipe.cost()))));
                AssemblyJeiRecipe disassembly = new AssemblyJeiRecipe(disassemblyInputs, disassemblyOutputs, true,
                        recipe.disassemblyTime(), recipe.fuelCost());
                disassemblyRecipes.get(disassemblyType(recipe.disassemblyTool())).add(disassembly);
            }
        }
        registration.addRecipes(ASSEMBLY, assemblyRecipes);
        disassemblyRecipes.forEach(registration::addRecipes);

        List<MutationJeiRecipe> mutationRecipes = new ArrayList<>();

        for (MutationRegistry.MutationRecipe recipe : MutationRegistry.getAll()) {
            List<ItemStack> outputs = new ArrayList<>();
            List<Double> weights = new ArrayList<>();

            for (MutationRegistry.MutationEntry entry : recipe.mutations()) {
                if (!(entry.mutation() instanceof MutationTransform transform)) {
                    continue;
                }

                for (MutationTransform.WeightedBlock block : transform.getBlocks()) {
                    outputs.add(new ItemStack(block.state().getBlock().asItem()));
                    weights.add(entry.weight());
                }
            }

            if (!outputs.isEmpty()) {
                ItemStack input = new ItemStack(
                        Registries.BLOCK.get(recipe.target()).asItem()
                );

                mutationRecipes.add(
                        new MutationJeiRecipe(input, outputs, weights)
                );
            }
        }

        registration.addRecipes(MUTATION, mutationRecipes);
    }

    private static RecipeType<AssemblyJeiRecipe> disassemblyType(ToolIngredient tool) {
        if (tool.needQualities().contains(ToolQuality.SCREWING)) return DISASSEMBLY_SCREWING;
        if (tool.needQualities().contains(ToolQuality.WELDING)) return DISASSEMBLY_WELDING;
        if (tool.needQualities().contains(ToolQuality.PRYING)) return DISASSEMBLY_PRYING;
        if (tool.needQualities().contains(ToolQuality.ANCHORING)) return DISASSEMBLY_ANCHORING;
        if (tool.needQualities().contains(ToolQuality.IGNITION)) return DISASSEMBLY_IGNITION;
        return DISASSEMBLY_GENERIC;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CHEM_MASTER), GRINDING, REACTIONS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SUBLIMATOR), SUBLIMATION);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.STEEL_WALL), ASSEMBLY, DISASSEMBLY_GENERIC);
        registration.addRecipeCatalyst(new ItemStack(ToolItems.SCREWDRIVER), DISASSEMBLY_SCREWING);
        registration.addRecipeCatalyst(new ItemStack(ToolItems.WELDER), DISASSEMBLY_WELDING);
        registration.addRecipeCatalyst(new ItemStack(ToolItems.CROWBAR), DISASSEMBLY_PRYING);
        registration.addRecipeCatalyst(new ItemStack(ToolItems.WRENCH), DISASSEMBLY_ANCHORING);
        registration.addRecipeCatalyst(new ItemStack(ToolItems.LIGHTER), DISASSEMBLY_IGNITION);
        registration.addRecipeCatalyst(new ItemStack(ToolItems.OMNITOOL),
                DISASSEMBLY_SCREWING, DISASSEMBLY_WELDING, DISASSEMBLY_PRYING,
                DISASSEMBLY_ANCHORING, DISASSEMBLY_IGNITION);
        registration.addRecipeCatalyst(new ItemStack(ChemItems.UNSTABLE_MUTAGEN_POWDER), MUTATION);
    }

    private static ItemStack beaker(String chemical, double amount) {
        ItemStack stack = new ItemStack(ChemItems.BEAKER);
        stack.set(ModComponents.CHEM_DATA,
                new ChemData(Map.of(chemical, amount), ChemData.EMPTY_BEAKER.capacity()));
        return stack;
    }
}
