package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class HudOverlayHandler {
    private static HudOverlayHandler instance;
    private final List<ARRenderAction> canvas = new ArrayList<>();

    public static void init() {
        instance = new HudOverlayHandler();
        MinecraftForge.EVENT_BUS.register(instance);
    }

    public static HudOverlayHandler getInstance() {
        return instance;
    }

    public static void updateCanvas(List<ARRenderAction> actions) {
        if (instance == null) return;
        instance.canvas.clear();
        instance.canvas.addAll(actions);
    }

    public static void clearCanvas() {
        if (instance == null) return;
        instance.canvas.clear();
    }

    //TODO: Use the forge Overlay System instead
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(RenderGuiOverlayEvent.Post event) {
        if (event.getWindow() == null) return;
        for (ARRenderAction action : canvas) {
            action.draw(event.getGuiGraphics(), event.getWindow().getScreenWidth(), event.getWindow().getScreenHeight());
        }
        //TODO RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
    }

    @SubscribeEvent
    public void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        // Clear the canvas when the player joins a world to prevent old renders from other worlds/servers
        clearCanvas();
    }
}
