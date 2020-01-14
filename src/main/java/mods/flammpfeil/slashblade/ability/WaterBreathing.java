package mods.flammpfeil.slashblade.ability;

import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/05/17.
 */
public class WaterBreathing {
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase target = event.getEntityLiving();
        if(target == null) return;
        if(!(target instanceof EntityPlayer)) return;
        if(target.getActiveItemStack() == null) return;

        ItemStack stack = target.getHeldItem(EnumHand.MAIN_HAND);
        if(stack.isEmpty()) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;
        if(!stack.isItemEnchanted()) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.RESPIRATION, stack);
        if(level <= 0) return;

        float speedfactor = 0.05f;
        speedfactor *= (float)level;

        if(target.isInWater())
            target.moveRelative(target.moveStrafing, 0, target.moveForward,0.1f + speedfactor);

        target.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING,2,level-1,true,false));
    }
}
