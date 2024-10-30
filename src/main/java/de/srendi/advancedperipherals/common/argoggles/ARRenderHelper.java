package de.srendi.advancedperipherals.common.argoggles;

import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ARRenderHelper {

    public static void drawRightboundString(GuiGraphics graphics, Font fontRenderer, String text, int x, int y, int color) {
        graphics.drawString(fontRenderer, text, x - fontRenderer.width(text), y, color);
    }

    public static int fixAlpha(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    public static void hLine(GuiGraphics graphics, int minX, int maxX, int y, int color) {
        color = ARRenderHelper.fixAlpha(color);
        graphics.hLine(minX, maxX, y, color);
    }

    public static void vLine(GuiGraphics graphics, int x, int minY, int maxY, int color) {
        color = ARRenderHelper.fixAlpha(color);
        graphics.vLine(x, minY, maxY, color);
    }

    protected static void fillGradient(GuiGraphics graphics, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        colorFrom = ARRenderHelper.fixAlpha(colorFrom);
        colorTo = ARRenderHelper.fixAlpha(colorTo);
        graphics.fillGradient(x1, y1, x2, y2, colorFrom, colorTo);
    }

    public static void drawItemIcon(GuiGraphics graphics, String item, int x, int y) {
        ItemStack stack = new ItemStack(ItemUtil.getRegistryEntry(item, ForgeRegistries.ITEMS));
        graphics.renderItem(stack, x, y);
    }
}
