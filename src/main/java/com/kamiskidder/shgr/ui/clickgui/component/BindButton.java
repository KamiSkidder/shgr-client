package com.kamiskidder.shgr.ui.clickgui.component;

import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.ui.clickgui.Component;
import com.kamiskidder.shgr.util.render.RenderUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class BindButton extends Component {
    private final Module module;
    private boolean waiting;

    public BindButton(Module module) {
        this.module = module;
        this.width = 140;
        this.height = 21;
    }

    @Override
    public float render(int x, int y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(17, 17, 17));
        if (waiting) {
            drawString("Listening...", x + 8, (int) getCenter(y, height, getStringHeight()), new Color(230, 230, 230), false);
        } else {
            drawString("Bind", x + 8, (int) getCenter(y, height, getStringHeight()), new Color(230, 230, 230), false);
            String str;
            if (module.getBind() != -1) {
                str = Keyboard.getKeyName(module.getBind());
            } else {
                str = "NONE";
            }
            drawString(str, x + width - (int) getStringWidth(str) - 6, (int) getCenter(y, height, getStringHeight()), new Color(230, 230, 230), false);
        }
        if (isMouseHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(255, 255, 255, 40));
        }

        return this.height;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseHovering(mouseX, mouseY) && button == 0) {
            waiting = !waiting;
        }
    }

    @Override
    public void keyTyped(char typed, int key) {
        if (waiting) {
            if (key == Keyboard.KEY_BACK) {
                module.setBind(-1);
            } else {
                module.setBind(key);
            }
            waiting = false;
        }
    }
}
