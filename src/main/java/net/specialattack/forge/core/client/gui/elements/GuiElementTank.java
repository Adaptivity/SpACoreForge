package net.specialattack.forge.core.client.gui.elements;

import net.minecraftforge.fluids.FluidTank;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.Positioning;

public class GuiElementTank extends GuiBaseElement {

    private FluidTank tank;

    public GuiElementTank(int posX, int posY, int width, int height, FluidTank tank, IGuiElement parent, float zLevel, Positioning positioningX, Positioning positioningY) {
        super(posX, posY, width, height, parent, zLevel, positioningX, positioningY);
        this.tank = tank;
    }

    public GuiElementTank(int posX, int posY, int width, int height, IGuiElement parent, float zLevel, Positioning positioningX, Positioning positioningY) {
        this(posX, posY, width, height, null, parent, zLevel, positioningX, positioningY);
    }

    public GuiElementTank(int posX, int posY, int width, int height, FluidTank tank, IGuiElement parent, float zLevel) {
        this(posX, posY, width, height, tank, parent, zLevel, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiElementTank(int posX, int posY, int width, int height, IGuiElement parent, float zLevel) {
        this(posX, posY, width, height, null, parent, zLevel, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiElementTank(int posX, int posY, int width, int height, FluidTank tank, IGuiElement parent) {
        this(posX, posY, width, height, tank, parent, 0.0F, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiElementTank(int posX, int posY, int width, int height, IGuiElement parent) {
        this(posX, posY, width, height, null, parent, 0.0F, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiElementTank(int posX, int posY, int width, int height, FluidTank tank) {
        this(posX, posY, width, height, tank, null, 0.0F, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiElementTank(int posX, int posY, int width, int height) {
        this(posX, posY, width, height, null, null, 0.0F, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public FluidTank getTank() {
        return this.tank;
    }

    public void setTank(FluidTank tank) {
        this.tank = tank;
    }

    @Override
    public void doDraw(float partialTicks) {
        super.doDraw(partialTicks);
        FluidTank tank = this.getTank();
        if (tank != null) {
            float filled = (float) tank.getFluidAmount() / (float) tank.getCapacity();
            if (filled > 1.0F) {
                filled = 1.0F;
            }
            int height = this.getHeight();
            int lheight = (int) (height * filled);
            if (tank.getFluid() != null) {
                GuiHelper.drawFluid(tank.getFluid().getFluid(), 0, height - lheight, this.getWidth(), lheight, this.getZLevel());
            }
        }
    }

    @Override
    public void setTextColor(Color color) {
    }

    @Override
    public void setDisabledColor(Color color) {
    }

}
