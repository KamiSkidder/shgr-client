package com.kamiskidder.shgr.module.combat;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
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
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGaiji extends Module {
    public static AutoGaiji INSTANCE;
    public Setting<Float> distance = register(new Setting("Distance", 15.0f, 30.0f, 5.0f));
    public Setting<Boolean> mct = register(new Setting("MiddleClick", true));
    public Setting<Boolean> right = register(new Setting("Right", false, v -> mct.getValue()));
    public Setting<Boolean> scaffold = register(new Setting("Scaffold", true));
    public Setting<Boolean> render = register(new Setting("Render", true));
    public Setting<Color> color = register(new Setting("Color", new Color(255, 0, 0, 100), v -> render.getValue()));
    public EntityPlayer target = null;
    public boolean isAiming = false;
    private int randomX, randomZ = 0;
    private boolean annoy = false;
    private boolean clicked = false;

    public AutoGaiji() {
        super("AutoGaiji", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        Scaffold module = (Scaffold) ModuleManager.INSTANCE.getModuleByClass(Scaffold.class);
        if (module.isToggled() && scaffold.getValue())
            module.toggle();

        target = null;
        mc.gameSettings.keyBindUseItem.pressed = false;
    }

    @Override
    public void onTick() {
        if (mct.getValue()) {
            boolean mouse = (Mouse.isButtonDown(2) || (right.getValue() && Mouse.isButtonDown(1)));
            if (!clicked && mouse) {
                clicked = true;
                onMouseClicked();
            } else if (clicked && !mouse) {
                clicked = false;
            }
        }

        if (target == null) return;
        //reset
        if (target.isDead || target.getHealth() < 0.1f) {
            target = null;
            sendMessage("The Target already dead or unloaded!");
            disable();
            return;
        }

        annoy = !PlayerUtil.canSeeEntity(target);
    }

    public void onMouseClicked() {
        Entity entityHit = getMouseOver(1.0f);

        if (entityHit instanceof EntityPlayer) {
            target = (EntityPlayer) entityHit;
            sendMessage("Target set to " + target.getDisplayNameString());
        }
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
        
        //bruhmoment
        if (BlockUtil.getBlock(EntityUtil.getEntityPos(target).add(0, 2, 0)) != Blocks.AIR)
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
                int dis = distance.getValue().intValue() / 2;
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

            isAiming = true;
            RotateManager.lookAtEntity(target);

            if (!mc.player.isHandActive()) {
                mc.rightClickMouse();
            }

            if (72000 - mc.player.getItemInUseCount() > 24) {
            	mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.01, mc.player.posZ, true));
                mc.playerController.onStoppedUsingItem(mc.player);
            }
        } else {
            isAiming = false;
        }

        //stopping
        if (Math.abs(mc.player.motionX) < 0.02 && Math.abs(mc.player.motionY) < 0.02 && Math.abs(mc.player.motionZ) < 0.02) {
            BlockPos pos = PlayerUtil.getPlayerPos();
            mc.player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }
    }

    @SubscribeEvent
    public void onUpdatePlayerMoveState(UpdatePlayerMoveStateEvent event) {
        if (target == null) return;

        if (!(mc.player.posY > 74)) {
            mc.player.movementInput.jump = true;
        }
    }

    public float getDistance(Entity entityIn) {
        float f = (float) (mc.player.posX - entityIn.posX);
        float f2 = (float) (mc.player.posZ - entityIn.posZ);
        return MathHelper.sqrt(f * f + f2 * f2);
    }

    private Entity getMouseOver(float partialTicks) {
        Entity entity = mc.player;

        if (entity != null) {
            if (this.mc.world != null) {
                double d0 = 100;
                double d1 = d0;
                Vec3d vec3d = entity.getPositionEyes(partialTicks);
                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
                float f = 1.0F;
                List<Entity> list = this.mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                    public boolean apply(@Nullable Entity p_apply_1_) {
                        return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                Entity pointedEntity = null;
                for (int j = 0; j < list.size(); ++j) {
                    Entity entity1 = list.get(j);
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(entity1.getCollisionBorderSize());
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

                    if (axisalignedbb.contains(vec3d)) {
                        if (d2 >= 0.0D) {
                            pointedEntity = entity1;
                            d2 = 0.0D;
                        }
                    } else if (raytraceresult != null) {
                        double d3 = vec3d.distanceTo(raytraceresult.hitVec);

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                                if (d2 == 0.0D) {
                                    pointedEntity = entity1;
                                }
                            } else {
                                pointedEntity = entity1;
                                d2 = d3;
                            }
                        }
                    }
                }

                return pointedEntity;
            }
        }

        return null;
    }
}
