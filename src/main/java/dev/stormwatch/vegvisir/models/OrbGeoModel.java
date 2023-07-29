package dev.stormwatch.vegvisir.models;

import dev.stormwatch.vegvisir.Vegvisir;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class OrbGeoModel extends GeoModel {
    @Override
    public ResourceLocation getModelResource(GeoAnimatable animatable) {
        return new ResourceLocation(Vegvisir.MOD_ID, "geo/orb.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable animatable) {
        return new ResourceLocation(Vegvisir.MOD_ID, "textures/models/orb.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return new ResourceLocation(Vegvisir.MOD_ID, "animations/orb.animation.json");
    }
}
