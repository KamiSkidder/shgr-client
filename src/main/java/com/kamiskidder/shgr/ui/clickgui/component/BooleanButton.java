package com.kamiskidder.shgr.ui.clickgui.component;

import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.ui.clickgui.Component;
import com.kamiskidder.shgr.util.render.RenderUtil;

import java.awt.*;

public class BooleanButton extends Component {
    private final Setting<Boolean> setting;
    private final Color color;

    public BooleanButton(Setting setting, Color color) {
        this.setting = setting;
        this.color = color;
        this.width = 140;
        this.height = 21;
    }

    @Override
    public float render(int x, int y, int mouseX, int mouseY) {
        if (setting.isVisible()) {
            this.x = x;
            this.y = y;
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(17, 17, 17));
            drawString(setting.getName(), x + 8, (int) getCenter(y, height, getStringHeight()), new Color(230, 230, 230), false);
            int rectScale = 13;
            int rectX = x + width - rectScale - 7;
            int rectY = (int) getCenter(y, height, rectScale);
            RenderUtil.drawRect(rectX, rectY, rectX + rectScale, rectY + rectScale, isMouseHovering(mouseX, mouseY) ? new Color(90, 90, 90) : new Color(60, 60, 60));
            RenderUtil.drawRect(rectX + 0.7F, rectY + 0.7F, rectX + rectScale - 0.7F, rectY + rectScale - 0.7F, new Color(17, 17, 17));
            if (setting.getValue())
                RenderUtil.drawRect(rectX + 1.5F, rectY + 1.5F, rectX + rectScale - 1.5F, rectY + rectScale - 1.5F, color);

            return height;
        }
        return 0.0F;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseHovering(mouseX, mouseY) && setting.isVisible()) {
            setting.setValue(!setting.getValue());
        }
    }
}
