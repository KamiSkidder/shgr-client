package com.kamiskidder.shgr.ui.notification;

import com.kamiskidder.shgr.manager.FontManager;
import com.kamiskidder.shgr.util.render.ColorUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;

import java.awt.*;

public class NotificationRenderer {
    public static final int HEIGHT = 40;

    public static void renderNotification(String title, String msg, float x, float y, int more) {
        int width = FontManager.notifMsgFont.getStringWidth(msg) + 70;
        RenderUtil.drawRect(x, y, x + width, y + HEIGHT, new Color(0, 0, 0, 140));
        RenderUtil.drawRect(x, y, x + 2, y + HEIGHT, new Color(240, 240, 240));
        FontManager.notifTitleFont.drawString(title, x + 15, y + 9, ColorUtil.toRGBA(240, 240, 240));
        FontManager.notifMsgFont.drawString(msg, x + 15, y + HEIGHT - FontManager.notifMsgFont.getHeight() - 8, ColorUtil.toRGBA(240, 240, 240));
        if (more > 0) {
            FontManager.notifCounterFont.drawString(more + " more", x + width - FontManager.notifCounterFont.getStringWidth(more + " more") - 7, y + 10, ColorUtil.toRGBA(100, 100, 100));
        }
    }

    public static float getNotificationWidth(String msg) {
        return FontManager.notifMsgFont.getStringWidth(msg) + 70;
    }
}
