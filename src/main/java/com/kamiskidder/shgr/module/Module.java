package com.kamiskidder.shgr.module;

import com.kamiskidder.shgr.SHGR;
import com.kamiskidder.shgr.manager.NotificationManager;
import com.kamiskidder.shgr.module.render.Notification;
import com.kamiskidder.shgr.util.client.EventUtil;
import com.kamiskidder.shgr.util.other.Base;
import com.mojang.realmsclient.gui.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class Module extends Base {
    private final String name;
    private final Category category;
    private final List<Setting> settings = new ArrayList<>();
    private boolean toggled;
    private int bind = -1;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public Module(String name, boolean defualt, Category category) {
        this.name = name;
        this.toggled = defualt;
        this.category = category;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onUpdate() {
    }

    public void onRender2D() {
    }

    public void onRender3D() {
    }

    public void toggle() {
        if (toggled)
            disable();
        else
            enable();
    }

    public void enable() {
        if (!toggled) {
            EventUtil.register(this);
            toggled = true;
            sendToggleMessage(ChatFormatting.GRAY + name + " : " + ChatFormatting.GREEN + "Enabled");
            onEnable();
        }
    }

    public void disable() {
        if (toggled) {
            EventUtil.unregister(this);
            toggled = false;
            sendToggleMessage(ChatFormatting.GRAY + name + " : " + ChatFormatting.RED + "Disabled");
            onDisable();
        }
    }

    public boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

    public Setting register(Setting setting) {
        settings.add(setting);
        return setting;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public boolean isToggled() {
        return toggled;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    private void sendToggleMessage(String msg) {
        if (Notification.INSTANCE.isToggled() && Notification.INSTANCE.toggleMessage.getValue()) {
            if (Notification.INSTANCE.mode.getValue().equalsIgnoreCase("modern")) {
                NotificationManager.sendMessage("Module", msg);
            } else {
                super.sendMessage("[" + SHGR.DISPLAY_COLOR + SHGR.DISPLAY_NAME + ChatFormatting.RESET + "]"
                        + ChatFormatting.GRAY + " " + msg);
            }
        }
    }
}
