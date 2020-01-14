package mods.flammpfeil.slashblade.ability;

import net.minecraft.init.Enchantments;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/05/10.
 */
public class ChargeFloating {
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase target = event.getEntityLiving();
        if(target == null) return;
        if(!(target instanceof EntityPlayer)) return;
        if(target.getActiveItemStack().isEmpty()) return;

        ItemStack stack = target.getHeldItem(EnumHand.MAIN_HAND);
        if(stack.isEmpty()) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;
        if(!stack.isItemEnchanted()) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING,stack);
        if(level <= 0) return;

        if(((EntityPlayer) target).motionY < 0)
            target.motionY /= level;
            ((EntityPlayer) target).fallDistance = 0.0f;
    }
}
