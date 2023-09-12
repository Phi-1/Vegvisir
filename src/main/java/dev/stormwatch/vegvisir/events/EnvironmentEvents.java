package dev.stormwatch.vegvisir.events;

import dev.stormwatch.vegvisir.Feedback;
import dev.stormwatch.vegvisir.capabilities.PlayerEnvironment;
import dev.stormwatch.vegvisir.capabilities.PlayerEnvironmentProvider;
import dev.stormwatch.vegvisir.environment.Shelter;
import dev.stormwatch.vegvisir.environment.Temperature;
import dev.stormwatch.vegvisir.registry.VegvisirEffects;
import dev.stormwatch.vegvisir.registry.VegvisirTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnvironmentEvents {
    // TODO: below certain temperature require nearby fire to sleep

    private static final int PLAYER_TICKRATE = 1 * 20;

    private static final int WET_DURATION = 2400;

    @SubscribeEvent
    public static void onPlayerDeathEvent(PlayerEvent.Clone event) {
        if (event.getEntity().level.isClientSide()) return;
        if (!event.isWasDeath()) return;
        Player newPlayer = event.getEntity();
        Player originalPlayer = event.getOriginal();

        PlayerEnvironment playerEnvironment = newPlayer.getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).orElse(PlayerEnvironment.EMPTY);
        PlayerEnvironment oldPlayerEnvironment = originalPlayer.getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).orElse(PlayerEnvironment.EMPTY);
        if (playerEnvironment == PlayerEnvironment.EMPTY || oldPlayerEnvironment == PlayerEnvironment.EMPTY) return;
        playerEnvironment.copyFrom(oldPlayerEnvironment);
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;

        Player player = event.player;
        PlayerEnvironment playerEnvironment = player.getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).orElse(PlayerEnvironment.EMPTY);
        if (playerEnvironment == PlayerEnvironment.EMPTY) return;

        // Tick wet if next to fire
        if (playerEnvironment.isWet() && playerEnvironment.isNearFire() && !player.isInWaterOrRain()) {
            MobEffectInstance wetEffect = player.getEffect(VegvisirEffects.WET.get());
            if (wetEffect != null) {
                int duration = wetEffect.getDuration();
                player.removeEffect(VegvisirEffects.WET.get());
                player.addEffect(new MobEffectInstance(VegvisirEffects.WET.get(), duration - 3));
            }
        }

        if (player.level.getGameTime() % PLAYER_TICKRATE == 0) {
            // TODO: this lags the game, not in new world though? keep an eye on it, might be a memory leak
            boolean wasSheltered = playerEnvironment.isSheltered();
            boolean wasWet = playerEnvironment.isWet();
            boolean isSheltered = Shelter.isSheltered(player);
            boolean isWet = player.isInWaterOrRain();

            // Wet
            processWetStatus(player, wasWet, isWet, playerEnvironment);

            // Temperature
            Temperature.Fire.NearbyFireInfo fireInfo = Temperature.Fire.findNearestFire(player.blockPosition(), player.level, isSheltered);
            playerEnvironment.setNearFire(fireInfo.nearbyFire() != null);
            double playerTemp = calcPlayerTemperature(player, fireInfo.nearbyFire(), isSheltered);
            playerEnvironment.setTemperature(playerTemp);
            Temperature.Stats.applyTemperatureStats(player, playerTemp);

            // Shelter
            playerEnvironment.setSheltered(isSheltered);
            if (isSheltered && !wasSheltered) Feedback.onBecomeSheltered(player);
        }
    }

    private static void processWetStatus(Player player, boolean wasWet, boolean isWet, PlayerEnvironment playerEnvironment) {
        if (isWet) {
            player.addEffect(new MobEffectInstance(VegvisirEffects.WET.get(), WET_DURATION));
            playerEnvironment.setWet(true);
            if (!wasWet) {
                Feedback.onBecomeWet(player);
            }
        } else if (!player.hasEffect(VegvisirEffects.WET.get())) {
            playerEnvironment.setWet(false);
            if (wasWet) {
                Feedback.onBecomeDry(player);
            }
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

        double clothingTemp = 0;
        IItemHandlerModifiable curios = CuriosApi.getCuriosHelper().getEquippedCurios(player).orElseThrow(() -> new IllegalStateException("Player does not have a curios inventory"));
        for (int slot = 0; slot < curios.getSlots(); slot++) {
            ItemStack stack = curios.getStackInSlot(slot);
            if (stack.is(VegvisirTags.Items.SWEATER)) clothingTemp += Temperature.SWEATER_MODIFIER;
            else if (stack.is(VegvisirTags.Items.BEANIE)) clothingTemp += Temperature.BEANIE_MODIFIER;
            else if (stack.is(VegvisirTags.Items.SOCKS)) clothingTemp += Temperature.SOCKS_MODIFIER;
        }

        double effectTemp = 0;
        if (player.hasEffect(VegvisirEffects.WARMTH.get())) {
            int amplifier = player.getEffect(VegvisirEffects.WARMTH.get()).getAmplifier();
            effectTemp += amplifier == 0 ? Temperature.WARMTH_EFFECT_MODIFIER : Temperature.WARMTH_EFFECT_MODIFIER_STRONG;
        }

        double temp = biomeTemp + fireTemp + altitudeTemp + weatherTemp + timeTemp + seasonTemp + clothingTemp + effectTemp;

//        player.displayClientMessage(Component.literal(temp + " C"), true);
        return temp;
    }

}
