package com.kamiskidder.shgr.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SHGREvent extends Event {
    public boolean canceled = false;

    public void cancel() {
        this.canceled = true;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

}
