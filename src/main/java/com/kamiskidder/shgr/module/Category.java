package com.kamiskidder.shgr.module;

import java.awt.*;

public enum Category {
    COMBAT(new Color(250, 60, 40)),
    EXPLOIT(new Color(70, 80, 180)),
    MOVEMENT(new Color(70, 170, 80)),
    MISC(new Color(220, 220, 80)),
    RENDER(new Color(170, 80, 170)),
    HUD(new Color(170, 80, 170));

    Color color;

    Category(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}
