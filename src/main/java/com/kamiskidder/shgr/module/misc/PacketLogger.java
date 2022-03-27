package com.kamiskidder.shgr.module.misc;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class PacketLogger extends Module {
    public Setting<Boolean> player = register(new Setting("Player", true));
    public Setting<Boolean> animation = register(new Setting("Animation", true));
    public Setting<Boolean> vehicle = register(new Setting("Vehicle", true));
    public Setting<Boolean> action = register(new Setting("Action", true));
    public Setting<Boolean> input = register(new Setting("Input", true));
    public Setting<Boolean> chat = register(new Setting("Chat", true));
    public Setting<Boolean> useEntity = register(new Setting("UseEntity", true));
    public Setting<Boolean> tryUseItemOnBlock = register(new Setting("TryUseItemOnBlock", true));
    public Setting<Boolean> tryUseItem = register(new Setting("TryUseItem", true));
    public Setting<Boolean> digging = register(new Setting("Digging", true));
    public Setting<Boolean> abilities = register(new Setting("Abilities", true));
    public Setting<Boolean> teleport = register(new Setting("Teleport", true));
    public Setting<Boolean> payload = register(new Setting("Payload", true));
    public Setting<Boolean> keepAlive = register(new Setting("KeepAlive", true));
    public Setting<Boolean> itemChange = register(new Setting("ItemChange", true));

    public PacketLogger() {
        super("PacketLogger", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        Packet packet = event.getPacket();

        if (packet instanceof CPacketPlayer && player.getValue()) {
            CPacketPlayer player = (CPacketPlayer) packet;
            sendLoggerMessage("CPacketPlayer",
                    getFormattedArg("X", player.x), getFormattedArg("Y", player.y), getFormattedArg("Z", player.z),
                    getFormattedArg("Pitch", player.pitch), getFormattedArg("Yaw", player.yaw),
                    getFormattedArg("OnGround", player.onGround), getFormattedArg("Moving", player.moving), getFormattedArg("Rotating", player.rotating));
        }
        if (packet instanceof CPacketAnimation && animation.getValue()) {
            CPacketAnimation animation = (CPacketAnimation) packet;
            sendLoggerMessage("CPacketAnimation", getFormattedArg("Hand", animation.getHand().name()));
        }
        if (packet instanceof CPacketVehicleMove && vehicle.getValue()) {
            CPacketVehicleMove move = (CPacketVehicleMove) packet;
            sendLoggerMessage("CPacketVehicleMove",
                    getFormattedArg("X", move.x), getFormattedArg("Y", move.y), getFormattedArg("Z", move.z),
                    getFormattedArg("Pitch", move.pitch), getFormattedArg("Yaw", move.yaw));
        }
        if (packet instanceof CPacketEntityAction && action.getValue()) {
            CPacketEntityAction action = (CPacketEntityAction) packet;
            sendLoggerMessage("CPacketEntityAction",
                    getFormattedArg("Action", action.getAction().name()), getFormattedArg("AuxData", action.getAuxData()));
        }
        if (packet instanceof CPacketInput && input.getValue()) {
            CPacketInput input = (CPacketInput) packet;
            sendLoggerMessage("CPacketInput",
                    getFormattedArg("Forward", input.getForwardSpeed()), getFormattedArg("Strafe", input.getStrafeSpeed()),
                    getFormattedArg("Sneaking", input.isSneaking()), getFormattedArg("Jumping", input.isJumping()));
        }
        if (packet instanceof CPacketChatMessage && chat.getValue()) {
            CPacketChatMessage chat = (CPacketChatMessage) packet;
            sendLoggerMessage("CPacketChatMessage", getFormattedArg("Msg", chat.getMessage()));
        }
        if (packet instanceof CPacketUseEntity && useEntity.getValue()) {
            CPacketUseEntity use = (CPacketUseEntity) packet;
            sendLoggerMessage("CPacketUseEntity",
                    getFormattedArg("EntityId", use.entityId), getFormattedArg("Action", use.action.name()), getFormattedArg("Hand", use.hand.name()));
        }
        if (packet instanceof CPacketPlayerTryUseItemOnBlock && tryUseItemOnBlock.getValue()) {
            CPacketPlayerTryUseItemOnBlock tryUseItemOnBlock = (CPacketPlayerTryUseItemOnBlock) packet;
            sendLoggerMessage("CPacketPlayerTryUseItemOnBlock",
                    getFormattedArg("X", tryUseItemOnBlock.position.getX()), getFormattedArg("Y", tryUseItemOnBlock.position.getY()), getFormattedArg("Z", tryUseItemOnBlock.position.getZ()),
                    getFormattedArg("FacingX", tryUseItemOnBlock.facingX), getFormattedArg("FacingY", tryUseItemOnBlock.facingY), getFormattedArg("FacingZ", tryUseItemOnBlock.facingZ),
                    getFormattedArg("PlacedBlockDirection", tryUseItemOnBlock.placedBlockDirection.name()), getFormattedArg("Hand", tryUseItemOnBlock.getHand().name()));
        }
        if (packet instanceof CPacketPlayerDigging && digging.getValue()) {
            CPacketPlayerDigging digging = (CPacketPlayerDigging) packet;
            sendLoggerMessage("CPacketPlayerDigging",
                    getFormattedArg("X", digging.getPosition().getX()), getFormattedArg("Y", digging.getPosition().getY()), getFormattedArg("Z", digging.getPosition().getZ()),
                    getFormattedArg("Action", digging.getAction().name()), getFormattedArg("Face", digging.getFacing().name()));
        }
        if (packet instanceof CPacketPlayerTryUseItem && tryUseItem.getValue()) {
            CPacketPlayerTryUseItem tryUseItem = (CPacketPlayerTryUseItem) packet;
            sendLoggerMessage("CPacketPlayerTryUseItem",
                    getFormattedArg("Hand", tryUseItem.getHand().name()));
        }
        if (packet instanceof CPacketPlayerAbilities && abilities.getValue()) {
            CPacketPlayerAbilities abilities = (CPacketPlayerAbilities) packet;
            sendLoggerMessage("CPacketPlayerAbilities",
                    getFormattedArg("Invulnerable", abilities.isInvulnerable()), getFormattedArg("IsFlying", abilities.isFlying()),
                    getFormattedArg("AllowFlying", abilities.isAllowFlying()), getFormattedArg("CreativeMode", abilities.isCreativeMode()));
        }
        if (packet instanceof CPacketConfirmTeleport && teleport.getValue()) {
            CPacketConfirmTeleport confirmTeleport = (CPacketConfirmTeleport) packet;
            sendLoggerMessage("CPacketConfirmTeleport",
                    getFormattedArg("TeleportId", confirmTeleport.getTeleportId()));
        }
        if (packet instanceof CPacketCustomPayload && payload.getValue()) {
            CPacketCustomPayload customPayload = (CPacketCustomPayload) packet;
            sendLoggerMessage("CPacketCustomPayload",
                    getFormattedArg("Channel", customPayload.getChannelName()), getFormattedArg("Data", customPayload.getBufferData()));
        }
        if (packet instanceof CPacketKeepAlive && keepAlive.getValue()) {
            CPacketKeepAlive keepAlive = (CPacketKeepAlive) packet;
            sendLoggerMessage("CPacketKeepAlive",
                    getFormattedArg("Key", keepAlive.getKey()));
        }
        if (packet instanceof CPacketHeldItemChange && itemChange.getValue()) {
            CPacketHeldItemChange heldItemChange = (CPacketHeldItemChange) packet;
            sendLoggerMessage("CPacketHeldItemChange",
                    getFormattedArg("Slot", heldItemChange.getSlotId()));
        }
    }

    private void sendLoggerMessage(String className, String... values) {
        sendMessage(String.format("%s > %s", className, Arrays.toString(values)));
    }

    private String getFormattedArg(String name, Object value) {
        return String.format("%s = %s", name, String.valueOf(value));
    }
}
