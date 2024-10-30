package de.srendi.advancedperipherals.network.argoggles;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import de.srendi.advancedperipherals.network.base.IPacket;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateHudCanvasMessage implements IPacket {
    private static final String LIST = "list";
    private List<ARRenderAction> canvas;

    public UpdateHudCanvasMessage(List<ARRenderAction> canvas) {
        this.canvas = canvas;
    }

    public static UpdateHudCanvasMessage decode(FriendlyByteBuf buf) {
        ByteBufInputStream streamin = new ByteBufInputStream(buf);
        CompoundTag nbt;
        List<ARRenderAction> canvas = new ArrayList<>();
        try {
            nbt = NbtIo.read(streamin, NbtAccounter.UNLIMITED);
            ListTag list = nbt.getList(LIST, Tag.TAG_COMPOUND);
            list.forEach(x -> canvas.add(ARRenderAction.deserialize((CompoundTag) x)));
        } catch (IOException e) {
            AdvancedPeripherals.LOGGER.error("Failed to decode UpdateHudCanvasMessage: {}", e.getMessage());
            e.printStackTrace();
        }
        return new UpdateHudCanvasMessage(canvas);
    }

    public List<ARRenderAction> getCanvas() {
        return canvas;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> HudOverlayHandler.updateCanvas(getCanvas()));
        context.setPacketHandled(true);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        getCanvas().forEach(x -> list.add(x.serializeNBT()));
        nbt.put(LIST, list);
        try {
            NbtIo.write(nbt, stream);
        } catch (IOException e) {
            AdvancedPeripherals.LOGGER.error("Failed to encode UpdateHudCanvasMessage: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
