package com.kamiskidder.shgr.ui.mainmenu;

import com.kamiskidder.shgr.SHGR;
import com.kamiskidder.shgr.manager.FontManager;
import com.kamiskidder.shgr.manager.MainMenuManager;
import com.kamiskidder.shgr.ui.font.CFontRenderer;
import com.kamiskidder.shgr.util.client.MathUtil;
import com.kamiskidder.shgr.util.render.ColorUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.GuiModList;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiCustomMainMenu extends GuiScreen {
    private static DynamicTexture background = null;
    private static DynamicTexture mountain = null;
    private static DynamicTexture flower1 = null;
    private static DynamicTexture flower2 = null;
    private static DynamicTexture flower3 = null;
    private static DynamicTexture flower4 = null;
    private static DynamicTexture flower5 = null;
    private static DynamicTexture flower6 = null;
    private static DynamicTexture flower7 = null;
    private static DynamicTexture flower8 = null;
    private static DynamicTexture flower9 = null;
    private static DynamicTexture elainaBase = null;
    private static DynamicTexture eye = null;
    private static DynamicTexture cape1 = null;
    private static DynamicTexture cape2 = null;
    private static DynamicTexture cape3 = null;
    private static DynamicTexture hear1 = null;
    private static DynamicTexture hear2 = null;
    private static DynamicTexture hear3 = null;
    private static DynamicTexture hear4 = null;
    private static DynamicTexture hear5 = null;
    private static DynamicTexture hear6 = null;
    private static DynamicTexture hear7 = null;
    private static DynamicTexture hear8 = null;
    private static DynamicTexture hear9 = null;
    private static DynamicTexture hear10 = null;
    private static DynamicTexture fish1 = null;
    private static DynamicTexture fish2 = null;
    private static DynamicTexture fish3 = null;
    private static DynamicTexture fish4 = null;
    private static DynamicTexture pedal1 = null;
    private static DynamicTexture pedal2 = null;
    private final CopyOnWriteArrayList<Fish> fish = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Pedal> pedal1List = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Pedal> pedal2List = new CopyOnWriteArrayList<>();
    private final CFontRenderer helvetica2 = FontManager.helvetica2;
    private final CFontRenderer icon = FontManager.icon;
    private boolean alreadyInit = false;
    private float tick = 0;
    private float fish1Tick, fish2Tick, fish3Tick, fish4Tick = 0;
    private float pedal1Tick, pedal2Tick = 0;
    private float lastRw, lastRh;
    private float singleX = 40f;
    private float singleR, singleG, singleB = 0;
    private float multiX = 56f;
    private float multiR, multiG, multiB = 0;
    private float modX = 56f;
    private float modR, modG, modB = 0;
    private float settingX = 50f;
    private float settingR, settingG, settingB = 0;
    private float exitX = 50f;
    private float exitR, exitG, exitB = 0;

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @Override
    public void initGui() {
        setupResource();
        if (!alreadyInit) {
            fish1Tick = 14 * 60;
            fish2Tick = 19 * 60;
            fish3Tick = 18 * 60;
            fish4Tick = 100 * 60;
            pedal2Tick = 60;
            pedal1Tick = 60;

            resetColor();
        }

        alreadyInit = true;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // the original wallpaper's resolution is  3840x2160 (8:5)
        //base: 549, 266
        float w = this.width;
        float h = this.height;
        float width, height = 0;

        if (w < h) {
            float r = h / 5.0f;
            width = 8.0f * r;
            height = h;
        } else {
            float r = w / 8.0f;
            width = w;
            height = 5.0f * r;
        }

        if (w > width) {
            float r = w / width;
            width *= r;
            height *= r;
        }
        if (h > height) {
            float r = h / height;
            width *= r;
            height *= r;
        }

        float rw = width / 549.0f;
        float rh = height / 266.0f;

        lastRw = rw;
        lastRh = rh;

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();

        GL11.glTranslatef((width / 2), (height / 2), 0);
        GL11.glScalef(1.09f, 1.09f, 1.09f);
        GL11.glTranslatef(-(width / 2), -(height / 2), 0);

        //background
        GL11.glTranslatef(mouseX * 0.0012f, mouseY * 0.012f, 0);
        drawImage(background, 0, 0, width, height);
        GL11.glTranslatef(-(mouseX * 0.0012f), -(mouseY * 0.012f), 0);
        //fish
        GL11.glTranslatef(mouseX * 0.0015f, mouseY * 0.015f, 0);
        drawFish(rw, rh);
        updateFish1();
        updateFish2();
        updateFish3();
        updateFish4();
        GL11.glTranslatef(-(mouseX * 0.0015f), -(mouseY * 0.015f), 0);
        //mountain
        GL11.glTranslatef(mouseX * 0.0018f, mouseY * 0.018f, 0);
        drawImage(mountain, 0, 190 * rh, width, 240 * rh);
        GL11.glTranslatef(-(mouseX * 0.0018f), -(mouseY * 0.018f), 0);
        //pedal1
        GL11.glTranslatef(mouseX * 0.002f, mouseY * 0.02f, 0);
        drawPedal1(rw, rh);
        updatePedal1();
        GL11.glTranslatef(-(mouseX * 0.002f), -(mouseY * 0.02f), 0);
        //elaina
        GL11.glTranslatef(mouseX * 0.03f, mouseY * 0.03f, 0);
        drawElaina(rw, rh);
        GL11.glTranslatef(-(mouseX * 0.03f), -(mouseY * 0.03f), 0);
        //pedal2
        GL11.glTranslatef(mouseX * 0.04f, mouseY * 0.04f, 0);
        drawPedal2(rw, rh);
        updatePedal2();
        GL11.glTranslatef(-(mouseX * 0.04f), -(mouseY * 0.04f), 0);
        //flower
        GL11.glTranslatef(mouseX * 0.05f, mouseY * 0.05f, 0);
        drawFlowers(width, height, rw, rh);
        GL11.glTranslatef(-(mouseX * 0.05f), -(mouseY * 0.05f), 0);

        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
        drawGUI(mouseX, mouseY, rw, rh, width, height);

        float add = getRenderSpeed();
        tick += add;
        fish1Tick += add;
        fish2Tick += add;
        fish3Tick += add;
        fish4Tick += add;
        pedal1Tick += add;
        pedal2Tick += add;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        //left click
        if (mouseButton == 0) {
            GuiCustomMainMenu instance = new GuiCustomMainMenu();
            handleButton(mouseX, mouseY, 0, 45, 140, 64, new GuiWorldSelection(instance));
            handleButton(mouseX, mouseY, 0, 70, 140, 89, new GuiMultiplayer(instance));
            handleButton(mouseX, mouseY, 0, 95, 140, 114, new GuiModList(instance));
            handleButton(mouseX, mouseY, 0, 120, 140, 139, new GuiOptions(instance, this.mc.gameSettings));
            handleExit(mouseX, mouseY);
        }
    }

    private void handleExit(int mouseX, int mouseY) {
        float bx = 0 * lastRw;
        float by = 170 * lastRh;
        float bw = 140 * lastRw;
        float bh = 189 * lastRh;
        if (isMouseHovering(mouseX, mouseY, bx, by, bw, bh)) {
            playClickSound();
            mc.shutdown();
        }
    }

    private void handleButton(int mouseX, int mouseY, float x1, float y1, float x2, float y2, GuiScreen to) {
        float bx = x1 * lastRw;
        float by = y1 * lastRh;
        float bw = x2 * lastRw;
        float bh = y2 * lastRh;
        if (isMouseHovering(mouseX, mouseY, bx, by, bw, bh)) {
            mc.displayGuiScreen(to);
            playClickSound();
        }
    }

    private void drawGUI(float mouseX, float mouseY, float rw, float rh,
                         float width, float height) {
        drawRect(0, 0, 140 * rw, (int) height, ColorUtil.toRGBA(255, 255, 255));
        RenderUtil.drawGradientRect(140 * rw, 0, 145 * rw, (int) height, ColorUtil.toRGBA(0, 0, 0, 50), ColorUtil.toRGBA(0, 0, 0, 0), ColorUtil.toRGBA(0, 0, 0, 50), ColorUtil.toRGBA(0, 0, 0, 0));

        drawString(FontManager.helvetica1, "SHGR", -25.0f, 5.0f, ColorUtil.toRGBA(200, 200, 200), rw, rh);
        drawString(FontManager.helvetica3, "v " + SHGR.MOD_VERSION, 79f, 23.0f, ColorUtil.toRGBA(50, 50, 50), rw, rh);

        drawSinglePlayer(mouseX, mouseY, rw, rh);
        drawMultiPlayer(mouseX, mouseY, rw, rh);
        drawMods(mouseX, mouseY, rw, rh);
        drawSettings(mouseX, mouseY, rw, rh);
        drawExit(mouseX, mouseY, rw, rh);
    }

    private void drawExit(float mouseX, float mouseY, float rw, float rh) {
        float bx = 0 * rw;
        float by = 170 * rh;
        float bw = 140 * rw;
        float bh = 189 * rh;
        if (isMouseHovering(mouseX, mouseY, bx, by, bw, bh)) {
            drawRect(bx, by, bw, bh, ColorUtil.toRGBA(200, 200, 200, 30));
            exitX = getEasingPos(exitX, 60f, 0.2f);
            exitR = getEasingPos(exitR, 42, 0.2f);
            exitG = getEasingPos(exitG, 229, 0.2f);
            exitB = getEasingPos(exitB, 226, 0.2f);
        } else {
            exitX = getEasingPos(exitX, 50f, 0.2f);
            exitR = getEasingPos(exitR, 20, 0.2f);
            exitG = getEasingPos(exitG, 20, 0.2f);
            exitB = getEasingPos(exitB, 20, 0.2f);
        }

        drawString(icon, "e", 29, 174f, ColorUtil.toRGBA((int) exitR, (int) exitG, (int) exitB, 255), rw, rh);
        drawString(helvetica2, "Exit", exitX, 175f, ColorUtil.toRGBA((int) exitR, (int) exitG, (int) exitB, 255), rw, rh);
    }

    private void drawSettings(float mouseX, float mouseY, float rw, float rh) {
        float bx = 0 * rw;
        float by = 120 * rh;
        float bw = 140 * rw;
        float bh = 139 * rh;
        if (isMouseHovering(mouseX, mouseY, bx, by, bw, bh)) {
            drawRect(bx, by, bw, bh, ColorUtil.toRGBA(200, 200, 200, 30));
            settingX = getEasingPos(settingX, 50f, 0.2f);
            settingR = getEasingPos(settingR, 42, 0.2f);
            settingG = getEasingPos(settingG, 229, 0.2f);
            settingB = getEasingPos(settingB, 226, 0.2f);
        } else {
            settingX = getEasingPos(settingX, 40f, 0.2f);
            settingR = getEasingPos(settingR, 20, 0.2f);
            settingG = getEasingPos(settingG, 20, 0.2f);
            settingB = getEasingPos(settingB, 20, 0.2f);
        }

        drawString(icon, "c", 29, 124f, ColorUtil.toRGBA((int) settingR, (int) settingG, (int) settingB, 255), rw, rh);
        drawString(helvetica2, "Settings", settingX, 125f, ColorUtil.toRGBA((int) settingR, (int) settingG, (int) settingB, 255), rw, rh);
    }

    private void drawMods(float mouseX, float mouseY, float rw, float rh) {
        float bx = 0 * rw;
        float by = 95 * rh;
        float bw = 140 * rw;
        float bh = 114 * rh;
        if (isMouseHovering(mouseX, mouseY, bx, by, bw, bh)) {
            drawRect(bx, by, bw, bh, ColorUtil.toRGBA(200, 200, 200, 30));
            modX = getEasingPos(modX, 56f, 0.2f);
            modR = getEasingPos(modR, 42, 0.2f);
            modG = getEasingPos(modG, 229, 0.2f);
            modB = getEasingPos(modB, 226, 0.2f);
        } else {
            modX = getEasingPos(modX, 46f, 0.2f);
            modR = getEasingPos(modR, 20, 0.2f);
            modG = getEasingPos(modG, 20, 0.2f);
            modB = getEasingPos(modB, 20, 0.2f);
        }

        drawString(icon, "d", 29, 99f, ColorUtil.toRGBA((int) modR, (int) modG, (int) modB, 255), rw, rh);
        drawString(helvetica2, "Mods", modX, 100f, ColorUtil.toRGBA((int) modR, (int) modG, (int) modB, 255), rw, rh);
    }

    private void drawMultiPlayer(float mouseX, float mouseY, float rw, float rh) {
        float bx = 0 * rw;
        float by = 70 * rh;
        float bw = 140 * rw;
        float bh = 89 * rh;
        if (isMouseHovering(mouseX, mouseY, bx, by, bw, bh)) {
            drawRect(bx, by, bw, bh, ColorUtil.toRGBA(200, 200, 200, 30));
            multiX = getEasingPos(multiX, 46f, 0.2f);
            multiR = getEasingPos(multiR, 42, 0.2f);
            multiG = getEasingPos(multiG, 229, 0.2f);
            multiB = getEasingPos(multiB, 226, 0.2f);
        } else {
            multiX = getEasingPos(multiX, 34f, 0.2f);
            multiR = getEasingPos(multiR, 20, 0.2f);
            multiG = getEasingPos(multiG, 20, 0.2f);
            multiB = getEasingPos(multiB, 20, 0.2f);
        }

        drawString(icon, "a", 28, 74f, ColorUtil.toRGBA((int) multiR, (int) multiG, (int) multiB, 255), rw, rh);
        drawString(helvetica2, "MultiPlayer", multiX, 75f, ColorUtil.toRGBA((int) multiR, (int) multiG, (int) multiB, 255), rw, rh);
    }

    private void drawSinglePlayer(float mouseX, float mouseY, float rw, float rh) {
        float bx = 0 * rw;
        float by = 45 * rh;
        float bw = 140 * rw;
        float bh = 64 * rh;
        if (isMouseHovering(mouseX, mouseY, bx, by, bw, bh)) {
            drawRect(bx, by, bw, bh, ColorUtil.toRGBA(200, 200, 200, 30));
            singleX = getEasingPos(singleX, 40f, 0.2f);
            singleR = getEasingPos(singleR, 42, 0.2f);
            singleG = getEasingPos(singleG, 229, 0.2f);
            singleB = getEasingPos(singleB, 226, 0.2f);
        } else {
            singleX = getEasingPos(singleX, 30f, 0.2f);
            singleR = getEasingPos(singleR, 20, 0.2f);
            singleG = getEasingPos(singleG, 20, 0.2f);
            singleB = getEasingPos(singleB, 20, 0.2f);
        }

        drawString(icon, "b", 30, 50f, ColorUtil.toRGBA((int) singleR, (int) singleG, (int) singleB, 255), rw, rh);
        drawString(helvetica2, "SinglePlayer", singleX, 50f, ColorUtil.toRGBA((int) singleR, (int) singleG, (int) singleB, 255), rw, rh);
    }

    private void resetColor() {
        singleR = 10;
        singleG = 10;
        singleB = 10;
        multiR = 10;
        multiG = 10;
        multiB = 10;
        modR = 10;
        modG = 10;
        modB = 10;
    }

    private float getEasingPos(float n, float g, float s) {
        return n + (g - n) * (s * getRenderSpeed());
    }

    private boolean isMouseHovering(float mouseX, float mouseY, float x1, float y1, float x2, float y2) {
        return mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2;
    }

    private void drawString(CFontRenderer font, String str, float x, float y, int color, float rw, float rh) {
        GlStateManager.pushMatrix();
        float strWidth = font.getStringWidth(str);
        float strHeight = font.getHeight();
        GL11.glTranslatef((x + strWidth / 2) * rw, (y + strHeight / 2) * rh, 0);
        GL11.glScalef(rw, rw, 1.00f);
        font.drawString(str, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private void updatePedal1() {
        if (pedal1Tick > 1 * 60) {
            pedal1Tick = 0;
            Pedal pedal = new Pedal(pedal1, -200, 1000 - MathUtil.getRandom(0, 800), 0.3f + (MathUtil.getRandom(-15, 15) * 0.01f), 20, MathUtil.getRandom(0, 360));
            pedal.setSize(212, 243);
            pedal.setSpeed(10f, 2.0f);
            pedal.setViewpoint(-300, -300, 549 + 300, 266 + 900);
            addPedalTo1(pedal);
        }
    }

    private void drawPedal1(float rw, float rh) {
        pedal1List.forEach(p -> p.drawPedal(rw, rh, getRenderSpeed()));
        try {
            pedal1List.removeIf(p -> p.isHidden(rw, rh));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addPedalTo1(Pedal pedal) {
        pedal1List.add(pedal);
    }

    private void updatePedal2() {
        if (pedal2Tick > 1 * 60) {
            pedal2Tick = 0;
            Pedal pedal = new Pedal(pedal2, -200, 1000 - MathUtil.getRandom(0, 800), 0.32f + (MathUtil.getRandom(-15, 15) * 0.01f), 20, MathUtil.getRandom(0, 360));
            pedal.setSize(242, 243);
            pedal.setSpeed(10f, 2.0f);
            pedal.setViewpoint(-300, -300, 549 + 300, 266 + 900);
            addPedalTo2(pedal);
        }
    }

    private void drawPedal2(float rw, float rh) {
        pedal2List.forEach(p -> p.drawPedal(rw, rh, getRenderSpeed()));
        try {
            pedal2List.removeIf(p -> p.isHidden(rw, rh));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addPedalTo2(Pedal pedal) {
        pedal2List.add(pedal);
    }

    private void drawFish(float rw, float rh) {
        fish.forEach(f -> f.drawFish(tick, rw, rh, getRenderSpeed()));
        try {
            fish.removeIf(f -> f.isHidden(rw, rh));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateFish1() {
        if (fish1Tick > 16 * 60) {
            fish1Tick = 0;
            float x = -50;
            float y = 120;
            float angle = 40;
            for (int i = 0; i < 14; i++) {
                Fish fish = new Fish(fish1, x, y, 0.5f, -20, 30);
                fish.setFoward(1);
                fish.setSize(105, 30);
                fish.setVertical(2f, 10);
                fish.setViewpoint(-300, -300, 549 + 300, 266 + 300);
                fish.setTickOffset(i * 20);
                addFish(fish);

                x -= sin1(90) * 30;
                y -= cos1(90) * 30;
                x -= sin1(angle) * 30;
                y -= cos1(angle) * 30;
                x -= sin1(i * 900) * 50;
                y -= cos1(i * 900) * 50;
            }
        }
    }

    private void updateFish2() {
        if (fish2Tick > 12 * 60) {
            fish2Tick = 0;
            float x = 900;
            float y = 400;
            float angle = -70;
            for (int i = 0; i < 30; i++) {
                Fish fish = new Fish(fish2, x, y, 0.5f, 150, -130);
                fish.setFoward(1.2f);
                fish.setSize(84, 99);
                fish.setVertical(2f, 10);
                fish.setViewpoint(-300, -300, 549 + 300, 266 + 300);
                fish.setTickOffset(i * 15);
                addFish(fish);

                x -= sin1(90) * -80;
                y -= cos1(90) * -80;
                x += sin1(angle) * 20;
                y += cos1(angle) * 20;
                x -= sin1(i * 900) * 20;
                y -= cos1(i * 900) * 20;
            }
        }
    }

    private void updateFish3() {
        if (fish3Tick > 14 * 60) {
            fish3Tick = 0;
            float x = 1100;
            float y = 50;
            float angle = -70;
            for (int i = 0; i < 12; i++) {
                Fish fish = new Fish(fish3, x, y, 0.5f, 195, -235);
                fish.setFoward(1.7f);
                fish.setSize(139, 75);
                fish.setVertical(3f, 5);
                fish.setViewpoint(-300, -300, 549 + 300, 266 + 300);
                fish.setTickOffset(i * 15);
                addFish(fish);

                x -= sin1(90) * -80;
                y -= cos1(90) * -80;
                x -= sin1(angle) * 20;
                y -= cos1(angle) * 20;
                x -= sin1(i * 900) * 20;
                y -= cos1(i * 900) * 20;
            }
        }
    }

    private void updateFish4() {
        if (fish4Tick > 13 * 60) {
            fish4Tick = 0;
            float x = -100;
            float y = 290;
            float angle = -70;
            for (int i = 0; i < 14; i++) {
                Fish fish = new Fish(fish4, x, y, 0.5f, 10, -10);
                fish.setFoward(1.7f);
                fish.setSize(137, 74);
                fish.setVertical(3f, 5);
                fish.setViewpoint(-300, -300, 549 + 300, 266 + 300);
                fish.setTickOffset(i * 15);
                addFish(fish);

                x -= sin1(90) * 80;
                y -= cos1(90) * 80;
                x += sin1(angle) * 20;
                y += cos1(angle) * 20;
            }
        }
    }

    private void addFish(Fish fish) {
        this.fish.add(fish);
    }

    private void drawFlowers(float width, float height, float rw, float rh) {
        drawImage(flower1, 0, 210 * rh, width, 270 * rh);

        drawFlower8(251, 412, 0.5f, rw, rh, 0.026f, -4, -10);
        drawFlower8(28, 396, 0.5f, rw, rh, 0.021f, -3, -10);
        drawFlower8(457, 422, 0.5f, rw, rh, 0.03f, 5, -10);
        drawFlower8(730, 435, 0.5f, rw, rh, 0.03f, -2, -10);
        drawFlower8(853, 424, 0.5f, rw, rh, 0.03f, -2, -10);

        drawFlower9(137 * 2, 290 * 2, 0.4f, rw, rh, 0.02f, 10f, -10);
        drawFlower9(293 * 2, 245 * 2, 0.5f, rw, rh, 0.022f, -12f, -10);
        drawFlower9(396 * 2, 225 * 2, 0.5f, rw, rh, 0.015f, 12f, -10);
        drawFlower9(54 * 2, 217 * 2, 0.5f, rw, rh, 0.02f, -15f, -10);
        drawFlower9(462 * 2, 205 * 2, 0.5f, rw, rh, 0.026f, 19f, -10);

        drawFlowerImage(flower3, 921, 525, 400, 400, 0.37f, 200, 200, 0.03f, 2, 0, rw, rh);
        drawFlowerImage(flower4, 626 * 2, 250 * 2, 400, 400, 0.37f, 200, 200, 0.04f, 1.5f, 0, rw, rh);
        drawFlowerImage(flower4, 280, 520, 400, 400, 0.37f, 200, 200, 0.025f, 2.5f, 50, rw, rh);
        drawFlowerImage(flower6, 110, 477, 400, 400, 0.37f, 0, 260, 0.03f, 3f, 0, rw, rh);

        drawFlower2(-20, 594, 0.37f, rw, rh, 0.026f, -1f, 0);
        drawFlower2(288, 390, 0.5f, rw, rh, 0.026f, 2f, -5);
        drawFlower2(1400, 485, 0.36f, rw, rh, 0.026f, -3f, -40);
        drawFlowerImage(flower7, 1930, 1345, 1776, 976, 0.17f, 0, 50, 0.02f, 5f, -10, rw, rh);
    }

    private void drawFlower9(float x, float y, float scale, float rw, float rh, float speed, float power, float offset) {
        float width = (269 / 3.0f);
        float height = (153 / 3.0f);
        GlStateManager.pushMatrix();
        GL11.glScalef(scale, scale, scale);
        float offsetx = 0;
        float offsety = 10;
        GL11.glTranslatef(((x + width / 2) + offsetx) * rw, ((y + height / 2) + offsety) * rh, 0.0f);
        GL11.glRotatef(-sin(tick * speed) * power + offset, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef((-(x + width / 2) - offsetx) * rw, (-(y + height / 2) - offsety) * rh, 0.0f);
        drawImage(flower9, x * rw, y * rh, x * rw, (y + height) * rh, (x + width) * rw, (y + height) * rh, (x + width) * rw, y * rh);
        GlStateManager.popMatrix();
    }

    private void drawFlower2(float x, float y, float scale, float rw, float rh, float speed, float power, float offset) {
        float width = (980 / 3.0f);
        float height = (460 / 3.0f);
        GlStateManager.pushMatrix();
        GL11.glScalef(scale, scale, scale);
        float offsetx = 200;
        float offsety = 200;
        GL11.glTranslatef(((x + width / 2) + offsetx) * rw, ((y + height / 2) + offsety) * rh, 0.0f);
        GL11.glRotatef(-sin(tick * speed) * power + offset, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef((-(x + width / 2) - offsetx) * rw, (-(y + height / 2) - offsety) * rh, 0.0f);
        drawImage(flower2, x * rw, y * rh, x * rw, (y + height) * rh, (x + width) * rw, (y + height) * rh, (x + width) * rw, y * rh);
        GlStateManager.popMatrix();
    }

    private void drawFlower8(float _x, float _y, float scale, float rw, float rh, float speed, float power, float offset) {
        drawFlowerImage(flower8, _x, _y, 269, 153, scale, 0, 10, speed, power, offset, rw, rh);
    }

    private void drawFlowerImage(DynamicTexture tex, float x, float y, float iwidth, float iheight, float scale,
                                 float rotateOffsetX, float rotateOffsetY, float angleSpeed, float anglePower, float angleOffset, float rw, float rh) {
        float width = (iwidth / 3.0f);
        float height = (iheight / 3.0f);
        GlStateManager.pushMatrix();
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(((x + width / 2) + rotateOffsetX) * rw, ((y + height / 2) + rotateOffsetY) * rh, 0.0f);
        GL11.glRotatef(sin(tick * angleSpeed) * anglePower + angleOffset, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef((-(x + width / 2) - rotateOffsetX) * rw, (-(y + height / 2) - rotateOffsetY) * rh, 0.0f);
        drawImage(tex, x * rw, y * rh, x * rw, (y + height) * rh, (x + width) * rw, (y + height) * rh, (x + width) * rw, y * rh);
        GlStateManager.popMatrix();
    }

    private void drawElaina(float rw, float rh) {
        drawCapeImage(cape2, 645, 265, 503, 422, 0.35f, 10,
                0, 0, -sin(tick * 0.02f + 90) * 5 * rw, cos(tick * 0.03f) * 6 * rh,
                0, cos(tick * 0.03f + 90) * -20 * rh, 0, 0,
                rw, rh);
        drawHearPhase1(rw, rh);
        drawImage(elainaBase, 175 * rw, 85 * rh, 410 * rw, 236 * rh);
        drawImage(eye, 253 * rw, 99.5f * rh, 295 * rw, 120.5f * rh);
        drawCapeImage(cape1, 556, 220, 650, 660, 0.4f, 10,
                0, 0, -sin(tick * 0.03f), cos(tick * 0.03f) * 6 * rh,
                0, cos(tick * 0.03f) * -5 * rh, 0, 0,
                rw, rh);
        drawCapeImage(cape3, 800, 359, 595, 794, 0.349f, 0,
                0, 0, 0, 0,
                -(sin(tick * 0.03f) * 4 * rw - 5), -(cos(tick * 0.03f) * -0.2f * rh + 10), -sin(tick * 0.02f) * 1 * rw, -cos(tick * 0.02f) * 3 * rh,
                rw, rh);
        drawHearPhase2(rw, rh);
    }

    private void drawHearPhase1(float rw, float rh) {
        drawHearImage(hear4, 670, 255, 105, 252, 0.37f,
                0, 0,
                -(sin(tick * 0.035f) * 1 * rw), -cos(tick * 0.035f) * 0.1f,
                -(sin(tick * 0.035f) * 5 * rw), -cos(tick * 0.035f) * 10 * rh, 0, 0, rw, rh);
        drawHearImage(hear10, 684, 286, 171, 230, 0.37f,
                0, 0, sin(tick * 0.03f) - 4, 0,
                0, 0, 0, 0, rw, rh);
    }

    private void drawHearPhase2(float rw, float rh) {
        drawHearImage(hear9, 763, 253, 204, 774, 0.37f,
                0, 0,
                0, -(cos(tick * 0.025f) * 2f * rh),
                (sin(tick * 0.025f) * 6f * rw + 1), -(cos(tick * 0.025f) * 2f * rh),
                0, 0, rw, rh);
        drawHearImage(hear1, 610, 180, 140, 95, 0.42f,
                0, 0, -(sin(tick * 0.03f) * 7) * rw, (cos(tick * 0.03f) * -5) * rh, 0, 0, 0, 0, rw, rh);
        drawHearImage(hear2, 712, 237, 111, 170, 0.37f,
                0, 0, 0, 0,
                -sin(tick * 0.032f) * 3 * rw, -(cos(tick * 0.032f) * 4 * rh + 10), 0, 0, rw, rh);
        drawHearImage(hear3, 692, 254, 85, 200, 0.37f,
                0, 0, 0, 0,
                -(sin(tick * 0.035f) * 6 * rw - 20), -cos(tick * 0.035f) * 8 * rh, 0, 0, rw, rh);
        drawHearImage(hear5, 721, 242, 110, 190, 0.37f,
                0, 0,
                0, -(cos(tick * 0.035f) * 2 * rh + 2),
                (sin(tick * 0.035f) * 3 * rw + 1), -(cos(tick * 0.035f) * 2 * rh + 2),
                0, 0, rw, rh);
        drawHearImage(hear6, 746, 245, 56, 173, 0.37f,
                0, 0,
                0, -(cos(tick * 0.035f) * 2.2f * rh + 2),
                (sin(tick * 0.035f) * 4 * rw + 1), -(cos(tick * 0.035f) * 2.2f * rh + 2),
                0, 0, rw, rh);
        drawHearImage(hear7, 753, 250, 73, 137, 0.37f,
                0, 0,
                0, -(cos(tick * 0.035f) * 1.5f * rh + 2),
                (sin(tick * 0.035f) * 3.5f * rw + 1), -(cos(tick * 0.035f) * 1.5f * rh + 2),
                0, 0, rw, rh);
        drawHearImage(hear8, 758, 240, 150, 273, 0.37f,
                0, 0,
                0, -(cos(tick * 0.025f) * 2f * rh),
                (sin(tick * 0.025f) * 3.5f * rw + 1), -(cos(tick * 0.025f) * 2f * rh),
                0, 0, rw, rh);
    }

    private void drawHearImage(DynamicTexture tex, float ix, float iy, float iwidth, float iheight, float scale,
                               float x1Offset, float y1Offset, float x2Offset, float y2Offset, float x3Offset, float y3Offset, float x4Offset, float y4Offset, float rw, float rh) {
        float x = ix * rw;
        float y = iy * rh;
        float width = (iwidth / 3.0f) * rw;
        float height = (iheight / 3.0f) * rh;
        GlStateManager.pushMatrix();
        GL11.glScalef(scale, scale, scale);
        drawImage(tex, x + x1Offset, y + y1Offset, x + x2Offset, y + height + y2Offset, x + width + x3Offset, y + height + y3Offset, x + width + x4Offset, y + y4Offset);
        GlStateManager.popMatrix();
    }

    private void drawCapeImage(DynamicTexture tex, float ix, float iy, float iwidth, float iheight, float scale, float angle,
                               float x1Offset, float y1Offset, float x2Offset, float y2Offset, float x3Offset, float y3Offset, float x4Offset, float y4Offset, float rw, float rh) {
        float x = ix * rw;
        float y = iy * rh;
        float width = (iwidth / 3.0f) * rw;
        float height = (iheight / 3.0f) * rh;
        GlStateManager.pushMatrix();
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-(x + width / 2) * scale, -(y + height / 2) * scale, 0.0f);
        GL11.glRotatef(angle, 0.0f, 0.0f, 1.0f);
        GL11.glTranslatef((x + width / 2) * scale, (y + height / 2) * scale, 0.0f);
        drawImage(tex, x + x1Offset, y + y1Offset, x + x2Offset, y + height + y2Offset, x + width + x3Offset, y + height + y3Offset, x + width + x4Offset, y + y4Offset);
        GlStateManager.popMatrix();
    }

    private void drawImage(DynamicTexture location, float x1, float y1, float x2, float y2,
                           float x3, float y3, float x4, float y4) {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GlStateManager.bindTexture(location.getGlTextureId());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GlStateManager.translate(0, 0, 0);
        GlStateManager.glBegin(7);

        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(x1, y1, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(x2, y2, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(x3, y3, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(x4, y4, 0.0f);

        GlStateManager.glEnd();

        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawImage(DynamicTexture location, float x1, float y1, float x2, float y2) {
        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GlStateManager.bindTexture(location.getGlTextureId());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GlStateManager.translate(0, 0, 0);
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

        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private float getRenderSpeed() {
        int fps = Minecraft.getDebugFPS();
        if (fps == 0)
            fps = mc.gameSettings.limitFramerate;
        return 60.0f / fps;
    }

    private float sin(float s) {
        return MathHelper.sin(s);
    }

    private float cos(float s) {
        return MathHelper.cos(s);
    }

    private float sin1(float s) {
        return (float) Math.sin(Math.toRadians(s));
    }

    private float cos1(float s) {
        return (float) Math.cos(Math.toRadians(s));
    }

    private void playClickSound() {
        mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void setupResource() {
        if (background == null)
            background = getDynamicTexture("background");
        if (mountain == null)
            mountain = getDynamicTexture("mountain");
        if (flower1 == null)
            flower1 = getDynamicTexture("flower1");
        if (flower2 == null)
            flower2 = getDynamicTexture("flower2");
        if (flower3 == null)
            flower3 = getDynamicTexture("flower3");
        if (flower4 == null)
            flower4 = getDynamicTexture("flower4");
        if (flower5 == null)
            flower5 = getDynamicTexture("flower5");
        if (flower6 == null)
            flower6 = getDynamicTexture("flower6");
        if (flower7 == null)
            flower7 = getDynamicTexture("flower7");
        if (flower8 == null)
            flower8 = getDynamicTexture("flower8");
        if (flower9 == null)
            flower9 = getDynamicTexture("flower9");
        if (elainaBase == null)
            elainaBase = getDynamicTexture("elainaBase");
        if (eye == null)
            eye = getDynamicTexture("eye");
        if (cape1 == null)
            cape1 = getDynamicTexture("cape1");
        if (cape2 == null)
            cape2 = getDynamicTexture("cape2");
        if (cape3 == null)
            cape3 = getDynamicTexture("cape3");
        if (hear1 == null)
            hear1 = getDynamicTexture("hear1");
        if (hear2 == null)
            hear2 = getDynamicTexture("hear2");
        if (hear3 == null)
            hear3 = getDynamicTexture("hear3");
        if (hear4 == null)
            hear4 = getDynamicTexture("hear4");
        if (hear5 == null)
            hear5 = getDynamicTexture("hear5");
        if (hear6 == null)
            hear6 = getDynamicTexture("hear6");
        if (hear7 == null)
            hear7 = getDynamicTexture("hear7");
        if (hear8 == null)
            hear8 = getDynamicTexture("hear8");
        if (hear9 == null)
            hear9 = getDynamicTexture("hear9");
        if (hear10 == null)
            hear10 = getDynamicTexture("hear10");
        if (fish1 == null)
            fish1 = getDynamicTexture("fish1");
        if (fish2 == null)
            fish2 = getDynamicTexture("fish2");
        if (fish3 == null)
            fish3 = getDynamicTexture("fish3");
        if (fish4 == null)
            fish4 = getDynamicTexture("fish4");
        if (pedal1 == null)
            pedal1 = getDynamicTexture("pedal1");
        if (pedal2 == null)
            pedal2 = getDynamicTexture("pedal2");
    }

    private DynamicTexture getDynamicTexture(String name) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(String.format("%s%s%s", MainMenuManager.folder, name, ".png")));
        } catch (Exception ex) {
            ex.printStackTrace();
            img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        return new DynamicTexture(img);
    }

    private ResourceLocation getImageLocation(String name) {
        return new ResourceLocation(String.format("images/%s.%s", name, "png"));
    }
}
