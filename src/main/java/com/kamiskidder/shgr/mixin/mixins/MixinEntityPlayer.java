package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.event.player.PlayerTravelEvent;
import com.kamiskidder.shgr.util.client.EventUtil;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPlayer.class, priority = Integer.MAX_VALUE)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(final float strafe, final float vertical, final float forward, final CallbackInfo info) {
        PlayerTravelEvent event = new PlayerTravelEvent(strafe, vertical, forward);
        EventUtil.post(event);
        if (event.isCanceled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            info.cancel();
        }
    }
}
