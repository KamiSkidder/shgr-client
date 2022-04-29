package com.kamiskidder.shgr.module.render;

import org.lwjgl.opengl.GL11;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import com.kamiskidder.shgr.util.render.ColorUtil;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

public class Waypoints extends Module {
    public Waypoints() {
        super("Waypoints", Category.RENDER);
    }

    @Override
    public void onRender3D() {
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        RenderHelper.disableStandardItemLighting();

        float posx = (float) (mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * mc.getRenderPartialTicks());
        float posy = (float) (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * mc.getRenderPartialTicks());
        float posz = (float) (mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * mc.getRenderPartialTicks());
        GlStateManager.translate(posx * -1, posy * -1, posz * -1);

        double distance = Math.max(PlayerUtil.getDistance(new Vec3d(x, y, z)) * 0.02 * 0.5, 0.2);
        GlStateManager.scale(distance, distance, distance);
        
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1.0f, 0);
        float viewx = mc.getRenderManager().playerViewX;
        GlStateManager.rotate(viewx * (mc.gameSettings.thirdPersonView == 2 ? -1 : 1), 1.0f, 0, 0f);

        float width = 1.0f;
        float height = 1.5f;
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        int color = 20; 
        bufferbuilder.pos(x - width, y - height, 0).color(color, color, color, 255).endVertex();
        bufferbuilder.pos(x - width, y + height, 0).color(color, color, color, 255).endVertex();
        bufferbuilder.pos(x + width, y + height, 0).color(color, color, color, 255).endVertex();
        bufferbuilder.pos(x + width, y - height, 0).color(color, color, color, 255).endVertex();
        tessellator.draw();
        
        GlStateManager.glLineWidth(3.0f);
        bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        color = 50;
        bufferbuilder.pos(x - width, y - height, 0).color(color, color, color, 255).endVertex();
        bufferbuilder.pos(x - width, y + height, 0).color(color, color, color, 255).endVertex();
        bufferbuilder.pos(x + width, y + height, 0).color(color, color, color, 255).endVertex();
        bufferbuilder.pos(x + width, y - height, 0).color(color, color, color, 255).endVertex();
        tessellator.draw();
        
        GlStateManager.scale(0.2f, 0.2f, 0.2f);
        GlStateManager.enableTexture2D();
        
        GlStateManager.rotate(180, 0, 0, 1.0f);
        mc.fontRenderer.drawString("bruh", (int)(x - width), (int)(y + height/2), ColorUtil.toRGBA(255, 255, 255));
        GlStateManager.disableTexture2D();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
