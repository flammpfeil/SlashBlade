package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/05/17.
 */
public class WaterBreathing {
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;
        if(!(target instanceof EntityPlayer)) return;
        if(!((EntityPlayer) target).isUsingItem()) return;

        ItemStack stack = target.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;
        if(!stack.isItemEnchanted()) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, stack);
        if(level <= 0) return;

        float speedfactor = 0.05f;
        speedfactor *= (float)level;

        if(target.isInWater())
            target.moveFlying(target.moveStrafing,target.moveForward,0.1f + speedfactor);

        target.addPotionEffect(new PotionEffect(Potion.waterBreathing.getId(),2,level-1,true));
    }
}
