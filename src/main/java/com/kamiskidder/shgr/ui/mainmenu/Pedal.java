package com.kamiskidder.shgr.ui.mainmenu;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class Pedal {
    private final DynamicTexture texture;
    private final float scale;
    private final float angle;
    private float x;
    private float y;
    private float viewAngle;
    private float width, height;
    private float forwardSpeed;
    private float angleSpeed;
    private float minX, minY, maxX, maxY;

    public Pedal(DynamicTexture texture, float x, float y, float scale, float angle, float viewAngle) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.angle = angle;
        this.viewAngle = viewAngle;
    }

    public void setViewpoint(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void setSpeed(float forwardSpeed, float angleSpeed) {
        this.forwardSpeed = forwardSpeed;
        this.angleSpeed = angleSpeed;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void drawPedal(float rw, float rh, float renderSpeed) {
        float width = (this.width / 3.0f);
        float height = this.height / 3.0f;
        GlStateManager.pushMatrix();

        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef((x + width / 2) * rw, (y + height / 2) * rh, 0.0f);
        GL11.glRotatef(viewAngle, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef((-(x + width / 2)) * rw, (-(y + height / 2)) * rh, 0.0f);

        drawImage(texture, x * rw, y * rh, (x + width) * rw, (y + height) * rh);

        GlStateManager.popMatrix();

        x += sin(angle + 90) * forwardSpeed * renderSpeed;
        y += cos(angle + 90) * forwardSpeed * renderSpeed;

        viewAngle += angleSpeed * renderSpeed;
    }

    private void drawImage(DynamicTexture location, float x1, float y1, float x2, float y2) {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        GlStateManager.bindTexture(location.getGlTextureId());

        GlStateManager.translate(0, 0, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
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
