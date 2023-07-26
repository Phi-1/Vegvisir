package dev.stormwatch.vegvisir;

import dev.stormwatch.vegvisir.capabilities.PlayerEnvironment;
import dev.stormwatch.vegvisir.capabilities.PlayerEnvironmentProvider;
import dev.stormwatch.vegvisir.environment.Shelter;
import dev.stormwatch.vegvisir.environment.Temperature;
import dev.stormwatch.vegvisir.registry.VegvisirEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnvironmentEvents {
    // TODO: below certain temperature require nearby fire to sleep
    // TODO: player cap reload on respawn

    private static final int playerTickRate = 1 * 20;
    private static final Map<UUID, Integer> playerTickCounts = new HashMap<>();

    private static final int WET_DURATION = 2400;

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;

        Player player = event.player;
        int playerTickCount = playerTickCounts.getOrDefault(player.getUUID(), 0);
        PlayerEnvironment playerEnvironment = player.getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).orElse(null);
        if (playerEnvironment == null) return;

        // Tick wet if next to fire
        if (playerEnvironment.isWet() && playerEnvironment.isNearFire()) {
            // FIXME: this doesn't work on logging in with wet status near a fire
            System.out.println("wet near fire");
            MobEffectInstance wetEffect = player.getEffect(VegvisirEffects.WET.get());
            // TODO: dont tick if player is in rain or water
            if (wetEffect != null) {
                int duration = wetEffect.getDuration();
                player.removeEffect(VegvisirEffects.WET.get());
                player.addEffect(new MobEffectInstance(VegvisirEffects.WET.get(), duration - 3));
            }
        }

        if (playerTickCount >= playerTickRate) {
            boolean wasSheltered = playerEnvironment.isSheltered();
            boolean wasWet = playerEnvironment.isWet();
            boolean isSheltered = Shelter.isSheltered(player);
            boolean isWet = player.isInWaterOrRain();

            processWetStatus(player, wasWet, isWet, playerEnvironment);

            Temperature.Fire.NearbyFireInfo fireInfo = Temperature.Fire.findNearestFire(player.blockPosition(), player.level, isSheltered);
            if (fireInfo.nearbyFire != null) playerEnvironment.setNearFire(true);
            else playerEnvironment.setNearFire(false);

            double playerTemp = calcPlayerTemperature(player, fireInfo.nearbyFire, isSheltered);
            playerEnvironment.setTemperature(playerTemp);

            if (isSheltered && !wasSheltered) Feedback.onBecomeSheltered(player);
            // TODO: this spams you are wet in rain
            if (isWet && !wasWet) Feedback.onBecomeWet(player);

            playerTickCounts.put(player.getUUID(), playerTickCount - playerTickRate);
        } else {
            playerTickCounts.put(player.getUUID(), ++playerTickCount);
        }
    }

    private static void processWetStatus(Player player, boolean wasWet, boolean isWet, PlayerEnvironment playerEnvironment) {
        MobEffectInstance wetEffect = player.getEffect(VegvisirEffects.WET.get());
        if (wetEffect == null) {
            if (wasWet && !isWet) {
                playerEnvironment.setWet(false);
                Feedback.onBecomeDry(player);
            }
            if (isWet) {
                player.addEffect(new MobEffectInstance(VegvisirEffects.WET.get(), WET_DURATION));
                playerEnvironment.setWet(true);
            }
        } else {
            if (isWet) player.addEffect(new MobEffectInstance(VegvisirEffects.WET.get(), WET_DURATION));
        }
    }

    private static double calcPlayerTemperature(Player player, BlockPos nearbyFire, boolean sheltered) {
        double biomeTemp = Temperature.Biome.convertBiomeTemperature(player.level.getBiome(player.getOnPos()).get().getBaseTemperature());
        double altitudeTemp = Temperature.Altitude.calcAltitudinalTemperatureModifier(player.getY());
        double weatherTemp = Temperature.Weather.calcWeatherTemperatureModifier(player.level, player.getOnPos());
        double seasonTemp = Temperature.Seasons.getSeasonalTemperatureModifier(player.level, player.blockPosition());
        double timeTemp = player.level.isDay() ? Temperature.DAY_TEMPERATURE_MODIFIER : Temperature.NIGHT_TEMPERATURE_MODIFIER;
        double fireTemp = 0;
        if (nearbyFire != null) {
            fireTemp = Temperature.Fire.calcFireTemperature(nearbyFire.distManhattan(player.blockPosition()), sheltered);
        }

        double temp = biomeTemp + fireTemp + altitudeTemp + weatherTemp + timeTemp + seasonTemp;

        player.displayClientMessage(Component.literal(temp + " C"), true);
        return temp;
    }

}
