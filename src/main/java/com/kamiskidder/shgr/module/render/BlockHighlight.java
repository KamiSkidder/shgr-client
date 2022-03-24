package com.kamiskidder.shgr.module.render;

import java.awt.Color;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.render.RenderUtil;

import net.minecraft.util.math.RayTraceResult;

public class BlockHighlight extends Module {

    public Setting<String> mode = register(new Setting("Mode", "Fill", new String[]{"Fill", "Outline", "Both"}));
    public Setting<Color> color = register(new Setting("Color", new Color(230, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Outline")));
    public Setting<Color> outlineColor = register(new Setting("Outline Color", new Color(255, 10, 10, 70), v -> !mode.getValue().equalsIgnoreCase("Fill")));
    public Setting<Float> thickness = register(new Setting("Thickness", 1.5F, 5.0F, 0.1F, v -> !mode.getValue().equalsIgnoreCase("Fill")));
    public BlockHighlight() {
        super("BlockHighlight", Category.RENDER);
    }

    @Override
    public void onRender3D() {
        if (nullCheck()) return;
        if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            RenderUtil.drawBox(mc.objectMouseOver.getBlockPos(), mode.getValue(), color.getValue(), outlineColor.getValue(), thickness.getValue());
        }
        super.onRender3D();
    }
}
