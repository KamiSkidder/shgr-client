package com.kamiskidder.shgr.util.render;

import java.awt.*;

public class ColorUtil {
    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorUtil.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toRGBA(Color c) {
        return (c.getRed() << 16) + (c.getGreen() << 8) + c.getBlue() + (c.getAlpha() << 24);
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return ColorUtil.toRGBA((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f), (int) (a * 255.0f));
    }

    public static Color getColor(int hex) {
        return new Color(hex);
    }

    public static int getRed(int hex) {
        return hex >> 16 & 255;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 255;
    }

    public static int getBlue(int hex) {
        return hex & 255;
    }

    public static int getHoovered(int color, boolean isHoovered) {
        return isHoovered ? (color & 0x7F7F7F) << 1 : color;
    }

    public static Color dynamicRainbow(long offset, float brightness, int speed) {
        float hue = (float) (System.nanoTime() + offset * speed) / 1.0E10f % 1.0f;
        int color = (int) Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, brightness, 1.0f)), 16);
        return new Color(new Color(color).getRed() / 255.0f, new Color(color).getGreen() / 255.0f, new Color(color).getBlue() / 255.0f, new Color(color).getAlpha() / 255.0f);
    }

    public static int getRainbow(int speed, int offset, float s, float b) {
        float hue = (System.currentTimeMillis() + (long) offset) % (long) speed;
        return Color.getHSBColor(hue /= (float) speed, s, b).getRGB();
    }
}
