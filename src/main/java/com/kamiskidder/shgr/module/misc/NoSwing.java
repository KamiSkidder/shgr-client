package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSwing extends Module {
    public NoSwing() {
        super("NoSwing", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketAnimation) e.setCanceled(true);
    }
}