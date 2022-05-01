package com.kamiskidder.shgr.mixin.mixins;

import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.client.SplashProgress;
import org.lwjgl.opengl.Drawable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.throwables.InvalidAccessorException;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

@Mixin(value = SplashProgress.class, remap = false)
public interface AccessorSplashProgress {
    @Accessor("done")
    static boolean isDone() {
        throw new AssertionError();
    }

    @Accessor
    static void setThread(Thread thread) {
        throw new AssertionError();
    }

    @Accessor
    static Lock getLock() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Semaphore getMutex() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static IResourcePack getMiscPack() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static boolean isPause() {
        throw new UnsupportedOperationException();
    }

    @Accessor("d")
    static Drawable getD() {
        throw new UnsupportedOperationException();
    }

    @Accessor("d")
    static void setD(Drawable d) {
        throw new UnsupportedOperationException();
    }
}
