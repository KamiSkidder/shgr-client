package com.kamiskidder.shgr.ui.clickgui.component;

import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.ui.clickgui.Component;
import com.kamiskidder.shgr.util.render.ColorUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;

import java.awt.*;

public class ColorPicker extends Component {
    private final Setting<Color> setting;
    private final float svPickerWidth;
    private final float hPickerWidth;
    private final float aPickerWidth;
    private boolean open;
    // positions
    private float svPickerX;
    private float svPickerY;
    private float svPickerHeight = 0;
    private float hPickerX;
    private float hPickerY;
    private float hPickerHeight = 0;
    private float aPickerX;
    private float aPickerY;
    private float aPickerHeight = 0;
    // cursor
    private float svCursorX, svCursorY = 0;
    private float hCursorX, hCursorY = 0;
    private float aCursorX, aCursorY = 0;
    private boolean svChanging, hChanging, aChanging;

    public ColorPicker(Setting setting) {
        this.setting = setting;
        this.width = 140;
        this.height = 21;
        // size
        this.svPickerWidth = 100;
        this.svPickerHeight = 100;

        this.hPickerWidth = 10;
        this.hPickerHeight = svPickerHeight;

        this.aPickerWidth = svPickerWidth + hPickerWidth + 8;
        this.aPickerHeight = 10;

        // cursors
        Color c = this.setting.getValue();
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getBlue(), c.getGreen(), null);
        hCursorX = hPickerWidth / 2;
        hCursorY = hsv[0] * hPickerHeight;

        svCursorX = hsv[1] * svPickerWidth;
        svCursorY = (1.0F - hsv[2]) * svPickerHeight;

