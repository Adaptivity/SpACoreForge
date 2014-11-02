package net.specialattack.forge.core.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.animation.IAnimation;
import net.specialattack.forge.core.client.gui.elements.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiScreen implements IGuiElement {

    private int width, height;
    private int posX, posY;
    private float zLevel;
    private Positioning positioningX, positioningY;
    private IGuiElement parent;
    private List<IGuiElement> elements;
    private List<ITickListener> tickListeners;
    private List<IAnimation> animations;
    private IFocusableElement focusedElement;
    private boolean enabled, visible;

    public GuiBase(int posX, int posY, int width, int height, IGuiElement parent, float zLevel, Positioning positioningX, Positioning positioningY) {
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
        this.zLevel = zLevel;
        this.positioningX = positioningX == null ? Positioning.MIN_OFFSET : positioningX;
        this.positioningY = positioningY == null ? Positioning.MIN_OFFSET : positioningY;
        this.enabled = true;
        this.visible = true;
        this.parent = parent;
        this.tickListeners = new ArrayList<ITickListener>();
        this.animations = new ArrayList<IAnimation>();
        this.elements = new ArrayList<IGuiElement>();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public GuiBase(int posX, int posY, int width, int height, IGuiElement parent, int posZ) {
        this(posX, posY, width, height, parent, posZ, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBase(int posX, int posY, int width, int height, IGuiElement parent) {
        this(posX, posY, width, height, parent, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBase(int posX, int posY, int width, int height) {
        this(posX, posY, width, height, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBase() {
        this(0, 0, 0, 0, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
        super.setWorldAndResolution(minecraft, width, height);
        if (this instanceof IResizableGuiElement) {
            ((IResizableGuiElement) this).setSize(width, height);
        } else {
            this.width = width;
            this.height = height;
        }
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
        if (this.getParent() != null) {
            return this.positioningX.position(this.posX, this.getWidth(), this.getParent().getWidth());
        } else {
            return this.posX;
        }
    }

    @Override
    public int getPosY() {
        if (this.getParent() != null) {
            return this.positioningY.position(this.posY, this.getHeight(), this.getParent().getHeight());
        } else {
            return this.posY;
        }
    }

    @Override
    public float getZLevel() {
        if (this.getParent() != null) {
            return this.zLevel + this.getParent().getZLevel();
        } else {
            return this.zLevel;
        }
    }

    @Override
    public IGuiElement getParent() {
        return this.parent;
    }

    @Override
    public List<IGuiElement> getChildElements() {
        return this.elements;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void addChild(IGuiElement element) {
        if (!this.elements.contains(element)) {
            this.elements.add(element);
        }
    }

    @Override
    public final void draw(float partialTicks) {
        if (!this.visible) {
            return;
        }
        GL11.glTranslatef(this.getPosX(), this.getPosY(), this.getZLevel());
        GL11.glTranslatef((float) this.getWidth() / 2.0F, (float) this.getHeight() / 2.0F, 0.0F);
        for (IAnimation animation : this.animations) {
            animation.prepareDraw(this, partialTicks);
        }
        if (this instanceof IRotatableGuiElement) {
            Vec3 rotation = ((IRotatableGuiElement) this).getRotation();
            if (rotation != null) {
                double largestAngle = Math.max(rotation.xCoord, Math.max(rotation.yCoord, rotation.zCoord));
                GL11.glRotated(largestAngle, rotation.xCoord / largestAngle, rotation.yCoord / largestAngle, rotation.zCoord / largestAngle);
            }
        }
        if (this instanceof IScalableGuiElement) {
            Vec3 scale = ((IScalableGuiElement) this).getScale();
            if (scale != null) {
                GL11.glScaled(scale.xCoord, scale.yCoord, scale.zCoord);
            }
        }
        GL11.glTranslatef((float) -this.getWidth() / 2.0F, (float) -this.getHeight() / 2.0F, 0.0F);
        boolean debug = false;
        this.doDraw(partialTicks);
        if (debug && this.parent == null) {
            int color = (this.hashCode() & 0xFFFFFF) | 0x88000000;
            GL11.glDepthMask(false);
            GuiHelper.drawGradientRect(0, 0, this.getWidth(), this.getHeight(), color, color, this.getZLevel(), true);
            GL11.glDepthMask(true);
        }
        for (IGuiElement element : this.elements) {
            GL11.glPushMatrix();
            element.draw(partialTicks);
            if (debug) {
                int color = (element.hashCode() & 0xFFFFFF) | 0x88000000;
                GL11.glDepthMask(false);
                GuiHelper.drawGradientRect(0, 0, element.getWidth(), element.getHeight(), color, color, element.getZLevel(), true);
                GL11.glDepthMask(true);
            }
            GL11.glPopMatrix();
        }
    }

    public abstract void doDraw(float partialTicks);

    @Override
    public boolean onClickMe(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        boolean clicked = false;
        for (IGuiElement element : this.elements) {
            if (!element.isVisible()) {
                continue;
            }
            int posX = element.getPosX();
            int posY = element.getPosY();
            if (GuiBase.isInsideBounds(mouseX, mouseY, posX, posY, posX + element.getWidth(), posY + element.getHeight())) {
                element.onClick(mouseX - posX, mouseY - posY, button);
                clicked = true;
            }
        }
        return !clicked && this.onClickMe(mouseX, mouseY, button);
    }

    @Override
    public boolean onKey(char character, int keycode) {
        return this.visible && this.focusedElement != null && this.focusedElement.onKey(character, keycode);
    }

    @Override
    public void propagateFocusChangeDown(IFocusableElement element) {
        this.focusedElement = element;
        for (IGuiElement currentElement : this.elements) {
            element.propagateFocusChangeDown(element);
        }
    }

    @Override
    public void propagateFocusChangeUp(IFocusableElement element) {
        if (this.parent != null) {
            this.parent.propagateFocusChangeUp(element);
        } else {
            this.propagateFocusChangeDown(element);
        }
    }

    @Override
    public void updateTick() {
        for (IGuiElement element : this.elements) {
            element.updateTick();
        }
        for (ITickListener listener : this.tickListeners) {
            listener.onTick();
        }
        for (IAnimation animation : this.animations) {
            animation.progressTicks(1);
        }
    }

    @Override
    public void addTickListener(ITickListener listener) {
        this.tickListeners.add(listener);
    }

    @Override
    public void addAnimation(IAnimation animation) {
        this.animations.add(animation);
    }

    public static void playButtonClick() {
        MC.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public static boolean isInsideBounds(int posX, int posY, int minX, int minY, int maxX, int maxY) {
        return posX >= minX && posX <= maxX && posY >= minY && posY <= maxY;
    }

    private Framebuffer framebuffer;

    private void deleteFrameBuffer() {
        if (this.framebuffer != null) {
            //GL11.glDeleteTextures(this.textureId);
            //this.textureId = -1;
            this.framebuffer.deleteFramebuffer();
        }
    }

    public void drawRoot(int mouseX, int mouseY, float partialTicks) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        int prevBuffer = 0;
        int displayWidth = MC.getMinecraft().displayWidth;
        int displayHeight = MC.getMinecraft().displayHeight;
        if (OpenGlHelper.isFramebufferEnabled()) {
            prevBuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            if (this.framebuffer == null) {
                //this.textureId = GL11.glGenTextures();
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
                //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
                //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                //GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.getWidth(), this.getHeight(), 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

                this.framebuffer = new Framebuffer(this.getWidth(), this.getHeight(), true);
                this.framebuffer.setFramebufferColor(1.0F, 0.0F, 0.0F, 1.0F);

                if (OpenGlHelper.isFramebufferEnabled()) {
                    OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, prevBuffer);
                }
            }

            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            this.framebuffer.framebufferClear();
            this.framebuffer.bindFramebuffer(false);
            //GL11.glMatrixMode(GL11.GL_PROJECTION);
            //GL11.glLoadIdentity();
            //GL11.glOrtho(0.0D, this.getWidth(), this.getHeight(), 0.0D, 100.0D, 300.0D);
            //GL11.glMatrixMode(GL11.GL_MODELVIEW);
            //GL11.glLoadIdentity();
            //GL11.glViewport(0, 0, displayWidth, displayHeight);
        }
        GL11.glPushMatrix();
        this.draw(partialTicks);
        GL11.glPopMatrix();
        if (OpenGlHelper.isFramebufferEnabled()) {
            //this.framebuffer.unbindFramebuffer();
            if (OpenGlHelper.isFramebufferEnabled()) {
                OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, prevBuffer);
            }
            //GL11.glViewport(0, 0, displayWidth, displayHeight);
            //GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            //GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);

            this.framebuffer.bindFramebufferTexture();
            int posX = this.getPosX();
            int posY = this.getPosY();
            int width = this.getWidth();
            int height = this.getHeight();
            double u = (double) this.framebuffer.framebufferWidth / (double) this.framebuffer.framebufferTextureWidth;
            double v = (double) this.framebuffer.framebufferHeight / (double) this.framebuffer.framebufferTextureHeight;
            Tessellator tess = Tessellator.instance;
            tess.startDrawingQuads();
            tess.addVertexWithUV(posX, posY, this.getZLevel(), 0.0D, 0.0D);
            tess.addVertexWithUV(posX + width, posY, this.getZLevel(), u, 0.0D);
            tess.addVertexWithUV(posX + width, posY + height, this.getZLevel(), u, v);
            //tess.addVertexWithUV(0, 0, this.getZLevel(), 0.0D, 0.0D);
            //tess.addVertexWithUV(width, 0, this.getZLevel(), u, 0.0D);
            //tess.addVertexWithUV(width, height, this.getZLevel(), u, v);
            //tess.addVertexWithUV(0, height, this.getZLevel(), 0.0D, v);
            tess.draw();
            //this.framebuffer.unbindFramebufferTexture();
            if (OpenGlHelper.isFramebufferEnabled()) {
                OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, prevBuffer);
            }
            GL11.glPopMatrix();
            GL11.glViewport(0, 0, displayWidth, displayHeight);
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.deleteFrameBuffer();
    }

    // GuiScreen overrides

    @Override
    public void initGui() {
        super.initGui();
        this.deleteFrameBuffer();
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawRoot(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.onClick(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char character, int keycode) {
        if (!this.onKey(character, keycode)) {
            super.keyTyped(character, keycode);
        }
    }

    @Override
    protected void drawGradientRect(int startX, int startY, int endX, int endY, int startColor, int endColor) {
        GuiHelper.drawGradientRect(startX, startY, endX, endY, startColor, endColor, this.getZLevel(), true);
    }
}
