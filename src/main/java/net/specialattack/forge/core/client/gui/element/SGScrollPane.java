package net.specialattack.forge.core.client.gui.element;

import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.SGUtils;
import net.specialattack.forge.core.client.gui.SizeContext;
import net.specialattack.forge.core.client.gui.layout.BorderedSGLayoutManager;
import net.specialattack.forge.core.client.gui.layout.Location;
import net.specialattack.forge.core.client.gui.layout.Region;
import net.specialattack.forge.core.client.gui.layout.SGLayoutManager;
import net.specialattack.forge.core.client.gui.style.StyleDefs;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class SGScrollPane extends SGComponent {

    private float scrollLeft, scrollTop;
    private boolean horizontal, vertical;
    private Inner innerPanel;
    private Color scrollbarBackground = StyleDefs.COLOR_SCROLLBAR_BACKGROUND, scrollbarForeground = StyleDefs.COLOR_SCROLLBAR_FOREGROUND;
    private int scrollbarWidth = 8;
    private byte dragging = -1;

    public SGScrollPane() {
        this.innerPanel = new Inner();
        super.setLayoutManager(new BorderedSGLayoutManager());
        super.addChild(this.innerPanel, BorderedSGLayoutManager.Border.CENTER);
        //this.innerPanel.setParent(null);
    }

    public void setCanScroll(boolean horizontal, boolean vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public void setScrollbarSize(int size) {
        this.scrollbarWidth = size;
    }

    @Override
    public void setDimensions(int left, int top, int width, int height) {
        super.setDimensions(left, top, width, height);
        this.innerPanel.setPreferredSize(this.getWidth(SizeContext.INNER), this.getHeight(SizeContext.INNER));
        this.updateLayout();
    }

    @Override
    public int getWidth(SizeContext context) {
        if (this.vertical) {
            return super.getWidth(context) - (context == SizeContext.INNER ? this.scrollbarWidth : 0);
        }
        return super.getWidth(context);
    }

    @Override
    public int getHeight(SizeContext context) {
        if (this.horizontal) {
            return super.getHeight(context) - (context == SizeContext.INNER ? this.scrollbarWidth : 0);
        }
        return super.getHeight(context);
    }

    @Override
    public Pair<SGComponent, Location> cascadeMouse(int mouseX, int mouseY) {
        if (!this.isVisible()) {
            return null;
        }
        int left = this.getLeft(SizeContext.INNER);
        int top = this.getTop(SizeContext.INNER);
        if (this.isErrored() && this.isMouseOver(mouseX, mouseY)) {
            return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
        }
        try {
            if (!this.isMouseOver(mouseX, mouseY)) {
                return null;
            }
            if (this.vertical) {
                if (mouseX > this.getWidth(SizeContext.INNER) && mouseY <= this.getHeight(SizeContext.INNER)) {
                    return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
                }
            }
            if (this.horizontal) {
                if (mouseY > this.getHeight(SizeContext.INNER) && mouseX <= this.getWidth(SizeContext.INNER)) {
                    return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
                }
            }
            List<SGComponent> children = this.getChildren();
            if (children != null) {
                for (SGComponent component : children) {
                    Pair<SGComponent, Location> over = component.cascadeMouse(mouseX - left + (int) this.scrollLeft, mouseY - top + (int) this.scrollTop);
                    if (over != null) {
                        return over;
                    }
                }
            }
            return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
        }
        return null;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(mouseX, mouseY, partialTicks);
        if (this.horizontal || this.vertical) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
            if (this.vertical) {
                int superWidth = super.getWidth(SizeContext.INNER);
                GuiHelper.drawColoredRect(superWidth - this.scrollbarWidth, 0, superWidth, this.getHeight(SizeContext.INNER), this.scrollbarBackground.colorHex, 0.0F);
            }
            if (this.horizontal) {
                int superHeight = super.getHeight(SizeContext.INNER);
                GuiHelper.drawColoredRect(0, superHeight - this.scrollbarWidth, this.getWidth(SizeContext.INNER), superHeight, this.scrollbarBackground.colorHex, 0.0F);
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        super.drawForeground(mouseX, mouseY, partialTicks);
        if (this.horizontal || this.vertical) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
            if (this.vertical) {
                int superWidth = super.getWidth(SizeContext.INNER);
                if (this.innerPanel.getHeight(SizeContext.OUTLINE) > this.getHeight(SizeContext.INNER)) {
                    int thumbHeight = GuiHelper.getScaled(this.getHeight(SizeContext.INNER), this.getHeight(SizeContext.INNER), this.innerPanel.getHeight(SizeContext.OUTLINE));
                    int position = GuiHelper.getScaled(this.getHeight(SizeContext.INNER) - thumbHeight, (int) this.scrollTop, this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER));
                    GuiHelper.drawColoredRect(superWidth - this.scrollbarWidth, position, superWidth, thumbHeight + position, this.scrollbarForeground.colorHex, 0.0F);
                } else {
                    GuiHelper.drawColoredRect(superWidth - this.scrollbarWidth, 0, superWidth, this.getHeight(SizeContext.INNER), this.scrollbarForeground.colorHex, 0.0F);
                }
            }
            if (this.horizontal) {
                int superHeight = super.getHeight(SizeContext.INNER);
                if (this.innerPanel.getWidth(SizeContext.OUTLINE) > this.getWidth(SizeContext.INNER)) {
                    int thumbWidth = GuiHelper.getScaled(this.getWidth(SizeContext.INNER), this.getWidth(SizeContext.INNER), this.innerPanel.getWidth(SizeContext.OUTLINE));
                    int position = GuiHelper.getScaled(this.getWidth(SizeContext.INNER) - thumbWidth, (int) this.scrollLeft, this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER));
                    GuiHelper.drawColoredRect(position, superHeight - this.scrollbarWidth, thumbWidth + position, superHeight, this.scrollbarForeground.colorHex, 0.0F);
                } else {
                    GuiHelper.drawColoredRect(0, superHeight - this.scrollbarWidth, this.getWidth(SizeContext.INNER), superHeight, this.scrollbarForeground.colorHex, 0.0F);
                }
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (this.isErrored()) {
            SGUtils.drawErrorBox(this);
            return;
        }
        if (!this.isVisible()) {
            return;
        }
        int stack = 0;
        try {
            stack++;
            GL11.glPushMatrix();
            stack++;
            GL11.glPushMatrix();
            this.drawBackground(mouseX, mouseY, partialTicks);
            this.drawForeground(mouseX, mouseY, partialTicks);
            GL11.glPopMatrix();
            stack--;
            if (SGComponent.DEBUG) {
                if (this.isMouseOver() || this.hasFocus()) {
                    int color = (this.hashCode() & 0xFFFFFF) | 0x88000000;
                    GL11.glDepthMask(false);
                    int left = this.getLeft(SizeContext.OUTLINE);
                    int top = this.getTop(SizeContext.OUTLINE);
                    int width = this.getWidth(SizeContext.OUTLINE);
                    int height = this.getHeight(SizeContext.OUTLINE);
                    if (this.isMouseOver()) {
                        GuiHelper.drawColoredRect(left, top, left + width, top + height, color, this.getZLevel());
                    }
                    if (this.hasFocus()) {
                        SGUtils.drawBox(left, top, width, height, this.getZLevel(), color);
                    }
                    GL11.glDepthMask(true);
                }
            }

            SGUtils.clipComponent(this.innerPanel);
            int left = this.getLeft(SizeContext.INNER) - (int) this.scrollLeft;
            int top = this.getTop(SizeContext.INNER) - (int) this.scrollTop;
            GL11.glTranslatef(left, top, this.getZLevel());
            List<SGComponent> children = this.getChildren();
            Region region = this.getDimensions().atZero().offset((int) this.scrollLeft, (int) this.scrollTop);
            if (children != null) {
                for (SGComponent component : children) {
                    if (component.getDimensions().intersects(region)) {
                        component.draw(mouseX - left, mouseY - top, partialTicks);
                    }
                }
            }
            GL11.glPopMatrix();
            stack--;
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
            while (stack > 0) {
                GL11.glPopMatrix();
                stack--;
            }
        } finally {
            try {
                SGUtils.endClip();
            } catch (Exception e) {
                e.printStackTrace();
                this.setErrored();
            }
        }
    }

    @Override
    public boolean onScroll(int mouseX, int mouseY, int scroll) {
        if (!this.isVisible() || this.isErrored()) {
            return false;
        }
        try {
            List<SGComponent> children = this.getChildren();
            if (children != null) {
                for (SGComponent component : children) {
                    int left = this.getLeft(SizeContext.INNER);
                    int top = this.getTop(SizeContext.INNER);
                    if (component.isMouseOver(mouseX - left, mouseY - top)) {
                        boolean scrolled = component.onScroll(mouseX - left - (int) this.scrollLeft, mouseY - top - (int) this.scrollTop, scroll);
                        if (scrolled) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
        }
        float amount = scroll;
        if (!GuiScreen.isCtrlKeyDown()) {
            amount /= 10;
        }
        if (this.vertical && this.horizontal) {
            if (GuiScreen.isShiftKeyDown()) {
                this.scrollLeft = MathHelper.clamp_float(this.scrollLeft - amount, 0, Math.max(0, this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER)));
            } else {
                this.scrollTop = MathHelper.clamp_float(this.scrollTop - amount, 0, Math.max(0, this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER)));
            }
        } else if (this.vertical) {
            this.scrollTop = MathHelper.clamp_float(this.scrollTop - amount, 0, Math.max(0, this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER)));
        } else if (this.horizontal) {
            this.scrollLeft = MathHelper.clamp_float(this.scrollLeft - amount, 0, Math.max(0, this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER)));
        }
        return false;
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);
        if (button == 0) {
            if (this.vertical) {
                if (mouseX > this.getWidth(SizeContext.INNER) && mouseY <= this.getHeight(SizeContext.INNER)) {
                    this.dragging = 1;
                    return;
                }
            }
            if (this.horizontal) {
                if (mouseY > this.getHeight(SizeContext.INNER) && mouseX <= this.getWidth(SizeContext.INNER)) {
                    this.dragging = 2;
                }
            }
        }
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button) {
        super.onMouseUp(mouseX, mouseY, button);
        this.dragging = 0;
    }

    @Override
    public void onMouseDrag(int oldX, int oldY, int newX, int newY, int button, long pressTime) {
        super.onMouseDrag(oldX, oldY, newX, newY, button, pressTime);
        if (this.dragging == 1) { // Vertical
            int thumbHeight = GuiHelper.getScaled(this.getHeight(SizeContext.INNER), this.getHeight(SizeContext.INNER), this.innerPanel.getHeight(SizeContext.OUTLINE));
            float scrollHeight = this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER); // Floats for precision
            float scaledHeight = this.getHeight(SizeContext.INNER) - thumbHeight;
            float position = GuiHelper.getScaled(scaledHeight, this.scrollTop, scrollHeight);
            this.scrollTop = GuiHelper.getScaled(scrollHeight, position + newY - oldY, scaledHeight);
        } else if (this.dragging == 2) { // Horizontal
            int thumbWidth = GuiHelper.getScaled(this.getWidth(SizeContext.INNER), this.getWidth(SizeContext.INNER), this.innerPanel.getWidth(SizeContext.OUTLINE));
            float scrollWidth = this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER); // Floats for precision
            float scaledWidth = this.getWidth(SizeContext.INNER) - thumbWidth;
            float position = GuiHelper.getScaled(scaledWidth, this.scrollLeft, scrollWidth);
            this.scrollLeft = GuiHelper.getScaled(scrollWidth, position + newX - oldX, scaledWidth);
        }
    }

    @Override
    public void addChild(SGComponent child, Object param) {
        this.innerPanel.addChild(child, param);
    }

    @Override
    public void removeChild(SGComponent child) {
        this.innerPanel.removeChild(child);
        this.getRoot().updateLayout();
    }

    @Override
    public void setLayoutManager(SGLayoutManager layoutManager) {
        this.innerPanel.setLayoutManager(layoutManager);
    }

    @Override
    public List<SGComponent> getChildren() {
        return this.innerPanel.getChildren();
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        this.innerPanel.setDimensions(this.innerPanel.predictSize());
        if (this.vertical) {
            this.scrollTop = MathHelper.clamp_float(this.scrollTop, 0, Math.max(0, this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER)));
        } else if (this.horizontal) {
            this.scrollLeft = MathHelper.clamp_float(this.scrollLeft, 0, Math.max(0, this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER)));
        }
    }

    private class Inner extends SGPanel {

        @Override
        public void setSizeRestrictions(int width, int height) {
            // There is no limit to our love
        }

        @Override
        public Region getRenderingRegion() {
            Region parentRegion = SGScrollPane.this.getRenderingRegion();
            //return super.getRenderingRegion().offset(parentRegion.left, parentRegion.top);
            return new Region(parentRegion.left, parentRegion.top, SGScrollPane.this.getWidth(SizeContext.INNER), SGScrollPane.this.getHeight(SizeContext.INNER));
        }

        @Override
        public Region predictSize() {
            return super.predictSize();
        }

        @Override
        public void updateLayout() {
            super.updateLayout();
        }

        @Override
        public void draw(int mouseX, int mouseY, float partialTicks) {
            //            if (this.isErrored()) {
            //                SGUtils.drawErrorBox(this);
            //                return;
            //            }
            //            if (!this.isVisible()) {
            //                return;
            //            }
            //            int stack = 0;
            //            try {
            //                stack++;
            //                GL11.glPushMatrix();
            //                stack++;
            //                GL11.glPushMatrix();
            //                this.drawBackground(mouseX, mouseY, partialTicks);
            //                this.drawForeground(mouseX, mouseY, partialTicks);
            //                GL11.glPopMatrix();
            //                stack--;
            //                int left = this.getLeft(SizeContext.INNER);
            //                int top = this.getTop(SizeContext.INNER);
            //
            //                if (SGComponent.DEBUG) {
            //                    if (this.isMouseOver() || this.hasFocus()) {
            //                        int color = (this.hashCode() & 0xFFFFFF) | 0x88000000;
            //                        GL11.glDepthMask(false);
            //                        int width = this.getWidth(SizeContext.OUTLINE);
            //                        int height = this.getHeight(SizeContext.OUTLINE);
            //                        if (this.isMouseOver()) {
            //                            GuiHelper.drawColoredRect(left, top, left + width, top + height, color, this.getZLevel());
            //                        }
            //                        if (this.hasFocus()) {
            //                            SGUtils.drawBox(left, top, width, height, this.getZLevel(), color);
            //                        }
            //                        GL11.glDepthMask(true);
            //                    }
            //                }
            //
            //                GL11.glTranslatef(left, top, this.getZLevel());
            //                List<SGComponent> children = this.getChildren();
            //                if (children != null) {
            //                    for (SGComponent component : children) {
            //                        component.draw(mouseX - left, mouseY - top, partialTicks);
            //                    }
            //                }
            //                GL11.glPopMatrix();
            //                stack--;
            //            } catch (Exception e) {
            //                e.printStackTrace();
            //                this.setErrored();
            //                while (stack > 0) {
            //                    GL11.glPopMatrix();
            //                    stack--;
            //                }
            //            }
        }

        @Override
        public IComponentHolder getRoot() {
            return SGScrollPane.this.getRoot();
        }
    }

}
