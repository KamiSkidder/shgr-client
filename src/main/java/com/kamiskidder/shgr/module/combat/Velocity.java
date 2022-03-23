package com.kamiskidder.shgr.module.combat;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {
    public Setting<Float> x = register(new Setting("X", 0.0F, 1.0F, 0.0F));
    public Setting<Float> y = register(new Setting("Y", 0.0F, 1.0F, 0.0F));
    public Setting<Float> z = register(new Setting("Z", 0.0F, 1.0F, 0.0F));
    public Setting<Boolean> cancel = register(new Setting("Cancel", true));

    public Velocity() {
        super("Velocity", Category.COMBAT);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (nullCheck())
            return;

        Entity entity;
        SPacketEntityStatus packet;
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.entityId) {
                event.cancel();
                return;
            }
        }
        if (event.getPacket() instanceof SPacketEntityStatus
                && (packet = (SPacketEntityStatus) event.getPacket()).getOpCode() == 31
                && (entity = packet.getEntity(mc.world)) instanceof EntityFishHook) {
            EntityFishHook fishHook = (EntityFishHook) entity;
            if (fishHook.caughtEntity == mc.player) {
                event.cancel();
            }
        }
        if (event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion p = (SPacketExplosion) event.getPacket();
            p.motionX *= x.getValue();
            p.motionY *= y.getValue();
            p.motionZ *= z.getValue();
            if (cancel.getValue()) {
                event.cancel();
            }
        }
    }
}
