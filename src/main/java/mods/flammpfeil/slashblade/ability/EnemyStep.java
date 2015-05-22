package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/05/21.
 */
public class EnemyStep {
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;

        ItemStack stack = target.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        ItemSlashBlade.ComboSequence seq = ItemSlashBlade.getComboSequence(tag);
        if(seq == ItemSlashBlade.ComboSequence.Iai)
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.None);
    }
}
