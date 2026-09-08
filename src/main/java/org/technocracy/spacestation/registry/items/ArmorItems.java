package org.technocracy.spacestation.registry.items;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.technocracy.spacestation.item.ModArmorMaterials;
import org.technocracy.spacestation.item.armor.NukeopsArmorItem;
import org.technocracy.spacestation.item.armor.GeckoArmorItem;

public class ArmorItems {

    /*
     * обычный сет      -> GeckoArmorItem
     * сет с эффектами  -> отдельный наследник GeckoArmorItem
     * модель/текстура  -> assetName + ресурсы
     * renderer         -> общий
     */

    // ================ ХРЮКА ================
    public static final NukeopsArmorItem NUKEOPS_HELMET_ITEM = new NukeopsArmorItem(ModArmorMaterials.NUKEOPS, ArmorItem.Type.HELMET,
            new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(520)));
    public static final Item NUKEOPS_HELMET = register("nukeops_helmet", NUKEOPS_HELMET_ITEM);

    public static final NukeopsArmorItem NUKEOPS_CHESTPLATE_ITEM = new NukeopsArmorItem(ModArmorMaterials.NUKEOPS, ArmorItem.Type.CHESTPLATE,
            new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(520)));
    public static final Item NUKEOPS_CHESTPLATE = register("nukeops_chestplate", NUKEOPS_CHESTPLATE_ITEM);

    public static final NukeopsArmorItem NUKEOPS_LEGGINGS_ITEM = new NukeopsArmorItem(ModArmorMaterials.NUKEOPS, ArmorItem.Type.LEGGINGS,
            new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(520)));
    public static final Item NUKEOPS_LEGGINGS = register("nukeops_leggings", NUKEOPS_LEGGINGS_ITEM);

    public static final NukeopsArmorItem NUKEOPS_BOOTS_ITEM = new NukeopsArmorItem(ModArmorMaterials.NUKEOPS, ArmorItem.Type.BOOTS,
            new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(520)));
    public static final Item NUKEOPS_BOOTS = register("nukeops_boots", NUKEOPS_BOOTS_ITEM);

    // ================ СТАЛЬ ================

    public static final GeckoArmorItem STEEL_HELMET_ITEM = new GeckoArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.HELMET,
            new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(180)), "steel_armor");
    public static final Item STEEL_HELMET = register("steel_helmet", STEEL_HELMET_ITEM);

    public static final GeckoArmorItem STEEL_CHESTPLATE_ITEM = new GeckoArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE,
            new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(180)), "steel_armor");
    public static final Item STEEL_CHESTPLATE = register("steel_chestplate", STEEL_CHESTPLATE_ITEM);

    public static final GeckoArmorItem STEEL_LEGGINGS_ITEM = new GeckoArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS,
            new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(180)), "steel_armor");
    public static final Item STEEL_LEGGINGS = register("steel_leggings", STEEL_LEGGINGS_ITEM);

    public static final GeckoArmorItem STEEL_BOOTS_ITEM = new GeckoArmorItem(ModArmorMaterials.STEEL, ArmorItem.Type.BOOTS,
            new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(180)), "steel_armor");
    public static final Item STEEL_BOOTS = register("steel_boots", STEEL_BOOTS_ITEM);

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("spacestation", name), item);
    }

    public static void register() {
    }
}