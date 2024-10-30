package de.srendi.advancedperipherals.network.argoggles;

import de.srendi.advancedperipherals.common.blocks.blockentities.ARControllerEntity;
import de.srendi.advancedperipherals.network.APNetworking;
import de.srendi.advancedperipherals.network.base.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestHudCanvasMessage implements IPacket {

    private final BlockPos blockPos;
    private final String dimensionKey;

    public RequestHudCanvasMessage(BlockPos blockPos, String dimensionKey) {
        this.blockPos = blockPos;
        this.dimensionKey = dimensionKey;
    }

    public static RequestHudCanvasMessage decode(FriendlyByteBuf buf) {
        BlockPos blockPos = buf.readBlockPos();
        String dimensionKey = buf.readUtf(Short.MAX_VALUE);
        return new RequestHudCanvasMessage(blockPos, dimensionKey);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public String getDimensionKey() {
        return dimensionKey;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Iterable<ServerLevel> worlds = context.getSender().getServer().getAllLevels();
            for (ServerLevel world : worlds) {
                if (world.dimension().toString().equals(getDimensionKey())) {
                    BlockEntity te = world.getBlockEntity(getBlockPos());
                    if (!(te instanceof ARControllerEntity controller)) return;
                    APNetworking.sendTo(new UpdateHudCanvasMessage(controller.getCanvas()), context.getSender());
                    break;
                }
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(getBlockPos());
        buffer.writeUtf(getDimensionKey(), Short.MAX_VALUE);
    }
}
