package com.kamiskidder.shgr.util.entity;

import com.kamiskidder.shgr.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;

public class EntityType implements Util {

    //skid from 3arthh4ck :skull:
    public static boolean isAnimal(Entity entity) {
        return entity instanceof EntityPig
                || entity instanceof EntityParrot
                || entity instanceof EntityCow
                || entity instanceof EntitySheep
                || entity instanceof EntityChicken
                || entity instanceof EntitySquid
                || entity instanceof EntityBat
                || entity instanceof EntityOcelot
                || entity instanceof EntityHorse
                || entity instanceof EntityLlama
                || entity instanceof EntityMule
                || entity instanceof EntityDonkey
                || entity instanceof EntitySkeletonHorse
                || entity instanceof EntityZombieHorse
                || entity instanceof EntitySnowman
                || entity instanceof EntityWolf
                || entity instanceof EntityRabbit;
    }

    public static boolean isNeutral(Entity entity) {
        return entity instanceof EntityPig
                || entity instanceof EntityParrot
                || entity instanceof EntityCow
                || entity instanceof EntitySheep
                || entity instanceof EntityChicken
                || entity instanceof EntitySquid
                || entity instanceof EntityBat
                || entity instanceof EntityOcelot
                || entity instanceof EntitySkeletonHorse
                || entity instanceof EntityZombieHorse
                || entity instanceof EntityRabbit;
    }

    public static boolean isMonster(Entity entity) {
        return entity instanceof EntityCreeper
                || entity instanceof EntityIllusionIllager
                || entity instanceof EntitySkeleton
                || entity instanceof EntityZombie
                || entity instanceof EntityBlaze
                || entity instanceof EntitySpider
                || entity instanceof EntityWitch
                || entity instanceof EntitySlime
                || entity instanceof EntitySilverfish
                || entity instanceof EntityGuardian
                || entity instanceof EntityEndermite
                || entity instanceof EntityGhast
                || entity instanceof EntityEvoker
                || entity instanceof EntityShulker
                || entity instanceof EntityWitherSkeleton
                || entity instanceof EntityStray
                || entity instanceof EntityVex
                || entity instanceof EntityVindicator
                || entity instanceof EntityPolarBear
                || entity instanceof EntityEnderman
                || entity instanceof EntityIronGolem;
    }

    public static boolean isBoss(Entity entity) {
        return entity instanceof EntityDragon
                || entity instanceof EntityWither
                || entity instanceof EntityGiantZombie;
    }
}
