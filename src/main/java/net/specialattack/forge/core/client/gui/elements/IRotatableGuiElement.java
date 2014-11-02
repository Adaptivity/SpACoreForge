package net.specialattack.forge.core.client.gui.elements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public interface IRotatableGuiElement extends IGuiElement {

    void setRotation(double rotX, double rotY, double rotZ);

    void setRotation(Vec3 rotation);

    Vec3 getRotation();

}
