package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.event.player.PlayerTravelEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoatFly extends Module {
    public Setting<String> type = register(new Setting("Type", new String[]{"Normal", "Teleport"}));
    public Setting<Float> speed = register(new Setting("Speed", 2.0F, 10.0F, 0.1F));
    public Setting<Float> ySpeed = register(new Setting("Y Speed", 1.0F, 10.0F, 0.1F));
    public Setting<Float> glide = register(new Setting("Glide", 0.0F, 3.0F, 0.0F));

    public BoatFly() {
        super("BoatFly", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTravel(PlayerTravelEvent event) {
        if (nullCheck())
            return;

        if (mc.player.isRiding() && mc.player.ridingEntity instanceof EntityBoat) {
            double x = 0;
            double y = 0;
            double z = 0;

            Entity riding = mc.player.ridingEntity;
            riding.rotationYaw = mc.player.rotationYaw;
            if (PlayerUtil.isPlayerMoving()) {
                double[] motion = PlayerUtil.directionSpeed(speed.getValue());
                x = motion[0];
                z = motion[1];
            } else {
                x = 0;
                y = 0;
            }

            y = 0;
            if (mc.player.movementInput.jump) {
                y = ySpeed.getValue();
            }
            if (mc.player.movementInput.sneak) {
                y = ySpeed.getValue() * -1;
            }

            riding.setVelocity(x, y, z);
            event.cancel();

            CPacketVehicleMove packet = new CPacketVehicleMove();
            packet.x = riding.posX;
            packet.y = -256;
            packet.z = riding.posZ;
            mc.player.connection.sendPacket(packet);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (nullCheck())
            return;

        if ((event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketInput)
                && mc.player.isRiding()) {
            event.cancel();
        }
    }

    @SubscribeEvent
    public void onPacketSent(PacketEvent.Sent event) {
        if (nullCheck())
            return;

        /*
        if (event.getPacket() instanceof CPacketVehicleMove && mc.player.isRiding() && mc.player.ticksExisted % 2 == 0) {
            mc.player.connection.sendPacket(new CPacketUseEntity(mc.player.getRidingEntity()));
        }
        */

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (nullCheck())
            return;

        if (event.getPacket() instanceof SPacketMoveVehicle && mc.player.isRiding()) {
            event.cancel();
        }

        if (event.getPacket() instanceof SPacketPlayerPosLook && mc.player.isRiding()) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
            packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.teleportId));
            event.cancel();
        }

    }
}
