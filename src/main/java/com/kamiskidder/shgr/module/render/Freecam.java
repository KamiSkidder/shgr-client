package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.event.player.UpdateWalkingPlayerEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam extends Module {
    public static Freecam INSTANCE;
    public Setting<Float> speed = register(new Setting("Speed", 3.0F, 10.0F, 0.1F));
    public Setting<Float> ySpeed = register(new Setting("Y Speed", 3.0F, 10.0F, 0.1F));

    public Freecam() {
        super("Freecam", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }

        EntityPlayer player = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
        player.inventory = mc.player.inventory;
        player.rotationYaw = mc.player.rotationYaw;
        player.rotationPitch = mc.player.rotationPitch;
        mc.world.addEntityToWorld(-998, player);

        mc.player.noClip = true;
    }

    @Override
    public void onDisable() {
        if (nullCheck()) {
            disable();
            return;
        }

        mc.world.removeEntityFromWorld(-998);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        mc.player.setVelocity(0, 0, 0);
        if (PlayerUtil.isPlayerMoving()) {
            double[] motion = PlayerUtil.directionSpeed(speed.getValue());
            mc.player.motionX = motion[0];
            mc.player.motionZ = motion[1];
        }

        if (mc.player.movementInput.jump) {
            mc.player.motionY = ySpeed.getValue();
        }
        if (mc.player.movementInput.sneak) {
            mc.player.motionY = ySpeed.getValue() * -1;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook)
            event.cancel();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer)
            event.cancel();
    }
}
