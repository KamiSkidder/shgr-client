package com.kamiskidder.shgr.manager;

import com.kamiskidder.shgr.SHGR;
import com.kamiskidder.shgr.util.Util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class RPCManager implements Util {
    public static RPCManager INSTANCE;

    private Thread thread = null;

    public RPCManager() {
        INSTANCE = this;
    }

    public void enable() {
        thread = new Thread(this::runRPC);
        thread.start();
    }

    public void disable() {
    	thread.interrupt();
        thread = null;
    }

    private void runRPC() {
        DiscordRPC rpc = DiscordRPC.INSTANCE;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.errored = (errorCode, errorMessage) -> {
            System.err.println("Error: " + errorMessage);
        };
        rpc.Discord_Initialize("957585998949269544", handlers, true, "");
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details = "Playing on SHGR";
        presence.largeImageKey = "inu";
        presence.largeImageText = String.format("v%s", SHGR.MOD_VERSION);
        while (!Thread.currentThread().isInterrupted()) {
            try {
            	if (mc.world != null) {
            		if (mc.world.isRemote) {
            			presence.details = mc.getCurrentServerData().serverIP;
            		}
            	} else {
            		presence.details = "Main Menu";
            	}
            	
                rpc.Discord_RunCallbacks();
                rpc.Discord_UpdatePresence(presence);

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
