package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.EntityPhantomSword;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.MessageRangeAttack;
import mods.flammpfeil.slashblade.PacketHandler;
import mods.flammpfeil.slashblade.entity.EntityPhantomSwordBase;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordAirTrickMarker;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 2015/11/19.
 */
public class AirTrick {
    static final String NextAirTrick = "NextAirTrick";
    static final long AirHikeInterval = 5;

    static public boolean doAirTrick(EntityPlayerMP entityPlayer){

        int entityId = entityPlayer.getEntityData().getInteger("LastHitSummonedSwords");

        if(entityId == 0) return false;

        Entity lastHitSS = entityPlayer.worldObj.getEntityByID(entityId);

        if(lastHitSS == null) return false;
        if(!(lastHitSS instanceof EntityPhantomSwordBase)) return false;
        if(lastHitSS.isDead) return false;

        if(lastHitSS.worldObj.getTotalWorldTime() < lastHitSS.getEntityData().getLong(NextAirTrick))
            return false;

        Entity target = ((EntityPhantomSwordBase) lastHitSS).getRidingEntity();

        if(target == null)
            target = lastHitSS;
        else{
            ItemStack blade = entityPlayer.getHeldItem();
            if(blade != null && blade.getItem() instanceof ItemSlashBlade){
                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                int lockonId = ItemSlashBlade.TargetEntityId.get(tag);

                if(lockonId != 0 && lockonId != target.getEntityId())
                    return false;
            }
        }

        if(entityPlayer.playerNetServerHandler == null) return false;

        Vec3 look = entityPlayer.getLookVec();

        look.yCoord = 0;
        look.normalize();
        entityPlayer.onEnchantmentCritical(entityPlayer);

        UntouchableTime.setUntouchableTime(entityPlayer,20,true);

        boolean teleported = false;
        for(look.yCoord = 0.5f; 0.0f < look.yCoord; look.yCoord -= 0.1f){
            Vec3 pos = Vec3.createVectorHelper(-look.xCoord + target.posX, look.yCoord + target.posY, -look.zCoord + target.posZ);

            if(getCanSpawnHere(entityPlayer,pos, target, lastHitSS)){
                entityPlayer.playerNetServerHandler.setPlayerLocation(pos.xCoord,pos.yCoord,pos.zCoord,entityPlayer.rotationYaw,entityPlayer.rotationPitch);
                entityPlayer.onEnchantmentCritical(entityPlayer);
                teleported = true;
                break;
            }
        }
        if(!teleported){
            for(look.yCoord = 0.6f; look.yCoord < 1.5f; look.yCoord += 0.1f){
                Vec3 pos = Vec3.createVectorHelper(-look.xCoord + target.posX, look.yCoord + target.posY, -look.zCoord + target.posZ);

                if(getCanSpawnHere(entityPlayer,pos, target, lastHitSS)){
                    entityPlayer.playerNetServerHandler.setPlayerLocation(pos.xCoord,pos.yCoord,pos.zCoord,entityPlayer.rotationYaw,entityPlayer.rotationPitch);
                    entityPlayer.onEnchantmentCritical(entityPlayer);
                    teleported = true;
                    break;
                }
            }
        }


        if(teleported){
            //lastHitSS.setDead();
            lastHitSS.getEntityData().setLong(NextAirTrick,lastHitSS.worldObj.getTotalWorldTime() + AirHikeInterval);
            entityPlayer.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, "mob.endermen.portal", 1.0F, 1.0F);

            if(!(target instanceof EntityPhantomSwordBase)){
                ItemStack blade = entityPlayer.getHeldItem();
                if(blade != null && blade.getItem() instanceof ItemSlashBlade){
                    NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                    ItemSlashBlade.TargetEntityId.set(tag,target.getEntityId());
                }
            }

            return true;
        }else{
            return false;
        }
    }

    static private boolean getCanSpawnHere(Entity target,Vec3 pos,Entity... ignore)
    {
        AxisAlignedBB bb = setPosition(target, pos.xCoord, pos.yCoord, pos.zCoord);

        List blockCollidList = target.worldObj.getCollidingBoundingBoxes(target, bb);

        return /*target.worldObj.checkNoEntityCollision(bb) && */blockCollidList.isEmpty();// && !target.worldObj.isAnyLiquid(bb);
    }

    static private AxisAlignedBB setPosition(Entity target, double p_70107_1_, double p_70107_3_, double p_70107_5_)
    {
        float f = target.width / 2.0F;
        float f1 = target.height;
        return AxisAlignedBB.getBoundingBox(p_70107_1_ - (double)f, p_70107_3_ - (double)target.yOffset + (double)target.ySize, p_70107_5_ - (double)f, p_70107_1_ + (double)f, p_70107_3_ - (double)target.yOffset + (double)target.ySize + (double)f1, p_70107_5_ + (double)f);
    }

    static public void SummonOrDo(EntityPlayerMP player){

        if(!doAirTrick(player)){

            if(player.worldObj.isRemote) return;

            ItemStack stack = player.getHeldItem();
            if(stack == null) return;
            if(!(stack.getItem() instanceof ItemSlashBlade)) return;

            ItemSlashBlade slashBlade = (ItemSlashBlade)stack.getItem();

            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

            EnumSet<ItemSlashBlade.SwordType> types = slashBlade.getSwordType(stack);


            if(types.contains(ItemSlashBlade.SwordType.Bewitched) && !types.contains(ItemSlashBlade.SwordType.Broken)){

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
                if(0 < level && ItemSlashBlade.ProudSoul.tryAdd(tag,-1,false)){
                    float magicDamage = 1;

                    EntitySummonedSwordAirTrickMarker entitySS = new EntitySummonedSwordAirTrickMarker(player.worldObj, player, magicDamage,90.0f);
                    if (entitySS != null) {

                        entitySS.setInterval(0);

                        entitySS.setLifeTime(30);

                        int targetid = ItemSlashBlade.TargetEntityId.get(tag);
                        entitySS.setTargetEntityId(targetid);

                        if(ItemSlashBlade.SummonedSwordColor.exists(tag))
                            entitySS.setColor(ItemSlashBlade.SummonedSwordColor.get(tag));

                        player.worldObj.spawnEntityInWorld(entitySS);

                        if(player instanceof EntityPlayer)
                            AchievementList.triggerAchievement((EntityPlayer) player, "phantomSword");

                    }
                }
            }
        }
    }
}
