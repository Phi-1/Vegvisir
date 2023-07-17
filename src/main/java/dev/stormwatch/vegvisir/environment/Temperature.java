package dev.stormwatch.vegvisir.environment;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class Temperature {
    // Temperatures are in degrees celsius (roughly)

    public static class Biome {
        // TODO: nether and end
        private static final double scalingFactor = 1.6;
        private static final double conversionFactor = 6;

        public static double convertBiomeTemperature(float biomeTemp) {
            return (biomeTemp + biomeTemp * scalingFactor) * conversionFactor;
        }
    }

    public static class Weather {
        public static final double RAIN_MODIFIER = -3;
        public static final double SNOW_MODIFIER = -5;

        public static double calcWeatherTemperatureModifier(Level level, BlockPos playerPos) {
            if (level.isRaining()) {
                if (level.getBiome(playerPos).get().coldEnoughToSnow(playerPos)) {
                    return SNOW_MODIFIER;
                }
                if (SeasonHelper.usesTropicalSeasons(level.getBiome(playerPos))
                        || SeasonHelper.getSeasonState(level).getSeason() != Season.WINTER) {
                    return RAIN_MODIFIER;
                }
                return SNOW_MODIFIER;
            }
            return 0;
        }
    }

    public static class Altitude {
        // TODO: add slight curve to temp func, temp should increase faster going down from neutral, and decrease slower going up from neutral
        // TODO: rework temp values once temp system is flushed out
        private static final int yAtNeutralTemp = 64;
        private static final int yAtMaxTemp = 0;
        private static final int yAtMinTemp = 320;
        private static final double neutralTemp = 0;
        private static final double maxTemp = 10;
        private static final double minTemp = -30;

        // Below neutral Y function constants
        private static final double lowerDY = neutralTemp - maxTemp;
        private static final double lowerDX = yAtNeutralTemp - yAtMaxTemp;
        private static final double lowerB = neutralTemp - yAtNeutralTemp * (lowerDY / lowerDX);
        // Above neutral Y function constants
        private static final double higherDY = minTemp - neutralTemp;
        private static final double higherDX = yAtMinTemp - yAtNeutralTemp;
        private static final double higherB = neutralTemp - yAtNeutralTemp * (higherDY / higherDX);

        public static double calcAltitudinalTemperatureModifier(double yPos) {
            if (yPos <= yAtNeutralTemp) {
                if (yPos <= yAtMaxTemp) return maxTemp;
                return yPos * (lowerDY / lowerDX) + lowerB;
            }
            if (yPos >= yAtMinTemp) return minTemp;
            return yPos * (higherDY / higherDX) + higherB;
        }
    }

    public static class Fire {
        // Fire should be able to keep a house warm in cold biomes
        public static final int FIRE_RANGE_OUTSIDE = 3;
        public static final int FIRE_RANGE_SHELTERED = 8;

        // min is at range, max is at distance 1
        private static final double minFireTempOutside = 3;
        private static final double maxFireTempOutside = 5;
        private static final double minFireTempSheltered = 5;
        private static final double maxFireTempSheltered = 15;

        // Outside function constants
        private static final double outsideDY = maxFireTempOutside - minFireTempOutside;
        private static final double outsideDX = 1 - FIRE_RANGE_OUTSIDE;
        private static final double outsideB = maxFireTempOutside - 1 * (outsideDY / outsideDX);
        // Sheltered function constants
        private static final double shelteredDY = maxFireTempSheltered - minFireTempSheltered;
        private static final double shelteredDX = 1 - FIRE_RANGE_SHELTERED;
        private static final double shelteredB = maxFireTempSheltered - 1 * (shelteredDY / shelteredDX);

        public static double calcFireTemperature(int distanceFromFire, boolean sheltered) {
            if  (distanceFromFire <= 0) {
                return sheltered ? maxFireTempSheltered : maxFireTempOutside;
            }
            if (sheltered) {
                if (distanceFromFire > FIRE_RANGE_SHELTERED) return 0;
                return distanceFromFire * (shelteredDY / shelteredDX) + shelteredB;
            } else {
                if (distanceFromFire > FIRE_RANGE_OUTSIDE) return 0;
                return distanceFromFire * (outsideDY / outsideDX) + outsideB;
            }
        }

        public static boolean isFire(BlockState block) {
            return block.is(BlockTags.FIRE) ||
                    block.is(BlockTags.CAMPFIRES);
        }
    }

}
