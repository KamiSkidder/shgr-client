package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.player.UpdateWalkingPlayerEvent;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.client.Timer;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Scaffold extends Module {
    private final Timer timer = new Timer();
    private final int crying = 0;
    public Setting<Boolean> render = register(new Setting("Render", true));
    public Setting<String> mode = register(new Setting("Mode", "Fill", new String[]{"Fill", "Outline", "Both"}, v -> render.getValue()));
    public Setting<Color> color = register(new Setting("Color", new Color(230, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Outline") && render.getValue()));
    public Setting<Float> thickness = register(new Setting("Thickness", 1.5F, 5.0F, 0.1F, v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));
    public Setting<Color> outlineColor = register(new Setting("Outline Color", new Color(255, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Fill") && render.getValue()));
    private BlockPos placePos = null;
    private boolean reset = false;

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        if (nullCheck()) return;

        if (timer.passedD(100) && placePos == null && !reset) {
            RotateManager.reset();
            reset = true;
        }

        if (event.isPre) {
            BlockPos feet = PlayerUtil.getPlayerPos().down();
            if (BlockUtil.getBlock(feet) instanceof BlockAir) {
                EnumFacing dire = BlockUtil.getPlaceableSide(feet);
                if (dire == null) {
                    BlockPos fuck = null;
                    for (EnumFacing facing : EnumFacing.values()) {
                        BlockPos pos = feet.add(facing.getDirectionVec());
                        EnumFacing side = BlockUtil.getPlaceableSide(pos);
                        if (side != null) {
                            fuck = pos;
                        }
                    }
                    if (fuck == null)
                        return;
                    placePos = fuck;
                    RotateManager.lookAtPos(fuck.add(BlockUtil.getPlaceableSide(fuck).getDirectionVec()));
                    reset = false;
                } else {
                    placePos = feet;
                    RotateManager.lookAtPos(feet.add(dire.getDirectionVec()));
                    reset = false;
                }

                if ((int) mc.player.posY > (int) mc.player.lastTickPosY && !PlayerUtil.isPlayerMoving()) {
                    mc.player.setPosition(mc.player.posX, (int) mc.player.posY, mc.player.posZ);
                    mc.player.motionY = 0;
                    if (mc.player.movementInput.jump)
                        mc.player.jump();
                }
            }
        } else {
            if (placePos != null) {
                int slot = -1;
                if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
                    for (int i = 0; i < 9; i++) {
                        Item item;
                        if ((item = mc.player.inventory.getStackInSlot(i).getItem()) instanceof ItemBlock
                                && !BlockUtil.blackList.contains(((ItemBlock) item).getBlock())) {
                            slot = mc.player.inventory.currentItem;
                            mc.player.inventory.currentItem = i;
                            mc.playerController.updateController();
                            break;
                        }
                    }

                    if (slot == -1) return;
                }

                BlockUtil.placeBlock(placePos, false);
                placePos = null;
                timer.reset();

                if (slot != -1) {
                    mc.player.inventory.currentItem = slot;
                    mc.playerController.updateController();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        RotateManager.reset();
    }

    @Override
    public void onRender3D() {
        if (nullCheck()) return;

        if (placePos != null && render.getValue()) {
            RenderUtil.drawBox(placePos, mode.getValue(), color.getValue(), outlineColor.getValue(), thickness.getValue());
        }
    }
}
