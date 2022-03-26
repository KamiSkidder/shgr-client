package com.kamiskidder.shgr.ui.clickgui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.kamiskidder.shgr.manager.ModuleManager;
import com.kamiskidder.shgr.ui.hud.Hud;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.render.HudEditor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class AtopiXGui extends GuiScreen {
    public static AtopiXGui INSTANCE;
    public static List<Panel> panels = new ArrayList<Panel>();
    public static Panel hudPanel;

    @Override
    public void initGui() {
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));

        if (panels.isEmpty()) {
            int x = 20;
            for (Category category : Category.values()) {
                if (category == Category.HUD) continue;

                panels.add(new Panel(category, x, 30));
                x += 160;
            }
            hudPanel = new Panel(Category.HUD, 100, 100);
        }

        INSTANCE = this;
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
        executeHud(h -> h.mouseUpdated(mouseX, mouseY));
    }

    public void scroll() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) execute(p -> p.y -= 15);
        else if (dWheel > 0) execute(p -> p.y += 15);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        AtomicBoolean clicked = new AtomicBoolean(false);
        execute(p -> {
            clicked.set(p.mouseClicked(mouseX, mouseY, mouseButton, clicked.get()));
        });
        executeHud(h -> h.mouseClicked(mouseX, mouseY));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        execute(p -> p.mouseReleased(mouseX, mouseY, state));
        executeHud(h -> h.mouseReleased(mouseX, mouseY));
    }

    @Override
    public void keyTyped(char typed, int key) {
        if (key == Keyboard.KEY_ESCAPE) {
            if (isHudEditorToggled())
                HudEditor.INSTANCE.disable();
            else
                mc.displayGuiScreen(null);
            return;
        }
        execute(p -> p.keyTyped(typed, key));
    }

    public void bringToFront(Panel instance) {
        if (panels.contains(instance)) {
            Collections.swap(panels, panels.size() - 1, panels.indexOf(instance));
        }
    }

    private void execute(Consumer<? super Panel> action) {
        if (!isHudEditorToggled())
            panels.forEach(action);
        else
            action.accept(hudPanel);
    }

    private void executeHud(Consumer<? super Hud> action) {
        ModuleManager.INSTANCE.getHudModules().forEach(action);
    }

    private boolean isHudEditorToggled() {
        return HudEditor.INSTANCE.isToggled();
    }
}
