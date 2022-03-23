package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.event.player.UpdatePlayerMoveStateEvent;
import com.kamiskidder.shgr.util.client.EventUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {
    @Shadow
    @Final
    private GameSettings gameSettings;

    @Inject(method = "updatePlayerMoveState", at = @At("RETURN"))
    public void updatePlayerMoveState(CallbackInfo ci) {
        EventUtil.post(new UpdatePlayerMoveStateEvent());
    }

    /*
    @Redirect(method = "updatePlayerMoveState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
    public boolean updatePlayerMoveState(KeyBinding instance) {
        if (Objects.equals(instance, gameSettings.keyBindSneak)) {
            if (NoSlow.INSTANCE.isToggled() && NoSlow.INSTANCE.sneak.getValue()) {
                return false;
            }
            else {
                return instance.isKeyDown();
            }
        }
        else {
            return instance.isKeyDown();
        }
    }
     */
}
