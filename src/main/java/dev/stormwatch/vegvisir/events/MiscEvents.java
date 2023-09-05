package dev.stormwatch.vegvisir.events;

import dev.stormwatch.vegvisir.registry.VegvisirItems;
import dev.stormwatch.vegvisir.registry.VegvisirTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MiscEvents {

    @SubscribeEvent
    public static void retainBowl(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        if (player.level.isClientSide()) return;
        if (!event.getCrafting().is(VegvisirTags.Items.RETURNS_BOWL)) return;

        if (!player.getInventory().add(new ItemStack(Items.BOWL))) {
            player.level.addFreshEntity(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.BOWL)));
        }
    }

}
