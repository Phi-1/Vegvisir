package dev.stormwatch.vegvisir;

import dev.stormwatch.vegvisir.capabilities.CampfireFuelLevelProvider;
import dev.stormwatch.vegvisir.capabilities.LevelCampfireTracker;
import dev.stormwatch.vegvisir.capabilities.LevelCampfireTrackerProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityEvents {
    @SubscribeEvent
    public static void entityCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
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

    @SubscribeEvent
    public static void onPlaceCampfire(BlockEvent.EntityPlaceEvent event) {
        Level level = event.getEntity().getLevel();
        if (level.isClientSide()) return;
        if (!(event.getPlacedBlock().getBlock() instanceof CampfireBlock)) return;
        System.out.println("Player placed campfire");
        LevelCampfireTracker tracker = level.getCapability(LevelCampfireTrackerProvider.LEVEL_CAMPFIRE_TRACKER).orElse(null);
        if (tracker == null) return;
        tracker.trackCampfire(event.getPos());
    }

    // TODO: move to config, and events to own class
    private static int campfireTicks = 0;
    private static int campfireTickRate = 1 * 20;
    private static float campfireFuelConsumption = 2.5f / 24000 * campfireTickRate; // 24000 = ticks per minecraft day, 10 / 24000 lasts 1 day on full fuel
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        if (level.isClientSide() || event.phase == TickEvent.Phase.START) return;
        if (campfireTicks >= campfireTickRate) {
            LevelCampfireTracker tracker = level.getCapability(LevelCampfireTrackerProvider.LEVEL_CAMPFIRE_TRACKER).orElse(null);
            if (tracker == null) return;
            tracker.tickAllCampfires(level, campfireFuelConsumption);
            campfireTicks -= campfireTickRate;
        }
        campfireTicks++;
    }

}
