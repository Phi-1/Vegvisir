package dev.stormwatch.vegvisir.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.ArrayList;

import static net.minecraft.world.level.block.CampfireBlock.LIT;

@AutoRegisterCapability
public class LevelCampfireTracker {

    private final ArrayList<BlockPos> campfirePositions = new ArrayList<>();

    public void trackCampfire(BlockPos pos) {
        for (BlockPos existingPos : this.campfirePositions) {
            if (pos.equals(existingPos)) return;
        }
        this.campfirePositions.add(pos);
    }

    public void tickAllCampfires(Level level, float amount) {
        ArrayList<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos campfirePos : this.campfirePositions) {
            BlockEntity campfireBlockEntity = level.getBlockEntity(campfirePos);
            if (!(campfireBlockEntity instanceof CampfireBlockEntity)) {
                toRemove.add(campfirePos);
                continue;
            }
            BlockState campfireState = level.getBlockState(campfirePos);
            if (!campfireState.getValue(LIT)) continue;
            CampfireFuelLevel fuelLevel = campfireBlockEntity.getCapability(CampfireFuelLevelProvider.CAMPFIRE_FUEL_LEVEL).orElse(null);
            if (fuelLevel == null) continue;
            fuelLevel.consumeFuel(amount);
            if (fuelLevel.getFuelLevel() <= 0) {
                level.setBlock(campfirePos, campfireState.setValue(LIT, Boolean.valueOf(false)), 3);
            }
        }
        for (BlockPos pos : toRemove) {
            this.campfirePositions.remove(pos);
        }
    }

    public void saveNBT(CompoundTag nbt) {
        ArrayList<Integer> asInts = new ArrayList<>();
        for (BlockPos pos : this.campfirePositions) {
            asInts.add(pos.getX());
            asInts.add(pos.getY());
            asInts.add(pos.getZ());
        }
        nbt.putIntArray("campfires", asInts);
    }

    public void loadNBT(CompoundTag nbt) {
        int[] intCoords = nbt.getIntArray("campfires");
        for (int i = 0; i < intCoords.length; i += 3) {
            BlockPos pos = new BlockPos(intCoords[i], intCoords[i+1], intCoords[i+2]);
            this.campfirePositions.add(pos);
        }
    }

}
