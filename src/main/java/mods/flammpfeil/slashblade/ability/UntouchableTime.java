package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Created by Furia on 15/05/16.
 */
public class UntouchableTime {

    static final String UntouchableTimeOut = "SB.UntouchableTimeOut";
    static final String IsAvoid = "SB.IsAvoid";

    static public void setUntouchableTime(Entity target, int ticks){
        setUntouchableTime(target,ticks,false);
    }
    static public void setUntouchableTime(Entity target, int ticks,boolean isAvoid){
        target.getEntityData().setLong(UntouchableTimeOut,target.worldObj.getTotalWorldTime() + ticks);

        setIsAvoid(target, isAvoid);
    }

    static public void setIsAvoid(Entity target, boolean isAvoid){
        target.getEntityData().setBoolean(IsAvoid,isAvoid);
    }

    static public void removeUntouchableTag(Entity target){
        NBTTagCompound tag = target.getEntityData();
        String[] targetKeys = {UntouchableTimeOut,IsAvoid};

        for(String key : targetKeys){
            if(tag.hasKey(key))
                tag.removeTag(key);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurt(LivingHurtEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;
        if(target.worldObj == null) return;
        NBTTagCompound tag = target.getEntityData();
        if(!tag.hasKey(UntouchableTimeOut)) return;

        long timeOut = tag.getLong(UntouchableTimeOut);

        long now = target.worldObj.getTotalWorldTime();

        if(timeOut < now) return;

        if(tag.getBoolean(IsAvoid)){
            StylishRankManager.addRankPoint(target,StylishRankManager.AttackTypes.AttackAvoidance);
            setIsAvoid(target, false);
        }

        event.setCanceled(true);
        event.ammount = 0;
    }

    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;
        if(target.worldObj == null) return;
        NBTTagCompound tag = target.getEntityData();
        if(!tag.hasKey(UntouchableTimeOut)) return;

        long timeOut = tag.getLong(UntouchableTimeOut);

        long now = target.worldObj.getTotalWorldTime();

        if(timeOut < now) return;

        if(tag.getBoolean(IsAvoid)){
            StylishRankManager.addRankPoint(target,StylishRankManager.AttackTypes.AttackAvoidance);
            setIsAvoid(target, false);
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void LivingUpdateEvent(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;
        if(target.worldObj == null) return;
        NBTTagCompound tag = target.getEntityData();
        if(!tag.hasKey(UntouchableTimeOut)) return;

        long timeOut = tag.getLong(UntouchableTimeOut);

        long now = target.worldObj.getTotalWorldTime();


        if(timeOut < now)
            removeUntouchableTag(target);
    }
}
