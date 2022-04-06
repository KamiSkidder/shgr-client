package com.kamiskidder.shgr.util.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kamiskidder.shgr.util.Util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class BlockUtil implements Util {
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.IRON_TRAPDOOR, Blocks.ENCHANTING_TABLE);

    public static Block getBlock(BlockPos pos) {
        return getBlockState(pos).getBlock();
    }

    public static IBlockState getBlockState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static EnumFacing getDirection(BlockPos pos) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ), new Vec3d(pos));
        if (result == null) return EnumFacing.UP;
        return result.sideHit;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int) r;
        while ((float) x <= (float) cx + r) {
            int z = cz - (int) r;
            while ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;
                while (true) {
                    float f;
                    float f2 = f = sphere ? (float) cy + r : (float) (cy + h);
                    if (!((float) y < f)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < (double) (r * r)) || hollow && dist < (double) ((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static boolean canSeePos(BlockPos pos) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ), new Vec3d(pos));
        return result != null;
    }

    public static BlockPos placeBlock(BlockPos pos, boolean packet) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof net.minecraft.block.BlockAir) && !(block instanceof net.minecraft.block.BlockLiquid))
            return null;
        EnumFacing side = getPlaceableSide(pos);
        if (side == null)
            return null;
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = (new Vec3d(neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
        if (packet) {
            rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite);
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }

        return neighbour;
    }

    public static void rightClickBlock(BlockPos pos, EnumFacing facing, boolean packet) {
        Vec3d hitVec = (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(facing.getDirectionVec())).scale(0.5D));

        if (packet) {
            rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction) {
        float f = (float) (vec.x - (double) pos.getX());
        float f1 = (float) (vec.y - (double) pos.getY());
        float f2 = (float) (vec.z - (double) pos.getZ());
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        mc.player.connection.sendPacket(new CPacketAnimation(hand));

        mc.rightClickDelayTimer = 4;
    }

    public static void rightClickBlock(BlockPos pos, EnumFacing facing, Vec3d hVec, boolean packet) {
        Vec3d hitVec = (new Vec3d(pos)).add(hVec).add((new Vec3d(facing.getDirectionVec())).scale(0.5D));

        if (packet) {
            rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable() && !blackList.contains(BlockUtil.getBlock(neighbour)))
                    return side;
            }
        }
        return null;
    }
}
