package com.kamiskidder.shgr.command.commands;

import com.kamiskidder.shgr.command.Command;
import com.kamiskidder.shgr.module.combat.AutoGaiji;
import com.kamiskidder.shgr.util.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;

public class TargetCommand extends Command {
    public TargetCommand() {
        super("Target");
    }

    @Override
    public void execute(String[] args) {
        if (args[0].equalsIgnoreCase("target")) {
            if (args.length != 2) {
                sendUsage();
                return;
            }

            EntityPlayer player = EntityUtil.getPlayerByName(args[1]);
            if (player == null) {
                sendMessage("Player not found!");
                return;
            }

            AutoGaiji.INSTANCE.target = player;
            sendMessage("Target set to " + player.getName());
        }
    }

    @Override
    public String getUsage() {
        return "target <player>";
    }
}
