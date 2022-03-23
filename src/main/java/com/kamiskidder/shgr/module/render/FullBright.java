package com.kamiskidder.shgr.module.render;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Module {
    public Setting<String> mode = register(new Setting("Mode", "Gamma", new String[]{"Gamma", "Potion"}));
    public Setting<Integer> gamma = register(new Setting("Gamma", 100, 150, 50));

    private float old;

    public FullBright() {
        super("FullBright", Category.RENDER);
    }

    @Override
    public void onUpdate() {
        if (nullCheck())
            return;

        if (mode.getValue().equalsIgnoreCase("Potion")) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 1000, 1, false, false));
        } else {
            mc.player.removeActivePotionEffect(MobEffects.NIGHT_VISION);
        }

        if (mode.getValue().equalsIgnoreCase("Gamma")) {
            mc.gameSettings.gammaSetting = gamma.getValue();
        } else {
            mc.gameSettings.gammaSetting = 0;
        }
    }
}
