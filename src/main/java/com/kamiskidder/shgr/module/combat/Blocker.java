package com.kamiskidder.shgr.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import com.kamiskidder.shgr.module.Category;
import com.kamiskidder.shgr.module.Module;
import com.kamiskidder.shgr.module.Setting;
import com.kamiskidder.shgr.util.entity.EntityUtil;
import com.kamiskidder.shgr.util.player.BlockUtil;
import com.kamiskidder.shgr.util.player.InventoryUtil;
import com.kamiskidder.shgr.util.player.PlayerUtil;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class Blocker extends Module {
    public Setting<Float> range = register(new Setting("Range", 5.0f, 10.0f, 0.5f));
    public Setting<Boolean> cev = register(new Setting("Cev Blocker", true));
    public Setting<Boolean> civ = register(new Setting("Civ Blocker", true));
    public Setting<Boolean> packetPlace = register(new Setting("Packet Place", true));

    private ArrayList<BlockPos> queue = new ArrayList<>();

    public Blocker() {
        super("Blocker", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        int obby = InventoryUtil.getBlockHotbar(Blocks.OBSIDIAN);
        if (obby == -1) return;

        BlockPos pos = PlayerUtil.getPlayerPos();

        if (cev.getValue()) {
            BlockPos[] offsets = new BlockPos[] {
                    new BlockPos(0, 2, 0),
                    new BlockPos(0, 3, 0),
            };

            for (BlockPos offset : offsets) {
                if (block(pos.add(offset))) break;
            }
        }

        if (civ.getValue()) {
            BlockPos[] offsets = new BlockPos[] {
                    new BlockPos(1, 1, 0),
                    new BlockPos(-1, 1, 0),
                    new BlockPos(0, 1, 1),
                    new BlockPos(0, 1, -1),
            };

            for (BlockPos offset : offsets) {
                if (block(pos.add(offset))) break;
            }
        }

        try {
            Iterator<BlockPos> it = queue.iterator();
            while (it.hasNext()) {
                BlockPos n = it.next();
                if (n == null) continue;
                
                if (PlayerUtil.getDistance(n) > range.getValue()) {
                    it.remove();
                    continue;
                }

                Entity crystal = getCrystal(n);
                if (crystal == null) {
                    int old = mc.player.inventory.currentItem;
                    EnumHand activeHand = mc.player.activeHand;
                    mc.player.inventory.currentItem = obby;
                    mc.playerController.updateController();

                    if (!BlockUtil.isBlockBreakable(n)) {
                        if (BlockUtil.getPlaceableSide(n) == null) {
                            it.remove();
                            return;
                        }
                        BlockUtil.placeBlock(n, packetPlace.getValue());
                    } 
                    
                    BlockUtil.rightClickBlock(n, EnumFacing.UP, packetPlace.getValue());

                    mc.player.inventory.currentItem = old;
                    mc.player.activeHand = activeHand;
                    mc.playerController.updateController();
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, pos.getY() + 0.1, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, pos.getY(), mc.player.posZ, false));
                }
                
                if (!(BlockUtil.getBlock(n.add(0, 1, 0)) instanceof BlockAir)) {
                    it.remove();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean block(BlockPos pos) {
        Entity entity = getCrystal(pos);
        if (entity != null && BlockUtil.getPlaceableSide(pos) != null) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            queue.add(pos);
            return true;
        }
        return false;
    }

    private EntityEnderCrystal getCrystal(BlockPos pos) {
        return (EntityEnderCrystal) mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityEnderCrystal && !e.isDead && pos.equals(EntityUtil.getEntityPos(e).add(0, -1, 0)))
                .min(Comparator.comparing(PlayerUtil::getDistance)).orElse(null);
    }
}
