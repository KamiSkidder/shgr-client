package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (nullCheck()) return;

        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    }
}
