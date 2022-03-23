package com.kamiskidder.shgr.event.player;

import com.kamiskidder.shgr.event.SHGREvent;

public class PlayerTravelEvent extends SHGREvent {
    public float strafe, vertical, forward;

    public PlayerTravelEvent(float strafe, float vertical, float forward) {
        this.strafe = strafe;
        this.vertical = vertical;
        this.forward = forward;
    }
}
