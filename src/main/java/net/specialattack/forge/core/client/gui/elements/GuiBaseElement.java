package net.specialattack.forge.core.client.gui.elements;

import net.minecraft.util.Vec3;
import net.specialattack.forge.core.client.gui.GuiBase;
import net.specialattack.forge.core.client.gui.Positioning;
import net.specialattack.forge.core.client.gui.style.IBackground;
import net.specialattack.forge.core.client.gui.style.IBorder;

public abstract class GuiBaseElement extends GuiBase implements IMovableGuiElement, IResizableGuiElement, IRotatableGuiElement, IScalableGuiElement, IStyleableElement {

    private int width, height;
    private int posX, posY;
    private float zLevel;
    private Positioning positioningX, positioningY;
    private Vec3 rotation, scale;
    private IBackground background;
    private IBorder border;

    public GuiBaseElement(int posX, int posY, int width, int height, IGuiElement parent, float zLevel, Positioning positioningX, Positioning positioningY) {
        super(posX, posY, width, height, parent, zLevel, positioningX, positioningY);
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
        this.zLevel = zLevel;
        this.positioningX = positioningX == null ? Positioning.MIN_OFFSET : positioningX;
        this.positioningY = positioningY == null ? Positioning.MIN_OFFSET : positioningY;
        this.rotation = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
        this.scale = Vec3.createVectorHelper(1.0D, 1.0D, 1.0D);
    }

    public GuiBaseElement(int posX, int posY, int width, int height, IGuiElement parent, float zLevel) {
        this(posX, posY, width, height, parent, zLevel, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBaseElement(int posX, int posY, int width, int height, IGuiElement parent) {
        this(posX, posY, width, height, parent, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBaseElement(int posX, int posY, int width, int height) {
        this(posX, posY, width, height, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBaseElement() {
        this(0, 0, 0, 0, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getPosX() {
        return this.posX;
    }

    @Override
    public int getPosY() {
        return this.posY;
    }

    @Override
    public float getZLevel() {
        return this.zLevel;
    }

    @Override
    public void move(int posX, int posY, float zLevel) {
        this.posX = posX;
        this.posY = posY;
        this.zLevel = zLevel;
    }

    @Override
    public void setPositionMethod(Positioning methodX, Positioning methodY) {
        this.positioningX = methodX == null ? Positioning.MIN_OFFSET : methodX;
        this.positioningY = methodY == null ? Positioning.MIN_OFFSET : methodY;
    }

    @Override
    public void setRotation(double rotX, double rotY, double rotZ) {
        this.rotation = Vec3.createVectorHelper(rotX, rotY, rotZ);
    }

    @Override
    public void setRotation(Vec3 rotation) {
        this.rotation = rotation == null ? Vec3.createVectorHelper(0.0D, 0.0D, 0.0D) : rotation;
    }

    @Override
    public Vec3 getRotation() {
        return this.rotation;
    }

    @Override
    public void setScale(double scaleX, double scaleY, double scaleZ) {
        this.scale = Vec3.createVectorHelper(scaleX, scaleY, scaleZ);
    }

    @Override
    public void setScale(Vec3 scale) {
        this.scale = scale == null ? Vec3.createVectorHelper(0.0D, 0.0D, 0.0D) : scale;
    }

    @Override
    public Vec3 getScale() {
        return this.scale;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
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
        if (this.border != null) {
            this.border.draw(0, 0, this.getWidth(), this.getHeight(), this.getZLevel());
        }
        if (this.background != null) {
            this.background.draw(0, 0, this.getWidth(), this.getHeight(), this.getZLevel());
        }
    }

}
