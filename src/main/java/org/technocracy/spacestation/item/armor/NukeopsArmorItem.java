package org.technocracy.spacestation.item.armor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import org.technocracy.spacestation.registry.items.ArmorItems;

public class NukeopsArmorItem extends GeckoArmorItem {

    public NukeopsArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings, "nukeops_armor");
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient() || !(entity instanceof LivingEntity living)) return;

        if (hasFullSuit(living)) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 220, 0, true, false));
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, true, false));
        }
    }

    private boolean hasFullSuit(LivingEntity living) {
        return living.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD).isOf(ArmorItems.NUKEOPS_HELMET)
                && living.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).isOf(ArmorItems.NUKEOPS_CHESTPLATE)
                && living.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS).isOf(ArmorItems.NUKEOPS_LEGGINGS)
                && living.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET).isOf(ArmorItems.NUKEOPS_BOOTS);
    }
}