package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.module.movement.NoPush;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    /*
    @Inject(method = "isInWater()Z", at = @At("HEAD"), cancellable = true)
    public void isInWater(CallbackInfoReturnable<Boolean> cir) {
        if (NoPush.INSTANCE.isToggled() && NoPush.INSTANCE.liquid.getValue()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "isInLava()Z", at = @At("HEAD"), cancellable = true)
    public void isInLava(CallbackInfoReturnable<Boolean> cir) {
        if (NoPush.INSTANCE.isToggled() && NoPush.INSTANCE.liquid.getValue()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
     */
}
