package dev.stormwatch.vegvisir.client;

import dev.stormwatch.vegvisir.blockentities.SpinningWheelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

public class SpinningWheelStates {

    private static boolean updated = false;
    private static final HashMap<BlockPos, Boolean> STATES = new HashMap<>();

    public static void setState(BlockPos pos, boolean processing) {
        if (Boolean.TRUE.equals(STATES.put(pos, processing)) != processing) {
            updated = true;
        }
    }

    public static boolean wasUpdated() {
        return updated;
    }

    public static void updateAllSpinningWheels(Level level) {
        STATES.forEach((pos, processing) -> {
            BlockEntity entity = level.getBlockEntity(pos);
            if (!(entity instanceof SpinningWheelBlockEntity spinningWheel)) return;
            spinningWheel.setProcessing(processing, false);
        });
    }

}
