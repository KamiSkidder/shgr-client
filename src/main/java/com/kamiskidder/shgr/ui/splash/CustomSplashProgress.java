package com.kamiskidder.shgr.ui.splash;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.kamiskidder.shgr.manager.MainMenuManager;
import com.kamiskidder.shgr.mixin.mixins.AccessorSplashProgress;
import com.kamiskidder.shgr.util.Util;
import com.kamiskidder.shgr.util.render.ColorUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ProgressManager;

public class CustomSplashProgress implements Util {
    private float ticks = 0;

    public static Thread createSplashScreen() {
        CustomSplashProgress customSplashProgress = new CustomSplashProgress();
        return new Thread(customSplashProgress.getTask());
    }

    public Runnable getTask() {
        return new Runnable() {
            @Override
            public void run() {
                setGL();
                setupTexture();
                Semaphore mutex = AccessorSplashProgress.getMutex();
                glDisable(GL_TEXTURE_2D);
                while (!AccessorSplashProgress.isDone()) {
                    glClear(GL_COLOR_BUFFER_BIT);
                    int w = Display.getWidth();
                    int h = Display.getHeight();
                    glViewport(0, 0, w, h);
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    glOrtho(0, w, h, 0, -1, 1);
                    glMatrixMode(GL_MODELVIEW);
                    glLoadIdentity();

                    final int mouseX = Mouse.getX();
                    final int mouseY = h-Mouse.getY();

                    drawScreen(mouseX, mouseY, w, h);

                    Display.update();
                    try {
                        Thread.sleep(1000/mc.gameSettings.limitFramerate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ticks += getRenderSpeed();

                    //debug
                    if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                        System.out.printf("X: %d, Y: %d%n", mouseX, mouseY);
                    }
                }
                clearGL();
            }
        };
    }

    private void drawScreen(int mouseX, int mouseY, int width, int height) {
        ProgressManager.ProgressBar first = null, penult = null, last = null;
        Iterator<ProgressManager.ProgressBar> i = ProgressManager.barIterator();
        while (i.hasNext()) {
            if (first == null) first = i.next();
            else {
                penult = last;
                last = i.next();
            }
        }

        float screenWidth, screenHeight = 0;
        if ((float) width < (float) height) {
            float r = (float) height / 5.0f;
            screenWidth = 8.0f * r;
            screenHeight = (float) height;
        } else {
            float r = (float) width / 8.0f;
            screenWidth = (float) width;
            screenHeight = 5.0f * r;
        }
        if ((float) width > screenWidth) {
            float r = (float) width / screenWidth;
            screenWidth *= r;
            screenHeight *= r;
        }
        if ((float) height > screenHeight) {
            float r = (float) height / screenHeight;
            screenWidth *= r;
            screenHeight *= r;
        }
        float widthRatio = screenWidth / 1920.0f;
        float heightRatio = screenHeight / 1080.0f;

        glTranslatef(screenWidth/2.0f, screenHeight/2.0f, 0.0f);
        glScalef(1.2f, 1.2f, 1.2f);
        glTranslatef(-screenWidth/2.0f, -screenHeight/2.0f, 0.0f);

        drawImage(background1, 0, 0, screenWidth, screenHeight, mouseX*0.01f, mouseY*0.01f);
        drawImage(background2, 0, 410*heightRatio, screenWidth, screenHeight+30, mouseX*0.02f, mouseY*0.02f);
        drawImage(background3, 0, 110*heightRatio, screenWidth, screenHeight+30, mouseX*0.03f, mouseY*0.03f);
        drawGrass(width, height, widthRatio, heightRatio, mouseX, mouseY);
        drawFlower(width, height, widthRatio, heightRatio, mouseX, mouseY);
    }
    
    private void drawGrass(float width, float height, float wr, float hr, int mouseX, int mouseY) {
        drawImage(grass1, (960-sin(ticks*3)*5)*wr, 975*hr, 960*wr, height, width, height, width-sin(ticks*3)*5*wr, 975*hr, mouseX*0.035f, mouseY*0.035f);
        drawImage(grass2, sin(ticks*3.2f)*3f*wr, 510*hr, 0, height, 570*wr, height, (570+sin(ticks*3.2f)*3f)*wr, 510*hr, mouseX*0.035f, mouseY*0.035f);
        drawImage(grass3, -sin(ticks*3.1f)*4f*wr, 550*hr, 0, height, 560*wr, height, (560-sin(ticks*3.1f)*4f)*wr, 550*hr, mouseX*0.035f, mouseY*0.035f);
    }
    
    private void drawFlower(float width, float height, float wr, float hr, int mouseX, int mouseY) {
        drawFlowerImage(flower1, 0, 490*hr, 450*wr, height, mouseX*0.045f, mouseY*0.045f,
                0, height, -sin(ticks*2)*0.8f);
        drawFlowerImage(flower2, 83, 658*hr, 443*wr, height+73, mouseX*0.045f, mouseY*0.045f,
                83, height+73, sin(ticks * 3));
        drawFlowerImage(flower3, 0, 0*hr, 500*wr, 100*hr, mouseX*0.045f, mouseY*0.045f,
                0, 100*hr, 0);
    }
    
    private void drawFlowerImage(DynamicTexture tex, float x1, float y1, float x2, float y2, 
    		float offsetX, float offsetY, float centerX, float centerY, float angle) {
        glPushMatrix();
        glTranslatef(centerX, centerY, 0);
        glRotatef(angle, 0, 0, 1);
        glTranslatef(-centerX, -centerY, 0);
        drawImage(tex, x1, y1, x2, y2, offsetX, offsetY);
        glPopMatrix();
    }
    
    private void drawImage(DynamicTexture tex, float x1, float y1, float x2, float y2, float offsetX, float offsetY) {
        glTranslatef(offsetX, offsetY, 0.0f);
        drawImage(tex, x1, y1, x2, y2);
        glTranslatef(-(offsetX), -(offsetY), 0.0f);
    }

    private void drawImage(DynamicTexture tex, float x1, float y1, float x2, float y2) {
        drawImage(tex, x1, y1, x1, y2, x2, y2, x2, y1);
    }

    private void drawImage(DynamicTexture tex, float x1, float y1, float x2, float y2,
                           float x3, float y3, float x4, float y4, float offsetX, float offsetY) {
        glTranslatef(offsetX, offsetY, 0.0f);
        drawImage(tex, x1, y1, x2, y2, x3, y3, x4, y4);
        glTranslatef(-(offsetX), -(offsetY), 0.0f);
    }

    private void drawImage(DynamicTexture tex, float x1, float y1, float x2, float y2,
                           float x3, float y3, float x4, float y4) {
        glPushMatrix();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_ALPHA_TEST);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glBindTexture(GL_TEXTURE_2D, tex.getGlTextureId());
        glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glBegin(GL_QUADS);

        glTexCoord2f(0.0f, 0.0f);
        glVertex3f(x1, y1, 0.0f);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3f(x2, y2, 0.0f);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3f(x3, y3, 0.0f);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3f(x4, y4, 0.0f);

        glEnd();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    private void drawRect(int x1, int y1, int x2, int y2, int color) {
        glPushMatrix();
        Color c = ColorUtil.getColor(color);
        glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glVertex2f(x1, y1);
        glVertex2f(x2, y1);
        glVertex2f(x2, y2);
        glVertex2f(x1, y2);
        glEnd();
        glPopMatrix();
    }

    private void drawLineRect(int x1, int y1, int x2, int y2, int color) {
        glPushMatrix();
        glBegin(GL_LINE_LOOP);
        Color c = ColorUtil.getColor(color);
        glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
        glDisable(GL_TEXTURE_2D);
        glVertex2f(x1, y1);
        glVertex2f(x2, y1);
        glVertex2f(x2, y2);
        glVertex2f(x1, y2);
        glEnd();
        glPopMatrix();
    }

    private void setColor(int color) {
        glColor3ub((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8) & 0xFF), (byte) (color & 0xFF));
    }

