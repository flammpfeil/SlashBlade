package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ibxm.Player;
import mods.flammpfeil.slashblade.EntityDrive;
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
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import java.util.List;

/**
 * Created by Furia on 15/06/20.
 */
public class ProjectileBarrier {
    static public boolean isAvailable(EntityLivingBase owner ,ItemStack stack ,int duration){

        if(owner == null) return false;
        if(owner.getHeldItem() == null) return false;
        if(stack == null) return false;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return false;
        if(!stack.isItemEnchanted()) return false;

        if(!owner.onGround)
            return false;

        if(!owner.isSneaking())
            return false;

        if(!(owner instanceof EntityPlayer))
            return false;

        if(!((EntityPlayer) owner).isUsingItem())
            return false;

        int ticks = stack.getMaxItemUseDuration() - duration;
        if(ticks < ItemSlashBlade.RequiredChargeTick) return false;

        if(((ItemSlashBlade)stack.getItem()).getSwordType(stack).contains(ItemSlashBlade.SwordType.Broken))
            return false;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
        if(level <= 0) return false;

        return true;
    }

    @SubscribeEvent
    public void onUpdate(PlayerUseItemEvent.Tick event){
        if(isAvailable(event.entityLiving, event.item, event.duration))
            expandBarrier(event.entityLiving);
    }

    private void expandBarrier(EntityLivingBase player){

        if(player.ticksExisted % 7 == 0)
            player.playSound("flammpfeil.slashblade:swingblade", 1.0F, 0.75F + player.getRNG().nextFloat() * 0.05f);


        AxisAlignedBB bb = player.boundingBox.expand(2,2,2);
        List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,bb, ItemSlashBlade.DestructableSelector);
        for(Entity target : list){

            if(target instanceof EntityPhantomSwordBase)
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
        if(player instanceof EntityPlayer) {
            ((EntityPlayer)player).onEnchantmentCritical(target);
            player.playSound("random.anvil_land" ,0.8f,1.5f + player.getRNG().nextFloat() * 0.5f);
        }
        /*
        if(player instanceof EntityPlayer)
            ((EntityPlayer)player).onEnchantmentCritical(target);
        */

        ItemSlashBlade.damageItem(player.getHeldItem(), 1, player);

        target.motionX = 0;
        target.motionY = 0;
        target.motionZ = 0;
        target.setDead();
    }
}
