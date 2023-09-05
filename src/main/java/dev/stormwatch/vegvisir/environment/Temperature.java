package dev.stormwatch.vegvisir.environment;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.UUID;

public class Temperature {
    // Temperatures are in degrees celsius (roughly)

    public static final double DAY_TEMPERATURE_MODIFIER = 3;
    public static final double NIGHT_TEMPERATURE_MODIFIER = -3;

    // TODO
    public static final double BEANIE_MODIFIER = 0;
    public static final double SWEATER_MODIFIER = 0;
    public static final double SOCKS_MODIFIER = 0;

    public static class Stats {

        private static final double MINIMAL_TEMPERATURE = -4;
        private static final double NEUTRAL_TEMPERATURE = 21;
        private static final double OPTIMAL_TEMPERATURE = 24;

        private static final double LOWER_DX = NEUTRAL_TEMPERATURE - MINIMAL_TEMPERATURE;
        private static final double UPPER_DX = OPTIMAL_TEMPERATURE - NEUTRAL_TEMPERATURE;

        private static final double NEUTRAL_STAT = 0;

        private static final String HEALTH_NAME = "vegvisir_temperature_health";
        private static final UUID   HEALTH_UUID = UUID.fromString("907f79c9-762e-481e-b8ec-3e22832f4f66");
        private static final double MIN_HEALTH = -0.7;
        private static final double MAX_HEALTH = 0.2;
        private static final double LOWER_HEALTH_B = NEUTRAL_STAT - NEUTRAL_TEMPERATURE * ((NEUTRAL_STAT - MIN_HEALTH) / LOWER_DX);
        private static final double UPPER_HEALTH_B = NEUTRAL_STAT - NEUTRAL_TEMPERATURE * ((MAX_HEALTH - NEUTRAL_STAT) / UPPER_DX);

        private static final String SPEED_NAME = "vegvisir_temperature_movespeed";
        private static final UUID   SPEED_UUID = UUID.fromString("e3358d22-95cd-4ca5-b6d7-60d520a9e200");
        private static final double MIN_SPEED = -0.4;
        private static final double MAX_SPEED = 0.2;
        private static final double LOWER_SPEED_B = NEUTRAL_STAT - NEUTRAL_TEMPERATURE * ((NEUTRAL_STAT - MIN_SPEED) / LOWER_DX);
        private static final double UPPER_SPEED_B = NEUTRAL_STAT - NEUTRAL_TEMPERATURE * ((MAX_SPEED - NEUTRAL_STAT) / UPPER_DX);

        private static final String DAMAGE_NAME = "vegvisir_temperature_damage";
        private static final UUID   DAMAGE_UUID = UUID.fromString("b023a486-3696-4f8d-9d48-d4af88200729");
        private static final double MIN_DAMAGE = -0.6;
        private static final double MAX_DAMAGE = 0.2;
        private static final double LOWER_DAMAGE_B = NEUTRAL_STAT - NEUTRAL_TEMPERATURE * ((NEUTRAL_STAT - MIN_DAMAGE) / LOWER_DX);
        private static final double UPPER_DAMAGE_B = NEUTRAL_STAT - NEUTRAL_TEMPERATURE * ((MAX_DAMAGE - NEUTRAL_STAT) / UPPER_DX);

        public static void applyTemperatureStats(Player player, double temperature) {
            double health = calcAttributeValue(temperature, MIN_HEALTH, MAX_HEALTH, LOWER_HEALTH_B, UPPER_HEALTH_B);
            setAttribute(player, Attributes.MAX_HEALTH, HEALTH_NAME, HEALTH_UUID, health);
            double speed = calcAttributeValue(temperature, MIN_SPEED, MAX_SPEED, LOWER_SPEED_B, UPPER_SPEED_B);
            setAttribute(player, Attributes.MOVEMENT_SPEED, SPEED_NAME, SPEED_UUID, speed);
            double damage = calcAttributeValue(temperature, MIN_DAMAGE, MAX_DAMAGE, LOWER_DAMAGE_B, UPPER_DAMAGE_B);
            setAttribute(player, Attributes.ATTACK_DAMAGE, DAMAGE_NAME, DAMAGE_UUID, damage);
        }

        private static double calcAttributeValue(double temperature, double minValue, double maxValue, double lowerIntercept, double upperIntercept) {
            if (temperature <= MINIMAL_TEMPERATURE) return minValue;
            else if (temperature >= OPTIMAL_TEMPERATURE) return maxValue;
            else return temperature < NEUTRAL_TEMPERATURE ?
                        temperature * ((NEUTRAL_STAT - minValue) / LOWER_DX) + lowerIntercept
                        : temperature * ((maxValue - NEUTRAL_STAT) / UPPER_DX) + upperIntercept;
        }

