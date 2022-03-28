package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.player.UpdateWalkingPlayerEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Step extends Module {
    public Setting<String> mode = register(new Setting("Mode", "Vanilla", new String[]{"Vanilla", "NCP"}));
    public Setting<Float> height = register(new Setting("Heigth", 1.0F, 5.0F, 0.1F));

    public Step() {
        super("Step", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        if (mc.player == null) return;

        if (event.isPre) {
            if (mode.getValue().equalsIgnoreCase("Vanilla")) {
                mc.player.stepHeight = height.getValue();
            } else {
                if (mc.player.collidedHorizontally && PlayerUtil.isPlayerMoving()) {
                    double x = mc.player.posX;
                    double y = mc.player.posY;
                    double z = mc.player.posZ;

                    mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + 0.41999998688698, z, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + 0.7531999805212, z, mc.player.onGround));
                    mc.player.setPosition(x, y + 1.00, z);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.5F;
    }

}