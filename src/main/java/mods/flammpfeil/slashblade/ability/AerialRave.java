package mods.flammpfeil.slashblade.ability;

import net.minecraft.init.Enchantments;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/05/25.
 */
public class AerialRave {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event){

        EntityLivingBase target = event.getEntityLiving();
        if(target == null) return;

        ItemStack stack = target.getHeldItem(EnumHand.MAIN_HAND);
        if(stack.func_190926_b()) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING,stack);

        if(0 == level) return;

        if(!target.isSwingInProgress) return;

        if(target.onGround) return;

        if(target.motionY < 0)
            target.motionY = 0;
    }
}