    private void setGL() {
        AccessorSplashProgress.getLock().lock();
        try {
            Display.getDrawable().makeCurrent();
        } catch (LWJGLException e) {
            FMLLog.log.error("Error setting GL context:", e);
            throw new RuntimeException(e);
        }
        int backgroundColor = 0xFFFFFF;
        glClearColor((float) ((backgroundColor >> 16) & 0xFF) / 0xFF, (float) ((backgroundColor >> 8) & 0xFF) / 0xFF, (float) (backgroundColor & 0xFF) / 0xFF, 1);
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void clearGL() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayWidth = Display.getWidth();
        mc.displayHeight = Display.getHeight();
        mc.resize(mc.displayWidth, mc.displayHeight);
        glClearColor(1, 1, 1, 1);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, .1f);
        try {
            Display.getDrawable().releaseContext();
        } catch (LWJGLException e) {
            FMLLog.log.error("Error releasing GL context:", e);
            throw new RuntimeException(e);
        } finally {
            AccessorSplashProgress.getLock().unlock();
        }
    }

    private float getRenderSpeed() {
        return 60.0f / mc.gameSettings.limitFramerate;
    }

    private float sin(float angle) {
        return (float) Math.sin(Math.toRadians(angle));
    }

    private float cos(float angle) {
        return (float) Math.cos(Math.toRadians(angle));
    }

    private static DynamicTexture background1 = null;
    private static DynamicTexture background2 = null;
    private static DynamicTexture background3 = null;
    private static DynamicTexture grass1 = null;
    private static DynamicTexture grass2 = null;
    private static DynamicTexture grass3 = null;
    private static DynamicTexture flower1 = null;
    private static DynamicTexture flower2 = null;
    private static DynamicTexture flower3 = null;

    private void setupTexture() {
        if (background1 == null)
            background1 = getDynamicTexture("background1");
        if (background2 == null)
            background2 = getDynamicTexture("background2");
        if (background3 == null)
            background3 = getDynamicTexture("background3");
        if (grass1 == null)
        	grass1 = getDynamicTexture("grass1");
        if (grass2 == null)
            grass2 = getDynamicTexture("grass2");
        if (grass3 == null)
            grass3 = getDynamicTexture("grass3");
        if (flower1 == null)
        	flower1 = getDynamicTexture("flower1");
        if (flower2 == null)
        	flower2 = getDynamicTexture("flower2");
        if (flower3 == null)
            flower3 = getDynamicTexture("flower3");
    }

    private DynamicTexture getDynamicTexture(String name) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(String.format("%s%s%s", MainMenuManager.splashScreenFolder, name, ".png")));
        } catch (Exception ex) {
            ex.printStackTrace();
            img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        return new DynamicTexture(img);
    }
}
