package mods.flammpfeil.slashblade.specialattack;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.entity.EntityPhantomSwordBase;
import mods.flammpfeil.slashblade.entity.EntityWitherSword;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Furia on 15/06/21.
 */
public class BlisteringWitherSwords extends SpecialAttackBase {
    @Override
    public String toString() {
        return "BlisteringWitherSwords";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.worldObj;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(!world.isRemote){

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

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

                final int cost = -40;
                if(!ItemSlashBlade.ProudSoul.tryAdd(tag,cost,false)){
                    ItemSlashBlade.damageItem(stack, 10, player);
                }


                StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.PhantomSword);
                blade.attackTargetEntity(stack, target, player, true);
                player.onCriticalHit(target);

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                //target.addVelocity(0.0, 0.55D, 0.0);

                if(target instanceof EntityLivingBase){
                    blade.setDaunting((EntityLivingBase)target);
                    ((EntityLivingBase) target).hurtTime = 0;
                    ((EntityLivingBase) target).hurtResistantTime = 0;
                }

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
                float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);

                int count = 1 + StylishRankManager.getStylishRank(player);

                for(int i = 0; i < count;i++){

                    if(!world.isRemote){
                        boolean isBurst = (i % 2 == 0);

                        EntityWitherSword entityDrive = new EntityWitherSword(world, player, magicDamage,90.0f);
                        if (entityDrive != null) {
                            entityDrive.setInterval(7+i*2);
                            entityDrive.setLifeTime(30);

                            int color = isBurst ? -0x6896cc : -0x1c1c1c;
                            entityDrive.setColor(color);

                            entityDrive.setBurst(isBurst);

                            entityDrive.setTargetEntityId(target.getEntityId());

                            world.spawnEntityInWorld(entityDrive);

                        }
                    }
                }
            }
        }
        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.Kiriorosi);
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
}
