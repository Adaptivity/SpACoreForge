package net.specialattack.forge.core.client.gui;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Timer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.specialattack.forge.core.Assets;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * A helper class used for rendering on GUIs
 *
 * @author heldplayer
 */
@SideOnly(Side.CLIENT)
public final class GuiHelper {

    private static final ArrayList<String> reusableArrayList = new ArrayList<String>();
    private static Timer minecraftTimer;

    private GuiHelper() {
    }

    /**
     * Draws a fluid tank
     *
     * @param fluid
     *         The fluid to render
     * @param left
     *         The x position to start at
     * @param top
     *         The y position to start at
     * @param width
     *         The width of the tank to render
     * @param height
     *         The height of the tank to render
     */
    public static void drawFluid(Fluid fluid, int left, int top, int width, int height, float zLevel) {
        if (fluid.getSpriteNumber() == 0) {
            MC.getRenderEngine().bindTexture(TextureMap.locationBlocksTexture);
        } else {
            MC.getRenderEngine().bindTexture(TextureMap.locationItemsTexture);
        }

        IIcon icon = RenderHelper.getIconSafe(fluid.getIcon(), fluid.getSpriteNumber() == 0);
        int color = fluid.getColor();
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GL11.glColor4f(red, green, blue, 1.0F);

        for (int x = 0; x < width; x += 16) {
            for (int y = 0; y < height; y += 16) {
                int drawWidth = width - x > 16 ? 16 : width - x;
                int drawHeight = height - y > 16 ? 16 : height - y;

                float minU = icon.getMinU();
                float minV = icon.getInterpolatedV(16.0F - drawHeight);
                float maxU = icon.getMaxU();
                float maxV = icon.getMaxV();

                GuiHelper.drawTexturedModalRect(left + x, top + height - y - drawHeight, drawWidth, drawHeight, zLevel, minU, minV, maxU, maxV);
            }
        }
    }

