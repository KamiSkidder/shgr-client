package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.player.UpdateWalkingPlayerEvent;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.util.client.Timer;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;

import net.minecraft.block.BlockAir;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold extends Module {
	public Scaffold() {
		super("Scaffold", Category.MOVEMENT);
	}

	private BlockPos placePos = null;
	private Timer timer = new Timer();
	private int crying = 0;
	
	@SubscribeEvent
	public void onUpdate(UpdateWalkingPlayerEvent event) {
		if (timer.passedD(150) && placePos == null) {
			RotateManager.reset();
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
				} else {
					placePos = feet;
					RotateManager.lookAtPos(feet.add(dire.getDirectionVec()));	
				}

				
				if (mc.player.movementInput.jump && !PlayerUtil.isPlayerMoving()) {
					if (crying > 2) {
						mc.player.motionY = 0.399;
						crying-=1;
					} else {
						mc.player.motionY = -0.5;
						crying+=1;
					}
				}
			}
		} else {
			if (placePos != null) {
				BlockUtil.placeBlock(placePos, false);
				placePos = null;
				timer.reset();
			}
		}
	}
}
