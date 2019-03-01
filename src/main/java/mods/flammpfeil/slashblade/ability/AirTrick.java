package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.entity.EntitySummonedSwordAirTrickMarker;
import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
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

        int entityId = entityPlayer.getEntityData().getInt("LastHitSummonedSwords");

        if(entityId == 0) return false;

        Entity lastHitSS = entityPlayer.world.getEntityByID(entityId);

        if(lastHitSS == null) return false;
        if(!(lastHitSS instanceof EntitySummonedSwordBase)) return false;
        if(lastHitSS.isDead) return false;

        if(lastHitSS.world.getGameTime() < lastHitSS.getEntityData().getLong(NextAirTrick))
            return false;

        Entity target = ((EntitySummonedSwordBase) lastHitSS).getRidingEntity();

        if(target == null)
            target = lastHitSS;
        else{
            ItemStack blade = entityPlayer.getHeldItemMainhand();
            if(!blade.isEmpty() && blade.getItem() instanceof ItemSlashBlade){
                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                int lockonId = ItemSlashBlade.TargetEntityId.get(tag);

                if(lockonId != 0 && lockonId != target.getEntityId())
                    return false;
            }
        }

        if(entityPlayer.connection == null) return false;

        Vec3d look = entityPlayer.getLookVec();

        look.normalize();
        entityPlayer.onEnchantmentCritical(entityPlayer);

        UntouchableTime.setUntouchableTime(entityPlayer,20,true);

        boolean teleported = false;
        for(double y = 0.5; 0.0 < y; y -= 0.1){
            Vec3d pos = new Vec3d(-look.x + target.posX, y + target.posY, -look.z + target.posZ);

            if(getCanSpawnHere(entityPlayer,pos, target, lastHitSS)){
                entityPlayer.connection.setPlayerLocation(pos.x,pos.y,pos.z,entityPlayer.rotationYaw,entityPlayer.rotationPitch);
                entityPlayer.onEnchantmentCritical(entityPlayer);
                teleported = true;
                break;
            }
        }
        if(!teleported){
            for(double y = 0.6; y < 1.5; y += 0.1){
                Vec3d pos = new Vec3d(-look.x + target.posX, y + target.posY, -look.z + target.posZ);

                if(getCanSpawnHere(entityPlayer,pos, target, lastHitSS)){
                    entityPlayer.connection.setPlayerLocation(pos.x,pos.y,pos.z,entityPlayer.rotationYaw,entityPlayer.rotationPitch);
                    entityPlayer.onEnchantmentCritical(entityPlayer);
                    teleported = true;
                    break;
                }
            }
        }


        if(teleported){
            //lastHitSS.setDead();
            lastHitSS.getEntityData().setLong(NextAirTrick,lastHitSS.world.getGameTime() + AirHikeInterval);
            //entityPlayer.world.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, "mob.endermen.portal", 1.0F, 1.0F);

            entityPlayer.world.playSound((EntityPlayer)null, entityPlayer.prevPosX, entityPlayer.prevPosY, entityPlayer.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, entityPlayer.getSoundCategory(), 1.0F, 1.0F);
            entityPlayer.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

            if(!(target instanceof EntitySummonedSwordBase)){
                ItemStack blade = entityPlayer.getHeldItemMainhand();
                if(!blade.isEmpty() && blade.getItem() instanceof ItemSlashBlade){
                    NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                    ItemSlashBlade.TargetEntityId.set(tag,target.getEntityId());
                }
            }

            return true;
        }else{
            return false;
        }
    }

    static private boolean getCanSpawnHere(Entity target,Vec3d pos,Entity... ignore)
    {
        AxisAlignedBB bb = setPosition(target, pos.x, pos.y, pos.z);

        return /*!target.world.isAnyLiquid(target.getBoundingBox()) && */target.world.getCollisionBoxes(target, bb).isEmpty() /*&& target.world.checkNoEntityCollision(bb, target)*/;

        //List blockCollidList = target.world.getCollidingBoundingBoxes(target, bb);

        //return /*target.world.checkNoEntityCollision(bb) && */blockCollidList.isEmpty();// && !target.world.isAnyLiquid(bb);
    }

    static private AxisAlignedBB setPosition(Entity target, double x, double y, double z)
    {
        float f = target.width / 2.0F;
        float f1 = target.height;
        return new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f);
    }

    static public void SummonOrDo(EntityPlayerMP player){

        if(!doAirTrick(player)){

            if(player.world.isRemote) return;

            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            if(stack.isEmpty()) return;
            if(!(stack.getItem() instanceof ItemSlashBlade)) return;

            ItemSlashBlade slashBlade = (ItemSlashBlade)stack.getItem();

            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

            EnumSet<ItemSlashBlade.SwordType> types = slashBlade.getSwordType(stack);


            if(types.contains(ItemSlashBlade.SwordType.Bewitched) && !types.contains(ItemSlashBlade.SwordType.Broken)){

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                if(0 < level && ItemSlashBlade.ProudSoul.tryAdd(tag,-1,false)){
                    float magicDamage = 1;

                    EntitySummonedSwordAirTrickMarker entitySS = new EntitySummonedSwordAirTrickMarker(player.world, player, magicDamage,90.0f);
                    if (entitySS != null) {

                        entitySS.setInterval(0);

                        entitySS.setLifeTime(30);

                        int targetid = ItemSlashBlade.TargetEntityId.get(tag);
                        entitySS.setTargetEntityId(targetid);

                        if(ItemSlashBlade.SummonedSwordColor.exists(tag))
                            entitySS.setColor(ItemSlashBlade.SummonedSwordColor.get(tag));

                        ScheduleEntitySpawner.getInstance().offer(entitySS);
                        //player.world.spawnEntity(entitySS);

                        /* todo:advancement
                        if(player instanceof EntityPlayer)
                            AchievementList.triggerAchievement((EntityPlayer) player, "phantomSword");
                        */

                    }
                }
            }
        }
    }
}
