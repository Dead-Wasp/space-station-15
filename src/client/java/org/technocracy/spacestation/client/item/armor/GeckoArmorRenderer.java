package org.technocracy.spacestation.client.item.armor;

import org.technocracy.spacestation.item.armor.GeckoArmorItem;
import org.technocracy.spacestation.item.armor.GeckoArmorModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GeckoArmorRenderer extends GeoArmorRenderer<GeckoArmorItem> {
    public GeckoArmorRenderer(String assetName) {
        super(new GeckoArmorModel(assetName));
    }
}