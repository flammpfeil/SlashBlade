package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.EntityDrive;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.entity.EntityJudgmentCutManager;
import mods.flammpfeil.slashblade.entity.EntitySlashDimension;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 14/05/27.
 */
public class SlashDimension extends SpecialAttackBase implements IJustSpecialAttack,ISuperSpecialAttack{
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

        if(target == null) {
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            //spawnParticle(world, target);

            player.playSound("mob.endermen.portal", 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            if(!player.worldObj.isRemote) {
                int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
                float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (0.5f + level / 5.0f);
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if (dim != null) {
                    Vec3 pos = player.getLookVec();
                    {
                        float scale = 5;
                        pos.xCoord *= scale;
                        pos.yCoord *= scale;
                        pos.zCoord *= scale;
                    }
                    pos = pos.addVector(player.posX, player.posY, player.posZ);
                    //pos = pos.add(player.getPositionVector());
                    pos = pos.addVector(0, player.getEyeHeight(), 0);

                    Vec3 offset = Vec3.createVectorHelper(player.posX, player.posY, player.posZ).addVector(0,player.getEyeHeight(),0);
                    Vec3 look = player.getLookVec();
                    Vec3 offsettedLook = offset.addVector(look.xCoord * 5, look.yCoord * 5, look.zCoord * 5);
                    MovingObjectPosition movingobjectposition = world.rayTraceBlocks(offset, offsettedLook);
                    if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                    {
                        Block block = world.getBlock(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
                        if(block != null && block.isCollidable()){
                            Vec3 tmppos = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
                            if(1 < tmppos.distanceTo(Vec3.createVectorHelper(player.posX, player.posY, player.posZ))){
                                pos = tmppos;
                            }
                        }
                    }

                    dim.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
                    dim.setLifeTime(10);
                    dim.setIsSlashDimension(true);
                    world.spawnEntityInWorld(dim);
                }
            }

        }else{
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            spawnParticle(world, target);

            player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.portal", 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            AxisAlignedBB bb = target.boundingBox.copy();
            bb = bb.expand(2.0f, 0.25f, 2.0f);

            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, bb, ItemSlashBlade.AttackableSelector);

            if(!ItemSlashBlade.AttackableSelector.isEntityApplicable(target))
                list.add(target);

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            for(Entity curEntity : list){
                StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.SlashDim);
                blade.attackTargetEntity(stack, curEntity, player, true);
            }

            if(!target.worldObj.isRemote){
                int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
                float magicDamage = 0.5f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if(dim != null){
                    dim.setPosition(target.posX,target.posY + target.height / 2.0,target.posZ);
                    dim.setLifeTime(10);
                    world.spawnEntityInWorld(dim);
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

    @Override
    public void doJustSpacialAttack(ItemStack stack, EntityPlayer player) {
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

        if(target == null) {
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            //spawnParticle(world, target);

            player.playSound("mob.endermen.portal", 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            if(!player.worldObj.isRemote) {
                Vec3 pos = player.getLookVec();
                {
                    float scale = 5;
                    pos.xCoord *= scale;
                    pos.yCoord *= scale;
                    pos.zCoord *= scale;
                }
                pos = pos.addVector(player.posX, player.posY, player.posZ);
                pos = pos.addVector(0, player.getEyeHeight(), 0);

                ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

                Vec3 offset = Vec3.createVectorHelper(player.posX, player.posY, player.posZ).addVector(0,player.getEyeHeight(),0);
                Vec3 look = player.getLookVec();
                Vec3 offsettedLook = offset.addVector(look.xCoord * 5, look.yCoord * 5, look.zCoord * 5);
                MovingObjectPosition movingobjectposition = world.rayTraceBlocks(offset, offsettedLook);
                if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    Block block = world.getBlock(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
                    if(block != null && block.isCollidable()){
                        Vec3 tmppos = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
                        if(1 < tmppos.distanceTo(Vec3.createVectorHelper(player.posX, player.posY, player.posZ))){
                            pos = tmppos;
                        }
                    }
                }

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
                float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (0.5f + level / 5.0f);
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if (dim != null) {
                    dim.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
                    dim.setLifeTime(10);
                    dim.setIsSlashDimension(true);
                    world.spawnEntityInWorld(dim);
                }

                magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);
                for(int i = 0; i < 5;i++){

                    EntityDrive entityDrive = new EntityDrive(world, player, Math.min(1.0f,magicDamage/3.0f),false,0);


                    float rotationYaw = 60 * i + (entityDrive.getRand().nextFloat() - 0.5f) * 60;
                    float rotationPitch = (entityDrive.getRand().nextFloat() - 0.5f) * 60;

                    float fYawDtoR = (  rotationYaw / 180F) * (float)Math.PI;
                    float fPitDtoR = (rotationPitch / 180F) * (float)Math.PI;
                    float fYVecOfst = 0.5f;

                    float motionX = -MathHelper.sin(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst * 2;
                    float motionY = -MathHelper.sin(fPitDtoR) * fYVecOfst;
                    float motionZ =  MathHelper.cos(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst * 2;

                    entityDrive.setLocationAndAngles(pos.xCoord - motionX,
                            pos.yCoord - motionY,
                            pos.zCoord - motionZ,
                            rotationYaw,
                            rotationPitch);
                    entityDrive.setDriveVector(fYVecOfst);
                    entityDrive.setLifeTime(8);
                    entityDrive.setIsMultiHit(false);


                    int rank = StylishRankManager.getStylishRank(player);
                    if(5 <= rank) {
                        EnumSet<ItemSlashBlade.SwordType> type = blade.getSwordType(stack);
                        entityDrive.setIsSlashDimension(type.contains(ItemSlashBlade.SwordType.FiercerEdge));
                    }

                    entityDrive.setRoll(90.0f + 120 * (entityDrive.getRand().nextFloat() - 0.5f));
                    if (entityDrive != null) {
                        world.spawnEntityInWorld(entityDrive);
                    }
                }
            }

        }else{
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            //spawnParticle(world, target);

            player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.portal", 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            AxisAlignedBB bb = target.boundingBox.copy();
            bb = bb.expand(2.0f, 0.25f, 2.0f);

            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, bb, ItemSlashBlade.AttackableSelector);

            if(!ItemSlashBlade.AttackableSelector.isEntityApplicable(target))
                list.add(target);

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            for(Entity curEntity : list){
                StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.SlashDim);
                blade.attackTargetEntity(stack, curEntity, player, true);
                player.onEnchantmentCritical(curEntity);
            }

            int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);
            if(0 < level){
                for(int i = 0; i < 5;i++){

                    EntityDrive entityDrive = new EntityDrive(world, player, Math.min(1.0f,magicDamage/3.0f),false,0);


                    float rotationYaw = target.rotationYaw + 60 * i + (entityDrive.getRand().nextFloat() - 0.5f) * 60;
                    float rotationPitch = (entityDrive.getRand().nextFloat() - 0.5f) * 60;

                    float fYawDtoR = (  rotationYaw / 180F) * (float)Math.PI;
                    float fPitDtoR = (rotationPitch / 180F) * (float)Math.PI;
                    float fYVecOfst = 0.5f;

                    float motionX = -MathHelper.sin(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst * 2;
                    float motionY = -MathHelper.sin(fPitDtoR) * fYVecOfst;
                    float motionZ =  MathHelper.cos(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst * 2;

                    entityDrive.setLocationAndAngles(target.posX - motionX,
                            target.posY + (double) target.getEyeHeight() / 2D - motionY,
                            target.posZ - motionZ,
                            rotationYaw,
                            rotationPitch);
                    entityDrive.setDriveVector(fYVecOfst);
                    entityDrive.setLifeTime(8);
                    entityDrive.setIsMultiHit(false);


                    int rank = StylishRankManager.getStylishRank(player);
                    if(5 <= rank) {
                        EnumSet<ItemSlashBlade.SwordType> type = blade.getSwordType(stack);
                        entityDrive.setIsSlashDimension(type.contains(ItemSlashBlade.SwordType.FiercerEdge));
                    }

                    entityDrive.setRoll(90.0f + 120 * (entityDrive.getRand().nextFloat() - 0.5f));
                    if (entityDrive != null) {
                        world.spawnEntityInWorld(entityDrive);
                    }
                }
            }


            if(!target.worldObj.isRemote){
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if(dim != null){
                    dim.setPosition(target.posX,target.posY + target.height / 2.0,target.posZ);
                    dim.setLifeTime(10);
                    dim.setIsSlashDimension(true);
                    world.spawnEntityInWorld(dim);
                }
            }
        }
    }

    @Override
    public void doSuperSpecialAttack(ItemStack stack, EntityPlayer player) {

        EntityJudgmentCutManager entityDA = new EntityJudgmentCutManager(player.worldObj, player);
        if (entityDA != null) {
            player.worldObj.spawnEntityInWorld(entityDA);
        }
        UntouchableTime.setUntouchableTime(player, 30, true);
    }
}
