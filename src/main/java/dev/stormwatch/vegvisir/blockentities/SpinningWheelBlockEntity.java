package dev.stormwatch.vegvisir.blockentities;

import dev.stormwatch.vegvisir.blocks.SpinningWheelBlock;
import dev.stormwatch.vegvisir.networking.VegvisirNetworking;
import dev.stormwatch.vegvisir.networking.packets.SpinningWheelProcessingS2CPacket;
import dev.stormwatch.vegvisir.registry.VegvisirBlockEntityTypes;
import dev.stormwatch.vegvisir.registry.VegvisirBlocks;
import dev.stormwatch.vegvisir.registry.VegvisirItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class SpinningWheelBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.spinningwheel.idle");
    protected static final RawAnimation SPIN_ANIMATION = RawAnimation.begin().thenLoop("animation.spinningwheel.spin");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // TODO: separate stacks for different colors of wool
    private static final int WOOL_PER_YARN = 4;
    private static final int TICKS_PER_YARN = 1800;
    private int storedWool = 0;
    private int progress = 0;
    private boolean processing = false;

    public SpinningWheelBlockEntity(BlockPos pos, BlockState blockState) {
        super(VegvisirBlockEntityTypes.SPINNING_WHEEL_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void addWool() {
        this.storedWool++;
        this.setChanged();
    }

    public void useWool(int amount) {
        this.storedWool -= amount;
        this.setChanged();
    }

    public int getStoredWool() {
        return this.storedWool;
    }

    public void dropOnRemove() {
        // TODO: test what this does when stored wool is > 64
        ItemStack wool = new ItemStack(Items.WHITE_WOOL, this.storedWool);
        ItemStack wheel = new ItemStack(VegvisirBlocks.SPINNING_WHEEL_BLOCK.get(), 1);
        ItemEntity woolEntity = new ItemEntity(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), wool);
        ItemEntity wheelEntity = new ItemEntity(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), wheel);
        this.level.addFreshEntity(woolEntity);
        this.level.addFreshEntity(wheelEntity);
    }

    public void dropYarn() {
        if (this.level == null) return;
        ItemStack yarnStack = new ItemStack(VegvisirItems.WOOL_YARN.get(), 1);
        Direction facing = this.getBlockState().getValue(DirectionalBlock.FACING);
        BlockPos dropPos = new BlockPos(this.worldPosition);
        dropPos = switch (facing) {
            case NORTH -> dropPos.north();
            case EAST -> dropPos.east();
            case SOUTH -> dropPos.south();
            case WEST -> dropPos.west();
            default -> new BlockPos(this.worldPosition);
        };
        ItemEntity yarnEntity = new ItemEntity(this.level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), yarnStack);
        this.level.addFreshEntity(yarnEntity);
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

    public void increaseProgress() {
        this.progress++;
        this.setChanged();
    }

    public void resetProgress() {
        this.progress = 0;
        this.setChanged();
    }

    public int getProgress() {
        return this.progress;
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
        if (level.isClientSide()) return;
        if (!spinningWheel.isProcessing() && spinningWheel.getStoredWool() >= WOOL_PER_YARN) {
            spinningWheel.setProcessing(true, true);
        }
        if (spinningWheel.isProcessing()) {
            spinningWheel.increaseProgress();
            if (spinningWheel.getProgress() >= TICKS_PER_YARN) {
                spinningWheel.useWool(WOOL_PER_YARN);
                spinningWheel.dropYarn();
                spinningWheel.resetProgress();
                if (spinningWheel.getStoredWool() < WOOL_PER_YARN) {
                    spinningWheel.setProcessing(false, true);
                }
            }
        }
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // TODO: send processing packet if on server
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
