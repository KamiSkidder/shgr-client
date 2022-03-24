package com.kamiskidder.shgr.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.kamiskidder.shgr.module.misc.*;
import com.kamiskidder.shgr.module.render.*;
import org.lwjgl.input.Keyboard;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.combat.AutoCrystal;
import com.kamiskidder.shgr.module.combat.KillAura;
import com.kamiskidder.shgr.module.combat.Velocity;
import com.kamiskidder.shgr.module.exploit.PacketFly;
import com.kamiskidder.shgr.module.exploit.XCarry;
import com.kamiskidder.shgr.module.movement.AutoWalk;
import com.kamiskidder.shgr.module.movement.BoatFly;
import com.kamiskidder.shgr.module.movement.EntitySpeed;
import com.kamiskidder.shgr.module.movement.Flight;
import com.kamiskidder.shgr.module.movement.NoFall;
import com.kamiskidder.shgr.module.movement.NoPush;
import com.kamiskidder.shgr.module.movement.NoRotate;
import com.kamiskidder.shgr.module.movement.NoSlow;
import com.kamiskidder.shgr.module.movement.Sprint;
import com.kamiskidder.shgr.module.movement.Step;
import com.kamiskidder.shgr.util.Util;
import com.kamiskidder.shgr.util.client.EventUtil;
import com.kamiskidder.shgr.util.render.RenderUtil;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModuleManager implements Util {
    public static ModuleManager INSTANCE;

    public List<Module> modules = new ArrayList<Module>();

    public ModuleManager() {
        INSTANCE = this;
        //combat
        register(new KillAura());
        register(new Velocity());
        register(new AutoCrystal());
        //exploit
        register(new XCarry());
        register(new PacketFly());
        //movement
        register(new NoRotate());
        register(new NoFall());
        register(new AutoWalk());
        register(new BoatFly());
        register(new Flight());
        register(new NoSlow());
        register(new Sprint());
        register(new NoPush());
        register(new EntitySpeed());
        register(new Step());
        //misc
        register(new AutoDupe());
        register(new LagbackLogger());
        register(new TunnelAssist());
        register(new FakePlayer());
        register(new Breaker());
        register(new BoatAura());
        register(new TimeChanger());
        register(new ChatSuffix());
        register(new FastUse());
        register(new Timer());
        //render
        register(new ClickGui());
        register(new FullBright());
        register(new CameraClip());
        register(new Notification());
        register(new HudEditor());
        register(new ESP());
        register(new Waypoints());
        register(new Nametags());
        register(new StorageESP());
        register(new BlockHighlight());
        register(new Freecam());

        EventUtil.register(this);
    }

    public static ModuleManager getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        execute(Module::onTick);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        execute(Module::onUpdate);
    }

    @SubscribeEvent
    public void onKeyTyped(InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState()) return;
        int key = Keyboard.getEventKey();
        modules.forEach(m -> {
            if (m.getBind() == key) m.toggle();
        });
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) return;
        if (mc.player == null || mc.world == null) return;

        mc.profiler.startSection("candy-plus");
        mc.profiler.startSection("setup");
        RenderUtil.prepare();
        mc.profiler.endSection();

        execute(m -> {
            mc.profiler.startSection(m.getName());
            m.onRender3D();
            mc.profiler.endSection();
        });

        mc.profiler.startSection("release");
        RenderUtil.release();
        mc.profiler.endSection();
        mc.profiler.endSection();
    }

    public List<Module> getModule() {
        return modules;
    }

    public Module getModuleByName(String name) {
        Module r = null;
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) r = module;
        }
        return r;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    private void register(Module module) {
        modules.add(module);
    }

    private void execute(Consumer<? super Module> action) {
        modules.stream().filter(Module::isToggled).forEach(action);
    }
}
