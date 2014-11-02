package net.specialattack.forge.core.client.gui.style;

import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;

public class SolidBackground implements IBackground {

    private Color color;

    public SolidBackground(Color color) {
        this.color = color;
    }

    @Override
    public void draw(int startX, int startY, int endX, int endY, float zLevel) {
        GuiHelper.drawGradientRect(startX, startY, endX, endY, this.color.colorHex, this.color.colorHex, zLevel, true);
    }

}
