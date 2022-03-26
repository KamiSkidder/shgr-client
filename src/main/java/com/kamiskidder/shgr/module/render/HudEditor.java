package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;

public class HudEditor extends Module {
    public static HudEditor INSTANCE;

    public HudEditor() {
        super("HudEditor", Category.RENDER);
        INSTANCE = this;
    }
}
