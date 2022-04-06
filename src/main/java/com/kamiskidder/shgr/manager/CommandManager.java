package com.kamiskidder.shgr.manager;

import java.util.ArrayList;
import java.util.List;

import com.kamiskidder.shgr.command.Command;
import com.kamiskidder.shgr.command.commands.BindCommand;
import com.kamiskidder.shgr.command.commands.FriendCommand;
import com.kamiskidder.shgr.command.commands.TargetCommand;
import com.kamiskidder.shgr.util.client.EventUtil;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommandManager {
    public static String prefix = "-";
    public List<Command> commands = new ArrayList<Command>();

    public CommandManager() {
        commands.add(new BindCommand());
        commands.add(new FriendCommand());
        commands.add(new TargetCommand());

        EventUtil.register(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        String msg = event.getMessage();
        if (!msg.isEmpty() && msg.startsWith(prefix)) {
            String[] args = msg.substring(1).split(" ");
            if (args.length == 0) return;
            commands.forEach(c -> c.execute(args));
            
            Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(msg);
            event.setCanceled(true);
        }
    }
}
