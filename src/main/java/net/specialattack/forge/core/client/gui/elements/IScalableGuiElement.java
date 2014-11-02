package net.specialattack.forge.core.client.gui.elements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public interface IScalableGuiElement extends IGuiElement {

    void setScale(double scaleX, double scaleY, double scaleZ);

    void setScale(Vec3 scale);

    Vec3 getScale();

}
