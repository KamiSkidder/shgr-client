package com.kamiskidder.shgr.module.movement;

import com.kamiskidder.shgr.event.player.UpdatePlayerEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerUpdate(UpdatePlayerEvent event) {
        mc.player.setSprinting(true);
    }
}
