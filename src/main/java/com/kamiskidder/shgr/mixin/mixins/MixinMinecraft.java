package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.event.client.KeyboardUpdateEvent;
import com.kamiskidder.shgr.manager.ConfigManager;
import com.kamiskidder.shgr.manager.FriendManager;
import com.kamiskidder.shgr.module.combat.AutoGaiji;
import com.kamiskidder.shgr.ui.mainmenu.GuiCustomMainMenu;
import com.kamiskidder.shgr.util.Util;
import com.kamiskidder.shgr.util.client.EventUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(value = Minecraft.class)
public abstract class MixinMinecraft implements Util {
    @Shadow
    public GameSettings gameSettings;

    @Shadow protected abstract void init() throws LWJGLException, IOException;

    @Inject(method = "runTick()V", at = @At("HEAD"))
    public void onKeyboardUpdate(CallbackInfo ci) {
        EventUtil.post(new KeyboardUpdateEvent());
    }

    @Inject(method = "getLimitFramerate()I", at = @At("RETURN"), cancellable = true)
    public void getLimitFramerate(CallbackInfoReturnable<Integer> cir) {
        if (mc.currentScreen instanceof GuiCustomMainMenu) {
            cir.setReturnValue(gameSettings.limitFramerate);
        }
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo info) {
        ConfigManager.save();
        FriendManager.save();
    }

    @Redirect(method = "processKeyBinds()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    public boolean isHandActive(EntityPlayerSP instance) {
        if (AutoGaiji.INSTANCE.isToggled())
            return false;
        return instance.isHandActive();
    }

    @Redirect(method = "processKeyBinds()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;rightClickMouse()V"))
    public void rightClickMouse(Minecraft instance) {
        if (AutoGaiji.INSTANCE.isToggled())
            return;
        instance.rightClickMouse();
    }
}
