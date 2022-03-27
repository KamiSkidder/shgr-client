package com.kamiskidder.shgr.ui.hud;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.module.render.HudEditor;
import com.kamiskidder.shgr.util.render.ColorUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

public class Hud extends Module {
    private final float offset = 10;
    public Setting<Float> x = register(new Setting("X", 0f, 1980f, 0f));
    public Setting<Float> y = register(new Setting("Y", 0f, 1280f, 0f));
    public float width, height = 0;
    private boolean dragging, touching = false;
    private float diffX, diffY = 0;

    public Hud(String name) {
        super(name, Category.HUD);
    }

    protected void onRenderHud() {
    }

    protected float getXPos() {
        return x.getValue();
    }

    protected float getYPos() {
        return y.getValue();
    }

    @Override
    public void onRender2D() {
        ScaledResolution sr = new ScaledResolution(mc);
        x.setMaxValue((float) sr.getScaledWidth());
        y.setMaxValue((float) sr.getScaledHeight());

        if (HudEditor.INSTANCE.isToggled()) {
            float x1 = getXPos() - offset;
            float y1 = getYPos() - offset;
            float x2 = getXPos() + offset + width;
            float y2 = getYPos() + offset + height;
            RenderUtil.drawRect(x1, y1, x2, y2, ColorUtil.toRGBA(30, 30, 30, dragging ? 150 : (touching ? 120 : 80)));

            RenderUtil.drawLine(x1, y1, x1, y2, 2, ColorUtil.toRGBA(50, 50, 50, 100));
            RenderUtil.drawLine(x2, y1, x2, y2, 2, ColorUtil.toRGBA(50, 50, 50, 100));
            RenderUtil.drawLine(x1, y1, x2, y1, 2, ColorUtil.toRGBA(50, 50, 50, 100));
            RenderUtil.drawLine(x1, y2, x2, y2, 2, ColorUtil.toRGBA(50, 50, 50, 100));
        }

        onRenderHud();
    }

    public void mouseUpdated(int mouseX, int mouseY) {
        if (dragging) {
            x.setValue(mouseX + diffX);
            y.setValue(mouseY + diffY);
        }

        touching = isMouseHovering(mouseX, mouseY);
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if (isMouseHovering(mouseX, mouseY)) {
            dragging = true;
            diffX = x.getValue() - mouseX;
            diffY = y.getValue() - mouseY;
        }
    }

    protected boolean isMouseHovering(int mouseX, int mouseY) {
        return isMouseHovering(mouseX, mouseY, getXPos() - offset, getYPos() - offset,
                getXPos() + width + offset, getYPos() + height + offset);
    }

    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }

    protected boolean isMouseHovering(float mouseX, float mouseY, float x1, float y1, float x2, float y2) {
        return x1 < mouseX && x2 > mouseX && y1 < mouseY && y2 > mouseY;
    }
}
