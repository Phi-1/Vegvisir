package dev.stormwatch.vegvisir.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelCampfireTrackerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<LevelCampfireTracker> LEVEL_CAMPFIRE_TRACKER = CapabilityManager.get(new CapabilityToken<LevelCampfireTracker>() {});
    private LevelCampfireTracker campfireTracker = null;
    private final LazyOptional<LevelCampfireTracker> optional = LazyOptional.of(this::getOrCreateLevelCampfireTracker);

    private LevelCampfireTracker getOrCreateLevelCampfireTracker() {
        if (this.campfireTracker == null) {
            this.campfireTracker = new LevelCampfireTracker();
        }
        return this.campfireTracker;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == LEVEL_CAMPFIRE_TRACKER) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        this.getOrCreateLevelCampfireTracker().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.getOrCreateLevelCampfireTracker().loadNBT(nbt);
    }
}
