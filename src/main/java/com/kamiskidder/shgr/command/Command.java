package com.kamiskidder.shgr.command;

import com.kamiskidder.shgr.SHGR;
import com.kamiskidder.shgr.manager.CommandManager;
import com.kamiskidder.shgr.util.other.Base;
import com.mojang.realmsclient.gui.ChatFormatting;

public class Command extends Base {
    private final String name;

    public Command(String name) {
        this.name = name;
    }

    public void execute(String[] args) {
    }

    public String getUsage() {
        return "";
    }

    public void sendUsage() {
        super.sendMessage("[" + SHGR.DISPLAY_COLOR + SHGR.DISPLAY_NAME + ChatFormatting.RESET + "] Usage : " + ChatFormatting.GRAY + CommandManager.prefix + getUsage());
    }

    @Override
    public void sendMessage(String msg) {
        super.sendMessage("[" + SHGR.DISPLAY_COLOR + SHGR.DISPLAY_NAME + ChatFormatting.RESET + "] " + ChatFormatting.GRAY + msg);
    }

    public void sendErrorMessage(String msg) {
        super.sendMessage("[" + SHGR.DISPLAY_COLOR + SHGR.DISPLAY_NAME + ChatFormatting.RESET + "] <" + ChatFormatting.RED + "ERROR" + ChatFormatting.RESET + "> " + ChatFormatting.GRAY + msg);
    }
}
