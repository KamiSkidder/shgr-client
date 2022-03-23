package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.ui.clickgui.AtopiXGui;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }

        if (mc.currentScreen == null)
            mc.displayGuiScreen(new AtopiXGui());
    }

    @Override
    public void onTick() {
        if (!(mc.currentScreen instanceof AtopiXGui))
            disable();
    }
}
