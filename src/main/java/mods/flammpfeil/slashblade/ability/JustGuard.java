package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import mods.flammpfeil.slashblade.entity.EntityJustGuardManager;
import mods.flammpfeil.slashblade.entity.EntitySakuraEndManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Furia on 14/07/26.
 */
public class JustGuard {

    public static int activeTicks = 7;
    public static long interval = -5;

    public static TagPropertyAccessor.TagPropertyLong ChargeStart = new TagPropertyAccessor.TagPropertyLong("SBChargeStart");

    public static  boolean atJustGuard(EntityLivingBase e){
        long last = ChargeStart.get(e.getEntityData());
        return last < 0;
    }

    public static void setJustGuardState(EntityLivingBase e){
        int hurtTicks = Math.min(
                20,
                Math.max(
                        0,
                        e.hurtResistantTime - (int) (e.maxHurtResistantTime / 2.0F)));

        long last = ChargeStart.get(e.getEntityData());
        if(!atJustGuard(e))
            ChargeStart.set(e.getEntityData(), e.worldObj.getTotalWorldTime() + hurtTicks);
    }

    @SubscribeEvent
    public void LivingHurtEvent(LivingHurtEvent e){
        String type = e.source.getDamageType();

        if(e.isCanceled()) return;
        if(e.entity == null) return;
        if(!(e.entity instanceof EntityPlayer)) return;

        EntityLivingBase el = e.entityLiving;

        ItemStack stack = e.entityLiving.getHeldItem();
        if(el instanceof  EntityPlayer && ((EntityPlayer)el).isUsingItem() && stack != null && stack.getItem() instanceof ItemSlashBlade){

            //mobからの攻撃は常にguard可能
            boolean guardable = e.source.getEntity() != null;
            //特殊guardも通常guard不可なものはguard不可
            if(!guardable && e.source.isUnblockable()) return;

            long cs = ChargeStart.get(el.getEntityData());
            if(0 < cs && el.worldObj.getTotalWorldTime() - cs < activeTicks){

                e.setCanceled(true);
                e.ammount = 0;
                UntouchableTime.setUntouchableTime(el,20);


                NBTTagCompound tag = stack.getTagCompound();

                el.setArrowCountInEntity(-1);

                el.motionX = 0;
                el.motionY = 0;
                el.motionZ = 0;


                double yOffset = 0;
                if(el.onGround){
                    yOffset = 0.5;
                }
                el.getEntityData().setDouble("SBLastPosY", el.posY + yOffset);

                storePotionEffect(el);

                ItemSlashBlade.IsCharged.set(tag,true);
                ItemSlashBlade.OnClick.set(tag,true);
                ItemSlashBlade.OnJumpAttacked.set(tag,false);

                ChargeStart.set(el.getEntityData(), interval);
                e.entityLiving.worldObj.playSoundAtEntity(el, "mob.blaze.hit", 1.0F, 1.0F);


                StylishRankManager.addRankPoint(el, StylishRankManager.AttackTypes.JustGuard);


                EntityJustGuardManager entityManager = new EntityJustGuardManager(el.worldObj, el);
                if (entityManager != null) {
                    el.worldObj.spawnEntityInWorld(entityManager);
                }
            }
        }
    }
    @SubscribeEvent
    public void LivingUpdateEvent(LivingEvent.LivingUpdateEvent e){
        EntityLivingBase el = e.entityLiving;

        ItemStack stack = el.getHeldItem();
        if(stack != null && stack.getItem() instanceof ItemSlashBlade){

            long cs = ChargeStart.get(el.getEntityData());
            cs = Math.max(interval,cs);
            if(cs < 0){

                if(cs == -1)
                    restorePotionEffect(el);

                el.motionX = 0;
                el.motionY = 0;
                el.motionZ = 0;

                el.posY = el.getEntityData().getDouble("SBLastPosY");

                el.setPositionAndUpdate(el.posX,el.posY,el.posZ);

                ChargeStart.set(el.getEntityData(),cs + 1);
            }
        }
    }


    void storePotionEffect(EntityLivingBase entity){
        Collection effects = entity.getActivePotionEffects();
        if (!effects.isEmpty())
        {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = effects.iterator();

            while (iterator.hasNext())
            {
                PotionEffect potioneffect = (PotionEffect)iterator.next();
                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            entity.getEntityData().setTag("SB_JG_LastEffects", nbttaglist);
        }
    }

    void restorePotionEffect(EntityLivingBase entity){

        if(!entity.getEntityData().hasKey("SB_JG_LastEffects")) return;

        entity.clearActivePotions();

        NBTTagList nbttaglist = entity.getEntityData().getTagList("SB_JG_LastEffects", 10);
        entity.getEntityData().removeTag("SB_JG_LastEffects");

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound1);

            if (potioneffect != null)
            {
                entity.addPotionEffect(potioneffect);
            }
        }
    }
}
