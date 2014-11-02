package net.specialattack.forge.core.client.gui.elements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.specialattack.forge.core.client.gui.animation.IAnimation;

/**
 * A GUI element that can be drawn
 */
@SideOnly(Side.CLIENT)
public interface IGuiElement {

    /**
     * @return width
     */
    int getWidth();

    /**
     * @return height
     */
    int getHeight();

    /**
     * @return X position
     */
    int getPosX();

    /**
     * @return Y position
     */
    int getPosY();

    /**
     * @return Z position (depth)
     */
    float getZLevel();

    IGuiElement getParent();

    void draw(float partialTicks);

    List<IGuiElement> getChildElements();

    void addChild(IGuiElement element);

    void propagateFocusChangeDown(IFocusableElement element);

    void propagateFocusChangeUp(IFocusableElement element);

    boolean onClickMe(int mouseX, int mouseY, int mouseButton);

    boolean onClick(int mouseX, int mouseY, int mouseButton);

    boolean onKey(char character, int keycode);

    void updateTick();

    void setEnabled(boolean enabled);

    boolean isEnabled();

    void setVisible(boolean visible);

    boolean isVisible();

    void addTickListener(ITickListener listener);

    void addAnimation(IAnimation animation);

}
