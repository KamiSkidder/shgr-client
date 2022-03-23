package com.kamiskidder.shgr.util.other;

import com.kamiskidder.shgr.util.Util;
import net.minecraft.util.text.TextComponentString;

public class Base implements Util {
    public void sendMessage(String msg) {
        if (nullCheck()) return;
        mc.player.sendMessage(new TextComponentString(msg));
    }

    public boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }
}
