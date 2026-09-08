package org.technocracy.spacestation.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.technocracy.spacestation.registry.items.MiscItems;

public class ModArmorMaterials {

    public static final TagKey<Item> NUKEOPS_REPAIR_TAG = TagKey.of(
            Registries.ITEM.getKey(),
            Identifier.of("spacestation", "nukeops_repair")
    );


    public static final RegistryEntry<ArmorMaterial> NUKEOPS = register(
            "nukeops",
            Map.of(
                    ArmorItem.Type.BOOTS, 4,
                    ArmorItem.Type.LEGGINGS, 7,
                    ArmorItem.Type.CHESTPLATE, 9,
                    ArmorItem.Type.HELMET, 4,
                    ArmorItem.Type.BODY, 13
            ),
            20,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            4.5F,
            0.2F,
            () -> Ingredient.fromTag(NUKEOPS_REPAIR_TAG)
    );

    public static final RegistryEntry<ArmorMaterial> STEEL = register(
            "steel",
            Map.of(
                    ArmorItem.Type.BOOTS, 2,
                    ArmorItem.Type.LEGGINGS, 5,
                    ArmorItem.Type.CHESTPLATE, 6,
                    ArmorItem.Type.HELMET, 2,
                    ArmorItem.Type.BODY, 6
            ),
            10,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            () -> Ingredient.ofItems(MiscItems.STEEL)
    );


    private static RegistryEntry<ArmorMaterial> register(
            String id,
            Map<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            RegistryEntry<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
    ) {

        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(
                        Identifier.of("spacestation", id)
                )
        );


        return Registry.registerReference(
                Registries.ARMOR_MATERIAL,
                Identifier.of("spacestation", id),
                new ArmorMaterial(
                        defense,
                        enchantmentValue,
                        equipSound,
                        repairIngredient,
                        layers,
                        toughness,
                        knockbackResistance
                )
        );
    }
}