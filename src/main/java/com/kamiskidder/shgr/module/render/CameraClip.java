package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;

public class CameraClip extends Module {
    public static CameraClip INSTANCE;
    public Setting<Boolean> extend = this.register(new Setting("Extend", false));
    public Setting<Float> distance = this.register(new Setting("Distance", 10.0F, 50.0F, 0.0F, v -> this.extend.getValue()));

    public CameraClip() {
        super("CameraClip", Category.RENDER);
        INSTANCE = this;
    }
}
