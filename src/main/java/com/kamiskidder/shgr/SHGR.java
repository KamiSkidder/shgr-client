package com.kamiskidder.shgr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import com.kamiskidder.shgr.manager.CommandManager;
import com.kamiskidder.shgr.manager.ConfigManager;
import com.kamiskidder.shgr.manager.FontManager;
import com.kamiskidder.shgr.manager.FriendManager;
import com.kamiskidder.shgr.manager.MainMenuManager;
import com.kamiskidder.shgr.manager.ModuleManager;
import com.kamiskidder.shgr.manager.NotificationManager;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.util.client.EventUtil;
import com.kamiskidder.shgr.util.client.LogUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = SHGR.MOD_ID, name = SHGR.MOD_NAME, version = SHGR.MOD_VERSION)
public class SHGR {
    public static final String MOD_ID = "shgr";
    public static final String MOD_NAME = "SHGR Client";
    public static final String MOD_VERSION = "0.1";

    public static final String DISPLAY_NAME = "SHGR";
    public static final ChatFormatting DISPLAY_COLOR = ChatFormatting.LIGHT_PURPLE;

    //Name of directory in which to save config
    public static final String DIR_NAME = "shgr";

    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static FontManager fontManager;
    public static RotateManager rotateManager;
    public static NotificationManager notificationManager;
    public static MainMenuManager mainMenuManager;

    public static Logger logger = LogManager.getLogger(MOD_ID);

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        LogUtil.info("Starting " + MOD_NAME + " Client...");
        Display.setTitle(DISPLAY_NAME + " " + MOD_VERSION);
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        fontManager = new FontManager();
        rotateManager = new RotateManager();
        notificationManager = new NotificationManager();
        mainMenuManager = new MainMenuManager();
        ConfigManager.load();
        FriendManager.load();
        //mainMenuManager.init();
        LogUtil.info(MOD_NAME + "Client started!");

        EventUtil.register(this);
    }

    /*
    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu) {
            event.setGui(new GuiCustomMainMenu());
        }
    }
    */
}
