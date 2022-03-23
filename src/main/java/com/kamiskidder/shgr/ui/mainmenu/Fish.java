package com.kamiskidder.shgr.ui.mainmenu;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class Fish {
    private final DynamicTexture texture;
    private final float scale;
    private final float angle;
    private final float angleOffset;
    private float x;
    private float y;
    private float fowardSpeed;
    private float verticalSpeed, verticalPower;
    private float minX, minY, maxX, maxY;
    private float width, height;
    private int tickOffset = 0;

    public Fish(DynamicTexture tex, float x, float y, float scale, float angle, float angleOffset) {
        this.texture = tex;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.angle = angle;
        this.angleOffset = angleOffset;
    }

    public void setFoward(float speed) {
        this.fowardSpeed = speed;
    }

    public void setVertical(float speed, float power) {
        this.verticalSpeed = speed;
        this.verticalPower = power;
    }

    public void setViewpoint(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setTickOffset(int offset) {
        tickOffset = offset;
    }

    public void drawFish(float originalTick, float rw, float rh, float renderSpeed) {
        float tick = originalTick + tickOffset;
        float o = sin(tick * verticalSpeed) * verticalPower * renderSpeed;
        float rx = x + sin(angle) * o;
        float ry = y + cos(angle) * o;
        float width = (this.width / 3.0f);
        float height = (this.height / 3.0f);
        GlStateManager.pushMatrix();
        GL11.glScalef(scale, scale, scale);
        float offsetx = 0;
        float offsety = 10;
        GL11.glTranslatef(((rx + width / 2) + offsetx) * rw, ((ry + height / 2) + offsety) * rh, 0.0f);
        GL11.glRotatef(angle + angleOffset, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef((-(rx + width / 2) - offsetx) * rw, (-(ry + height / 2) - offsety) * rh, 0.0f);
        drawImage(texture, rx * rw, ry * rh, (rx + width) * rw, (ry + height) * rh);
        GlStateManager.popMatrix();

        //forward
        x += sin(angle + 90) * fowardSpeed * renderSpeed;
        y += cos(angle + 90) * fowardSpeed * renderSpeed;
    }

    private void drawImage(DynamicTexture location, float x1, float y1, float x2, float y2) {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        GlStateManager.bindTexture(location.getGlTextureId());

        GlStateManager.translate(0, 0, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.8f);
        GlStateManager.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(x1, y1, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(x1, y2, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(x2, y2, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(x2, y1, 0.0f);

        GlStateManager.glEnd();

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public boolean isHidden(float rw, float rh) {
        return (x < (minX * rw) && y < (minY * rh)) || (x > (maxX * rw) && y > (maxY * rh));
    }

    private float sin(float s) {
        return (float) Math.sin(Math.toRadians(s));
    }

    private float cos(float s) {
        return (float) Math.cos(Math.toRadians(s));
    }
}
