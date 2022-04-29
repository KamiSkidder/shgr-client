package com.kamiskidder.shgr.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraftforge.fml.client.SplashProgress;

@Mixin(value = SplashProgress.class, priority = -1000, remap = false)
public class MixinSplashProgress {
    @Inject(method = "start()V", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;setUncaughtExceptionHandler(Ljava/lang/Thread$UncaughtExceptionHandler;)V"))
    private static void setThread(CallbackInfo ci) {
        //AccessorSplashProgress.setThread(CustomSplashProgress.createSplashScreen());
    }
}
