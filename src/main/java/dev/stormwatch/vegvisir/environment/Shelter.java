package dev.stormwatch.vegvisir.environment;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Shelter {
    // TODO: shelter checking is ass in small caves and buildings
    // TODO: if shelter check encounters one block above player it stops searching, even if there is valid shelter somewhere above it

    private static final int maxShelterCheckingHeight = 64;
    private static final int maxVerticalDistanceFromCenter = 9;

    public static boolean isValidShelter(BlockState block) {
        return block.getMaterial().isSolid() &&
                !block.is(BlockTags.LEAVES) &&
                !block.is(BlockTags.FENCES) &&
                !block.is(BlockTags.FENCE_GATES);
    }

    public static boolean isSheltered(Player player) {
        BlockPos playerPos = player.blockPosition();
        BlockPos nextPos = playerPos.above().above();
        Level level = player.getLevel();

        for (int i = 0; i < maxShelterCheckingHeight; i++) {
            BlockState blockState = level.getBlockState(nextPos);
            if (Shelter.isValidShelter(blockState)) {
                // TODO: player should still be sheltered if only a couple blocks are missing from ring
                // TODO: update with spiralAround, or findClosestMatch
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

    public static boolean adjacentColumnHasShelter(BlockPos centerPos, int playerY, Level level) {
        int remainder = 0;
        int halfHeight = maxVerticalDistanceFromCenter / 2;
        // TODO: rework this, at least calc the remainder without an entire for loop
        for (int i = 0; i < halfHeight; i++) {
            centerPos = centerPos.below();
            // stop before player's feet
            if (centerPos.getY() <= playerY + 1) {
                remainder = halfHeight - i;
                break;
            }
        }
        for (int i = 0; i < maxVerticalDistanceFromCenter - remainder; i++) {
            if (Shelter.isValidShelter(level.getBlockState(centerPos))) {
                return true;
            }
            centerPos = centerPos.above();
        }
        return false;
    }

}
