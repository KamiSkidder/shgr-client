package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LagbackLogger extends Module {
    public LagbackLogger() {
        super("LagBack", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (nullCheck()) return;

        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            sendMessage("xyz : " + packet.x + "," + packet.y + "," + packet.z + "; rotate :" + packet.yaw + " - " + packet.pitch);
        }
    }
}
