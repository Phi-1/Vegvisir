package dev.stormwatch.vegvisir.networking.packets;

import dev.stormwatch.vegvisir.client.SpinningWheelStates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpinningWheelProcessingS2CPacket {

    private final boolean processing;
    private final BlockPos pos;

    public SpinningWheelProcessingS2CPacket(boolean processing, BlockPos pos) {
        this.processing = processing;
        this.pos = pos;
    }

    public SpinningWheelProcessingS2CPacket(FriendlyByteBuf buffer) {
        this.processing = buffer.readBoolean();
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        this.pos = new BlockPos(x, y, z);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBoolean(processing);
        buffer.writeInt(pos.getX());
        buffer.writeInt(pos.getY());
        buffer.writeInt(pos.getZ());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                SpinningWheelStates.setState(this.pos, this.processing);
            });
        });
        context.get().setPacketHandled(true);
    }

}
