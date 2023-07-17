package dev.stormwatch.vegvisir;

import dev.stormwatch.vegvisir.capabilities.PlayerEnvironment;
import dev.stormwatch.vegvisir.capabilities.PlayerEnvironmentProvider;
import dev.stormwatch.vegvisir.environment.Shelter;
import dev.stormwatch.vegvisir.environment.Temperature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnvironmentEvents {

    private static final int playerTickRate = 1 * 20;
    private static final Map<UUID, Integer> playerTickCounts = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;

        Player player = event.player;
        int playerTickCount = playerTickCounts.getOrDefault(player.getUUID(), 0);

        if (playerTickCount >= playerTickRate) {
            PlayerEnvironment playerEnvironment = player.getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).orElse(null);
            if (playerEnvironment == null) {
                playerTickCounts.put(player.getUUID(), playerTickCount - playerTickRate);
                return;
            }

            boolean sheltered = Shelter.isSheltered(player);
            boolean wet = player.isInWaterOrRain();

            BlockPos nearbyFire = findNearestFire(player.getOnPos(), player.level, sheltered);
            double fireTemp = 0;
            if (nearbyFire != null) {
                fireTemp = Temperature.Fire.calcFireTemperature(nearbyFire.distManhattan(player.getOnPos()), sheltered);
            }

            double altitudeTemp = Temperature.Altitude.calcAltitudinalTemperatureModifier(player.getY());

            double weatherTemp = Temperature.Weather.calcWeatherTemperatureModifier(player.level, player.getOnPos());

            boolean wasSheltered = playerEnvironment.isSheltered();
            boolean wasWet = playerEnvironment.isWet();

            if (sheltered && !wasSheltered) Feedback.onBecomeSheltered(player);
            if (wet && !wasWet) Feedback.onBecomeWet(player);

            playerTickCounts.put(player.getUUID(), playerTickCount - playerTickRate);
        } else {
            playerTickCounts.put(player.getUUID(), ++playerTickCount);
        }
    }

    private static BlockPos findNearestFire(BlockPos playerPos, Level level, boolean sheltered) {
        // TODO: check for lava, somewhere
        BlockPos nearestFire = null;
        int range = sheltered ? Temperature.Fire.FIRE_RANGE_SHELTERED : Temperature.Fire.FIRE_RANGE_OUTSIDE;
        // start at top of range y-wise
        BlockPos pos = new BlockPos(playerPos.getX(), playerPos.getY() + range, playerPos.getZ());
        for (int y = 0; y < range * 2; y++) {
            if (Temperature.Fire.isFire(level.getBlockState(pos))) {
                if (nearestFire == null) nearestFire = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
                else nearestFire = nearestFire.distManhattan(playerPos) < pos.distManhattan(playerPos) ? nearestFire : new BlockPos(pos.getX(), pos.getY(), pos.getZ());
            }
            for (BlockPos spiralPos : BlockPos.spiralAround(pos, range, Direction.NORTH, Direction.EAST)) {
                if (Temperature.Fire.isFire(level.getBlockState(spiralPos))) {
                    if (nearestFire == null) nearestFire = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
                    else nearestFire = nearestFire.distManhattan(playerPos) < pos.distManhattan(playerPos) ? nearestFire : new BlockPos(pos.getX(), pos.getY(), pos.getZ());
                }
            }
            pos = pos.below();
        }
        return nearestFire;
    }



    // TODO: entity.isInWaterOrRain()
    // TODO: popup message: you are wet, you are dry again, you are cold, you are sheltered...
    // TODO: test setTicksFrozen
    // TODO: check if sereneseasons modifies biomebasetemp, api may not be needed
}
