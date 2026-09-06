package org.technocracy.spacestation.registry.items;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.technocracy.spacestation.SpaceStation;
import org.technocracy.spacestation.chemistry.ChemContainer;
import org.technocracy.spacestation.chemistry.ChemData;
import org.technocracy.spacestation.item.MutatorItem;
import org.technocracy.spacestation.registry.ModComponents;

public final class ChemItems {

    public static final Item BEAKER = Registry.register(
            Registries.ITEM, Identifier.of(SpaceStation.MOD_ID, "beaker"),
            new ChemContainer(new Item.Settings()
                    .maxCount(1)
                    .component(ModComponents.CHEM_DATA, ChemData.EMPTY_BEAKER))
    );

    public static final Item CANISTER = Registry.register(
            Registries.ITEM, Identifier.of(SpaceStation.MOD_ID, "canister"),
            new ChemContainer(new Item.Settings()
                    .maxCount(1)
                    .component(ModComponents.CHEM_DATA, ChemData.EMPTY_CANISTER))
    );

    public static final Item COFFEE_POWDER = register("coffee_powder");
    public static final Item COPPER_SULFATE = register("copper_sulfate");
    public static final Item SALT = register("salt");

    public static final Item UNSTABLE_MUTAGEN_POWDER = Registry.register(
            Registries.ITEM, Identifier.of(SpaceStation.MOD_ID, "unstable_mutagen_powder"),
            new MutatorItem(new MutatorItem.MutatorSettings())
    );

    // Ден, ну будь ты человеком хоть в комменте оставь настройки...
    // Короче чтоб мутациям добавлять новые настройки писать после мутатор сеттингс:
    // .negativeMultiplier(1) (где 1 = 100%, а 0 = 0%)
    // .mutationSound(SoundEvents.ITEM_BONE_MEAL_USE)


    private static Item register(String name) {
        return Registry.register(
                Registries.ITEM, Identifier.of(SpaceStation.MOD_ID, name),
                new Item(new Item.Settings())
        );
    }

    private ChemItems() {}

    public static void register() {}
}