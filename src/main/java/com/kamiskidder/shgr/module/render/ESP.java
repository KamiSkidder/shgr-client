package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.manager.FriendManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.entity.EntityType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class ESP extends Module {
    private final CopyOnWriteArraySet<CirclePos> positions = new CopyOnWriteArraySet<>();
    public Setting<Float> speed = register(new Setting("Speed", 1.0f, 10.0f, 0.1f));
    public Setting<Float> power = register(new Setting("Power", 1.0f, 1.5f, 0.5f));
    public Setting<Float> scale = register(new Setting("Scale", 1.0f, 5.0f, 0.5f));
    public Setting<Float> width = register(new Setting("Width", 1.0f, 5.0f, 0.5f));
    public Setting<Float> fade = register(new Setting("Fade", 50.0f, 100.0f, 10.0f));
    public Setting<Boolean> player = register(new Setting("Player", true));
    public Setting<Color> playerColor = register(new Setting("Player Color", new Color(255, 10, 10, 255), v -> player.getValue()));
    public Setting<Color> friendColor = register(new Setting("Friend Color", new Color(50, 200, 205, 255), v -> player.getValue()));
    public Setting<Boolean> villager = register(new Setting("Villager", true));
    public Setting<Color> villagerColor = register(new Setting("Villager Color", new Color(25, 150, 50, 255), v -> player.getValue()));
    public Setting<Boolean> animal = register(new Setting("Animal", true));
    public Setting<Color> animalColor = register(new Setting("Animal Color", new Color(150, 255, 100, 255), v -> animal.getValue()));
    public Setting<Boolean> monster = register(new Setting("Animal", true));
    public Setting<Color> monsterColor = register(new Setting("Monster Color", new Color(255, 255, 0, 255), v -> monster.getValue()));
    public Setting<Boolean> boss = register(new Setting("Boss", true));
    public Setting<Color> bossColor = register(new Setting("Boss Color", new Color(0, 0, 255, 255), v -> boss.getValue()));
    public Setting<Integer> alpha = register(new Setting("Alpha", 255, 255, 100));
    private int ticks = 0;

    public ESP() {
        super("ESP", Category.RENDER);
    }

    @Override
    public void onRender3D() {
        try {
            float spd = speed.getValue() * 0.01f;

            for (Entity entity : mc.world.loadedEntityList.stream().filter(e -> e.entityId != mc.player.entityId).collect(Collectors.toList())) {
                double y = (entity.posY + entity.height / 2) + MathHelper.sin(ticks * spd) * power.getValue();

                if (entity instanceof EntityPlayer && player.getValue()) {
                    if (FriendManager.isFriend((EntityPlayer) entity)) {
                        positions.add(new CirclePos(entity.posX, y, entity.posZ, entity.width, friendColor.getValue(), alpha.getValue()));
                    } else {
                        positions.add(new CirclePos(entity.posX, y, entity.posZ, entity.width, playerColor.getValue(), alpha.getValue()));
                    }
                }

                if (entity instanceof EntityVillager && villager.getValue()) {
                    positions.add(new CirclePos(entity.posX, y, entity.posZ, entity.width, villagerColor.getValue(), alpha.getValue()));
                }

                if (EntityType.isAnimal(entity) && animal.getValue()) {
                    positions.add(new CirclePos(entity.posX, y, entity.posZ, entity.width, animalColor.getValue(), alpha.getValue()));
                }

                if (EntityType.isMonster(entity) && monster.getValue()) {
                    positions.add(new CirclePos(entity.posX, y, entity.posZ, entity.width, monsterColor.getValue(), alpha.getValue()));
                }

                if (EntityType.isBoss(entity) && boss.getValue()) {
                    positions.add(new CirclePos(entity.posX, y, entity.posZ, entity.width, bossColor.getValue(), alpha.getValue()));
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            GL11.glLineWidth(width.getValue());

            float posx = (float) (mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * mc.getRenderPartialTicks());
            float posy = (float) (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * mc.getRenderPartialTicks());
            float posz = (float) (mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * mc.getRenderPartialTicks());
            GlStateManager.translate(posx * -1, posy * -1, posz * -1);

            for (CirclePos pos : positions) {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                builder.begin(3, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i < 360; i++) {
                    double x1 = pos.x + (MathHelper.sin(i * (float) Math.PI / 180) * pos.width * scale.getValue());
                    double z1 = pos.z + (MathHelper.cos(i * (float) Math.PI / 180) * pos.width * scale.getValue());

                    double x2 = pos.x + (MathHelper.sin((i + 1) * (float) Math.PI / 180) * pos.width * scale.getValue());
                    double z2 = pos.z + (MathHelper.cos((i + 1) * (float) Math.PI / 180) * pos.width * scale.getValue());

                    Color color = pos.color;

                    if (pos.alpha < 0) {
                        break;
                    }

                    builder.pos(x1, pos.y, z1).color(color.getRed(), color.getGreen(), color.getBlue(), pos.alpha).endVertex();
                    builder.pos(x2, pos.y, z2).color(color.getRed(), color.getGreen(), color.getBlue(), pos.alpha).endVertex();
                }
                tessellator.draw();

                pos.alpha -= fade.getValue();
            }
            positions.removeIf(p -> p.alpha <= 0);

            ticks++;

            GL11.glLineWidth(1.0F);
            GlStateManager.shadeModel(7424);
            GL11.glDisable(2848);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        } catch (Exception ex) {

        }
    }

    private class CirclePos {
        public double x;
        public double y;
        public double z;
        public double width;
        public Color color;
        public int alpha;

        public CirclePos(double x, double y, double z, double width, Color color, int alpha) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.color = color;
            this.alpha = alpha;
        }
    }
}
