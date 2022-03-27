package com.kamiskidder.shgr.module.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.kamiskidder.shgr.event.client.KeyboardUpdateEvent;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TunnelAssist extends Module {
    private final List<Block> ignoreBlocks = Arrays.asList(Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON,
            Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.VINE);
    public Setting<Float> range = register(new Setting("Range", 3.0F, 10.0F, 1.0F));
    public Setting<Boolean> render = register(new Setting("Render", true));
    public Setting<String> mode = register(new Setting("Mode", "Fill", new String[]{"Fill", "Outline", "Both"}, v -> render.getValue()));
    public Setting<Color> color = register(new Setting("Color", new Color(230, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Outline") && render.getValue()));
    public Setting<Float> thickness = register(new Setting("Thickness", 1.5F, 5.0F, 0.1F, v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));
    public Setting<Color> outlineColor = register(new Setting("Outline Color", new Color(255, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));
    private BlockPos lastBreak;

    public TunnelAssist() {
        super("TunnelAssist", Category.MISC);
    }

    @Override
    public void onDisable() {
        RotateManager.reset();
    }

    @Override
    public void onRender3D() {
        if (nullCheck()) return;

        if (lastBreak != null && render.getValue()) {
            RenderUtil.drawBox(lastBreak, mode.getValue(), color.getValue(), outlineColor.getValue(), thickness.getValue());
        }
    }

    @SubscribeEvent
    public void onKeyboardUpdate(KeyboardUpdateEvent event) {
        if (nullCheck()) return;

        yawLock();
        lastBreak = null;
        BlockPos pos = PlayerUtil.getPlayerPos();
        for (int i = 0; i < range.getValue() + 1; i++) {
            Vec3i v = mc.player.getHorizontalFacing().getDirectionVec();
            Vec3d d = new Vec3d(v.getX(), v.getY(), v.getZ()).scale(i);
            BlockPos p = pos.add(d.x, d.y, d.z);
            if (!(BlockUtil.getBlock(p) instanceof BlockAir) && !(ignoreBlocks.contains(BlockUtil.getBlock(p)))) {
                mc.playerController.onPlayerDamageBlock(p, BlockUtil.getDirection(p));
                RotateManager.lookAtPos(p);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                lastBreak = p;
                return;
            }
            BlockPos p1 = p.add(0, 1, 0);
            if (!(BlockUtil.getBlock(p1) instanceof BlockAir) && !(ignoreBlocks.contains(BlockUtil.getBlock(p1)))) {
                mc.playerController.onPlayerDamageBlock(p1, BlockUtil.getDirection(p1));
                RotateManager.lookAtPos(p1);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                lastBreak = p1;
                return;
            }
        }
    }

    private void yawLock() {
        float yaw = MathHelper.wrapDegrees(mc.player.rotationYaw);
        List<Direction> l = new ArrayList();
        l.add(new Direction(0, yaw));
        l.add(new Direction(90, yaw));
        l.add(new Direction(180, yaw));
        l.add(new Direction(-90, yaw));
        l.add(new Direction(-180, yaw));
        Direction f = l.stream().min(Comparator.comparing(d -> d.diff)).orElse(null);
        if (f == null) {
            disable();
            return;
        }
        mc.player.rotationYaw = f.yaw;
    }

    private class Direction {
        public float yaw, diff;

        public Direction(int yaw, float playerYaw) {
            this.yaw = yaw;
            this.diff = Math.abs(yaw - playerYaw);
        }
    }
}
