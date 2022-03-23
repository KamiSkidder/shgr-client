package com.kamiskidder.shgr.ui.clickgui;

import com.kamiskidder.shgr.module.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AtopiXGui extends GuiScreen {
    public static List<Panel> panels = new ArrayList<Panel>();

    @Override
    public void initGui() {
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));

        if (panels.isEmpty()) {
            int x = 20;
            for (Category category : Category.values()) {
                panels.add(new Panel(category, x, 30));
                x += 160;
            }
        }
    }

    @Override
    public void onGuiClosed() {
        execute(p -> p.mouseReleased(0, 0, 0));

        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scroll();

        execute(p -> p.renderPanel(mouseX, mouseY));
    }

    public void scroll() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) execute(p -> p.y -= 15);
        else if (dWheel > 0) execute(p -> p.y += 15);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        execute(p -> p.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        execute(p -> p.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    public void keyTyped(char typed, int key) {
        if (key == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        execute(p -> p.keyTyped(typed, key));
    }

    private void execute(Consumer<? super Panel> action) {
        panels.forEach(action);
    }
}
