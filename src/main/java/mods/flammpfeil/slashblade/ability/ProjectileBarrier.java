package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.EntityPhantomSwordBase;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;

/**
 * Created by Furia on 15/06/20.
 */
public class ProjectileBarrier {
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){
        if(event.entityLiving == null) return;
        if(!(event.entityLiving instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer)event.entityLiving;
        if(!player.isUsingItem()) return;
        if(player.getItemInUseDuration() < ItemSlashBlade.RequiredChargeTick) return;

        ItemStack stack = player.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;
        if(!stack.isItemEnchanted()) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
        if(level <= 0) return;

        expandBarrier(player);
    }

    private void expandBarrier(EntityPlayer player){
        AxisAlignedBB bb = player.boundingBox.expand(2,2,2);
        List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,bb, ItemSlashBlade.DestructableSelector);
        for(Entity target : list){

            if(target instanceof EntityPhantomSwordBase)
                continue;

            destructEntity(player, target);
        }
    }

    private void destructEntity(EntityPlayer player, Entity target){
        player.onEnchantmentCritical(target);
        target.motionX = 0;
        target.motionY = 0;
        target.motionZ = 0;
        target.setDead();
    }
}
