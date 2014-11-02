package net.specialattack.forge.core.client.gui.screens;

import java.util.ArrayList;
import java.util.List;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiBase;
import net.specialattack.forge.core.client.gui.Positioning;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;
import net.specialattack.forge.core.client.gui.elements.IStyleableElement;
import net.specialattack.forge.core.client.gui.elements.ITickListener;
import net.specialattack.forge.core.client.gui.style.IBackground;
import net.specialattack.forge.core.client.gui.style.IBorder;

public class GuiAdvScreen extends GuiBase implements IStyleableElement {

    private IBackground background;
    private IBorder border;

    public GuiAdvScreen(int posX, int posY, int width, int height, IGuiElement parent, float zLevel, Positioning positioningX, Positioning positioningY) {
        super(posX, posY, width, height, parent, zLevel, positioningX, positioningY);
    }

    public GuiAdvScreen(int posX, int posY, int width, int height, IGuiElement parent, float zLevel) {
        this(posX, posY, width, height, parent, zLevel, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiAdvScreen(int posX, int posY, int width, int height, IGuiElement parent) {
        this(posX, posY, width, height, parent, 0.0F, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiAdvScreen(int posX, int posY, int width, int height) {
        this(posX, posY, width, height, null, 0.0F, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiAdvScreen() {
        this(0, 0, 0, 0, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    @Override
    public void setBackground(IBackground background) {
        this.background = background;
    }

    @Override
    public boolean hasBackground() {
        return this.background != null;
    }

    @Override
    public void setBorder(IBorder border) {
        this.border = border;
    }

    @Override
    public boolean hasBorder() {
        return this.border != null;
    }

    @Override
    public void doDraw(float partialTicks) {
        if (this.background != null) {
            this.background.draw(0, 0, this.getWidth(), this.getHeight(), this.getZLevel());
        }
        if (this.border != null) {
            this.border.draw(0, 0, this.getWidth(), this.getHeight(), this.getZLevel());
        }
    }

    @Override
    public void setTextColor(Color color) {
    }

    @Override
    public void setDisabledColor(Color color) {
    }

}
