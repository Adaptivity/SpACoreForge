package net.specialattack.forge.core.client.gui.animation;

import net.minecraft.util.Vec3;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;
import net.specialattack.forge.core.client.gui.elements.IRotatableGuiElement;

public class AddRotationTrack implements IAnimationTrack {

    private final Vec3 rotation;
    private Vec3 originalRotation;

    public AddRotationTrack(Vec3 rotation) {
        this.rotation = rotation;
    }

    @Override
    public void updateAnimation(IGuiElement element, float ticks) {
        if (!(element instanceof IRotatableGuiElement)) {
            return;
        }
        IRotatableGuiElement rotatable = (IRotatableGuiElement) element;
        if (this.originalRotation == null) {
            this.originalRotation = rotatable.getRotation();
        }
        rotatable.setRotation(originalRotation.addVector(rotation.xCoord * ticks, rotation.yCoord * ticks, rotation.zCoord * ticks));
    }

}
