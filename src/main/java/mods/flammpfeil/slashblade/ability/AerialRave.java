package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ItemSlashBlade;
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

        EntityLivingBase target = event.entityLiving;
        if(target == null) return;

        ItemStack stack = target.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId,stack);

        if(0 == level) return;

        if(!target.isSwingInProgress) return;

        if(target.onGround) return;

        if(target.motionY < 0)
            target.motionY = 0;
    }
}
