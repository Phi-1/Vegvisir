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

public class PlayerNutritionProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerNutrition> PLAYER_NUTRITION = CapabilityManager.get(new CapabilityToken<PlayerNutrition>() {});
    private PlayerNutrition playerNutrition = null;
    private final LazyOptional<PlayerNutrition> optional = LazyOptional.of(this::getOrCreatePlayerNutrition);

    private PlayerNutrition getOrCreatePlayerNutrition() {
        if (this.playerNutrition == null) {
            this.playerNutrition = new PlayerNutrition();
        }
        return this.playerNutrition;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_NUTRITION) {
            return this.optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        this.getOrCreatePlayerNutrition().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.getOrCreatePlayerNutrition().loadNBT(nbt);
    }
}
