package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.capability.MobEffect.CapabilityMobEffectHandler;
import mods.flammpfeil.slashblade.capability.MobEffect.IMobEffectHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.entity.ai.EntityAIStun;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/06/20.
 */
public class StunManager {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event){
        if(!(event.getEntity() instanceof EntityLiving)) return;
        EntityLiving entity = (EntityLiving) event.getEntity();

        entity.tasks.addTask(-1,new EntityAIStun(entity));
    }

    @SubscribeEvent
    public void onEntityLivingUpdate(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase target = event.getEntityLiving();
        if(target == null) return;
        if(target.world == null) return;

        IMobEffectHandler mobEffect = target.getCapability(CapabilityMobEffectHandler.MOB_EFFECT, null);
        if(mobEffect == null) return;

        if(!mobEffect.isStun(target.world.getTotalWorldTime(), EntityAIStun.timeoutLimit)){
            return;
        }

        /*
        long timeout = target.getEntityData().getLong(EntityAIStun.StunTimeout);
        if(timeout == 0) return;
        timeout = timeout - target.world.getTotalWorldTime();
        if(timeout <= 0 || EntityAIStun.timeoutLimit < timeout){
            target.getEntityData().removeTag(EntityAIStun.StunTimeout);
            return;
        }
        */

        if(target.motionY < 0)
            target.motionY *= 0.5f;
    }

    public static void setStun(EntityLivingBase target, long duration){
        if(target.world == null) return;
        if(!(target instanceof EntityLiving)) return;
        if(duration <= 0) return;

        duration = Math.min(duration, EntityAIStun.timeoutLimit);


        IMobEffectHandler mobEffect = target.getCapability(CapabilityMobEffectHandler.MOB_EFFECT, null);
        if(mobEffect == null) return;

        mobEffect.setStunTimeOut(target.world.getTotalWorldTime() + duration);

        //target.getEntityData().setLong(EntityAIStun.StunTimeout,target.world.getTotalWorldTime() + duration);
    }

    public static void removeStun(EntityLivingBase target){
        if(target.world == null) return;
        if(!(target instanceof EntityLiving)) return;


        IMobEffectHandler mobEffect = target.getCapability(CapabilityMobEffectHandler.MOB_EFFECT, null);
        if(mobEffect == null) return;

        mobEffect.clearFreezeTimeOut();
        mobEffect.clearStunTimeOut();
        /*
        target.getEntityData().removeTag(EntityAIStun.StunTimeout);
        target.getEntityData().removeTag(FreezeTimeout);
        */
    }

    static final String FreezeTimeout = "FreezeTimeout";
    static final long freezeLimit = 200;

    @SubscribeEvent
    public void onEntityCanUpdate(EntityEvent.CanUpdate event){
        if(event.isCanceled()) return;
        Entity target = event.getEntity();
        if(target == null) return;
        if(target.world == null) return;

        IMobEffectHandler mobEffect = target.getCapability(CapabilityMobEffectHandler.MOB_EFFECT, null);
        if(mobEffect == null) return;

        if(mobEffect.isFreeze(target.world.getTotalWorldTime(), freezeLimit)){
            event.setCanUpdate(false);
        }

        /*
        long timeout = target.getEntityData().getLong(FreezeTimeout);
        if(timeout == 0) return;

        long timeLeft = timeout - target.world.getTotalWorldTime();
        if(timeLeft <= 0 || freezeLimit < timeLeft){
            target.getEntityData().removeTag(FreezeTimeout);
            return;
        }else{
            event.setCanUpdate(false);
        }
        */
    }

    public static void setFreeze(EntityLivingBase target, long duration){
        if(target.world == null) return;
        if(!(target instanceof EntityLiving)) return;
        if(duration <= 0) return;

        duration = Math.min(duration, freezeLimit);

        IMobEffectHandler mobEffect = target.getCapability(CapabilityMobEffectHandler.MOB_EFFECT, null);
        if(mobEffect == null) return;

        long oldTimeout = mobEffect.getFreezeTimeOut();
        long timeout = target.world.getTotalWorldTime() + duration;
        if(oldTimeout < timeout)
           mobEffect.setFreezeTimeOut(timeout);

        /*
        long oldTimeout = target.getEntityData().getLong(FreezeTimeout);
        long timeout = target.world.getTotalWorldTime() + duration;

        timeout = Math.max(oldTimeout, timeout);

        target.getEntityData().setLong(FreezeTimeout,timeout);
        */
    }
}
