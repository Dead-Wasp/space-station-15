package org.technocracy.spacestation.client.item.armor;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.technocracy.spacestation.item.armor.GeckoArmorItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GeckoArmorRendererProvider {
    private static final Map<String, GeckoArmorRenderer> RENDERERS = new ConcurrentHashMap<>();

    private GeckoArmorRendererProvider() {
    }

    public static GeoRenderProvider create() {
        return new GeoRenderProvider() {
            @Override
            public <T extends LivingEntity> BipedEntityModel<?> getGeoArmorRenderer(
                    @Nullable T livingEntity,
                    ItemStack itemStack,
                    @Nullable EquipmentSlot equipmentSlot,
                    @Nullable BipedEntityModel<T> original) {
                if (!(itemStack.getItem() instanceof GeckoArmorItem armorItem)) {
                    return null;
                }

                return RENDERERS.computeIfAbsent(
                        armorItem.getAssetName(),
                        GeckoArmorRenderer::new
                );
            }
        };
    }
}