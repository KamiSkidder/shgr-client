package com.kamiskidder.shgr.event.client;

import com.kamiskidder.shgr.event.SHGREvent;
import net.minecraft.network.Packet;

public class PacketEvent extends SHGREvent {
    public Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Sent extends PacketEvent {
        public Sent(Packet<?> packet) {
            super(packet);
        }
    }
}
