package com.kamiskidder.shgr.ui.hud.component;

import com.kamiskidder.shgr.manager.FontManager;
import com.kamiskidder.shgr.ui.hud.Hud;
import com.kamiskidder.shgr.util.render.ColorUtil;

public class TestHud extends Hud {
    public TestHud() {
        super("Test");
    }

    @Override
    public void onRenderHud() {
        FontManager.notifMsgFont.drawStringWithShadow("Hud System!", getXPos(), getYPos(), ColorUtil.toRGBA(255, 255, 255));
        width = FontManager.notifMsgFont.getStringWidth("Hud System!");
        height = FontManager.notifMsgFont.getHeight();
    }
}
