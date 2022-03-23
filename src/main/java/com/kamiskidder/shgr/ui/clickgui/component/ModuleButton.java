package com.kamiskidder.shgr.ui.clickgui.component;

import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.ui.clickgui.Component;
import com.kamiskidder.shgr.util.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModuleButton extends Component {
    private final Module module;
    private final List<Component> settings = new ArrayList<>();
    private boolean open;

    public ModuleButton(Module module) {
        this.module = module;
        this.width = 140;
        this.height = 21;
        initSetting();
    }

    @Override
    public float render(int x, int y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(25, 25, 25));
        drawString(module.getName(), x + 6, (int) getCenter(y, height, getStringHeight()), module.isToggled() ? module.getCategory().getColor() : new Color(230, 230, 230), false);
        if (isMouseHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(255, 255, 255, 40));
        }

        int finallyHeight = height;
        if (open) {
            for (Component comp : settings) {
                finallyHeight += comp.render(x, y + finallyHeight, mouseX, mouseY);
            }
        }

        return finallyHeight;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseHovering(mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.toggle();
            } else if (mouseButton == 1) {
                open = !open;
            }
        }

        if (open) execute(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (open) execute(c -> c.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    public void keyTyped(char typed, int key) {
        if (open) execute(p -> p.keyTyped(typed, key));
    }

    private void initSetting() {
        for (Setting setting : module.getSettings()) {
            Object value = setting.getValue();
            Color moduleColor = module.getCategory().getColor();
            if (value instanceof Boolean)
                settings.add(new BooleanButton(setting, moduleColor));
            if (value instanceof String)
                settings.add(new ModeButton(setting));
            if (value instanceof Integer)
                settings.add(new IntegerSlider(setting, moduleColor));
            if (value instanceof Float)
                settings.add(new FloatSlider(setting, moduleColor));
            if (value instanceof Color)
                settings.add(new ColorPicker(setting));
        }
        settings.add(new BindButton(module));
    }

    private void execute(Consumer<? super Component> action) {
        settings.forEach(action);
    }
}
