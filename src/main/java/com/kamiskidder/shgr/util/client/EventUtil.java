package com.kamiskidder.shgr.util.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventUtil {
    public static void register(Object obj) {
        MinecraftForge.EVENT_BUS.register(obj);
    }

    public static void unregister(Object obj) {
        MinecraftForge.EVENT_BUS.unregister(obj);
    }

    public static void post(Event event) {
        MinecraftForge.EVENT_BUS.post(event);
    }
}
