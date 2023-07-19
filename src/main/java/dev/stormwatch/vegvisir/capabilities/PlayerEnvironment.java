package dev.stormwatch.vegvisir.capabilities;

import net.minecraft.nbt.CompoundTag;

public class PlayerEnvironment {

    private boolean sheltered = false;
    private boolean wet = false;
    private boolean nearFire = false;
    private double temperature = 15;

    public double getTemperature() {
        return this.temperature;
    }

    public boolean isSheltered() {
        return this.sheltered;
    }

    public boolean isWet() {
        return wet;
    }

    public boolean isNearFire() {
        return nearFire;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setSheltered(boolean sheltered) {
        this.sheltered = sheltered;
    }

    public void setWet(boolean wet) {
        this.wet = wet;
    }

    public void setNearFire(boolean nearFire) {
        this.nearFire = nearFire;
    }

    public void saveNBT(CompoundTag nbt) {
        nbt.putDouble("temperature", temperature);
    }

    public void loadNBT(CompoundTag nbt) {
        this.temperature = nbt.getDouble("temperature");
    }

}
