package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.player.PlayerTravelEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.entity.EntityType;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntitySpeed extends Module {
    public Setting<Float> speed = register(new Setting("Speed", 1.0f, 3.0f, 0.1f));
    public Setting<Float> jump = register(new Setting("Jump", 1.0f, 3.0f, 0.1f));

    public EntitySpeed() {
        super("EntitySpeed", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTravel(PlayerTravelEvent event) {
        if (nullCheck())
            return;

        if (mc.player.isRiding() && EntityType.isNeutral(mc.player.ridingEntity)) {
            double x = 0;
            double y = mc.player.ridingEntity.motionY;
            double z = 0;

            mc.player.ridingEntity.rotationYaw = mc.player.rotationYaw;
            if (PlayerUtil.isPlayerMoving()) {
                double[] dir = PlayerUtil.directionSpeed(speed.getValue());
                x = dir[0];
                z = dir[1];
            }

            if (mc.player.ridingEntity.onGround && mc.player.movementInput.jump) {
                y = jump.getValue();
            }

            mc.player.ridingEntity.setVelocity(x, y, z);
        }
    }
}
