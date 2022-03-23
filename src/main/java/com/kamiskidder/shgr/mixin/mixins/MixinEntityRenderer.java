package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.module.render.CameraClip;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @ModifyVariable(method = {"orientCamera"}, ordinal = 3, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double changeCameraDistanceHook(double range) {
        return CameraClip.INSTANCE.isToggled() && CameraClip.INSTANCE.extend.getValue() ? CameraClip.INSTANCE.distance.getValue() : range;
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 7, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double orientCameraHook(double range) {
        return CameraClip.INSTANCE.isToggled() && CameraClip.INSTANCE.extend.getValue() ? CameraClip.INSTANCE.distance.getValue() : (CameraClip.INSTANCE.isToggled() && !CameraClip.INSTANCE.extend.getValue() ? 4.0 : range);
    }
}
