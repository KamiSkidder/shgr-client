package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.manager.FontManager;
import com.kamiskidder.shgr.manager.FriendManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.ui.font.CFontRenderer;
import com.kamiskidder.shgr.util.entity.EntityUtil;
import com.kamiskidder.shgr.util.render.ColorUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Nametags extends Module {
    public static Nametags INSTANCE;

    public Setting<Float> scale = register(new Setting("Scale", 2.5f, 4.0f, 1.5f));
    public Setting<Boolean> ping = register(new Setting("Ping", true));
    public Setting<Boolean> health = register(new Setting("Health", true));
    public Setting<Boolean> inventory = register(new Setting("Inventory", true));
    public Setting<Boolean> hpbar = register(new Setting("HPBar", true));
    public Setting<Float> barScale = register(new Setting("BarScale", 1.0f, 2.0f, 0.1f));
    public Setting<String> position = register(new Setting("Position", "Bottom", new String[]{"Bottom", "Top"}));
    public Setting<Boolean> plate = register(new Setting("Plate", true));
    public Setting<Color> plateColor = register(new Setting("PlateColor", new Color(0, 0, 0, 150)));
    public Setting<Boolean> customFont = register(new Setting("CustomFont", true));
    public Setting<Boolean> shadow = register(new Setting("Shadow", true));

    public Nametags() {
        super("Nametags", Category.RENDER);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (nullCheck()) return;

        try {
            for (Entity entity : new ArrayList<Entity>(mc.world.playerEntities.stream().filter(e -> e.entityId != mc.player.entityId).collect(Collectors.toList()))) {
                String name = entity.getDisplayName().getFormattedText();
                double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) event.getPartialTicks();
                double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) event.getPartialTicks();
                double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) event.getPartialTicks();
                boolean flag = entity.isSneaking();
                float f = mc.renderManager.playerViewY;
                float f1 = mc.renderManager.playerViewX;
                boolean flag1 = mc.renderManager.options.thirdPersonView == 2;
                float f2 = entity.height + 0.5F - (flag ? 0.25F : 0.0F);
                renderNametag((EntityPlayer) entity, buildNametagString((EntityPlayer) entity), d0 - mc.getRenderManager().viewerPosX, (d1 + f2) - mc.getRenderManager().viewerPosY, d2 - mc.getRenderManager().viewerPosZ, f, f1, flag1, flag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderNametag(EntityPlayer player, String str, double x, double y, double z, double viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) -viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-(scale.getValue() * 0.01), -(scale.getValue() * 0.01), (scale.getValue() * 0.01));
        GlStateManager.disableLighting();
        GL11.glDisable(2929);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i = (int) (getStringWidth(str) / 2);
        if (plate.getValue()) {
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            float r = plateColor.getValue().getRed() / 255.0F;
            float g = plateColor.getValue().getGreen() / 255.0F;
            float b = plateColor.getValue().getBlue() / 255.0F;
            float a = plateColor.getValue().getAlpha() / 255.0F;
            bufferbuilder.pos(-i - 5, -4 - (hpbar.getValue() && position.getValue().equalsIgnoreCase("bottom") ? barScale.getValue() : 0), 0.0D).color(r, g, b, a).endVertex();
            bufferbuilder.pos(-i - 5, 10 + (hpbar.getValue() && position.getValue().equalsIgnoreCase("top") ? barScale.getValue() : 0), 0.0D).color(r, g, b, a).endVertex();
            bufferbuilder.pos(i + 3, 10 + (hpbar.getValue() && position.getValue().equalsIgnoreCase("top") ? barScale.getValue() : 0), 0.0D).color(r, g, b, a).endVertex();
            bufferbuilder.pos(i + 3, -4 - (hpbar.getValue() && position.getValue().equalsIgnoreCase("bottom") ? barScale.getValue() : 0), 0.0D).color(r, g, b, a).endVertex();
            tessellator.draw();
        }
        if (hpbar.getValue()) {
            if (position.getValue().equalsIgnoreCase("top")) {
                RenderUtil.drawGradientRect(-i - 5, -4 - barScale.getValue(), -i - 5 + ((i * 2 + 8) * (EntityUtil.getPlayerHealth(player) / 36.0F)), -4,
                        ColorUtil.toRGBA(255, 0, 0), ColorUtil.toRGBA(getHealthColor((int) (EntityUtil.getPlayerHealth(player)))), ColorUtil.toRGBA(255, 0, 0), ColorUtil.toRGBA(getHealthColor((int) (EntityUtil.getPlayerHealth(player)))));
            } else {
                RenderUtil.drawGradientRect(-i - 5, 10, -i - 5 + ((i * 2 + 8) * (EntityUtil.getPlayerHealth(player) / 36.0F)), 10 + barScale.getValue().floatValue(),
                        ColorUtil.toRGBA(255, 0, 0), ColorUtil.toRGBA(getHealthColor((int) (EntityUtil.getPlayerHealth(player)))), ColorUtil.toRGBA(255, 0, 0), ColorUtil.toRGBA(getHealthColor((int) (EntityUtil.getPlayerHealth(player)))));
            }
        }
        GlStateManager.enableTexture2D();
        drawString(str, -getStringWidth(str) / 2, 0, -1, shadow.getValue());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(true);
        if (inventory.getValue()) {
            if (!Objects.isNull(player.inventory)) {
                ItemStack mainHand = player.getHeldItemMainhand();
                ItemStack offhand = player.getHeldItemOffhand();
                if (!mainHand.isEmpty) {
                    renderItemStack(mainHand, -60, -25, 0);
                }
                List<ItemStack> armor = new ArrayList<>(player.inventory.armorInventory);
                Collections.reverse(armor);
                ItemStack currentStack;
                if (!(currentStack = armor.get(0)).isEmpty) {
                    renderItemStack(currentStack, -35, -25, 0);
                }
                if (!(currentStack = armor.get(1)).isEmpty) {
                    renderItemStack(currentStack, -15, -25, 0);
                }
                if (!(currentStack = armor.get(2)).isEmpty) {
                    renderItemStack(currentStack, 10, -25, 0);
                }
                if (!(currentStack = armor.get(3)).isEmpty) {
                    renderItemStack(currentStack, 35, -25, 0);
                }
                if (!offhand.isEmpty) {
                    renderItemStack(offhand, 60, -25, 0);
                }
            }
        }
        GL11.glEnable(2929);
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderItemStack(final ItemStack itemStack, final int n, final int n2, final int n3) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        this.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        final int n4 = (n3 > 4) ? ((n3 - 4) * 8 / 2) : 0;
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, n, n2 + n4);
        this.mc.getRenderItem().renderItemOverlays(this.mc.fontRenderer, itemStack, n, n2 + n4);
        this.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        final float n5 = 0.5f;
        final float n6 = 0.5f;
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        final float n7 = 2.0f;
        final int n8 = 2;
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private String buildNametagString(EntityPlayer player) {
        String r = "";
        if (ping.getValue()) {
            int p = -1;
            if (!Objects.isNull(mc.getConnection()) && !Objects.isNull(mc.getConnection().getPlayerInfo(player.getUniqueID())))
                p = mc.getConnection().getPlayerInfo(player.getUniqueID()).getResponseTime();
            ChatFormatting color;
            if (p == -1) color = ChatFormatting.DARK_RED;
            else if (p < 50) color = ChatFormatting.GREEN;
            else if (p < 100) color = ChatFormatting.YELLOW;
            else if (p < 300) color = ChatFormatting.RED;
            else color = ChatFormatting.DARK_RED;
            r += color + String.valueOf(p) + "ms" + ChatFormatting.RESET;
        }

        r += " " + (FriendManager.isFriend(player) ? ChatFormatting.AQUA : ChatFormatting.RESET) + player.getDisplayNameString() + ChatFormatting.RESET;

        if (health.getValue()) {
            int h = (int) EntityUtil.getPlayerHealth(player);
            ChatFormatting color;
            if (h < 10) color = ChatFormatting.RED;
            else if (h < 20) color = ChatFormatting.YELLOW;
            else color = ChatFormatting.GREEN;
            r += " " + color + h + ChatFormatting.RESET;
        }

        return r;
    }

    private Color getHealthColor(int health) {
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }

        int red = 0;
        int green = 0;
        if (health > 18) {
            red = (int) ((36 - health) * 14.1666666667);
            green = 255;
        } else {
            red = 255;
            green = (int) (255 - ((18 - health) * 14.1666666667));
        }

        return new Color(red, green, 0, 255);
    }

    private float drawString(String str, double x, double y, int color, boolean shadow) {
        if (customFont.getValue()) {
            CFontRenderer font = FontManager.guiFont;
            if (shadow)
                font.drawString(str, x + (0.5), y + (0.5), ColorUtil.toRGBA(new Color(0, 0, 0, 255)), true);
            return font.drawString(str, x, y, color, false);
        }

        return mc.fontRenderer.drawString(str, (float) x, (float) y, color, shadow);
    }

    private float getStringWidth(String str) {
        if (customFont.getValue()) {
            CFontRenderer font = FontManager.guiFont;
            return font.getStringWidth(str);
        }
        return mc.fontRenderer.getStringWidth(str);
    }

    private float getStringHeight() {
        if (customFont.getValue()) {
            CFontRenderer font = FontManager.guiFont;
            return (font.getHeight() - 1);
        }
        return mc.fontRenderer.FONT_HEIGHT;
    }

    private enum Position {
        TOP,
        BOTTOM
    }
}
