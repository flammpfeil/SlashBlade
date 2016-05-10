package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

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
        if(!(lastHitSS instanceof EntitySummonedSwordBase)) return false;
        if(lastHitSS.isDead) return false;

        if(lastHitSS.worldObj.getTotalWorldTime() < lastHitSS.getEntityData().getLong(NextAirTrick))
            return false;

        Entity target = ((EntitySummonedSwordBase) lastHitSS).getRidingEntity();

        if(target == null)
            target = lastHitSS;

        if(entityPlayer.playerNetServerHandler == null) return false;

        Vec3d look = entityPlayer.getLookVec();

        look.normalize();
        entityPlayer.onEnchantmentCritical(entityPlayer);

        UntouchableTime.setUntouchableTime(entityPlayer,20,true);

        boolean teleported = false;
        for(double y = 0.5; 0.0 < y; y -= 0.1){
            Vec3d pos = new Vec3d(-look.xCoord + target.posX, y + target.posY, -look.zCoord + target.posZ);

            if(getCanSpawnHere(entityPlayer,pos, target, lastHitSS)){
                entityPlayer.playerNetServerHandler.setPlayerLocation(pos.xCoord,pos.yCoord,pos.zCoord,entityPlayer.rotationYaw,entityPlayer.rotationPitch);
                entityPlayer.onEnchantmentCritical(entityPlayer);
                teleported = true;
                break;
            }
        }
        if(!teleported){
            for(double y = 0.6; y < 1.5; y += 0.1){
                Vec3d pos = new Vec3d(-look.xCoord + target.posX, y + target.posY, -look.zCoord + target.posZ);

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
            //entityPlayer.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, "mob.endermen.portal", 1.0F, 1.0F);

            entityPlayer.worldObj.playSound((EntityPlayer)null, entityPlayer.prevPosX, entityPlayer.prevPosY, entityPlayer.prevPosZ, SoundEvents.entity_endermen_teleport, entityPlayer.getSoundCategory(), 1.0F, 1.0F);
            entityPlayer.playSound(SoundEvents.entity_endermen_teleport, 1.0F, 1.0F);

            return true;
        }else{
            return false;
        }
    }

    static private boolean getCanSpawnHere(Entity target,Vec3d pos,Entity... ignore)
    {
        AxisAlignedBB bb = setPosition(target, pos.xCoord, pos.yCoord, pos.zCoord);

        return /*!target.worldObj.isAnyLiquid(target.getEntityBoundingBox()) && */target.worldObj.getCubes(target, bb).isEmpty() /*&& target.worldObj.checkNoEntityCollision(bb, target)*/;

        //List blockCollidList = target.worldObj.getCollidingBoundingBoxes(target, bb);

        //return /*target.worldObj.checkNoEntityCollision(bb) && */blockCollidList.isEmpty();// && !target.worldObj.isAnyLiquid(bb);
    }

    static private AxisAlignedBB setPosition(Entity target, double x, double y, double z)
    {
        float f = target.width / 2.0F;
        float f1 = target.height;
        return new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f);
    }

    static public void SummonOrDo(EntityPlayerMP player){

        if(!doAirTrick(player)){

            if(player.worldObj.isRemote) return;

            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            if(stack == null) return;
            if(!(stack.getItem() instanceof ItemSlashBlade)) return;

            ItemSlashBlade slashBlade = (ItemSlashBlade)stack.getItem();

            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

            EnumSet<ItemSlashBlade.SwordType> types = slashBlade.getSwordType(stack);


            if(types.contains(ItemSlashBlade.SwordType.Bewitched) && !types.contains(ItemSlashBlade.SwordType.Broken)){

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.power, stack);
                if(0 < level && ItemSlashBlade.ProudSoul.tryAdd(tag,-1,false)){
                    float magicDamage = 1;

                    EntitySummonedSwordBase entitySS = new EntitySummonedSwordBase(player.worldObj, player, magicDamage,90.0f);
                    if (entitySS != null) {

                        entitySS.getEntityData().setBoolean("IsAirTrick",true);

                        entitySS.setInterval(0);

                        entitySS.setLifeTime(30);

                        int targetid = ItemSlashBlade.TargetEntityId.get(tag);
                        entitySS.setTargetEntityId(targetid);

                        Vec3d eyeDir = player.getLookVec();

                        entitySS.setLocationAndAngles(
                                player.posX + eyeDir.xCoord * 2,
                                player.posY + eyeDir.yCoord * 2 + player.getEyeHeight(),
                                player.posZ + eyeDir.zCoord * 2,
                                player.rotationYaw,
                                player.rotationPitch);

                        entitySS.setDriveVector(1.75f,true);

                        if(ItemSlashBlade.SummonedSwordColor.exists(tag))
                            entitySS.setColor(ItemSlashBlade.SummonedSwordColor.get(tag));

                        ScheduleEntitySpawner.getInstance().offer(entitySS);
                        //player.worldObj.spawnEntityInWorld(entitySS);

                        if(player instanceof EntityPlayer)
                            AchievementList.triggerAchievement((EntityPlayer) player, "phantomSword");

                    }
                }
            }
        }
    }
}
