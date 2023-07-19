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
    // TODO: split neatly into functions, and calculate state at proper points to avoid duplication
    // TODO: below certain temperature require nearby fire to sleep

    private static final int playerTickRate = 1 * 20;
    private static final Map<UUID, Integer> playerTickCounts = new HashMap<>();

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
            MobEffectInstance wetEffect = player.getEffect(VegvisirEffects.WET.get());
            if (wetEffect != null) {
                wetEffect.tick(player, () -> playerEnvironment.setWet(false));
            }
        }

        if (playerTickCount >= playerTickRate) {
            boolean sheltered = Shelter.isSheltered(player);
            boolean wet = player.isInWaterOrRain();
            boolean wasSheltered = playerEnvironment.isSheltered();
            boolean wasWet = playerEnvironment.isWet();

            MobEffectInstance wetEffect = player.getEffect(VegvisirEffects.WET.get());
            if (wetEffect == null && wasWet) {
                playerEnvironment.setWet(false);
            }

            if (wet && !wasWet) {
                player.addEffect(new MobEffectInstance(VegvisirEffects.WET.get(), 2400));
                playerEnvironment.setWet(true);
            }

            double biomeTemp = Temperature.Biome.convertBiomeTemperature(player.level.getBiome(player.getOnPos()).get().getBaseTemperature());

            BlockPos nearbyFire = Temperature.Fire.findNearestFire(player.getOnPos(), player.level, sheltered);
            double fireTemp = 0;
            if (nearbyFire != null) {
                playerEnvironment.setNearFire(true);
                fireTemp = Temperature.Fire.calcFireTemperature(nearbyFire.distManhattan(player.getOnPos()), sheltered);
            }

            double altitudeTemp = Temperature.Altitude.calcAltitudinalTemperatureModifier(player.getY());
            double weatherTemp = Temperature.Weather.calcWeatherTemperatureModifier(player.level, player.getOnPos());
            double timeTemp = player.level.isDay() ? Temperature.DAY_TEMPERATURE_MODIFIER : Temperature.NIGHT_TEMPERATURE_MODIFIER;
            double temp = biomeTemp + fireTemp + altitudeTemp + weatherTemp + timeTemp;
            // TODO: season, day/night

            player.displayClientMessage(Component.literal(temp + " C"), true);

//            if (sheltered && !wasSheltered) Feedback.onBecomeSheltered(player);
//            if (wet && !wasWet) Feedback.onBecomeWet(player);

            playerTickCounts.put(player.getUUID(), playerTickCount - playerTickRate);
        } else {
            playerTickCounts.put(player.getUUID(), ++playerTickCount);
        }
    }


}
