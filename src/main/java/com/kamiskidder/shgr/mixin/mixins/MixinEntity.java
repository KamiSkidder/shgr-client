package com.kamiskidder.shgr.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kamiskidder.shgr.module.movement.NoPush;
import com.kamiskidder.shgr.module.render.Freecam;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;

    @Shadow
    public abstract boolean isInWater();

    @Shadow
    public abstract boolean isInLava();

    @Shadow
    public abstract void move(MoverType type, double x, double y, double z);

    @Inject(method = "applyEntityCollision(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void applyEntityCollision(Entity d0, CallbackInfo ci) {
        if (NoPush.INSTANCE.isToggled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;calculateXOffset(Lnet/minecraft/util/math/AxisAlignedBB;D)D"))
    public double calculateXOffset(AxisAlignedBB instance, AxisAlignedBB bb, double d) {
        if (Freecam.INSTANCE.isToggled())
            return d;
        return instance.calculateXOffset(bb, d);
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;calculateYOffset(Lnet/minecraft/util/math/AxisAlignedBB;D)D"))
    public double calculateYOffset(AxisAlignedBB instance, AxisAlignedBB bb, double d) {
        if (Freecam.INSTANCE.isToggled())
            return d;
        return instance.calculateYOffset(bb, d);
    }
    
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;calculateZOffset(Lnet/minecraft/util/math/AxisAlignedBB;D)D"))
    public double calculateZOffset(AxisAlignedBB instance, AxisAlignedBB bb, double d) {
        if (Freecam.INSTANCE.isToggled())
            return d;
        return instance.calculateZOffset(bb, d);
    }
}
