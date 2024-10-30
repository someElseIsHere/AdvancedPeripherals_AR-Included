package de.srendi.advancedperipherals.network.argoggles;

import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.network.base.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClearHudCanvasMessage implements IPacket {
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(HudOverlayHandler::clearCanvas);
        context.setPacketHandled(true);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {}

    public static ClearHudCanvasMessage decode(FriendlyByteBuf buffer) {
        return new ClearHudCanvasMessage();
    }
}
