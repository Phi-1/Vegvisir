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

import javax.sound.midi.SysexMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnvironmentEvents {
    // TODO: below certain temperature require nearby fire to sleep

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
            // FIXME: this is never true, also campfire temps arent working
            System.out.println("wet near fire");
            MobEffectInstance wetEffect = player.getEffect(VegvisirEffects.WET.get());
            if (wetEffect != null) {
                int duration = wetEffect.getDuration();
                player.addEffect(new MobEffectInstance(VegvisirEffects.WET.get(), duration - 1));
            }
        }

        if (playerTickCount >= playerTickRate) {
            boolean wasSheltered = playerEnvironment.isSheltered();
            boolean wasWet = playerEnvironment.isWet();
            boolean isSheltered = Shelter.isSheltered(player);
            boolean isWet = player.isInWaterOrRain();

            processWetStatus(player, wasWet, isWet, playerEnvironment);

            BlockPos nearbyFire = Temperature.Fire.findNearestFire(player.blockPosition(), player.level, isSheltered);
            if (nearbyFire != null) playerEnvironment.setNearFire(true);
            else playerEnvironment.setNearFire(false);

            double playerTemp = calcPlayerTemperature(player, nearbyFire, isSheltered);
            playerEnvironment.setTemperature(playerTemp);

            if (isSheltered && !wasSheltered) Feedback.onBecomeSheltered(player);
            if (isWet && !wasWet) Feedback.onBecomeWet(player);

            playerTickCounts.put(player.getUUID(), playerTickCount - playerTickRate);
        } else {
            playerTickCounts.put(player.getUUID(), ++playerTickCount);
        }
    }

    private static void processWetStatus(Player player, boolean wasWet, boolean isWet, PlayerEnvironment playerEnvironment) {
        MobEffectInstance wetEffect = player.getEffect(VegvisirEffects.WET.get());
        if (wetEffect == null) {
            if (wasWet && !isWet) playerEnvironment.setWet(false);
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
        // TODO: season
        double timeTemp = player.level.isDay() ? Temperature.DAY_TEMPERATURE_MODIFIER : Temperature.NIGHT_TEMPERATURE_MODIFIER;
        double fireTemp = 0;
        if (nearbyFire != null) {
            fireTemp = Temperature.Fire.calcFireTemperature(nearbyFire.distManhattan(player.blockPosition()), sheltered);
        }

        double temp = biomeTemp + fireTemp + altitudeTemp + weatherTemp + timeTemp;

        player.displayClientMessage(Component.literal(temp + " C"), true);
        return temp;
    }

}
