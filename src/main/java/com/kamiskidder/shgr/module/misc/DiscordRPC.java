package com.kamiskidder.shgr.module.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.kamiskidder.shgr.SHGR;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;

import java.util.Objects;

public class DiscordRPC extends Module {
    private Thread _thread = null;

    public DiscordRPC() {
        super("DiscordRPC", Category.MISC);
    }

    @Override
    public void onEnable() {
        club.minnced.discord.rpc.DiscordRPC lib = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
        String applicationId = "957582451776061491";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        lib.Discord_Initialize(applicationId, handlers, true, "");
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        lib.Discord_UpdatePresence(presence);
        presence.largeImageText = "";
        _thread = new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                presence.details = "Enjoying SHGR Client";
                presence.state = "discord.gg/BVYNJNdbbk";
                //presence.state = getDetails();
                presence.largeImageKey = "logo";
                presence.largeImageText = SHGR.MOD_VERSION;
                lib.Discord_UpdatePresence(presence);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler");

        _thread.start();
    }

    @Override
    public void onDisable() {
        club.minnced.discord.rpc.DiscordRPC.INSTANCE.Discord_Shutdown();
        _thread = null;
    }

    public String getDetails() {
        return Objects.isNull(mc.player) ? "MainMenu" : mc.player.getName();
    }

    public String getState() {
        return Objects.isNull(mc.player) ? "" : Objects.isNull(mc.getCurrentServerData()) ? "SinglePlayer" : mc.getCurrentServerData().serverIP;
    }
}

