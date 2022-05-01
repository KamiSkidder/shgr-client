package com.kamiskidder.shgr.util.player;

import com.kamiskidder.shgr.util.Util;
import net.minecraft.block.Block;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class InventoryUtil implements Util {
    public static int getBlockHotbar(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || (block = ((ItemBlock) stack.getItem()).getBlock()) != blockIn)
                continue;
            return i;
        }
        return -1;
    }

    public static int getBlockHotbar(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || (block = ((ItemBlock) stack.getItem()).getBlock()).getClass() != clazz)
                continue;
            return i;
        }
        return -1;
    }

    public static int getItemHotbar(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) != Item.getIdFromItem(input)) continue;
            return i;
        }
        return -1;
    }

    public static void moveItemTo(int item, int empty) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, item, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, empty, 0, ClickType.PICKUP, mc.player);
        //anti desync
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, item, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    public static void moveItem(int item) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, item, 0, ClickType.QUICK_MOVE, mc.player);
        mc.playerController.updateController();
    }

    public static void dropItem(int item) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, item, 0, ClickType.THROW, mc.player);
        mc.playerController.updateController();
    }
}
