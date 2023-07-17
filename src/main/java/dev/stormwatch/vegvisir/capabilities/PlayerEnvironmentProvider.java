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

public class PlayerEnvironmentProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerEnvironment> PLAYER_ENVIRONMENT = CapabilityManager.get(new CapabilityToken<PlayerEnvironment>() {});
    private PlayerEnvironment playerEnvironment = null;
    private final LazyOptional<PlayerEnvironment> optional = LazyOptional.of(this::getOrCreatePlayerEnvironment);

    private PlayerEnvironment getOrCreatePlayerEnvironment() {
        if (this.playerEnvironment == null) {
            this.playerEnvironment = new PlayerEnvironment();
        }
        return this.playerEnvironment;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_ENVIRONMENT) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        this.getOrCreatePlayerEnvironment().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.getOrCreatePlayerEnvironment().loadNBT(nbt);
    }
}
