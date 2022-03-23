package com.kamiskidder.shgr.ui.clickgui;

import com.kamiskidder.shgr.manager.FontManager;
import com.kamiskidder.shgr.util.render.ColorUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;

import java.awt.*;

public class Component {
    public int x, y, width, height;

    public float render(int x, int y, int mouseX, int mouseY) {
        return 0.0F;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void keyTyped(char typed, int key) {
    }


    public float drawString(String str, int x, int y, int color, boolean shadow) {
        return RenderUtil.drawString(FontManager.guiFont, str, x, y, color, shadow);
    }

    public float drawString(String str, int x, int y, Color color, boolean shadow) {
        return RenderUtil.drawString(FontManager.guiFont, str, x, y, ColorUtil.toRGBA(color), shadow);
    }

    public float getStringWidth(String str) {
        return RenderUtil.getStringWidth(FontManager.guiFont, str);
    }

    public float getStringHeight() {
        return RenderUtil.getStringHeight(FontManager.guiFont);
    }

    public float getCenter(float a, float b, float c) {
        return a + (b - c) / 2;
    }

    public boolean isMouseHovering(int mouseX, int mouseY) {
        return x < mouseX && x + width > mouseX && y < mouseY && y + height > mouseY;
    }

    public boolean isMouseHovering(float mouseX, float mouseY, float x1, float y1, float x2, float y2) {
        return x1 < mouseX && x2 > mouseX && y1 < mouseY && y2 > mouseY;
    }

    public String getName(String name) {
        String r = "";
        boolean a = false;
        for (char c : name.toCharArray()) {
            if (!a) r += String.valueOf(c).toUpperCase();
            else r += String.valueOf(c).toLowerCase();
            a = true;
        }
        return r;
    }
}
