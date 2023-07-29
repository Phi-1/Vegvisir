package dev.stormwatch.vegvisir.renderers;

import dev.stormwatch.vegvisir.blockentities.OrbBlockEntity;
import dev.stormwatch.vegvisir.blockentities.SpinningWheelBlockEntity;
import dev.stormwatch.vegvisir.models.OrbGeoModel;
import dev.stormwatch.vegvisir.models.SpinningWheelGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class OrbRenderer extends GeoBlockRenderer<OrbBlockEntity> {
    public OrbRenderer(BlockEntityRendererProvider.Context context) {
        super(new OrbGeoModel());
    }
}
