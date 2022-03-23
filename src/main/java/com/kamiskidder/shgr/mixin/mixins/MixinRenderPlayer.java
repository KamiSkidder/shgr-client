package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.render.Nametags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    private float renderPitch;
    private float renderYaw;
    private float renderHeadYaw;
    private float prevRenderHeadYaw;
    private float prevRenderPitch;
    private float prevRenderYawOffset;
    private float prevPrevRenderYawOffset;

    @Inject(method = "renderEntityName(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDLjava/lang/String;D)V", at = @At("HEAD"), cancellable = true)
    public void renderName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (Nametags.INSTANCE.isToggled()) info.cancel();
    }

    @Inject(method = "doRender", at = @At("HEAD"))
    private void rotateBegin(AbstractClientPlayer entity, double x, double y, double z, float entityYaw,
                             float partialTicks, CallbackInfo ci) {
        if (entity == Minecraft.getMinecraft().player) {
            this.prevRenderHeadYaw = entity.prevRotationYawHead;
            this.prevRenderPitch = entity.prevRotationPitch;
            this.renderPitch = entity.rotationPitch;
            this.renderYaw = entity.rotationYaw;
            this.renderHeadYaw = entity.rotationYawHead;
            this.prevPrevRenderYawOffset = entity.prevRenderYawOffset;
            this.prevRenderYawOffset = entity.renderYawOffset;
            if (RotateManager.isRotating()) {
                float yaw = RotateManager.getYaw();
                entity.rotationYaw = yaw;
                entity.rotationYawHead = yaw;
                entity.prevRotationYawHead = yaw;
                entity.prevRenderYawOffset = yaw;
                entity.renderYawOffset = yaw;
            }
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void rotateEnd(AbstractClientPlayer entity, double x, double y, double z, float entityYaw,
                           float partialTicks, CallbackInfo ci) {
        if (entity == Minecraft.getMinecraft().player) {
            entity.rotationPitch = this.renderPitch;
            entity.rotationYaw = this.renderYaw;
            entity.rotationYawHead = this.renderHeadYaw;
            entity.prevRotationYawHead = this.prevRenderHeadYaw;
            entity.prevRotationPitch = this.prevRenderPitch;
            entity.prevRotationYawHead = this.prevRenderHeadYaw;
            entity.renderYawOffset = this.prevRenderYawOffset;
            entity.prevRenderYawOffset = this.prevPrevRenderYawOffset;
        }
    }
}
