package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;

public class PhaseWalk extends Module {
    public static PhaseWalk INSTANCE;

    public Setting<Boolean> onlyPhasing = register(new Setting("OnlyPhasing", true));

    public PhaseWalk() {
        super("PhaseWalk", Category.MOVEMENT);

        INSTANCE = this;
    }
}
