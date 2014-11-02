package net.specialattack.forge.core.client.gui.elements;

public interface IFocusableElement extends IGuiElement {

    boolean canGetFocus();

    void giveFocus();

}
