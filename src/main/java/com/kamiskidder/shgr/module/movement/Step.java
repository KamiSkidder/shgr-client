package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;

public class Step extends Module {
    public Step() {
        super("Step", Category.MOVEMENT);
    }

    public Setting<Float> height = register(new Setting("heigth", 1.0F, 5.0F, 0.1F));

    @Override
    public void onTick() {
        if (mc.player == null) return;
        mc.player.stepHeight = height.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.5F;
    }

}