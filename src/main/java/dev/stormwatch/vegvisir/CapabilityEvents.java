package dev.stormwatch.vegvisir;

import dev.stormwatch.vegvisir.capabilities.CampfireFuelLevelProvider;
import dev.stormwatch.vegvisir.capabilities.LevelCampfireTrackerProvider;
import dev.stormwatch.vegvisir.capabilities.PlayerEnvironmentProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityEvents {

    @SubscribeEvent
    public static void entityCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerEnvironmentProvider.PLAYER_ENVIRONMENT).isPresent()) {
                event.addCapability(new ResourceLocation(Vegvisir.MOD_ID, "properties"), new PlayerEnvironmentProvider());
            }
        }
    }

    @SubscribeEvent
    public static void blockEntityCapabilityEvent(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof CampfireBlockEntity) {
            if (!event.getObject().getCapability(CampfireFuelLevelProvider.CAMPFIRE_FUEL_LEVEL).isPresent()) {
                event.addCapability(new ResourceLocation(Vegvisir.MOD_ID, "properties"), new CampfireFuelLevelProvider());
            }
        }
    }

    @SubscribeEvent
    public static void levelCapabilityEvent(AttachCapabilitiesEvent<Level> event) {
        if (!event.getObject().getCapability(LevelCampfireTrackerProvider.LEVEL_CAMPFIRE_TRACKER).isPresent()) {
            event.addCapability(new ResourceLocation(Vegvisir.MOD_ID, "properties"), new LevelCampfireTrackerProvider());
        }
    }

}
