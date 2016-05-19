package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Created by Furia on 15/05/17.
 */
public class FireResistance {

    @SubscribeEvent
    public void onUpdate(LivingEntityUseItemEvent.Tick event){
        EntityLivingBase player = event.getEntityLiving();
        if(player == null) return;
        if(player.getActiveItemStack() == null) return;

        ItemStack stack = event.getItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;
        if(!stack.isItemEnchanted()) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, stack);
        if(level <= 0) return;

        float speedfactor = 0.05f;
        speedfactor *= (float)level;

        if(player.isBurning()){
            player.moveRelative(player.moveStrafing,player.moveForward,0.25f + speedfactor);

            int ticks = stack.getMaxItemUseDuration() - event.getDuration();
            if(ItemSlashBlade.RequiredChargeTick < ticks){
                ReflectionAccessHelper.setFire(player,0);
            }
        }

        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE,2,level-1,true,false));
    }
}
