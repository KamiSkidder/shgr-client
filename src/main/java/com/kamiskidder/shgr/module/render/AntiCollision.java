package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;

public class AntiCollision extends Module {
    public static AntiCollision INSTANCE;
    public Setting<Integer> alpha = register(new Setting("Alpha", 50, 255, 10));

    public AntiCollision() {
        super("AntiCollision", Category.RENDER);

        INSTANCE = this;
    }
}
