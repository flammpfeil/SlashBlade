package mods.flammpfeil.slashblade.ability;

import com.google.common.collect.Sets;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Furia on 15/05/16.
 */
public class UntouchableTime {

    static private final String UntouchableTimeOut = "SB.UntouchableTimeOut";
    static private final String IsAvoid = "SB.IsAvoid";
    static private final String LastEffects = "SB.UT.LastEffects";
    static private final String LastNoBurning = "SB.UT.LastNoBurning";

    static public void setUntouchableTime(Entity target, int ticks){
        setUntouchableTime(target,ticks,false);
    }
    static public void setUntouchableTime(Entity target, int ticks,boolean isAvoid){
        target.getEntityData().setLong(UntouchableTimeOut, target.world.getTotalWorldTime() + ticks);

        setIsAvoid(target, isAvoid);

        if(target instanceof EntityLivingBase)
            storePotionEffect((EntityLivingBase)target);
    }

    static private void setIsAvoid(Entity target, boolean isAvoid){
        target.getEntityData().setBoolean(IsAvoid, isAvoid);
    }
    static private boolean isAvoid(Entity target){
        return target.getEntityData().getBoolean(IsAvoid);
    }

    static private void removeUntouchableTag(Entity target){
        NBTTagCompound tag = target.getEntityData();
        String[] targetKeys = {UntouchableTimeOut,IsAvoid,LastEffects,LastNoBurning};

        for(String key : targetKeys){
            if(tag.hasKey(key))
                tag.removeTag(key);
        }
    }

    static private boolean isUntouchable(EntityLivingBase target) {
        if (target == null) return false;
        if (target.world == null) return false;
        NBTTagCompound tag = target.getEntityData();
        if (!tag.hasKey(UntouchableTimeOut)) return false;

        long timeOut = tag.getLong(UntouchableTimeOut);
        long now = target.world.getTotalWorldTime();

        if (timeOut < now) {
            removeUntouchableTag(target);
            return false;
        }
		return true;
    }

    static private void doAvoid(EntityLivingBase target){
        if(!isAvoid(target)) return;

        StylishRankManager.addRankPoint(target,StylishRankManager.AttackTypes.AttackAvoidance);
        setIsAvoid(target, false);
    }

    private void WitchTime(Entity target, int duration) {
        if (target == null) return;
        if (!(target instanceof EntityLivingBase)) return;

        StunManager.setStun((EntityLivingBase) target, duration);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurt(LivingHurtEvent event){
        EntityLivingBase target = event.getEntityLiving();
        if(!isUntouchable(target)) return;

        doAvoid(target);

        WitchTime(event.getSource().getTrueSource(), 10);

        event.setCanceled(true);
        event.setAmount(0);
    }

    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event){
        EntityLivingBase target = event.getEntityLiving();
        if(!isUntouchable(target)) return;

        doAvoid(target);

        WitchTime(event.getSource().getTrueSource(), 10);

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void LivingUpdateEvent(LivingEvent.LivingUpdateEvent event){
        if(isUntouchable(event.getEntityLiving()))
            restorePotionEffect(event.getEntityLiving());
    }

    static private void storePotionEffect(EntityLivingBase entity){
        Collection<?> effects = entity.getActivePotionEffects();
        if (!effects.isEmpty())
        {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator<?> iterator = effects.iterator();

            while (iterator.hasNext())
            {
                PotionEffect potioneffect = (PotionEffect)iterator.next();
                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            entity.getEntityData().setTag(LastEffects, nbttaglist);
        }

        if(!entity.isBurning())
            entity.getEntityData().setBoolean(LastNoBurning,true);
    }

    static private void restorePotionEffect(EntityLivingBase entity){
        if(entity.getEntityData().hasKey(LastNoBurning)){
            ReflectionAccessHelper.setFire(entity,0);
        }

        if(!entity.getEntityData().hasKey(LastEffects)) return;

        for(Potion potion : Sets.newHashSet(entity.getActivePotionMap().keySet()))
           entity.removePotionEffect(potion);

        NBTTagList nbttaglist = entity.getEntityData().getTagList(LastEffects, 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
            PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(tag);

            if (potioneffect != null)
            {
                entity.addPotionEffect(potioneffect);
            }
        }
    }
}