        private static void setAttribute(Player player, Attribute attribute, String name, UUID uuid, double amount) {
            // TODO: set player health if max health decreased and current health is > new max
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null) {
                AttributeModifier modifier = instance.getModifier(uuid);
                if (modifier != null) {
                    instance.removeModifier(uuid);
                }
                instance.addPermanentModifier(new AttributeModifier(uuid, name, amount, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

    }

    public static class Biome {
        // TODO: nether and end
        // TODO: rivers should use nearest biome
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
            // TODO: use shouldSnow, sereneseasons patches it
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

    public static class Seasons {
        private static final ImmutableMap<Season.SubSeason, Double> SEASON_TEMPERATURE_MODIFIERS = new ImmutableMap.Builder<Season.SubSeason, Double>()
                .put(Season.SubSeason.EARLY_SPRING, -3.0)
                .put(Season.SubSeason.MID_SPRING,   0.0)
                .put(Season.SubSeason.LATE_SPRING,  4.0)
                .put(Season.SubSeason.EARLY_SUMMER, 10.0)
                .put(Season.SubSeason.MID_SUMMER,   12.0)
                .put(Season.SubSeason.LATE_SUMMER,  10.0)
                .put(Season.SubSeason.EARLY_AUTUMN, 4.0)
                .put(Season.SubSeason.MID_AUTUMN,   0.0)
                .put(Season.SubSeason.LATE_AUTUMN,  -3.0)
                .put(Season.SubSeason.EARLY_WINTER, -8.0)
                .put(Season.SubSeason.MID_WINTER,   -10.0)
                .put(Season.SubSeason.LATE_WINTER,  -8.0)
                .build();

        private static final double TROPICAL_TEMPERATURE_FACTOR = 0.7;

        public static double getSeasonalTemperatureModifier(Level level, BlockPos at) {
            Season.SubSeason season = SeasonHelper.getSeasonState(level).getSubSeason();
            Double temp = SEASON_TEMPERATURE_MODIFIERS.get(season);
            if (temp == null) return 0;
            if (SeasonHelper.usesTropicalSeasons(level.getBiome(at))) {
                temp *= TROPICAL_TEMPERATURE_FACTOR;
            }
            return temp;
        }
    }

    public static class Altitude {
        // TODO: add slight curve to temp func, temp should increase faster going down from neutral, and decrease slower going up from neutral
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
            if (distanceFromFire <= 0) {
                return sheltered ? maxFireTempSheltered : maxFireTempOutside;
            }
            if (sheltered) {
                return distanceFromFire * (shelteredDY / shelteredDX) + shelteredB;
            } else {
                return distanceFromFire * (outsideDY / outsideDX) + outsideB;
            }
        }

        public static boolean isFire(BlockState block) {
            return block.is(BlockTags.FIRE) ||
                    block.is(BlockTags.CAMPFIRES);
        }

        public static NearbyFireInfo findNearestFire(BlockPos playerPos, Level level, boolean sheltered) {
            // TODO: check for lava, somewhere
            BlockPos nearestFire = null;
            int range = sheltered ? Temperature.Fire.FIRE_RANGE_SHELTERED : Temperature.Fire.FIRE_RANGE_OUTSIDE;
            // start at top of range y-wise
            BlockPos pos = new BlockPos(playerPos.getX(), playerPos.getY() + range, playerPos.getZ());
            for (int y = 0; y < range * 2; y++) {
                // Fire checking happens in a square spiral for efficiency, but should be a diamond
                int distance = pos.distManhattan(playerPos);
                if (Temperature.Fire.isFire(level.getBlockState(pos)) && !(distance > (sheltered ? FIRE_RANGE_SHELTERED : FIRE_RANGE_OUTSIDE))) {
                    if (nearestFire == null) nearestFire = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
                    else nearestFire = nearestFire.distManhattan(playerPos) < distance ? nearestFire : new BlockPos(pos.getX(), pos.getY(), pos.getZ());
                }
                for (BlockPos spiralPos : BlockPos.spiralAround(pos, range, Direction.NORTH, Direction.EAST)) {
                    // Fire checking happens in a square spiral for efficiency, but should be a diamond
                    distance = spiralPos.distManhattan(playerPos);
                    if (distance > (sheltered ? FIRE_RANGE_SHELTERED : FIRE_RANGE_OUTSIDE)) continue;
                    if (Temperature.Fire.isFire(level.getBlockState(spiralPos))) {
                        if (nearestFire == null) nearestFire = new BlockPos(spiralPos.getX(), spiralPos.getY(), spiralPos.getZ());
                        else nearestFire = nearestFire.distManhattan(playerPos) < distance ? nearestFire : new BlockPos(spiralPos.getX(), spiralPos.getY(), spiralPos.getZ());
                    }
                }
                pos = pos.below();
            }
            // TODO: lava checkign
            return new NearbyFireInfo(nearestFire, false);
        }

        public record NearbyFireInfo(BlockPos nearbyFire, boolean nearLava) {}
    }

}
