package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Created by Furia on 14/07/26.
 */
public class JustGuard {

    public static int activeTicks = 7;

    public static TagPropertyAccessor.TagPropertyLong ChargeStart = new TagPropertyAccessor.TagPropertyLong("SBChargeStart");

    public void setJustGuardState(EntityLivingBase e){
        int hurtTicks = Math.min(
                20,
                Math.max(
                        0,
                        e.hurtResistantTime - (int) (e.maxHurtResistantTime / 2.0F)));

        ChargeStart.set(e.getEntityData(), e.worldObj.getTotalWorldTime() + hurtTicks);
    }

    @SubscribeEvent
    public void LivingHurtEvent(LivingHurtEvent e){
        String type = e.source.getDamageType();
        if(!type.equals("anvil")
           && !type.equals("fallingBlock")
           && !type.equals("magic")
           && e.source.getEntity() == null){
            return;
        }

        EntityLivingBase el = e.entityLiving;

        ItemStack stack = e.entityLiving.getHeldItem();
        if(stack != null && stack.getItem() instanceof ItemSlashBlade){

            long cs = ChargeStart.get(el.getEntityData());
            if(0 < cs && el.worldObj.getTotalWorldTime() - cs < activeTicks){
                    e.setCanceled(true);
                NBTTagCompound tag = stack.getTagCompound();

                el.setArrowCountInEntity(-1);

                el.setJumping(true);
                el.motionX = 0;
                el.motionY = 0;
                el.motionZ = 0;


                double yOffset = 0;
                if(!el.onGround){
                    ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.Iai);
                }else{
                    yOffset = 0.5;
                    ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.Saya2);
                }
                el.getEntityData().setDouble("SBLastPosY", el.posY + yOffset);

                ItemSlashBlade.IsCharged.set(tag,true);
                ItemSlashBlade.OnClick.set(tag,true);
                ItemSlashBlade.OnJumpAttacked.set(tag,false);

                ChargeStart.set(el.getEntityData(),-5l);
                e.entityLiving.worldObj.playSoundAtEntity(el, "mob.blaze.hit", 1.0F, 1.0F);
            }
        }
    }
    @SubscribeEvent
    public void LivingUpdateEvent(LivingEvent.LivingUpdateEvent e){
        EntityLivingBase el = e.entityLiving;

        ItemStack stack = el.getHeldItem();
        if(stack != null && stack.getItem() instanceof ItemSlashBlade){

            long cs = ChargeStart.get(el.getEntityData());
            if(cs < 0){
                el.motionX = 0;
                el.motionY = 0;
                el.motionZ = 0;

                el.posY = el.getEntityData().getDouble("SBLastPosY");

                el.setPositionAndUpdate(el.posX,el.posY,el.posZ);

                ChargeStart.set(el.getEntityData(),cs + 1);
            }
        }
    }
}
