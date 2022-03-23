package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;

public class NoSlow extends Module {
    public static NoSlow INSTANCE;
    public Setting<Boolean> item = register(new Setting("Item", true));

    public NoSlow() {
        super("NoSlow", Category.MOVEMENT);
        INSTANCE = this;
    }
}
