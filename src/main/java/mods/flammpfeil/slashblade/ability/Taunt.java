package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;

import java.util.List;

/**
 * Created by Furia on 2016/10/07.
 */
public class Taunt {

    public static void fire(ItemStack stack , EntityLivingBase player){
        if(player.worldObj.isRemote)
           return;
        if(!(player.worldObj instanceof WorldServer))
            return;

        AxisAlignedBB bb = player.getEntityBoundingBox();
        bb = bb.expand(10, 5, 10);
        List<Entity> list = player.worldObj.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());

        int soundCounter = 0;
        for(Entity entity : list){
            if(!(entity instanceof EntityLivingBase))
                continue;
            EntityLivingBase livingEntity = (EntityLivingBase) entity;
            DamageSource ds = DamageSource.causeMobDamage(player);

            livingEntity.setRevengeTarget(player);
            livingEntity.getCombatTracker().trackDamage(ds, livingEntity.getHealth(), 100);

            livingEntity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,600,1));
            livingEntity.addPotionEffect(new PotionEffect(MobEffects.SPEED,600,1));
            PotionEffect effect = livingEntity.removeActivePotionEffect(MobEffects.RESISTANCE);
            int level = -1;
            if(effect != null)
                level = Math.max(level ,effect.getAmplifier() - 1);
            livingEntity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,600, level));

            ((WorldServer)livingEntity.worldObj).spawnParticle(EnumParticleTypes.VILLAGER_ANGRY,
                    livingEntity.posX,
                    livingEntity.posY,
                    livingEntity.posZ,
                    5,
                    livingEntity.width * 2.0F, livingEntity.height, livingEntity.width * 2.0F,
                    0.02d, new int[0]);

            if(soundCounter++ < 3)
                player.worldObj.playSound(null, livingEntity.posX, livingEntity.posY, livingEntity.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5F, 1.0F);
        }
    }
}
