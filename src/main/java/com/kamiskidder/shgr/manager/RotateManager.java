package com.kamiskidder.shgr.manager;

import com.kamiskidder.shgr.util.Util;
import com.kamiskidder.shgr.util.client.EventUtil;
import com.kamiskidder.shgr.util.client.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RotateManager implements Util {
    private static float yaw, pitch;
    private static boolean rotating = false;

    public RotateManager() {
        EventUtil.register(this);
    }

    public static void reset() {
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
        rotating = false;
    }

    public static void setRotation(float yaw, float pitch) {
        RotateManager.yaw = yaw;
        RotateManager.pitch = pitch;
        rotating = true;
    }

    public static void lookAtPos(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
        setRotation(angle[0], angle[1]);
    }

    public static void lookAtPos(Vec3d pos) {
        float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), pos);
        setRotation(angle[0], angle[1]);
    }

    public static void lookAtEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        setRotation(angle[0], angle[1]);
    }

    public static boolean isRotating() {
        return rotating;
    }

    public static float getYaw() {
        return yaw;
    }

    public static float getPitch() {
        return pitch;
    }
}
