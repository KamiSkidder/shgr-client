package com.kamiskidder.shgr.module.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.kamiskidder.shgr.SHGR;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;

import java.util.Objects;

public class DiscordRpc extends Module {
    public DiscordRpc() {
        super("DiscordRPC", Category.MISC);
    }

    private Thread _thread = null;

    @Override
    public void onEnable() {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "957582451776061491";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        lib.Discord_UpdatePresence(presence);
        presence.largeImageText = "";
        _thread = new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                presence.details = "Enjoy Shgr";
                presence.state = getDetails();
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
        DiscordRPC.INSTANCE.Discord_Shutdown();
        _thread = null;
    }

    public String getDetails() {
        return Objects.isNull(mc.player) ? "MainMenu" : mc.player.getName();
    }

    public String getState() {
        return Objects.isNull(mc.player) ? "" : Objects.isNull(mc.getCurrentServerData()) ? "SinglePlayer" : mc.getCurrentServerData().serverIP;
    }
}

