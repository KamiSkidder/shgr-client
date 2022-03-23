package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.player.UpdatePlayerMoveStateEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdatePlayerMoveState(UpdatePlayerMoveStateEvent event) {
        mc.player.movementInput.moveForward++;
    }
}
