package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.ui.splash.CustomSplashProgress;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ICrashCallable;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.SharedDrawable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraftforge.fml.client.SplashProgress;

import static org.lwjgl.opengl.GL11.*;

@Mixin(value = SplashProgress.class, priority = 9999, remap = false)
public class MixinSplashProgress {
    @Inject(method = "start()V", at = @At(value = "HEAD", target = "Ljava/lang/Thread;setUncaughtExceptionHandler(Ljava/lang/Thread$UncaughtExceptionHandler;)V"), cancellable = true)
    private static void setThread(CallbackInfo ci) {
        /*
        ci.cancel();
        run();
         */
    }

    private static void run() {
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable()
        {
            @Override
            public String call() throws Exception
            {
                return "' Vendor: '" + glGetString(GL_VENDOR) +
                        "' Version: '" + glGetString(GL_VERSION) +
                        "' Renderer: '" + glGetString(GL_RENDERER) +
                        "'";
            }

            @Override
            public String getLabel()
            {
                return "GL info";
            }
        });
        CrashReport report = CrashReport.makeCrashReport(new Throwable(), "Loading screen debug info");
        StringBuilder systemDetailsBuilder = new StringBuilder();
        report.getCategory().appendToStringBuilder(systemDetailsBuilder);
        FMLLog.log.info(systemDetailsBuilder.toString());

        try
        {
            Drawable d = new SharedDrawable(Display.getDrawable());
            AccessorSplashProgress.setD(d);
            Display.getDrawable().releaseContext();
            d.makeCurrent();
        } catch (LWJGLException e)
        {
            FMLLog.log.error("Error starting SplashProgress:", e);
        }

        SplashProgress.getMaxTextureSize();
        Thread thread = CustomSplashProgress.createSplashScreen();
        AccessorSplashProgress.setThread(thread);

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                FMLLog.log.error("Splash thread Exception", e);
            }
        });

        thread.start();
    }
}
