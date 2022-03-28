package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;

public class AntiCollision extends Module {
    public static AntiCollision INSTANCE;

    public AntiCollision() {
        super("AntiCollision", Category.RENDER);
        INSTANCE = this;
    }
}
