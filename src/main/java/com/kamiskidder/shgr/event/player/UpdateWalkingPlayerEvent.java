package com.kamiskidder.shgr.event.player;

import com.kamiskidder.shgr.event.SHGREvent;

public class UpdateWalkingPlayerEvent extends SHGREvent {
	public boolean isPre = false;
	
	public UpdateWalkingPlayerEvent(boolean pre) {
		this.isPre = pre;
	}
}
