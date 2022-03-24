package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;

public class Timer extends Module {
    public Setting<Float> timer = register(new Setting("Timer", 1.0f, 10.0f, 0.1f));

    public Timer() {
        super("Timer", Category.MISC);
    }

    @Override
    public void onTick() {
        mc.timer.tickLength = 50 / timer.getValue();
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50;
    }
}
