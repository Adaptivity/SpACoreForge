package net.specialattack.forge.core.client.gui.animation;

import net.specialattack.forge.core.client.gui.elements.IGuiElement;

public interface IAnimation {

    void progressTicks(int ticks);

    void prepareDraw(IGuiElement element, float partialTicks);

}
