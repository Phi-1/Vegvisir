package dev.stormwatch.vegvisir.blockentities;

import dev.stormwatch.vegvisir.blocks.SpinningWheelBlock;
import dev.stormwatch.vegvisir.networking.VegvisirNetworking;
import dev.stormwatch.vegvisir.networking.packets.SpinningWheelProcessingS2CPacket;
import dev.stormwatch.vegvisir.registry.VegvisirBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class SpinningWheelBlockEntity extends BlockEntity implements GeoBlockEntity {
    // TODO: dont forget this.setChanged when changing data that needs to be saved to NBT
    protected static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.spinningwheel.idle");
    protected static final RawAnimation SPIN_ANIMATION = RawAnimation.begin().thenLoop("animation.spinningwheel.spin");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int WOOL_PER_YARN = 2;
    private int storedWool = 0;
    private int progress = 0;
    private boolean processing = false;

    public SpinningWheelBlockEntity(BlockPos pos, BlockState blockState) {
        super(VegvisirBlockEntityTypes.SPINNING_WHEEL_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void addWool() {
        this.storedWool++;
        this.setProcessing(true, true); // TODO: Temp
        this.setChanged();
    }

    public boolean isProcessing() {
        return this.processing;
    }

    public void setProcessing(boolean processing, boolean sendUpdate) {
        this.processing = processing;
        if (sendUpdate) {
            VegvisirNetworking.sendToAllConnectedClients(new SpinningWheelProcessingS2CPacket(processing, this.worldPosition));
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        this.storedWool = nbt.getInt("wool");
        this.progress = nbt.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("wool", this.storedWool);
        nbt.putInt("progress", this.progress);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpinningWheelBlockEntity spinningWheel) {
        // this gets called on both client and server
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> {
            if (state.getAnimatable().isProcessing()) {
                return state.setAndContinue(SPIN_ANIMATION);
            }
            else {
                return state.setAndContinue(IDLE_ANIMATION);
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
