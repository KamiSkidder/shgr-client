package com.kamiskidder.shgr.manager;

import com.kamiskidder.shgr.ui.font.CFont;
import com.kamiskidder.shgr.ui.font.CFontRenderer;

import java.awt.*;

public class FontManager {
    public static CFontRenderer guiFont, sliderFont;
    //notification
    public static CFontRenderer notifTitleFont, notifMsgFont, notifCounterFont;
    //mainmenu
    public static CFontRenderer helvetica1, helvetica2, helvetica3, icon;

    public FontManager() {
        guiFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/SF-UI-Display-Medium.ttf", 19F, Font.PLAIN), true, false);
        sliderFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/SF-UI-Display-Medium.ttf", 15F, Font.PLAIN), true, false);

        notifTitleFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/SF-UI-Display-Medium.ttf", 21F, Font.PLAIN), true, false);
        notifMsgFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/SF-UI-Display-Medium.ttf", 18F, Font.PLAIN), true, false);
        notifCounterFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/SF-UI-Display-Medium.ttf", 16F, Font.PLAIN), true, false);

        helvetica1 = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/Helvetica.ttf", 50f, Font.PLAIN), true, false);
        helvetica2 = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/Helvetica.ttf", 19f, Font.PLAIN), true, false);
        helvetica3 = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/Helvetica.ttf", 16f, Font.PLAIN), true, false);

        icon = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/font/Icon.ttf", 19f, Font.PLAIN), true, false);
    }
}
