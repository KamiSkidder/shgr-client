package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;

public class NoPush extends Module {
    public static NoPush INSTANCE;

    public NoPush() {
        super("NoPush", Category.MOVEMENT);
        INSTANCE = this;
    }
}