    /**
     * Draws a textured rectangle
     *
     * @param startX
     *         The starting x position
     * @param startY
     *         The starting y position
     * @param width
     *         The width of the rectangle
     * @param height
     *         The height of the rectangle
     * @param zLevel
     *         The z-level for rendering
     * @param startU
     *         The starting texture u location
     * @param startV
     *         The starting texture v location
     * @param endU
     *         The ending texture u location
     * @param endV
     *         The ending texture v location
     */
    public static void drawTexturedModalRect(int startX, int startY, int width, int height, float zLevel, float startU, float startV, float endU, float endV) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(startX, startY + height, zLevel, startU, endV);
        tes.addVertexWithUV(startX + width, startY + height, zLevel, endU, endV);
        tes.addVertexWithUV(startX + width, startY, zLevel, endU, startV);
        tes.addVertexWithUV(startX, startY, zLevel, startU, startV);
        tes.draw();
    }

    /**
     * Scales an int for drawing in a GUI
     *
     * @param scale
     *         The resulting max value of the int
     * @param amount
     *         The amount
     * @param total
     *         The max amount that can be entered
     *
     * @return Scaled int
     */
    public static int getScaled(int scale, int amount, int total) {
        if (amount > total) {
            amount = total;
        }

        return amount * scale / total;
    }

    /**
     * Draws a tooltip
     *
     * @param strings
     *         The strings to draw
     * @param fontRenderer
     *         The font renderer instance to use
     * @param mouseX
     *         The x position of the mouse
     * @param mouseY
     *         The y position of the mouse
     * @param guiTop
     *         The top position of the GUI
     * @param height
     *         The height of the GUI
     */
    public static void drawTooltip(List<String> strings, FontRenderer fontRenderer, int mouseX, int mouseY, int guiTop, int height) {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        if (!strings.isEmpty()) {
            int startY = 0;
            int xPos;
            int yPos;

            for (xPos = 0; xPos < strings.size(); ++xPos) {
                yPos = fontRenderer.getStringWidth(strings.get(xPos));

                if (yPos > startY) {
                    startY = yPos;
                }
            }

            xPos = mouseX + 12;
            yPos = mouseY - 12;
            int width = 8;

            if (strings.size() > 1) {
                width += 2 + (strings.size() - 1) * 10;
            }

            if (guiTop + yPos + width + 6 > height) {
                yPos = height - width - guiTop - 6;
            }

            int color1 = 0xF0100010;
            GuiHelper.drawGradientRect(xPos - 3, yPos - 4, xPos + startY + 3, yPos - 3, color1, color1, 300.0F);
            GuiHelper.drawGradientRect(xPos - 3, yPos + width + 3, xPos + startY + 3, yPos + width + 4, color1, color1, 300.0F);
            GuiHelper.drawGradientRect(xPos - 3, yPos - 3, xPos + startY + 3, yPos + width + 3, color1, color1, 300.0F);
            GuiHelper.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + width + 3, color1, color1, 300.0F);
            GuiHelper.drawGradientRect(xPos + startY + 3, yPos - 3, xPos + startY + 4, yPos + width + 3, color1, color1, 300.0F);
            int color2 = 0x505000FF;
            int color3 = (color2 & 0xFEFEFE) >> 1 | color2 & 0x1000000;
            GuiHelper.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + width + 3 - 1, color2, color3, 300.0F);
            GuiHelper.drawGradientRect(xPos + startY + 2, yPos - 3 + 1, xPos + startY + 3, yPos + width + 3 - 1, color2, color3, 300.0F);
            GuiHelper.drawGradientRect(xPos - 3, yPos - 3, xPos + startY + 3, yPos - 3 + 1, color2, color2, 300.0F);
            GuiHelper.drawGradientRect(xPos - 3, yPos + width + 2, xPos + startY + 3, yPos + width + 3, color3, color3, 300.0F);

            for (int i = 0; i < strings.size(); ++i) {
                String currentLine = strings.get(i);

                if (i > 0) {
                    currentLine = "\u00a77" + currentLine;
                }

                fontRenderer.drawStringWithShadow(currentLine, xPos, yPos, -1);

                if (i == 0) {
                    yPos += 2;
                }

                yPos += 10;
            }
        }
    }

    /**
     * Draws a gradient rectangle
     *
     * @param startX
     *         The starting x position
     * @param startY
     *         The starting y position
     * @param endX
     *         The ending x position
     * @param endY
     *         The ending y position
     * @param startColor
     *         The top colour
     * @param endColor
     *         The bottom colour
     * @param zLevel
     *         The z-level for rendering
     */
    public static void drawGradientRect(int startX, int startY, int endX, int endY, int startColor, int endColor, float zLevel) {
        drawGradientRect(startX, startY, endX, endY, startColor, endColor, zLevel, false);
    }

    public static void drawGradientRect(int startX, int startY, int endX, int endY, int startColor, int endColor, float zLevel, boolean antiAlias) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        if (!antiAlias) {
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        }
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glColor4f(startRed, startGreen, startBlue, startAlpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(endX, startY, zLevel);
        GL11.glVertex3f(startX, startY, zLevel);
        GL11.glColor4f(endRed, endGreen, endBlue, endAlpha);
        GL11.glVertex3f(startX, endY, zLevel);
        GL11.glVertex3f(endX, endY, zLevel);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static ArrayList<String> getFluidString(IFluidTank tank) {
        GuiHelper.reusableArrayList.clear();

        if (tank == null) {
            GuiHelper.reusableArrayList.add("This tank is broken");
            return GuiHelper.reusableArrayList;
        }

        FluidStack stack = tank.getFluid();

        if (stack != null && stack.amount > 0) {
            GuiHelper.reusableArrayList.add(stack.getFluid().getLocalizedName(stack));
            GuiHelper.reusableArrayList.add(StatCollector.translateToLocalFormatted(Assets.DOMAIN + "gui.container.fluid.filled", stack.amount, tank.getCapacity()).trim());
        } else {
            GuiHelper.reusableArrayList.add(StatCollector.translateToLocal(Assets.DOMAIN + "gui.container.fluid.empty"));
            GuiHelper.reusableArrayList.add(StatCollector.translateToLocalFormatted(Assets.DOMAIN + "gui.container.fluid.filled", 0, tank.getCapacity()).trim());
        }

        return GuiHelper.reusableArrayList;
    }

    public static void drawTexturedModalRect(int startX, int startY, int width, int height, double zLevel, double startU, double startV, double endU, double endV) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(startX, startY + (double) height, zLevel, startU, endV);
        tes.addVertexWithUV(startX + (double) width, startY + (double) height, zLevel, endU, endV);
        tes.addVertexWithUV(startX + (double) width, startY, zLevel, endU, startV);
        tes.addVertexWithUV(startX, startY, zLevel, startU, startV);
        tes.draw();
    }

    public static void drawTexturedModalRect(int startX, int startY, int u, int v, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(startX, startY + height, 0.0D, u * f, (v + height) * f1);
        tessellator.addVertexWithUV(startX + width, startY + height, 0.0D, (u + width) * f, (v + height) * f1);
        tessellator.addVertexWithUV(startX + width, startY, 0.0D, (u + width) * f, v * f1);
        tessellator.addVertexWithUV(startX, startY, 0.0D, u * f, v * f1);
        tessellator.draw();
    }

    public static Timer getMinecraftTimer() {
        if (GuiHelper.minecraftTimer == null) {
            GuiHelper.minecraftTimer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");
        }
        return GuiHelper.minecraftTimer;
    }

}
