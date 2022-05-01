package com.kamiskidder.shgr.module.combat;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.entity.EntityUtil;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Comparator;

public class BurrowBreaker extends Module {
    public Setting<String> mode = register(new Setting("Mode", "Vanilla", new String[] { "Vanilla" , "packet"}));
    private Setting<Float> range = register(new Setting("Range", 5.0f, 10.0f, 0.5f));
    public Setting<Boolean> autoSwitch = register(new Setting("Auto Switch", true));
    public Setting<Boolean> swingArm = register(new Setting("Swing Arm", true));
    public Setting<Boolean> render = register(new Setting("Render", true));
    public Setting<String> renderMode = register(new Setting("Render Mode", "Fill", new String[]{"Fill", "Outline", "Both"}, v -> render.getValue()));
    public Setting<Color> color = register(new Setting("Color", new Color(230, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Outline") && render.getValue()));
    public Setting<Float> thickness = register(new Setting("Thickness", 1.5F, 5.0F, 0.1F, v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));
    public Setting<Color> outlineColor = register(new Setting("Outline Color", new Color(255, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));

    private EntityPlayer target = null;
    private boolean attacked = false;

    public BurrowBreaker() {
        super("BurrowBreaker", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        if (target == null) {
            target = EntityUtil.getPlayers().stream()
                    .filter(p -> BlockUtil.isBlockBreakable(EntityUtil.getEntityPos(p)) && PlayerUtil.getDistance(p) <= range.getValue()).min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
            if (target == null) {
                sendMessage("Cannot find target! disabling...");
                disable();
                return;
            }
        }

        BlockPos pos = EntityUtil.getEntityPos(target);

        if (!BlockUtil.isBlockBreakable(pos)) {
            disable();
            return;
        }

        if (autoSwitch.getValue()) {
            switchSlot();
        }

        if (mode.getValue().equalsIgnoreCase("vanilla")) {
            mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
            if (swingArm.getValue())
                mc.player.swingArm(EnumHand.MAIN_HAND);
        } else {
            if (!attacked) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
                attacked = true;
            }
        }
    }

    @Override
    public void onRender3D() {
        if (nullCheck()) return;

        if (target != null && render.getValue()) {
            RenderUtil.drawBox(EntityUtil.getEntityPos(target), renderMode.getValue(), color.getValue(), outlineColor.getValue(), thickness.getValue());
        }
    }

    private void switchSlot() {
        if (autoSwitch.getValue()) {
            IBlockState b = BlockUtil.getBlockState(EntityUtil.getEntityPos(target));
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
