package com.kamiskidder.shgr.ui.clickgui.component;

import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.ui.clickgui.Component;
import com.kamiskidder.shgr.util.render.RenderUtil;

import java.awt.*;
import java.util.Arrays;

public class ModeButton extends Component {
    private final Setting<String> setting;

    public ModeButton(Setting mode) {
        this.setting = mode;
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
            drawString(setting.getValue(), x + width - (int) getStringWidth(setting.getValue()) - 8, (int) getCenter(y, height, getStringHeight()), isMouseHovering(mouseX, mouseY) ? new Color(180, 180, 180) : new Color(120, 120, 120), false);
            return this.height;
        }

        return 0.0F;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (isMouseHovering(mouseX, mouseY) && clickedButton == 0 && setting.isVisible()) {
            int index = Arrays.asList(setting.getValues()).indexOf(setting.getValue());
            if (index == -1 || index == setting.getValues().length - 1) {
                setting.setValue(setting.getValues()[0]);
            } else {
                setting.setValue(setting.getValues()[index + 1]);
            }
        }
    }
}
