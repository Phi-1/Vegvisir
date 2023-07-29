package dev.stormwatch.vegvisir.renderers;

import dev.stormwatch.vegvisir.blockentities.SpinningWheelBlockEntity;
import dev.stormwatch.vegvisir.models.SpinningWheelGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SpinningWheelRenderer extends GeoBlockRenderer<SpinningWheelBlockEntity> {
    public SpinningWheelRenderer(BlockEntityRendererProvider.Context context) {
        super(new SpinningWheelGeoModel());
    }
}
