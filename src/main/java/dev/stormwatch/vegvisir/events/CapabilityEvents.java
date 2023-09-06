package dev.stormwatch.vegvisir.events;

import dev.stormwatch.vegvisir.Vegvisir;
import dev.stormwatch.vegvisir.capabilities.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityEvents {

    @SubscribeEvent
    public static void entityCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).isPresent()) {
                event.addCapability(new ResourceLocation(Vegvisir.MOD_ID, "player_environment"), new PlayerEnvironmentProvider());
            }
            if (!event.getObject().getCapability(PlayerNutritionProvider.PLAYER_NUTRITION).isPresent()) {
                event.addCapability(new ResourceLocation(Vegvisir.MOD_ID, "player_nutrition"), new PlayerNutritionProvider());
            }
        }
    }

    @SubscribeEvent
    public static void blockEntityCapabilityEvent(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof CampfireBlockEntity) {
            if (!event.getObject().getCapability(CampfireFuelLevelProvider.CAMPFIRE_FUEL_LEVEL).isPresent()) {
                event.addCapability(new ResourceLocation(Vegvisir.MOD_ID, "campfire_fuel"), new CampfireFuelLevelProvider());
            }
        }
    }

    @SubscribeEvent
    public static void levelCapabilityEvent(AttachCapabilitiesEvent<Level> event) {
        if (!event.getObject().getCapability(LevelCampfireTrackerProvider.LEVEL_CAMPFIRE_TRACKER).isPresent()) {
            event.addCapability(new ResourceLocation(Vegvisir.MOD_ID, "campfire_tracker"), new LevelCampfireTrackerProvider());
        }
    }

    @SubscribeEvent
    public static void clonePlayerCapabilitiesOnDeath(PlayerEvent.Clone event) {
        if (event.getEntity().level.isClientSide()) return;
        if (!event.isWasDeath()) return;
        Player newPlayer = event.getEntity();
        Player originalPlayer = event.getOriginal();

        PlayerNutrition playerNutrition = newPlayer.getCapability(PlayerNutritionProvider.PLAYER_NUTRITION).orElse(PlayerNutrition.EMPTY);
        PlayerNutrition oldPlayerNutrition = originalPlayer.getCapability(PlayerNutritionProvider.PLAYER_NUTRITION).orElse(PlayerNutrition.EMPTY);
        if (playerNutrition == PlayerNutrition.EMPTY || oldPlayerNutrition == PlayerNutrition.EMPTY) return;
        playerNutrition.copyFrom(oldPlayerNutrition);

        PlayerEnvironment playerEnvironment = newPlayer.getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).orElse(PlayerEnvironment.EMPTY);
        PlayerEnvironment oldPlayerEnvironment = originalPlayer.getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).orElse(PlayerEnvironment.EMPTY);
        if (playerEnvironment == PlayerEnvironment.EMPTY || oldPlayerEnvironment == PlayerEnvironment.EMPTY) return;
        playerEnvironment.copyFrom(oldPlayerEnvironment);
    }

}
