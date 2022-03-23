package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.manager.ConfigManager;
import com.kamiskidder.shgr.manager.FriendManager;
import com.kamiskidder.shgr.ui.mainmenu.GuiCustomMainMenu;
import com.kamiskidder.shgr.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements Util {
    @Shadow
    public GameSettings gameSettings;

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
}
