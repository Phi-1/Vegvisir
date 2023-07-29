package dev.stormwatch.vegvisir.blockentities;

import dev.stormwatch.vegvisir.registry.VegvisirBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class OrbBlockEntity extends BlockEntity implements GeoBlockEntity {
    // TODO: dont forget this.setChanged when changing data that needs to be saved to NBT
    protected static final RawAnimation HOVER_ANIMATION = RawAnimation.begin().then("animation.orb.hover", Animation.LoopType.LOOP);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public OrbBlockEntity(BlockPos pos, BlockState blockState) {
        super(VegvisirBlockEntityTypes.ORB_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public void load(CompoundTag nbt) {
        // load from nbt
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        // save to nbt
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::hoverAnimationController));
    }

    protected <E extends OrbBlockEntity> PlayState hoverAnimationController(final AnimationState<E> state) {
        return state.setAndContinue(HOVER_ANIMATION);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
