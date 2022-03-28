package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomFov extends Module {
    public Setting<Float> fov = new Setting<Float>("Fov", 70.0f);

    public CustomFov() {
        super("CustomFov", Category.RENDER);
    }

    @SubscribeEvent
    public void onEntityViewRender(EntityViewRenderEvent.FOVModifier event) {
        event.setFOV(fov.getValue());
    }
}
