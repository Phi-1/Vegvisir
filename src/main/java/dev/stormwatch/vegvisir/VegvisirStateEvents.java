package dev.stormwatch.vegvisir;

import dev.stormwatch.vegvisir.data.Temperature;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import sereneseasons.api.season.SeasonHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VegvisirStateEvents {

    private static final int ticksPerShelterCheck = 5 * 20;
    private static final Map<UUID, Integer> playerTickCounts = new HashMap<>();

    // Shelter
    private static final int maxShelterCheckingHeight = 64;
    private static final int maxVerticalDistanceFromCenter = 5;

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return;

        int playerTickCount = playerTickCounts.getOrDefault(event.player.getUUID(), 0);
        if (playerTickCount >= ticksPerShelterCheck) {
            System.out.println(Temperature.Altitude.calcAltitudinalTemperatureModifier(event.player.getY()));
            boolean sheltered = isSheltered(event.player);
            System.out.println("Sheltered: " + sheltered);
            System.out.println("Wet: " + event.player.isInWaterOrRain());
            event.player.displayClientMessage(Component.literal("Season: " + SeasonHelper.getSeasonState(event.player.getLevel()).getSeason()), true);
            playerTickCounts.put(event.player.getUUID(), playerTickCount - ticksPerShelterCheck);
        } else {
            playerTickCounts.put(event.player.getUUID(), ++playerTickCount);
        }
    }

    private static boolean isSheltered(Player player) {
        BlockPos playerPos = player.blockPosition();
        BlockPos nextPos = playerPos.above().above();
        Level level = player.getLevel();

        for (int i = 0; i < maxShelterCheckingHeight; i++) {
            boolean isSolid = level.getBlockState(nextPos).getMaterial().isSolid();
            if (isSolid) {
                // TODO: player should still be sheltered if only a couple blocks are missing from ring
                if (!adjacentColumnHasShelter(nextPos.north()       , playerPos.getY(), level)) return false;
                if (!adjacentColumnHasShelter(nextPos.north().east(), playerPos.getY(), level)) return false;
                if (!adjacentColumnHasShelter(nextPos.south().east(), playerPos.getY(), level)) return false;
                if (!adjacentColumnHasShelter(nextPos.south()       , playerPos.getY(), level)) return false;
                if (!adjacentColumnHasShelter(nextPos.east()        , playerPos.getY(), level)) return false;
                if (!adjacentColumnHasShelter(nextPos.south().west(), playerPos.getY(), level)) return false;
                if (!adjacentColumnHasShelter(nextPos.west()        , playerPos.getY(), level)) return false;
                if (!adjacentColumnHasShelter(nextPos.north().west(), playerPos.getY(), level)) return false;

                return true;
            }
            nextPos = nextPos.above();
        }

        return false;
    }

    private static boolean adjacentColumnHasShelter(BlockPos centerPos, int playerY, Level level) {
        int remainder = 0;
        int halfHeight = maxVerticalDistanceFromCenter / 2;
        for (int i = 0; i < halfHeight; i++) {
            centerPos = centerPos.below();
            // stop before player's feet
            if (centerPos.getY() <= playerY + 1) {
                remainder = halfHeight - i;
                break;
            }
        }
        for (int i = 0; i < maxVerticalDistanceFromCenter - remainder; i++) {
            if (level.getBlockState(centerPos).getMaterial().isSolid()) {
                return true;
            }
            centerPos = centerPos.above();
        }
        return false;
    }

    // TODO: entity.isInWaterOrRain()
    // TODO: popup message: you are wet, you are dry again, you are cold, you are sheltered...
    // TODO: test setTicksFrozen
    // TODO: check if sereneseasons modifies biomebasetemp, api may not be needed
}
