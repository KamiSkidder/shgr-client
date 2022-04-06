package com.kamiskidder.shgr.module.combat;

import com.kamiskidder.shgr.event.player.UpdateLivingPlayerEvent;
import com.kamiskidder.shgr.event.player.UpdatePlayerMoveStateEvent;
import com.kamiskidder.shgr.manager.ModuleManager;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.module.movement.Scaffold;
import com.kamiskidder.shgr.util.client.MathUtil;
import com.kamiskidder.shgr.util.entity.EntityUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class AutoGaiji extends Module {
    public static AutoGaiji INSTANCE;
    public Setting<Float> distance = register(new Setting("Distance", 15.0f, 30.0f, 5.0f));
    public Setting<Boolean> scaffold = register(new Setting("Scaffold", true));
    public Setting<Boolean> render = register(new Setting("Render", true));
    public Setting<Color> color = register(new Setting("Color", new Color(255, 0, 0, 100), v -> render.getValue()));
    public EntityPlayer target = null;
    private int randomX, randomZ = 0;
    private boolean annoy = false;
    public AutoGaiji() {
        super("AutoGaiji", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        Scaffold module = (Scaffold) ModuleManager.INSTANCE.getModuleByClass(Scaffold.class);
        if (module.isToggled() && scaffold.getValue())
            module.toggle();

        mc.gameSettings.keyBindUseItem.pressed = false;
    }

    @Override
    public void onTick() {
        if (target == null) return;

        annoy = !PlayerUtil.canSeeEntity(target);
    }

    @SubscribeEvent
    public void onPlayerUpdate(UpdateLivingPlayerEvent event) {
        if (nullCheck() || target == null) return;

        Scaffold module = (Scaffold) ModuleManager.INSTANCE.getModuleByClass(Scaffold.class);
        if (!module.isToggled() && scaffold.getValue())
            module.toggle();

        //75
        if (mc.player.posY < 74 || !mc.player.onGround)
            return;

        boolean cansee = true;
        if (getDistance(target) > distance.getValue() || annoy) {
            cansee = false;
            BlockPos pos = EntityUtil.getEntityPos(target);

            if (annoy) {
                pos = pos.add(randomX, 0, randomZ);
            }

            if (annoy
                    && PlayerUtil.getDistance(new BlockPos(pos.getX(), mc.player.posY, pos.getZ())) < 5) {
                int dis = distance.getValue().intValue() + 1;
                randomX = dis * MathUtil.getRandom(-1, 1);
                randomZ = dis * MathUtil.getRandom(-1, 1);
            }

            double x = pos.getX() - mc.player.posX;
            double z = pos.getZ() - mc.player.posZ;

            double dx = x * x;
            double dz = z * z;

            double speed = 0.25;
            if (Math.abs(dx) > Math.abs(dz)) {
                mc.player.motionX = x > 0 ? speed : -speed;
            } else {
                mc.player.motionZ = z > 0 ? speed : -speed;
            }
        } else {
            int dis = distance.getValue().intValue() + 1;
            randomX = dis * MathUtil.getRandom(-1, 1);
            randomZ = dis * MathUtil.getRandom(-1, 1);
        }

        if (cansee && mc.player.posY > 74) {
            if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBow) {
                        mc.player.inventory.currentItem = i;
                        mc.playerController.updateController();
                        break;
                    }
                }
            }

            RotateManager.lookAtEntity(target);

            if (mc.currentScreen == null)
                mc.gameSettings.keyBindUseItem.pressed = true;

            if (72000 - mc.player.getItemInUseCount() > 24)
                mc.playerController.onStoppedUsingItem(mc.player);
        }

        //stopping
        if (Math.abs(mc.player.motionX) < 0.05 && Math.abs(mc.player.motionY) < 0.05 && Math.abs(mc.player.motionZ) < 0.05) {
            //BlockPos pos = PlayerUtil.getPlayerPos();
            //mc.player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }
    }

    @SubscribeEvent
    public void onUpdatePlayerMoveState(UpdatePlayerMoveStateEvent event) {
        if (!(mc.player.posY > 74)) {
            mc.player.movementInput.jump = true;
        }
    }

    public float getDistance(Entity entityIn) {
        float f = (float) (mc.player.posX - entityIn.posX);
        float f2 = (float) (mc.player.posZ - entityIn.posZ);
        return MathHelper.sqrt(f * f + f2 * f2);
    }
}
