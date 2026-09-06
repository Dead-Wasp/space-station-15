package org.technocracy.spacestation.mutation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.technocracy.spacestation.SpaceStation;
import org.technocracy.spacestation.mutation.mutations.MutationDeath;
import org.technocracy.spacestation.mutation.mutations.MutationGrow;
import org.technocracy.spacestation.mutation.mutations.MutationNothing;
import org.technocracy.spacestation.mutation.mutations.MutationTransform;

import java.io.InputStreamReader;
import java.util.*;

public class MutationRegistry {

    private static final Gson GSON = new Gson();

    private static final Map<Identifier, MutationRecipe> MUTATIONS = new HashMap<>();

    public record MutationEntry(
            Mutation mutation,
            double weight,
            boolean isNegative,
            String type
    ) {}

    public record MutationRecipe(
            Identifier target,
            List<MutationEntry> mutations
    ) {}

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(
                        new SimpleSynchronousResourceReloadListener() {

                            @Override
                            public Identifier getFabricId() {
                                return Identifier.of(SpaceStation.MOD_ID, "mutation_registry");
                            }

                            @Override
                            public void reload(ResourceManager manager) {
                                loadMutations(manager);
                            }
                        }
                );
    }

    private static void loadMutations(ResourceManager manager) {
        MUTATIONS.clear();

        MutationGrow generalMutationGrow = new MutationGrow();
        MutationNothing generalMutationNothing = new MutationNothing();


        manager.findResources(
                "mutations",
                id -> id.getPath().endsWith(".json")
        ).forEach((id, resource) -> {

            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {

                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                Identifier target = Identifier.of(json.get("target").getAsString());

                if (!Registries.BLOCK.containsId(target)) {
                    throw new IllegalArgumentException("Unknown block: " + target);
                }

                JsonArray array = json.getAsJsonArray("mutations");

                List<MutationEntry> mutations = new ArrayList<>();

                for (JsonElement element : array) {
                    JsonObject mutation = element.getAsJsonObject();

                    String mutationType = mutation.get("type").getAsString();

                    double chance = mutation.get("weight").getAsDouble();

                    if (chance <= 0) {
                        throw new IllegalArgumentException("Mutation chance must be positive: " + mutationType);
                    }

                    // =================== РЕГИСТРАЦИЯ МУТАЦИЙ ===================

                    switch (mutationType) {
                        case "grow" -> mutations.add(new MutationEntry(generalMutationGrow, chance, false, mutationType));
                        case "nothing" -> mutations.add(new MutationEntry(generalMutationNothing, chance, false, mutationType));
                        case "death" -> {
                            Identifier blockId = Identifier.of(
                                    mutation.get("block").getAsString()
                            );

                            BlockState state = getBlockStateById(blockId);
                            mutations.add(new MutationEntry(new MutationDeath(state), chance, true, mutationType));
                        }

                        case "transform" -> {
                            List<MutationTransform.WeightedBlock> variants = parseTransformVariants(mutation);
                            mutations.add(new MutationEntry(new MutationTransform(variants), chance, false, mutationType));
                        }
                        default -> throw new IllegalArgumentException("Unknown mutation type: " + mutationType);
                    }

                }

                MUTATIONS.put(target, new MutationRecipe(target, List.copyOf(mutations)));

            } catch (Exception e) {
                SpaceStation.LOGGER.error("Ошибка загрузки mutation {}: {}", id, e.getMessage());
            }
        });

        SpaceStation.LOGGER.info("Загружено mutation рецептов: {}", MUTATIONS.size()
        );
    }

    private static List<MutationTransform.WeightedBlock> parseTransformVariants(JsonObject mutation) {
        JsonArray blocksArray = mutation.getAsJsonArray("blocks");
        List<MutationTransform.WeightedBlock> blocks = new ArrayList<>();

        for (JsonElement element : blocksArray) {
            JsonObject blockJson = element.getAsJsonObject();

            Identifier blockId = Identifier.of(
                    blockJson.get("block").getAsString()
            );

            double weight = blockJson.get("weight").getAsDouble();

            if (weight <= 0) {
                throw new IllegalArgumentException(
                        "Block weight must be positive: " + blockId
                );
            }

            BlockState state = getBlockStateById(blockId);

            blocks.add(new MutationTransform.WeightedBlock(state, weight));
        }

        if (blocks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Transform mutation must contain at least one block"
            );
        }

        return List.copyOf(blocks);
    }

    private static BlockState getBlockStateById(Identifier blockId) {
        if (!Registries.BLOCK.containsId(blockId)) {
            throw new IllegalArgumentException(
                    "Unknown block: " + blockId
            );
        }

        return Registries.BLOCK
                .get(blockId)
                .getDefaultState();
    }

    public static Optional<MutationRecipe> get(Identifier target) {
        return Optional.ofNullable(MUTATIONS.get(target));
    }

    public static Collection<MutationRecipe> getAll() {
        return Collections.unmodifiableCollection(MUTATIONS.values());
    }
}