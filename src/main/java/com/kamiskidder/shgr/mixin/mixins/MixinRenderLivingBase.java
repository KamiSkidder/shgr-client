package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.module.combat.AutoGaiji;
import com.kamiskidder.shgr.module.combat.KillAura;
import com.kamiskidder.shgr.module.render.AntiCollision;
import com.kamiskidder.shgr.module.render.Freecam;
import com.kamiskidder.shgr.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Objects;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase implements Util {
    @Final
    @Shadow
    private static DynamicTexture TEXTURE_BRIGHTNESS;
    @Shadow
    protected FloatBuffer brightnessBuffer;

    @Shadow
    protected abstract void unsetBrightness();

    @Inject(method = "renderModel", at = @At("HEAD"))
    public void renderModel(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        if (AntiCollision.INSTANCE.isToggled() && mc.player.getDistance(entitylivingbaseIn) < 1 && entitylivingbaseIn != mc.player) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
    }

    @Inject(method = "renderLayers", at = @At("HEAD"), cancellable = true)
    public void renderLayers(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo callbackInfo) {
        if (AntiCollision.INSTANCE.isToggled() && mc.player.getDistance(entitylivingbaseIn) < 1 && entitylivingbaseIn != mc.player) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void doRender(EntityLivingBase f3, double flag1, double flag, double f, float f1, float f2, CallbackInfo ci) {
        if (f3.entityId == Minecraft.getMinecraft().player.entityId && Freecam.INSTANCE.isToggled())
            ci.cancel();
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableCull()V"))
    public void setBrightness(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        Color color;
        if ((color = isTarget(entity)) != null) {
            setEntityBrightness(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
    }

    private void setEntityBrightness(int r, int g, int b, int a) {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        this.brightnessBuffer.position(0);

        this.brightnessBuffer.put(r / 255.0f);
        this.brightnessBuffer.put(g / 255.0f);
        this.brightnessBuffer.put(b / 255.0f);
        this.brightnessBuffer.put(a / 255.0f);

        this.brightnessBuffer.flip();
        GlStateManager.glTexEnv(8960, 8705, this.brightnessBuffer);
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
        GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Inject(method = "setBrightness(Lnet/minecraft/entity/EntityLivingBase;FZ)Z", at = @At("HEAD"), cancellable = true)
    public void setBrightness(EntityLivingBase entity, float f3, boolean f4, CallbackInfoReturnable<Boolean> cir) {
        if (isTarget(entity) != null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;setActiveTexture(I)V"), cancellable = true)
    public void unsetBrightness(EntityLivingBase entity, double x, double y, double z, float yaw, float ticks, CallbackInfo ci) {
        if (isTarget(entity) != null) {
            unsetBrightness();
        }
    }

    private Color isTarget(EntityLivingBase entity) {
        if (KillAura.INSTANCE.isToggled() && KillAura.INSTANCE.render.getValue() && Objects.equals(entity, KillAura.INSTANCE.target))
            return KillAura.INSTANCE.color.getValue();
        if (AutoGaiji.INSTANCE.isToggled() && AutoGaiji.INSTANCE.render.getValue() && Objects.equals(entity, AutoGaiji.INSTANCE.target))
            return AutoGaiji.INSTANCE.color.getValue();

        return null;
    }
}
