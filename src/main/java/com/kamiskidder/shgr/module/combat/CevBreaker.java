package com.kamiskidder.shgr.module.combat;

import com.kamiskidder.shgr.event.client.PacketEvent;
import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.client.Timer;
import com.kamiskidder.shgr.util.entity.EntityUtil;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.InventoryUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CevBreaker extends Module {
    public Setting<Float> range = register(new Setting("Range", 5.0f, 15.0f, 1.0f));
    public Setting<Boolean> predict = register(new Setting("Predict", true));
    public Setting<String> targetMode = register(new Setting("Target Mode", "Best", new String[]{"Best", "Nearest"}));
    public Setting<String> swingArm = register(new Setting("Swing Arm", "Main Hand", new String[]{"Main Hand", "Off Hand", "None"}));
    public Setting<Boolean> packetPlace = register(new Setting("Packet Place", true));
    public Setting<Float> blockDelay = register(new Setting("Block Delay", 0.0f, 1.0f, 0.0f));
    public Setting<Float> crystalDelay = register(new Setting("Crystal Delay", 0.0f, 1.0f, 0.0f));
    public Setting<Float> breakDelay = register(new Setting("Break Delay", 0.0f, 1.0f, 0.0f));
    public Setting<Float> explodeDelay = register(new Setting("Explode Delay", 0.0f, 1.0f, 0.0f));
    public Setting<Boolean> tick = register(new Setting("One Tick", true));
    public Setting<Boolean> strict = register(new Setting("Strict", false));

    private EntityPlayer target;
    private BlockPos lastPos;
    private boolean builtCev, placedCrystal, brokeCeil, brokeCrystal;
    private boolean receivedBrokePacket, sentUsePacket;
    private Timer blockTimer, crystalTimer, breakTimer, explodeTimer;
    private int obby;
    private boolean placed0, placed1, placed2, placed3;
    private int counter;

    public CevBreaker() {
        super("CevBreaker", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        target = null;
        lastPos = null;
        builtCev = false;
        placedCrystal = false;
        brokeCeil = false;
        brokeCrystal = false;
        receivedBrokePacket = false;
        blockTimer = new Timer();
        crystalTimer = new Timer();
        breakTimer = new Timer();
        explodeTimer = new Timer();
        obby = -1;
        placed0 = false;
        placed1 = false;
        placed2 = false;
        placed3 = false;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            if (lastPos == null) return;

            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51 && predict.getValue() &&
                    packet.getX() == lastPos.getX() && packet.getY() == lastPos.getY() && packet.getZ() == lastPos.getZ()) {
                CPacketUseEntity useEntity = new CPacketUseEntity();
                useEntity.entityId = packet.getEntityID();
                useEntity.action = CPacketUseEntity.Action.ATTACK;

                mc.player.connection.sendPacket(useEntity);
                sentUsePacket = true;
            }
        }

        if (event.getPacket() instanceof SPacketBlockChange) {
            if (lastPos == null) return;

            SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();
            if (packet.getBlockPosition().equals(lastPos) && packet.getBlockState().getBlock() instanceof BlockAir) {
                receivedBrokePacket = true;
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS
                    && packet.getSound().equals(SoundEvents.ENTITY_GENERIC_EXPLODE)) {
                for (EntityEnderCrystal crystal : mc.world.getEntities(EntityEnderCrystal.class, e -> true)) {
                    if (packet.getX() == crystal.posX && packet.getY() == crystal.posY && packet.getZ() == crystal.posZ) {
                        crystal.setDead();
                    }
                }
            }
        }
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;
        counter = 0;
        updateCev();
    }

    private void updateCev() {
        obby = InventoryUtil.getBlockHotbar(Blocks.OBSIDIAN);
        int crystalSlot = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL
                ? 999 : InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        int pix = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);

        if (obby == -1 || crystalSlot == -1 || pix == -1) {
            return;
        }

        if (!findTarget() || !canCevBreak()) {
            return;
        }

        if (PlayerUtil.getDistance(target) > range.getValue()) {
            target = null;
            return;
        }

        BlockPos pos = EntityUtil.getEntityPos(target);
        BlockPos ceil = pos.add(0, 2, 0);

        if (blockTimer.passedD(blockDelay.getValue()) && !builtCev) {
            crystalTimer.reset();
            if (!buildCev()) {
                if (target == null) return;
                if (tick.getValue() && counter < 8) updateCev();
                return;
            }
            builtCev = true;
        }

        if (crystalTimer.passedD(crystalDelay.getValue()) && builtCev && !placedCrystal) {
            breakTimer.reset();
            if (crystalSlot != 999) {
                mc.player.inventory.currentItem = crystalSlot;
                mc.playerController.updateController();
            }
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(ceil,
                    EnumFacing.UP, crystalSlot == 999 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            placedCrystal = true;
        }

        if (breakTimer.passedD(breakDelay.getValue()) && placedCrystal && !brokeCeil) {
            explodeTimer.reset();
            
            mc.player.inventory.currentItem = pix;
            mc.playerController.updateController();
            
            if (!ceil.equals(lastPos)) {
                receivedBrokePacket = false;
                EnumFacing facing = BlockUtil.getDirection(ceil);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, ceil, facing));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, ceil, facing));
                mc.playerController.onPlayerDamageBlock(ceil, facing);
            }
            
            lastPos = ceil;

            if (BlockUtil.getBlock(ceil) != Blocks.OBSIDIAN && receivedBrokePacket) {
                brokeCeil = true;
            }
        }

        if (explodeTimer.passedD(explodeDelay.getValue()) && brokeCeil && !brokeCrystal) {
            if (!sentUsePacket) {
                Entity crystal = mc.world.loadedEntityList.stream()
                        .filter(e -> e instanceof EntityEnderCrystal && EntityUtil.getEntityPos(e).equals(ceil.add(0, 1, 0))).findFirst().orElse(null);
                if (crystal == null)
                    return;
                mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            }
            brokeCrystal = true;
        }
        
        if (brokeCrystal) {
            Entity crystal = mc.world.loadedEntityList.stream()
                    .filter(e -> !e.isDead && e instanceof EntityEnderCrystal && EntityUtil.getEntityPos(e).equals(ceil.add(0, 1, 0)))
                    .findFirst().orElse(null);
            if (crystal == null) {
                builtCev = false;
                placedCrystal = false;
                brokeCeil = false;
                brokeCrystal = false;
                sentUsePacket = false;
                receivedBrokePacket = false;
                blockTimer = new Timer();
                crystalTimer = new Timer();
                breakTimer = new Timer();
                explodeTimer = new Timer();
                obby = -1;
                placed0 = false;
                placed1 = false;
                placed2 = false;
                placed3 = false;
            }
        }
    }

    private boolean buildCev() {
    	counter++;
        BlockPos targetPos = EntityUtil.getEntityPos(target).add(0, 2, 0);
        EnumFacing offset = getCevOffset(target);
        if (offset == null) {
            target = null;
            return false;
        }

        if (BlockUtil.isBlockBreakable(targetPos) || placed0) {
            return true;
        }

        mc.player.inventory.currentItem = obby;
        mc.playerController.updateController();

        BlockPos pos0 = targetPos.offset(offset);
        BlockPos pos1 = targetPos.offset(offset).add(0, -1, 0);
        EnumFacing side;
        if ((side = BlockUtil.getPlaceableSide(targetPos)) != null || placed1) {
            if (side != null) {
                BlockUtil.placeBlock(targetPos, packetPlace.getValue());
            } else {
                EnumFacing reverse;
                switch (offset) {
                    case NORTH: reverse = EnumFacing.SOUTH; break;
                    case SOUTH: reverse = EnumFacing.NORTH; break;
                    case EAST: reverse = EnumFacing.WEST; break;
                    case WEST: default: reverse = EnumFacing.EAST; break;
                }
                BlockUtil.rightClickBlock(pos0, reverse, packetPlace.getValue());
            }
            placed0 = true;
        }
        else if ((side = BlockUtil.getPlaceableSide(pos0)) != null || placed2) {
            placeCevBlock(pos0, side);
            if (!strict.getValue())
                placed1 = true;
        } else if ((side = BlockUtil.getPlaceableSide(pos1)) != null || placed3) {
            placeCevBlock(pos1, side);
            if (!strict.getValue())
                placed2 = true;
        } else {
            BlockUtil.placeBlock(pos1.add(0, -1, 0), packetPlace.getValue());
            if (!strict.getValue())
                placed3 = true;
        }

        return false;
    }

    private void placeCevBlock(BlockPos pos, EnumFacing side) {
        if (side != null) {
            BlockUtil.placeBlock(pos, packetPlace.getValue());
        } else {
            BlockUtil.rightClickBlock(pos.add(0, -1, 0), EnumFacing.UP, packetPlace.getValue());
        }
    }

    private boolean canCevBreak() {
        //crystal check
        if (BlockUtil.isBlockBreakable(EntityUtil.getEntityPos(target).add(0, 3, 0))) {
            return false;
        }
        //jumping check
        if (target.posY - Math.floor(target.posY) > 0.5) {
            return false;
        }
        return true;
    }

    private EnumFacing getCevOffset(EntityPlayer target) {
        BlockPos targetPos = EntityUtil.getEntityPos(target);
        BlockPos pos = targetPos.add(0, 2, 0);

        List<EnumFacing> n = new ArrayList<>();
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH}) {
            BlockPos p = pos.offset(facing);
            if (BlockUtil.canRightClickBlock(p)) {
            	n.clear();
            	n.add(facing);
            	break;
            } else if (BlockUtil.isBlockAir(p)) {
            	n.add(facing);
            }
        }

        if (n.size() == 0) return null;

        List<EnumFacing> offsets = new ArrayList<>();
        for (EnumFacing facing : n) {
            BlockPos offsetPos = pos.offset(facing);
            BlockPos p = offsetPos.add(0, -1, 0);
            BlockPos p1 = offsetPos.add(0, -2, 0);
            if (!isBlockAirOrPlaceable(p)) continue;
            if (!isBlockAirOrPlaceable(p1)) continue;
            if (BlockUtil.isBlockAir(p1) && BlockUtil.getPlaceableSide(p1) == null) continue;
            offsets.add(facing);
        }

        return offsets.stream()
                .max(Comparator.comparing(o -> PlayerUtil.getDistance(targetPos.offset(o)))).orElse(null);
    }

    private boolean isBlockAirOrPlaceable(BlockPos pos) {
        return BlockUtil.canRightClickBlock(pos) || BlockUtil.isBlockAir(pos);
    }

    private boolean findTarget() {
        if (target != null) return true;

        if (targetMode.getValue().equals("Best")) {
            target = EntityUtil.getEnemyPlayers()
                    .stream().filter(e -> {
                        BlockPos pos = EntityUtil.getEntityPos(e).add(0, 2, 0);
                        Block block = BlockUtil.getBlock(pos);
                        return (block instanceof BlockObsidian || block instanceof BlockAir) && getCevOffset(e) != null && PlayerUtil.getDistance(e) <= range.getValue();
                    }).min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        } else {
            target = EntityUtil.getEnemyPlayers().stream().filter(e -> PlayerUtil.getDistance(e) <= range.getValue())
                    .min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
        }

        return target != null;
    }
}
