package de.srendi.advancedperipherals.common.argoggles;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import owmii.powah.lib.client.util.GUI;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class ARRenderAction implements INBTSerializable<CompoundTag> {

    private static final String TYPE = "type";
    private static final String STRING_ARG = "string_arg";
    private static final String INT_ARGS = "int_args";
    private static final String VIRTUAL_SCREEN_SIZE = "virtualScreenSize";

    private String id;
    private RenderActionType type;
    private String stringArg = "";
    private int[] intArgs = new int[0];
    private Optional<int[]> virtualScreenSize = Optional.empty();

    public ARRenderAction() {

    }

    public ARRenderAction(String id, RenderActionType type, int... intArgs) {
        this();
        this.id = id;
        this.type = type;
        this.intArgs = intArgs;
    }

    public ARRenderAction(RenderActionType type, int... intArgs) {
        this(null, type, intArgs);
    }

    public ARRenderAction(RenderActionType type, String stringArg, int... intArgs) {
        this(null, type, stringArg, intArgs);
    }

    public ARRenderAction(String id, RenderActionType type, String stringArg, int... intArgs) {
        this(id, type, intArgs);
        this.stringArg = stringArg;
    }

    public static ARRenderAction deserialize(CompoundTag nbt) {
        ARRenderAction action = new ARRenderAction();
        action.deserializeNBT(nbt);
        return action;
    }

    public String getId() {
        return id;
    }

    public void draw(GuiGraphics graphics, int w, int h) {
        if (!type.ensureArgs(intArgs)) return;
        int[] i = intArgs;
        switch (type) {
            case DRAW_CENTERED_STRING:
                graphics.drawCenteredString(Minecraft.getInstance().font, stringArg, relativeX(i[0], w), relativeY(i[1], h), i[2]);
                break;
            case DRAW_STRING:
                graphics.drawString(Minecraft.getInstance().font, stringArg, relativeX(i[0], w), relativeY(i[1], h), i[2]);
                break;
            case DRAW_RIGHTBOUND_STRING:
                ARRenderHelper.drawRightboundString(graphics, Minecraft.getInstance().font, stringArg, relativeX(i[0], w), relativeY(i[1], h), i[2]);
                break;
            case FILL:
                i[4] = ARRenderHelper.fixAlpha(i[4]);
                graphics.fill(relativeX(i[0], w), relativeY(i[1], h), relativeX(i[2], w), relativeY(i[3], h), i[4]);
                break;
            case HORIZONTAL_LINE:
                ARRenderHelper.hLine(graphics, relativeX(i[0], w), relativeX(i[1], w), relativeY(i[2], h), i[3]);
                break;
            case VERTICAL_LINE:
                ARRenderHelper.vLine(graphics, relativeX(i[0], w), relativeY(i[1], h), relativeY(i[2], h), i[3]);
                break;
            case FILL_GRADIENT:
                ARRenderHelper.fillGradient(graphics, relativeX(i[0], w), relativeY(i[1], h), relativeX(i[2], w), relativeY(i[3], h), i[4], i[5]);
                break;
            case DRAW_ITEM_ICON:
                ARRenderHelper.drawItemIcon(graphics, stringArg, relativeX(i[0], w), relativeY(i[1], h));
                break;
            default:
                AdvancedPeripherals.LOGGER.warn("Failed to execute AR render action of unimplemented type " + type);
                break;
        }
    }

    private int relativeX(int x, int windowWidth) {
        if (virtualScreenSize.isPresent()) {
            x = x >= 0 ? x : virtualScreenSize.get()[0] + x;
            return (int) Math.round((double) x / virtualScreenSize.get()[0] * windowWidth);
        } else return x >= 0 ? x : windowWidth + x;
    }

    private int relativeY(int y, int windowHeight) {
        if (virtualScreenSize.isPresent()) {
            y = y >= 0 ? y : virtualScreenSize.get()[1] + y;
            return (int) Math.round((double) y / virtualScreenSize.get()[1] * windowHeight);
        } else return y >= 0 ? y : windowHeight;
    }

    private float relativeAverage(int i, int w, int h) {
        if (virtualScreenSize.isPresent()) {
            float xfactor = (float) w / virtualScreenSize.get()[0];
            float yfactor = (float) h / virtualScreenSize.get()[1];
            return i * (xfactor + yfactor) / 2;
        } else return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ARRenderAction renderAction) {
            return type.equals(renderAction.type) && stringArg.equals(renderAction.stringArg) && Arrays.equals(intArgs, renderAction.intArgs);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, type, stringArg, virtualScreenSize);
        result = 31 * result + Arrays.hashCode(intArgs);
        return result;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int[] virtualScreenSizeFromNbt = nbt.getIntArray(VIRTUAL_SCREEN_SIZE);

        type = RenderActionType.valueOf(nbt.getString(TYPE));
        stringArg = nbt.getString(STRING_ARG);
        intArgs = nbt.getIntArray(INT_ARGS);
        virtualScreenSize = virtualScreenSizeFromNbt.length == 0 ? Optional.empty() : Optional.of(virtualScreenSizeFromNbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(TYPE, type.toString());
        nbt.putString(STRING_ARG, stringArg);
        nbt.putIntArray(INT_ARGS, intArgs);
        nbt.putIntArray(VIRTUAL_SCREEN_SIZE, virtualScreenSize.orElse(new int[]{}));
        return nbt;
    }

    public int[] getVirtualScreenSize() {
        if (virtualScreenSize.isPresent()) return virtualScreenSize.get();
        else return null;
    }

    public void setRelativeMode(int virtualScreenWidth, int virtualScreenHeight) {
        virtualScreenSize = Optional.of(new int[]{virtualScreenWidth, virtualScreenHeight});
    }

    public void disableRelativeMode() {
        virtualScreenSize = Optional.empty();
    }

    public ARRenderAction copyWithVirtualScreenSize(Optional<int[]> virtualScreenSize2) {
        ARRenderAction action = new ARRenderAction(type, stringArg, intArgs);
        if (virtualScreenSize2.isPresent())
            action.setRelativeMode(virtualScreenSize2.get()[0], virtualScreenSize2.get()[1]);
        return action;
    }
}
