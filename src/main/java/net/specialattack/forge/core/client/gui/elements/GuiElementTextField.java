package net.specialattack.forge.core.client.gui.elements;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.Positioning;
import org.lwjgl.opengl.GL11;

public class GuiElementTextField extends GuiBaseElement implements IFocusableElement {

    private FontRenderer font = MC.getFontRenderer();
    private String value;
    private int maxLength;
    private int cursorCounter;
    private int cursorPosition, selectionPosition, scrollOffset;
    private boolean hasFocus;
    private Color defaultColor, disabledColor;

    public GuiElementTextField(int posX, int posY, int width, int height, IGuiElement parent, float zLevel, Positioning positioningX, Positioning positioningY) {
        super(posX, posY, width, height, parent, zLevel, positioningX, positioningY);
        this.value = "";
        this.maxLength = 32;
        this.defaultColor = Color.TEXT_FOREGROUND;
        this.disabledColor = Color.TEXT_FOREGROUND_DISABLED;
    }

    public GuiElementTextField(int posX, int posY, int width, int height, IGuiElement parent, int posZ) {
        this(posX, posY, width, height, parent, posZ, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiElementTextField(int posX, int posY, int width, int height, IGuiElement parent) {
        this(posX, posY, width, height, parent, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiElementTextField(int posX, int posY, int width, int height) {
        this(posX, posY, width, height, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    @Override
    public void updateTick() {
        super.updateTick();
        this.cursorCounter++;
    }

    @Override
    public boolean canGetFocus() {
        return this.isEnabled();
    }

    @Override
    public void giveFocus() {
        if (this.isEnabled()) {
            this.hasFocus = true;
        }
    }

    @Override
    public void doDraw(float partialTicks) {
        super.doDraw(partialTicks);
        int drawColor = this.isEnabled() ? this.defaultColor.colorHex : this.disabledColor.colorHex;
        int cursorOffset = this.cursorPosition - this.scrollOffset;
        int selectionOffset = this.selectionPosition - this.scrollOffset;
        String visibleText = this.font.trimStringToWidth(this.value.substring(this.scrollOffset), this.getWidth() - (this.hasBorder() ? 4 : 0));
        boolean cursorVisible = cursorOffset >= 0 && cursorOffset <= visibleText.length();
        boolean drawCursor = this.hasFocus && this.cursorCounter / 6 % 2 == 0 && cursorVisible;
        int posX = this.hasBorder() ? 4 : 0;
        int posY = this.hasBorder() ? 4 : 0;
        int cursorPosition = posX;

        if (selectionOffset > visibleText.length()) {
            selectionOffset = visibleText.length();
        }

        if (visibleText.length() > 0) {
            String text = cursorVisible ? visibleText.substring(0, cursorOffset) : visibleText;
            cursorPosition = this.font.drawStringWithShadow(text, posX, posY, drawColor);
        }

        boolean cursorInbetween = this.cursorPosition < this.value.length() || this.value.length() >= this.maxLength;
        int cursorDrawPosition = cursorPosition;

        if (!cursorVisible) {
            cursorDrawPosition = cursorOffset > 0 ? posX + this.getWidth() : posX;
        } else if (cursorInbetween) {
            cursorDrawPosition = cursorPosition - 1;
            cursorPosition--;
        }

        if (visibleText.length() > 0 && cursorVisible && cursorOffset < visibleText.length()) {
            this.font.drawStringWithShadow(visibleText.substring(cursorOffset), cursorPosition, posY, drawColor);
        }

        if (drawCursor) {
            if (cursorInbetween) {
                Gui.drawRect(cursorDrawPosition, posY - 1, cursorDrawPosition + 1, posY + 1 + this.font.FONT_HEIGHT, -3092272);
            } else {
                this.font.drawStringWithShadow("_", cursorDrawPosition, posY, drawColor);
            }
        }

        if (selectionOffset != cursorOffset) {
            int selectionLength = posX + this.font.getStringWidth(visibleText.substring(0, selectionOffset));
            this.drawSelection(cursorDrawPosition, posY - 1, selectionLength - 1, posY + 1 + this.font.FONT_HEIGHT);
        }
    }

    private void drawSelection(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
        }

        if (startY < endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
        }

        int posX = this.getPosX();
        int width = this.getWidth();

        if (endX > posX + width) {
            endX = posX + width;
        }

        if (startX > posX + width) {
            startX = posX + width;
        }

        Tessellator tess = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tess.startDrawingQuads();
        tess.addVertex(startX, endY, 0.0D);
        tess.addVertex(endX, endY, 0.0D);
        tess.addVertex(endX, startY, 0.0D);
        tess.addVertex(startX, startY, 0.0D);
        tess.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void propagateFocusChangeDown(IFocusableElement element) {
        this.hasFocus = false;
        super.propagateFocusChangeDown(element);
    }

    @Override
    public void setTextColor(Color color) {
        this.defaultColor = color == null ? Color.TEXT_FOREGROUND : color;
    }

    @Override
    public void setDisabledColor(Color color) {
        this.disabledColor = color == null ? Color.TEXT_FOREGROUND_DISABLED : color;
    }

    public void setValue(String value) {
        if (value.length() > this.maxLength) {
            value = value.substring(0, this.maxLength);
        }
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getSelectedText() {
        int start = this.selectionPosition < this.cursorPosition ? this.selectionPosition : this.cursorPosition;
        int end = this.selectionPosition < this.cursorPosition ? this.cursorPosition : this.selectionPosition;
        return this.value.substring(start, end);
    }

    public void writeText(String text) {
        text = ChatAllowedCharacters.filerAllowedCharacters(text);
        int start = this.selectionPosition < this.cursorPosition ? this.selectionPosition : this.cursorPosition;
        int end = this.selectionPosition < this.cursorPosition ? this.cursorPosition : this.selectionPosition;
        int maxLength = this.maxLength - this.value.length() - (start - this.selectionPosition);

        StringBuilder result = new StringBuilder();
        if (this.value.length() > 0) {
            result.append(this.value.substring(0, start));
        }

        int offset;

        if (maxLength < this.maxLength) {
            result.append(text.substring(0, maxLength));
            offset = maxLength;
        } else {
            result.append(text);
            offset = text.length();
        }

        if (this.value.length() > 0 && end < this.value.length()) {
            result.append(this.value.substring(end));
        }

        this.value = result.toString();
        this.setPosition(start - this.selectionPosition + offset);
    }

    public void deleteWords(int amount) {
        if (this.value.length() != 0) {
            if (this.selectionPosition != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(amount) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int number) {
        if (this.value.length() != 0) {
            if (this.selectionPosition != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean deleteLeft = number < 0;
                int start = deleteLeft ? this.cursorPosition + number : this.cursorPosition;
                int end = deleteLeft ? this.cursorPosition : this.cursorPosition + number;

                StringBuilder result = new StringBuilder();
                if (start >= 0) {
                    result.append(this.value.substring(0, start));
                }

                if (end < this.value.length()) {
                    result.append(this.value.substring(end));
                }

                this.value = result.toString();

                if (deleteLeft) {
                    this.setCursorPosition(this.cursorPosition - number);
                }
            }
        }
    }

    public int getNthWordFromCursor(int amount) {
        return this.getNthWordFromPosition(amount, this.cursorPosition);
    }

    public int getNthWordFromPosition(int amount, int position) {
        return getNthWordFromPosition(amount, position, true);
    }

    public int getNthWordFromPosition(int amount, int position, boolean skipSpaces) {
        int currentPos = position;
        boolean searchLeft = amount < 0;
        amount = Math.abs(amount);

        for (int i = 0; i < amount; ++i) {
            if (searchLeft) {
                while (skipSpaces && currentPos > 0 && this.value.charAt(currentPos - 1) == 32) {
                    currentPos--;
                }

                while (currentPos > 0 && this.value.charAt(currentPos - 1) != 32) {
                    currentPos--;
                }
            } else {
                int length = this.value.length();
                currentPos = this.value.indexOf(32, currentPos);

                if (currentPos == -1) {
                    currentPos = length;
                } else {
                    while (skipSpaces && currentPos < length && this.value.charAt(currentPos) == 32) {
                        currentPos++;
                    }
                }
            }
        }

        return currentPos;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setCursorPositionStart() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.value.length());
    }

    public void setCursorPosition(int position) {
        this.cursorPosition = MathHelper.clamp_int(position, 0, this.value.length());
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public void setSelectionPositionStart() {
        this.setSelectionPosition(0);
    }

    public void setSelectionPositionEnd() {
        this.setSelectionPosition(this.value.length());
    }

    public void setSelectionPosition(int position) {
        this.selectionPosition = MathHelper.clamp_int(position, 0, this.value.length());
    }

    public int getSelectionPosition() {
        return this.selectionPosition;
    }

    public void setPositionStart() {
        this.setCursorPosition(0);
        this.setSelectionPosition(0);
    }

    public void setPositionEnd() {
        this.setCursorPosition(this.value.length());
        this.setSelectionPosition(this.value.length());
    }

    public void setPosition(int position) {
        this.setCursorPosition(position);
        this.setSelectionPosition(position);
    }

    @Override
    public boolean onKey(char character, int keycode) {
        if (this.hasFocus) {
            switch (character) {
                case 1:
                    this.setCursorPositionEnd();
                    this.setSelectionPosition(0);
                    return true;
                case 3:
                    GuiScreen.setClipboardString(this.getSelectedText());
                    return true;
                case 22:
                    if (this.isEnabled()) {
                        this.writeText(GuiScreen.getClipboardString());
                    }

                    return true;
                case 24:
                    GuiScreen.setClipboardString(this.getSelectedText());

                    if (this.isEnabled()) {
                        this.writeText("");
                    }

                    return true;
                default:
                    switch (keycode) {
                        case 14:
                            if (GuiScreen.isCtrlKeyDown()) {
                                if (this.isEnabled()) {
                                    this.deleteWords(-1);
                                }
                            } else if (this.isEnabled()) {
                                this.deleteFromCursor(-1);
                            }

                            return true;
                        case 199:
                            if (GuiScreen.isShiftKeyDown()) {
                                this.setSelectionPositionStart();
                            } else {
                                this.setPositionStart();
                            }

                            return true;
                        case 203:
                            if (GuiScreen.isShiftKeyDown()) {
                                if (GuiScreen.isCtrlKeyDown()) {
                                    this.setSelectionPosition(this.getNthWordFromPosition(-1, this.selectionPosition));
                                } else {
                                    this.setSelectionPosition(this.selectionPosition - 1);
                                }
                            } else if (GuiScreen.isCtrlKeyDown()) {
                                this.setCursorPosition(this.getNthWordFromCursor(-1));
                            } else {
                                this.setPosition(this.cursorPosition - 1);
                            }

                            return true;
                        case 205:
                            if (GuiScreen.isShiftKeyDown()) {
                                if (GuiScreen.isCtrlKeyDown()) {
                                    this.setSelectionPosition(this.getNthWordFromPosition(1, this.selectionPosition));
                                } else {
                                    this.setSelectionPosition(this.selectionPosition + 1);
                                }
                            } else if (GuiScreen.isCtrlKeyDown()) {
                                this.setCursorPosition(this.getNthWordFromCursor(1));
                            } else {
                                this.setPosition(this.cursorPosition + 1);
                            }

                            return true;
                        case 207:
                            if (GuiScreen.isShiftKeyDown()) {
                                this.setSelectionPositionEnd();
                            } else {
                                this.setPositionEnd();
                            }

                            return true;
                        case 211:
                            if (GuiScreen.isCtrlKeyDown()) {
                                if (this.isEnabled()) {
                                    this.deleteWords(1);
                                }
                            } else if (this.isEnabled()) {
                                this.deleteFromCursor(1);
                            }

                            return true;
                        default:
                            if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                                if (this.isEnabled()) {
                                    this.writeText(Character.toString(character));
                                }

                                return true;
                            } else {
                                return false;
                            }
                    }
            }
        }

        return true;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        if (!this.isEnabled()) {
            return false;
        }

        this.giveFocus();
        this.propagateFocusChangeUp(this);

        if (this.hasFocus && button == 0) {
            int offset = mouseX - this.getPosX();

            String trimmed = this.font.trimStringToWidth(this.value.substring(this.scrollOffset), this.getWidth());
            this.setCursorPosition(this.font.trimStringToWidth(trimmed, offset).length() + this.scrollOffset);
        }

        return true;
    }
}
