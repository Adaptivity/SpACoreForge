package net.specialattack.forge.core.client.gui.style;

import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;

public class SolidBorder implements IBorder {

    private Color color;
    private int width;

    public SolidBorder(Color color, int width) {
        this.color = color;
        this.width = width;
    }

    @Override
    public void draw(int startX, int startY, int endX, int endY, float zLevel) {
        GuiHelper.drawGradientRect(startX - width, startY, startX, endY, this.color.colorHex, this.color.colorHex, zLevel, true); // Left border
        GuiHelper.drawGradientRect(endX, startY, endX + width, endY, this.color.colorHex, this.color.colorHex, zLevel, true); // Right border

        GuiHelper.drawGradientRect(startX - width, startY - width, endX + width, startY, this.color.colorHex, this.color.colorHex, zLevel, true); // Top border
        GuiHelper.drawGradientRect(startX - width, endY, endX + width, endY + width, this.color.colorHex, this.color.colorHex, zLevel, true); // Bottom border
    }

}
