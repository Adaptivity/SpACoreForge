package net.specialattack.forge.core.client.gui.elements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.Positioning;

@SideOnly(Side.CLIENT)
public interface IMovableGuiElement extends IGuiElement {

    void move(int posX, int posY, float zLevel);

    void setPositionMethod(Positioning methodX, Positioning methodY);

}
