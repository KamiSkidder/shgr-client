package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatSuffix extends Module {
    public ChatSuffix() {
        super("ChatSuffix", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            s += " | S1gur3_g4m3s";
            if (s.length() >= 256) s = s.substring(0, 256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    }
}
