package dev.stormwatch.vegvisir.networking;

import dev.stormwatch.vegvisir.Vegvisir;
import dev.stormwatch.vegvisir.networking.packets.SpinningWheelProcessingS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class VegvisirNetworking {

    private static SimpleChannel INSTANCE;

    private static int packetID = 0;
    private static int id() { return packetID++; }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Vegvisir.MOD_ID, "networking"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE.messageBuilder(SpinningWheelProcessingS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SpinningWheelProcessingS2CPacket::new)
                .encoder(SpinningWheelProcessingS2CPacket::toBytes)
                .consumerMainThread(SpinningWheelProcessingS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClientsTrackingChunk(MSG message, LevelChunk chunk) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }

    public static <MSG> void sendToAllConnectedClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

}
