package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class Waypoints extends Module {
    public Waypoints() {
        super("Waypoints", Category.RENDER);
    }

    @Override
    public void onRender3D() {
        if (mc.renderManager.options == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        float posx = (float) (mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * mc.getRenderPartialTicks());
        float posy = (float) (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * mc.getRenderPartialTicks());
        float posz = (float) (mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * mc.getRenderPartialTicks());
        GlStateManager.translate(posx * -1, posy * -1, posz * -1);

        GlStateManager.rotate(-mc.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        boolean flag1 = mc.renderManager.options.thirdPersonView == 2;
        GlStateManager.rotate((float) (flag1 ? -1 : 1) * mc.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        //render waypoint
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(100, 0, 100).color(255, 255, 255, 255).endVertex();
        builder.pos(110, 0, 100).color(255, 255, 255, 255).endVertex();
        builder.pos(110, 2, 100).color(255, 255, 255, 255).endVertex();
        builder.pos(100, 2, 100).color(255, 255, 255, 255).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
