package org.technocracy.spacestation.item.armor;

import net.minecraft.util.Identifier;
import org.technocracy.spacestation.SpaceStation;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class GeckoArmorModel extends DefaultedGeoModel<GeckoArmorItem> {
    public GeckoArmorModel(String assetName) {
        super(Identifier.of(SpaceStation.MOD_ID, assetName));
    }

    @Override
    public Identifier getTextureResource(GeckoArmorItem animatable) {
        return Identifier.of(SpaceStation.MOD_ID,
                "textures/models/armor/" + animatable.getAssetName() + ".png");
    }

    @Override
    public Identifier getModelResource(GeckoArmorItem animatable) {
        return Identifier.of(SpaceStation.MOD_ID,
                "geo/armor/" + animatable.getAssetName() + ".geo.json");
    }

    @Override
    protected String subtype() {
        return "armor";
    }
}