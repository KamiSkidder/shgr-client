package com.kamiskidder.shgr.util.entity;

import com.kamiskidder.shgr.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;

public class EntityUtil implements Util {
    public static List<EntityPlayer> getPlayers() {
        return mc.world.playerEntities.stream().filter(e -> e.entityId != mc.player.entityId).collect(Collectors.toList());
    }

    public static BlockPos getEntityPos(Entity entity) {
        return new BlockPos(entity);
    }

    public static float getPlayerHealth(EntityPlayer player) {
        return player.getHealth() + player.getAbsorptionAmount();
    }
}
