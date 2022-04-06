package com.kamiskidder.shgr.mixin.mixins;

import com.kamiskidder.shgr.event.player.PlayerMoveEvent;
import com.kamiskidder.shgr.event.player.UpdateLivingPlayerEvent;
import com.kamiskidder.shgr.event.player.UpdatePlayerEvent;
import com.kamiskidder.shgr.event.player.UpdateWalkingPlayerEvent;
import com.kamiskidder.shgr.manager.RotateManager;
import com.kamiskidder.shgr.module.movement.NoPush;
import com.kamiskidder.shgr.module.movement.NoSlow;
import com.kamiskidder.shgr.util.client.EventUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends EntityPlayer {
    @Shadow
    @Final
    public NetHandlerPlayClient connection;
    @Shadow
    public MovementInput movementInput;
    @Shadow
    protected Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Shadow
    protected abstract void updateAutoJump(float p_189810_1_, float p_189810_2_);

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayer(CallbackInfo ci) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(true);
        EventUtil.post(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void onUpdateWalkingPlayerReturn(CallbackInfo ci) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(false);
        EventUtil.post(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "onLivingUpdate()V", at = @At("HEAD"))
    public void onLivingUpdate(CallbackInfo ci) {
        EventUtil.post(new UpdateLivingPlayerEvent());
    }

    @Redirect(method = "onLivingUpdate()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    public boolean isHandActive(EntityPlayerSP instance) {
        if (NoSlow.INSTANCE.isToggled() && NoSlow.INSTANCE.item.getValue()) {
            return false;
        } else {
            return instance.handActive;
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void onUpdatePlayer(CallbackInfo ci) {
        UpdatePlayerEvent event = new UpdatePlayerEvent();
        EventUtil.post(event);

        if (RotateManager.isRotating()) {
            ci.cancel();
            if (this.world.isBlockLoaded(new BlockPos(this.posX, 0.0D, this.posZ))) {
                super.onUpdate();
                if (this.isRiding()) {
                    this.connection.sendPacket(new CPacketPlayer.Rotation(RotateManager.getYaw(),
                            RotateManager.getPitch(), this.onGround));
                    this.connection.sendPacket(new CPacketInput(this.moveStrafing, this.moveForward,
                            this.movementInput.jump, this.movementInput.sneak));
                    Entity entity = this.getLowestRidingEntity();

                    if (entity != this && entity.canPassengerSteer()) {
                        this.connection.sendPacket(new CPacketVehicleMove(entity));
                    }
                } else {
                    UpdateWalkingPlayerEvent uwpEvent = new UpdateWalkingPlayerEvent(true);
                    EventUtil.post(uwpEvent);
                    if (!uwpEvent.isCanceled()) {
                        updateWalkingPlayer();
                    }
                }
            }
        }
    }

    private void updateWalkingPlayer() {
        EntityPlayerSP a = mc.player;

        boolean flag = a.isSprinting();
        if (flag != a.serverSprintState) {
            if (flag) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            a.serverSprintState = flag;
        }

        boolean flag1 = this.isSneaking();

        if (flag1 != a.serverSneakState) {
            if (flag1) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            a.serverSneakState = flag1;
        }

        if (mc.getRenderViewEntity() == a) {
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            double d0 = this.posX - a.lastReportedPosX;
            double d1 = axisalignedbb.minY - a.lastReportedPosY;
            double d2 = this.posZ - a.lastReportedPosZ;
            double d3 = RotateManager.getYaw() - a.lastReportedYaw;
            double d4 = RotateManager.getPitch() - a.lastReportedPitch;
            ++a.positionUpdateTicks;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || a.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;

            if (this.isRiding()) {
                this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0D, this.motionZ,
                        RotateManager.getYaw(), RotateManager.getPitch(), this.onGround));
                flag2 = false;
            } else if (flag2 && flag3) {
                this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.posX, axisalignedbb.minY, this.posZ,
                        RotateManager.getYaw(), RotateManager.getPitch(), this.onGround));
            } else if (flag2) {
                this.connection.sendPacket(
                        new CPacketPlayer.Position(this.posX, axisalignedbb.minY, this.posZ, this.onGround));
            } else if (flag3) {
                this.connection.sendPacket(
                        new CPacketPlayer.Rotation(RotateManager.getYaw(), RotateManager.getPitch(), this.onGround));
            } else if (a.prevOnGround != this.onGround) {
                this.connection.sendPacket(new CPacketPlayer(this.onGround));
            }
            if (flag2) {
                a.lastReportedPosX = this.posX;
                a.lastReportedPosY = axisalignedbb.minY;
                a.lastReportedPosZ = this.posZ;
                a.positionUpdateTicks = 0;
            }
            if (flag3) {
                a.lastReportedYaw = RotateManager.getYaw();
                a.lastReportedPitch = RotateManager.getPitch();
            }
            a.prevOnGround = this.onGround;
            a.autoJumpEnabled = this.mc.gameSettings.autoJump;
        }

        UpdateWalkingPlayerEvent uwpEvent = new UpdateWalkingPlayerEvent(false);
        EventUtil.post(uwpEvent);
    }

    @Redirect(method = {"move"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType moverType, double x, double y, double z) {
        PlayerMoveEvent event = new PlayerMoveEvent(moverType, x, y, z);
        EventUtil.post(event);
        if (!event.isCanceled()) {
            super.move(event.type, event.x, event.y, event.z);
        }
    }

    @Inject(method = "pushOutOfBlocks(DDD)Z", at = @At(value = "HEAD"), cancellable = true)
    public void pushOutOfBlocks(double d2, double f, double blockpos, CallbackInfoReturnable<Boolean> cir) {
        if (NoPush.INSTANCE.isToggled()) {
            cir.setReturnValue(true);
        }
    }
}
