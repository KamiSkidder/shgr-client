package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.event.client.KeyboardUpdateEvent;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Breaker extends Module {
    public Setting<Float> range = register(new Setting("Range", 5.0f, 15.0f, 1.0f));
    public Setting<Float> wallRange = register(new Setting("Wall Range", 3.0f, 15.0f, 1.0f));
    public Setting<Boolean> spawner = register(new Setting("Spawner", false));
    public Setting<Boolean> wood = register(new Setting("Wood", false));
    public Setting<Boolean> autoSwitch = register(new Setting("AutoSwitch", true));
    public Setting<Boolean> render = register(new Setting("Render", true));
    public Setting<String> mode = register(new Setting("Mode", "Fill", new String[]{"Fill", "Outline", "Both"}, v -> render.getValue()));
    public Setting<Color> color = register(new Setting("Color", new Color(230, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Outline") && render.getValue()));
    public Setting<Float> thickness = register(new Setting("Thickness", 1.5F, 5.0F, 0.1F, v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));
    public Setting<Color> outlineColor = register(new Setting("Outline Color", new Color(255, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));

    private BlockPos target = null;
    private Block oldBlock = null;

    public Breaker() {
        super("Breaker", Category.MISC);
    }

    @Override
    public void onEnable() {
        target = null;
        oldBlock = null;
    }

    @SubscribeEvent
    public void onKeyboardUpdate(KeyboardUpdateEvent event) {
        if (nullCheck())
            return;

        if (target == null) {
            List<BlockPos> positions = new ArrayList<>();
            for (BlockPos pos : BlockUtil.getSphere(PlayerUtil.getPlayerPos(), range.getValue(),
                    range.getValue().intValue(), false, true, 0)) {
                if (!BlockUtil.canSeePos(pos) && PlayerUtil.getDistance(pos) > wallRange.getValue())
                    continue;

                Block b = BlockUtil.getBlock(pos);

                if (wood.getValue() && b.getRegistryName().toString().contains("log")) {
                    positions.add(pos);
                    oldBlock = b;
                }

                if (spawner.getValue() && (b = BlockUtil.getBlock(pos)) instanceof BlockMobSpawner) {
                    positions.add(pos);
                    oldBlock = b;
                }
            }
            target = positions.stream().min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        }

        if (target != null) {
            if (BlockUtil.getBlock(target) != oldBlock) {
                target = null;
                return;
            }
            switchSlot();
            RotateManager.lookAtPos(target);
            mc.playerController.onPlayerDamageBlock(target, BlockUtil.getDirection(target));
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    @Override
    public void onDisable() {
        RotateManager.reset();
    }

    @Override
    public void onRender3D() {
        if (nullCheck()) return;

        if (target != null) {
            RenderUtil.drawBox(target, mode.getValue(), color.getValue(), outlineColor.getValue(), thickness.getValue());
        }
    }

    private void switchSlot() {
        if (autoSwitch.getValue()) {
            IBlockState b = BlockUtil.getBlockState(target);
            double maxDamage = 1.0;
            int best = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack item = mc.player.inventory.getStackInSlot(i);
                double dmg = item.getDestroySpeed(b);
                if (maxDamage < dmg) {
                    best = i;
                    maxDamage = dmg;
                }
            }
            if (best == -1)
                return;

            if (mc.player.inventory.currentItem != best) {
                mc.player.inventory.currentItem = best;
                mc.playerController.updateController();
            }
        }
    }
}
