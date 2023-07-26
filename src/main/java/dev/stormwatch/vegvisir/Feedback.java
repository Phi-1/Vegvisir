package dev.stormwatch.vegvisir;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class Feedback {

    public static void onBecomeSheltered(Player player) {
        player.displayClientMessage(Component.literal("You are sheltered"), true);
    }

    public static void onBecomeWet(Player player) {
        player.displayClientMessage(Component.literal("You are wet"), true);
    }

    public static void onBecomeDry(Player player) {
        player.displayClientMessage(Component.literal("You are dry again"), true);
    }

}
