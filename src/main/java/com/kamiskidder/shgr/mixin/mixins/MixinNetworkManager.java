package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.util.client.EventUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At(value = "HEAD"), cancellable = true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        EventUtil.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At(value = "HEAD"), cancellable = true)
    private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        EventUtil.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At(value = "RETURN"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Sent event = new PacketEvent.Sent(packet);
        EventUtil.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }
}