package com.kamiskidder.shgr.util.player;

import com.kamiskidder.shgr.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class PlayerUtil implements Util {
    public static boolean canSeeEntity(Entity e) {
        return mc.player.canEntityBeSeen(e);
    }

    public static double getDistance(Entity e) {
        return mc.player.getDistance(e);
    }

    public static double getDistance(BlockPos e) {
        return mc.player.getDistance(e.getX(), e.getY(), e.getZ());
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(mc.player);
    }

    public static double[] directionSpeed(final double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static boolean isPlayerMoving() {
        return mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f;
    }
}
