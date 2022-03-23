package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;

public class FastUse extends Module {
    public FastUse() {
        super("FastUse", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        mc.rightClickDelayTimer = 0;

    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;
        mc.rightClickDelayTimer = 10;

    }
}
