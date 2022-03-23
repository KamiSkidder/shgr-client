package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.render.RenderUtil;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class StorageESP extends Module {
    public Setting<String> mode = register(new Setting("Mode", "Fill", new String[]{"Fill", "Outline", "Both"}));
    public Setting<Float> thickness = register(new Setting("Thickness", 1.5F, 5.0F, 0.1F, v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("outline")));
    public Setting<Boolean> chest = register(new Setting("Chest", true));
    public Setting<Color> chestColor = register(new Setting("Chest Color", new Color(200, 200, 90, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("fill")));
    public Setting<Color> chestOutline = register(new Setting("Chest Outline", new Color(200, 200, 140, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("outline")));
    public Setting<Boolean> enderChest = register(new Setting("EnderChest", true));
    public Setting<Color> enderChestColor = register(new Setting("EnderChest Color", new Color(200, 0, 200, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("fill")));
    public Setting<Color> enderChestOutline = register(new Setting("EnderChest Outline", new Color(200, 0, 250, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("outline")));
    public Setting<Boolean> shulker = register(new Setting("Shulker", true));
    public Setting<Color> shulkerColor = register(new Setting("Shulker Color", new Color(100, 150, 200, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("fill")));
    public Setting<Color> shulkerOutline = register(new Setting("Shulker Outline", new Color(100, 150, 250, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("outline")));
    public Setting<Boolean> furnace = register(new Setting("Furnace", true));
    public Setting<Color> furnaceColor = register(new Setting("Furnace Color", new Color(200, 200, 200, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("fill")));
    public Setting<Color> furnaceOutline = register(new Setting("Furnace Outline", new Color(230, 230, 230, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("outline")));
    public Setting<Boolean> hopper = register(new Setting("Hopper", true));
    public Setting<Color> hopperColor = register(new Setting("Hopper Color", new Color(50, 50, 50, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("fill")));
    public Setting<Color> hopperOutline = register(new Setting("Hopper Outline", new Color(50, 50, 50, 100), v -> mode.getValue().equalsIgnoreCase("both") || mode.getValue().equalsIgnoreCase("outline")));

    public StorageESP() {
        super("StorageESP", Category.RENDER);
    }

    @Override
    public void onRender3D() {
        if (nullCheck()) return;

        for (TileEntity entity : mc.world.loadedTileEntityList) {
            BlockPos pos = entity.getPos();

            if (entity instanceof TileEntityChest && chest.getValue())
                RenderUtil.drawBox(pos, mode.getValue(), chestColor.getValue(), chestOutline.getValue(), thickness.getValue());
            else if (entity instanceof TileEntityEnderChest && enderChest.getValue())
                RenderUtil.drawBox(pos, mode.getValue(), enderChestColor.getValue(), enderChestOutline.getValue(), thickness.getValue());
            else if (entity instanceof TileEntityShulkerBox && shulker.getValue())
                RenderUtil.drawBox(pos, mode.getValue(), shulkerColor.getValue(), shulkerOutline.getValue(), thickness.getValue());
            else if (entity instanceof TileEntityFurnace && furnace.getValue())
                RenderUtil.drawBox(pos, mode.getValue(), furnaceColor.getValue(), furnaceOutline.getValue(), thickness.getValue());
            else if (entity instanceof TileEntityHopper && hopper.getValue())
                RenderUtil.drawBox(pos, mode.getValue(), hopperColor.getValue(), hopperOutline.getValue(), thickness.getValue());
        }
    }
}
