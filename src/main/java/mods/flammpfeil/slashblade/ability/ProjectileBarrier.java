package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import java.util.List;

/**
 * Created by Furia on 15/06/20.
 */
public class ProjectileBarrier {
    @SubscribeEvent
    public void onUpdate(PlayerUseItemEvent.Tick event){
        EntityPlayer player = event.entityPlayer;
        if(player == null) return;
        if(!player.isUsingItem()) return;
        ItemStack stack = event.item;
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;
        if(!stack.isItemEnchanted()) return;


        int ticks = stack.getMaxItemUseDuration() - event.duration;
        if(ticks < ItemSlashBlade.RequiredChargeTick) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
        if(level <= 0) return;

        expandBarrier(player);
    }

    private void expandBarrier(EntityPlayer player){
        AxisAlignedBB bb = player.getEntityBoundingBox().expand(2,2,2);
        List<Entity> list = player.worldObj.getEntitiesInAABBexcluding(player,bb, EntitySelectorDestructable.getInstance());
        for(Entity target : list){

            if(target instanceof EntitySummonedSwordBase)
                continue;

            if(target instanceof IThrowableEntity){
                if(((IThrowableEntity) target).getThrower() == player)
                    continue;
            }

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
