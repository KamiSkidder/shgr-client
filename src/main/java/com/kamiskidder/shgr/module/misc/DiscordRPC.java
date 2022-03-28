package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.manager.RPCManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;

public class DiscordRPC extends Module {
    public static DiscordRPC INSTANCE;

    public DiscordRPC() {
        super("DiscordRPC", Category.MISC);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        RPCManager.INSTANCE.enable();
    }

    @Override
    public void onDisable() {
        RPCManager.INSTANCE.disable();
    }
}
