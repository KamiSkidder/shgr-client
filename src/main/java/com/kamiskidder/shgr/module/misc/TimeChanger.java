package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TimeChanger extends Module {
    public Setting<Integer> time = register(new Setting("Time", 0, 30000, 0));

    public TimeChanger() {
        super("TimeChanger", Category.MISC);
    }

    @Override
    public void onTick() {
        if (nullCheck())
            return;

        mc.world.setWorldTime(time.getValue());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            SPacketTimeUpdate packet = (SPacketTimeUpdate) event.getPacket();
            packet.worldTime = time.getValue();
            packet.totalWorldTime = time.getValue();
        }
    }
}
