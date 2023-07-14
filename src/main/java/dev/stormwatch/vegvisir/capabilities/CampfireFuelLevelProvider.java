package dev.stormwatch.vegvisir.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CampfireFuelLevelProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<CampfireFuelLevel> CAMPFIRE_FUEL_LEVEL = CapabilityManager.get(new CapabilityToken<CampfireFuelLevel>() {});
    private CampfireFuelLevel fuelLevel = null;
    private final LazyOptional<CampfireFuelLevel> optional = LazyOptional.of(this::getOrCreateCampfireFuelLevel);

    private CampfireFuelLevel getOrCreateCampfireFuelLevel() {
        if (this.fuelLevel == null) {
            this.fuelLevel = new CampfireFuelLevel();
        }
        return this.fuelLevel;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CAMPFIRE_FUEL_LEVEL) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        this.getOrCreateCampfireFuelLevel().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.getOrCreateCampfireFuelLevel().loadNBT(nbt);
    }
}
