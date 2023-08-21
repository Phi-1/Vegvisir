package dev.stormwatch.vegvisir.events;

import dev.stormwatch.vegvisir.capabilities.CampfireFuelLevel;
import dev.stormwatch.vegvisir.capabilities.CampfireFuelLevelProvider;
import dev.stormwatch.vegvisir.capabilities.LevelCampfireTracker;
import dev.stormwatch.vegvisir.capabilities.LevelCampfireTrackerProvider;
import dev.stormwatch.vegvisir.environment.FuelLevels;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class CampfireEvents {

    private static int campfireTicks = 0;
    private static final int campfireTickRate = 1 * 20;
    private static final float campfireFuelConsumption = 2.5f / 24000 * campfireTickRate; // 24000 = ticks per minecraft day, 10 / 24000 lasts 1 day on full fuel

    @SubscribeEvent
    public static void onPlaceCampfire(BlockEvent.EntityPlaceEvent event) {
        // FIXME: this triggers on campfire relighting
        Level level = event.getEntity().getLevel();
        if (level.isClientSide()) return;
        if (!(event.getPlacedBlock().getBlock() instanceof CampfireBlock)) return;
        LevelCampfireTracker tracker = level.getCapability(LevelCampfireTrackerProvider.LEVEL_CAMPFIRE_TRACKER).orElse(null);
        if (tracker == null) return;
        // FIXME: for now it checks for duplicate positions in tracker
        tracker.trackCampfire(event.getPos());
    }

    @SubscribeEvent
    public static void onRightClickCampfire(PlayerInteractEvent.RightClickBlock event) {
        // This event triggers once for each hand
        if (event.getSide() == LogicalSide.CLIENT) return;
        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (blockEntity instanceof CampfireBlockEntity) {
            CampfireBlockEntity campfire = (CampfireBlockEntity) blockEntity;
            ItemStack usedItem = event.getItemStack();
            if (usedItem.is(Items.STICK)) {
                CampfireFuelLevel fuelLevel = campfire.getCapability(CampfireFuelLevelProvider.CAMPFIRE_FUEL_LEVEL).orElse(null);
                if (fuelLevel == null) return;
                if (Math.ceil(fuelLevel.getFuelLevel()) >= fuelLevel.maxFuelLevel) return;
                addFuelToFire(FuelLevels.STICK, fuelLevel, usedItem, event.getEntity());
            } else if (usedItem.is(Items.COAL) || usedItem.is(Items.CHARCOAL)) {
                CampfireFuelLevel fuelLevel = campfire.getCapability(CampfireFuelLevelProvider.CAMPFIRE_FUEL_LEVEL).orElse(null);
                if (fuelLevel == null) return;
                if (Math.ceil(fuelLevel.getFuelLevel()) >= fuelLevel.maxFuelLevel) return;
                addFuelToFire(FuelLevels.COAL, fuelLevel, usedItem, event.getEntity());
            }
        }
    }

    // TODO: tick campfires after sleeping, maybe just a set amount regardless of sleeping time
    // TODO: sleeping needs nearby fire
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

    private static void addFuelToFire(float amount, CampfireFuelLevel fuelLevel, ItemStack usedItem, Player player) {
        fuelLevel.addFuel(amount);
        usedItem.shrink(1);
        player.getLevel().playSound(null, player.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS);
        player.displayClientMessage(Component.literal("Added fuel to fire " + (int) Math.ceil(fuelLevel.getFuelLevel()) + " / " + (int) fuelLevel.maxFuelLevel), true);
    }
}
