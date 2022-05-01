package com.kamiskidder.shgr.module.combat;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.client.Timer;
import com.kamiskidder.shgr.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {
    public Setting<Float> delay = register(new Setting("Delay", 0.0f, 10.0f, 0.0f));

    private Timer timer = new Timer();

    public AutoTotem() {
        super("AutoTotem", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (shouldTotem() && timer.passedD(delay.getValue())) {
            timer.reset();
            int totem = findTotemSlot();
            if (totem == -1) return;
            InventoryUtil.moveItemTo(totem, 45);
        }
    }

    private boolean shouldTotem() {
        return !(mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) && mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING;
    }

    public int findTotemSlot() {
        int i, n;
        for (i = (n = 44); n >= 9; n = --i) {
            if (i == 45)
                continue;
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }

        return -1;
    }
}
