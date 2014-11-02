package net.specialattack.forge.core.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class GuiScreenWrap extends GuiScreen {

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected final void keyTyped(char character, int keycode) {
        super.keyTyped(character, keycode);
    }

    @Override
    protected final void renderToolTip(ItemStack stack, int mouseX, int mouseY) {
        super.renderToolTip(stack, mouseX, mouseY);
    }

    @Override
    protected final void drawCreativeTabHoveringText(String text, int mouseX, int mouseY) {
        super.drawCreativeTabHoveringText(text, mouseX, mouseY);
    }

    @Override
    protected final void func_146283_a(List text, int mouseX, int mouseY) {
        super.func_146283_a(text, mouseX, mouseY);
    }

    @Override
    protected final void drawHoveringText(List text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        super.drawHoveringText(text, mouseX, mouseY, fontRenderer);
    }

    @Override
    protected final void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected final void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
    }

    @Override
    protected final void mouseClickMove(int mouseX, int mouseY, int mouseButton, long dragTime) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, dragTime);
    }

    @Override
    protected final void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
    }

    @Override
    public final void setWorldAndResolution(Minecraft minecraft, int scaledWidth, int scaledHeight) {
        super.setWorldAndResolution(minecraft, scaledWidth, scaledHeight);
    }

    @Override
    public final void initGui() {
        super.initGui();
    }

    @Override
    public final void handleInput() {
        super.handleInput();
    }

    @Override
    public final void handleMouseInput() {
        super.handleMouseInput();
    }

    @Override
    public final void handleKeyboardInput() {
        super.handleKeyboardInput();
    }

    @Override
    public final void updateScreen() {
        super.updateScreen();
    }

    @Override
    public final void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public final void drawDefaultBackground() {
        super.drawDefaultBackground();
    }

    @Override
    public final void drawWorldBackground(int offsetV) {
        super.drawWorldBackground(offsetV);
    }

    @Override
    public final void drawBackground(int offsetV) {
        super.drawBackground(offsetV);
    }

    @Override
    public final boolean doesGuiPauseGame() {
        return super.doesGuiPauseGame();
    }

    @Override
    public final void confirmClicked(boolean clickYes, int clickId) {
        super.confirmClicked(clickYes, clickId);
    }
}
