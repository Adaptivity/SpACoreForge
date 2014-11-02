package net.specialattack.forge.core.client.gui.elements;

import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.style.IBackground;
import net.specialattack.forge.core.client.gui.style.IBorder;

public interface IStyleableElement {

    void setBackground(IBackground background);

    boolean hasBackground();

    void setBorder(IBorder border);

    boolean hasBorder();

    void setTextColor(Color color);

    void setDisabledColor(Color color);

}
