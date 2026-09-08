package org.technocracy.spacestation.item.armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GeckoArmorItem extends ArmorItem implements GeoItem {
    private final String assetName;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings, String assetName) {
        super(material, type, settings);
        this.assetName = assetName;
        GeoItem.registerSyncedAnimatable(this);
    }

    public String getAssetName() {
        return assetName;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void createGeoRenderer(Consumer consumer) {
        try {
            Class<?> providerFactory = Class.forName("org.technocracy.spacestation.client.item.armor.GeckoArmorRendererProvider");
            consumer.accept(providerFactory.getMethod("create").invoke(null));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to create armor renderer", exception);
        }
    }
}