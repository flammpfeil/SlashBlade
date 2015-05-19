package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Furia on 14/05/27.
 */
public class SlashDimension extends SpecialAttackBase{
    @Override
    public String toString() {
        return "slashdimension";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.worldObj;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        Entity target = null;

        int entityId = ItemSlashBlade.TargetEntityId.get(tag);

        if(entityId != 0){
            Entity tmp = world.getEntityByID(entityId);
            if(tmp != null){
                if(tmp.getDistanceToEntity(player) < 30.0f)
                    target = tmp;
            }
        }

        if(target == null){
            target = getEntityToWatch(player);
        }

        if(target != null){
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            spawnParticle(world, target);
            player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.portal", 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                stack.damageItem(10, player);
            }

            AxisAlignedBB bb = target.boundingBox.copy();
            bb = bb.expand(2.0f, 0.25f, 2.0f);

            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, bb, ItemSlashBlade.AttackableSelector);

            if(!ItemSlashBlade.AttackableSelector.isEntityApplicable(target))
                list.add(target);

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);
            for(Entity curEntity : list){
                StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.SlashDim);
                blade.attackTargetEntity(stack, curEntity, player, true);
                player.onCriticalHit(curEntity);

                if(0 < level){
                    curEntity.hurtResistantTime = 0;
                    DamageSource ds = new EntityDamageSource("directMagic",player).setDamageBypassesArmor().setMagicDamage();
                    curEntity.attackEntityFrom(ds, magicDamage);
                    if(curEntity instanceof EntityLivingBase){
                        StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.SlashDimMagic);
                        stack.hitEntity((EntityLivingBase)curEntity, player);
                    }
                }
            }

        }
    }

    private Entity getEntityToWatch(EntityPlayer player){
        World world = player.worldObj;
        Entity target = null;
        for(int dist = 2; dist < 20; dist+=2){
            AxisAlignedBB bb = player.boundingBox.copy();
            Vec3 vec = player.getLookVec();
            vec = vec.normalize();
            bb = bb.expand(2.0f, 0.25f, 2.0f);
            bb = bb.offset(vec.xCoord*(float)dist,vec.yCoord*(float)dist,vec.zCoord*(float)dist);

            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, bb, ItemSlashBlade.AttackableSelector);
            float distance = 30.0f;
            for(Entity curEntity : list){
                float curDist = curEntity.getDistanceToEntity(player);
                if(curDist < distance)
                {
                    target = curEntity;
                    distance = curDist;
                }
            }
            if(target != null)
                break;
        }
        return target;
    }

    private void spawnParticle(World world, Entity target){
        //target.spawnExplosionParticle();
        world.spawnParticle("largeexplode",
                target.posX,
                target.posY + target.height,
                target.posZ,
                3.0, 3.0, 3.0);
        world.spawnParticle("largeexplode",
                target.posX + 1.0,
                target.posY + target.height + 1.0,
                target.posZ,
                3.0, 3.0, 3.0);
        world.spawnParticle("largeexplode",
                target.posX,
                target.posY + target.height + 0.5,
                target.posZ + 1.0,
                3.0, 3.0, 3.0);
    }
}
