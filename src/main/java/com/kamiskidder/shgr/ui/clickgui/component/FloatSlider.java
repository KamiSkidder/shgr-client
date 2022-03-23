package com.kamiskidder.shgr.ui.clickgui.component;

import com.kamiskidder.shgr.manager.FontManager;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.ui.clickgui.Component;
import com.kamiskidder.shgr.ui.font.CFontRenderer;
import com.kamiskidder.shgr.util.render.ColorUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;

import java.awt.*;

public class FloatSlider extends Component {
    private final Setting<Float> setting;
    private final Color color;
    private float ratio;
    private boolean changing;

    public FloatSlider(Setting setting, Color color) {
        this.setting = setting;
        this.color = color;
        this.width = 140;
        this.height = 21;
        this.ratio = getRatio(this.setting.getValue(), this.setting.getMaxValue(), this.setting.getMinValue());
    }

    @Override
    public float render(int x, int y, int mouseX, int mouseY) {
        if (setting.isVisible()) {
            this.x = x;
            this.y = y;
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(17, 17, 17));

            CFontRenderer font = FontManager.sliderFont;
            font.drawString(setting.getName(), x + 7, y + 3, ColorUtil.toRGBA(230, 230, 230));
            font.drawString(String.valueOf(setting.getValue()), x + width - (int) getStringWidth(String.valueOf(setting.getValue())) - 10, y + 3, ColorUtil.toRGBA(230, 230, 230));
            int barX1 = x + 8;
            int barY1 = y + 13;
            int barWidth = 120;
            int barX2 = barX1 + barWidth;
            int barY2 = y + 15;
            RenderUtil.drawRect(barX1, barY1, barX1 + barWidth, barY2, new Color(10, 10, 10));
            RenderUtil.drawRect(barX1, barY1, barX1 + (barWidth * ratio), barY2, color);
            if (isMouseHovering(mouseX, mouseY) || changing) {
                RenderUtil.drawRect(barX1, barY1, barX2, barY2, new Color(255, 255, 255, 40));
            }

            if (changing) {
                setValue(mouseX);
                ratio = getRatio(this.setting.getValue(), this.setting.getMaxValue(), this.setting.getMinValue());
            }

            return height;
        }

        return 0.0F;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseHovering(mouseX, mouseY) && setting.isVisible()) {
            changing = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        changing = false;
    }

    public void setValue(float mouseX) {
        float v = (mouseX - this.x) / (width);
        if (v > 1.0F) v = 1.0F;
        if (v < 0.0F) v = 0.0F;
        this.ratio = v;
        float newValue = ((setting.getMaxValue() - setting.getMinValue()) * ratio) + setting.getMinValue();
        setting.setValue(Math.round(newValue * 10) / 10.0F);
    }

    public float getRatio(float value, float max, float min) {
        return (value - min) / (max - min);
    }
}
