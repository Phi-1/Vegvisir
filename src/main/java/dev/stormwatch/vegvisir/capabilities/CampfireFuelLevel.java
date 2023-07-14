package dev.stormwatch.vegvisir.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class CampfireFuelLevel {

    private float fuelLevel = 3;
    public final float maxFuelLevel = 10f;

    public float getFuelLevel() {
        return this.fuelLevel;
    }

    public void addFuel(float amount) {
        this.fuelLevel = Math.min(this.fuelLevel + amount, 10);
    }

    public void consumeFuel(float amount) {
        this.fuelLevel = Math.max(this.fuelLevel - amount, 0);
    }

    public void saveNBT(CompoundTag nbt) {
        nbt.putFloat("fuel", this.fuelLevel);
    }

    public void loadNBT(CompoundTag nbt) {
        this.fuelLevel = nbt.getFloat("fuel");
    }
}