        aCursorX = (c.getAlpha() / 255.0F) * aPickerWidth;
        aCursorY = aPickerHeight / 2;
    }

    @Override
    public float render(int x, int y, int mouseX, int mouseY) {
        if (setting.isVisible()) {
            this.x = x;
            this.y = y;
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(17, 17, 17));
            drawString(setting.getName(), x + 8, (int) getCenter(y, height, getStringHeight()),
                    new Color(230, 230, 230), false);
            drawString("...", x + width - (int) getStringWidth("...") - 5,
                    (int) getCenter(y, height, getStringHeight()), new Color(230, 230, 230), false);
            int rectScale = 13;
            int rectX = x + width - rectScale - 17;
            int rectY = (int) getCenter(y, height, rectScale);
            RenderUtil.drawRect(rectX, rectY, rectX + rectScale, rectY + rectScale, new Color(60, 60, 60));
            RenderUtil.drawRect(rectX + 0.7F, rectY + 0.7F, rectX + rectScale - 0.7F, rectY + rectScale - 0.7F,
                    new Color(17, 17, 17));
            Color c = this.setting.getValue();
            Color renderColor = new Color(c.getRed(), c.getGreen(), c.getBlue());
            RenderUtil.drawRect(rectX + 1.5F, rectY + 1.5F, rectX + rectScale - 1.5F, rectY + rectScale - 1.5F,
                    renderColor);

            if (isMouseHovering(mouseX, mouseY)) {
                RenderUtil.drawRect(x, y, x + width, y + height, new Color(255, 255, 255, 40));
            }

            float offsetHeight = 0.0F;
            if (open) {
                boolean changed = false;

                float pickerX = this.x;
                float pickerY = this.y + this.height;
                float pickerWidth = this.width;
                float pickerHeight = 130;
                offsetHeight = pickerHeight;
                RenderUtil.drawRect(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight,
                        new Color(10, 10, 10));
                // sv
                float h = (hPickerHeight - hCursorY) / hPickerHeight;

                float r;

                this.svPickerX = pickerX + 12;
                this.svPickerY = pickerY + 5;
                r = 1.0F / svPickerHeight;
                for (int i = 0; i < svPickerHeight; i++) {
                    float v0 = r * (svPickerHeight - i);
                    float v1 = r * (svPickerHeight - (i + 1));
                    int left = Color.HSBtoRGB(h, 0F, v0);
                    int right = Color.HSBtoRGB(h, 1.0F, v1);
                    RenderUtil.drawGradientRect(svPickerX, svPickerY + i, svPickerX + svPickerWidth,
                            svPickerY + i + 1.1F, left, right, left, right);
                }

                // sv cursor
                float svCx = svCursorX + svPickerX;
                float svCy = svCursorY + svPickerY;
                renderCursor(svCx, svCy);
                if (svChanging) {
                    svCursorX = mouseX - svPickerX;
                    if (svCursorX < 0)
                        svCursorX = 0;
                    if (svCursorX > svPickerWidth)
                        svCursorX = svPickerWidth;

                    svCursorY = mouseY - svPickerY;
                    if (svCursorY < 0)
                        svCursorY = 0;
                    if (svCursorY > svPickerHeight)
                        svCursorY = svPickerHeight;

                    changed = true;
                }

                // h
                this.hPickerX = svPickerX + svPickerWidth + 8;
                this.hPickerY = svPickerY;
                r = 1.0F / hPickerHeight;
                for (int i = 0; i < hPickerHeight; i++) {
                    float h0 = r * (hPickerHeight - i);
                    float h1 = r * (hPickerHeight - (i + 1));
                    int top = Color.HSBtoRGB(h0, 1.0F, 1.0F);
                    int bottom = Color.HSBtoRGB(h1, 1.0F, 1.0F);
                    RenderUtil.drawGradientRect(hPickerX, hPickerY + i, hPickerX + hPickerWidth, hPickerY + i + 1, top,
                            top, bottom, bottom);
                }

                // h cursor
                float hCx = hCursorX + hPickerX;
                float hCy = hCursorY + hPickerY;
                renderCursor(hCx, hCy);
                if (hChanging) {
                    hCursorX = mouseX - hPickerX;
                    if (hCursorX < 0)
                        hCursorX = 0;
                    if (hCursorX > hPickerWidth)
                        hCursorX = hPickerWidth;

                    hCursorY = mouseY - hPickerY;
                    if (hCursorY < 0)
                        hCursorY = 0;
                    if (hCursorY > hPickerHeight)
                        hCursorY = hPickerHeight;

                    changed = true;
                }

                // alpha
                this.aPickerX = svPickerX;
                this.aPickerY = svPickerY + svPickerHeight + 7;
                // rect
                float aRectScale = aPickerHeight / 2;
                float rectCount = aPickerWidth / aRectScale;
                for (int i = 0; i < rectCount; i++) {
                    float aRectX1 = aPickerX + i * aRectScale;
                    float aRectX2 = aPickerX + (i + 1) * aRectScale;
                    if (aRectX2 > aPickerX + aPickerWidth)
                        aRectX2 = aPickerX + aPickerWidth;

                    RenderUtil.drawRect(aRectX1, aPickerY, aRectX2, aPickerY + aRectScale,
                            i % 2 == 0 ? new Color(255, 255, 255) : new Color(204, 204, 204));
                    RenderUtil.drawRect(aRectX1, aPickerY + aRectScale, aRectX2, aPickerY + aRectScale * 2,
                            (i + 1) % 2 == 0 ? new Color(255, 255, 255) : new Color(204, 204, 204));
                }
                c = this.setting.getValue();
                int right = ColorUtil.toRGBA(c.getRed(), c.getGreen(), c.getBlue(), 1);
                int left = ColorUtil.toRGBA(c.getRed(), c.getGreen(), c.getBlue(), 255);
                RenderUtil.drawGradientRect(aPickerX, aPickerY, aPickerX + aPickerWidth, aPickerY + aPickerHeight,
                        right, left, right, left);
                // cursor
                float aCx = aCursorX + aPickerX;
                float aCy = aCursorY + aPickerY;
                renderCursor(aCx, aCy);
                if (aChanging) {
                    aCursorX = mouseX - aPickerX;
                    if (aCursorX < 0)
                        aCursorX = 0;
                    if (aCursorX > aPickerWidth)
                        aCursorX = aPickerWidth;

                    aCursorY = mouseY - aPickerY;
                    if (aCursorY < 0)
                        aCursorY = 0;
                    if (aCursorY > aPickerHeight)
                        aCursorY = aPickerHeight;

                    changed = true;
                }

                if (changed) {
                    float s = svCursorX / svPickerWidth;
                    float v = (svPickerHeight - svCursorY) / svPickerHeight;
                    float a = aCursorX / aPickerWidth;
                    c = new Color(Color.HSBtoRGB(h, s, v));
                    setting.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (a * 255.0F)));
                }
            }
            return this.height + offsetHeight;
        }
        return 0.0F;
    }

    private void renderCursor(float x, float y) {
        RenderUtil.drawRect(x - 2, y - 2, x + 2, y + 2, new Color(20, 20, 20));
        RenderUtil.drawRect(x - 1F, y - 1F, x + 1F, y + 1F, new Color(250, 250, 250));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (open && clickedButton == 0) {
            if (isMouseHovering(mouseX, mouseY, svPickerX, svPickerY, svPickerX + svPickerWidth,
                    svPickerY + svPickerHeight))
                svChanging = true;

            if (isMouseHovering(mouseX, mouseY, hPickerX, hPickerY, hPickerX + hPickerWidth, hPickerY + hPickerHeight))
                hChanging = true;

            if (isMouseHovering(mouseX, mouseY, aPickerX, aPickerY, aPickerX + aPickerWidth, aPickerY + aPickerHeight))
                aChanging = true;
        }

        if (isMouseHovering(mouseX, mouseY) && setting.isVisible() && clickedButton == 1) {
            open = !open;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        svChanging = false;
        hChanging = false;
        aChanging = false;
    }
}
