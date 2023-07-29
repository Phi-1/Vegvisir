package dev.stormwatch.vegvisir.models;

import dev.stormwatch.vegvisir.Vegvisir;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class SpinningWheelGeoModel extends GeoModel {
    @Override
    public ResourceLocation getModelResource(GeoAnimatable animatable) {
        return new ResourceLocation(Vegvisir.MOD_ID, "geo/spinningwheel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable animatable) {
        return new ResourceLocation(Vegvisir.MOD_ID, "textures/models/spinningwheel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return new ResourceLocation(Vegvisir.MOD_ID, "animations/spinningwheel.animation.json");
    }
}
