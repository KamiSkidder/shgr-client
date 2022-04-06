package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;

public class AntiCollision extends Module {
    public static AntiCollision INSTANCE;

    public AntiCollision() {
        super("AntiCollision", Category.RENDER);
        INSTANCE = this;
    }
}
