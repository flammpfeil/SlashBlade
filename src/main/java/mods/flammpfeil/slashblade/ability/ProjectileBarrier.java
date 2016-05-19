package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Furia on 15/06/20.
 */
public class ProjectileBarrier {
    @SubscribeEvent
    public void onUpdate(LivingEntityUseItemEvent.Tick event){
        EntityLivingBase player = event.getEntityLiving();
        if(player == null) return;
        if(player.getActiveItemStack() == null) return;
        ItemStack stack = event.getItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;
        if(!stack.isItemEnchanted()) return;


        int ticks = stack.getMaxItemUseDuration() - event.getDuration();
        if(ticks < ItemSlashBlade.RequiredChargeTick) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack);
        if(level <= 0) return;

        expandBarrier(player);
    }

    private void expandBarrier(EntityLivingBase player){
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

    private void destructEntity(EntityLivingBase player, Entity target){
        //EnumParticleTypes.CRIT_MAGIC
        if(player.worldObj instanceof WorldServer)
            ((WorldServer)player.worldObj).getEntityTracker().sendToAllTrackingEntity(player, new SPacketAnimation(target, 5));

        /*
        if(player instanceof EntityPlayer)
            ((EntityPlayer)player).onEnchantmentCritical(target);
        */
        target.motionX = 0;
        target.motionY = 0;
        target.motionZ = 0;
        target.setDead();
    }
}
