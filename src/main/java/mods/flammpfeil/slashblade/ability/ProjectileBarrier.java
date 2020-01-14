package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.SoundCategory;
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
    static public boolean isAvailable(EntityLivingBase owner ,ItemStack stack ,int duration){

        if(owner == null) return false;
        if(owner.getActiveItemStack().isEmpty()) return false;
        if(stack.isEmpty()) return false;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return false;
        if(!stack.isItemEnchanted()) return false;

        if(!owner.onGround)
            return false;

        if(!owner.isSneaking())
            return false;

        int ticks = stack.getMaxItemUseDuration() - duration;
        if(ticks < ItemSlashBlade.RequiredChargeTick) return false;

        if(((ItemSlashBlade)stack.getItem()).getSwordType(stack).contains(ItemSlashBlade.SwordType.Broken))
            return false;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack);
        if(level <= 0) return false;

        return true;
    }

    @SubscribeEvent
    public void onUpdate(LivingEntityUseItemEvent.Tick event){
        if(isAvailable(event.getEntityLiving(), event.getItem(), event.getDuration()))

        expandBarrier(event.getEntityLiving());
    }

    private void expandBarrier(EntityLivingBase player){

        if(player.ticksExisted % 7 == 0)
            player.world.playSound(null, player.posX, player.posY, player.posZ
                    , SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS,0.45f,0.5f);


        AxisAlignedBB bb = player.getEntityBoundingBox().grow(2,2,2);
        List<Entity> list = player.world.getEntitiesInAABBexcluding(player,bb, EntitySelectorDestructable.getInstance());
        for(Entity target : list){
            if(player.getHeldItemMainhand().isEmpty()) break;

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
        if(player.world instanceof WorldServer) {
            ((WorldServer) player.world).getEntityTracker().sendToTrackingAndSelf(player, new SPacketAnimation(target, 5));
            player.world.playSound(null, player.posX, player.posY, player.posZ
                    , SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS,0.8f,1.5f + player.getRNG().nextFloat() * 0.5f);
        }
        /*
        if(player instanceof EntityPlayer)
            ((EntityPlayer)player).onEnchantmentCritical(target);
        */

        ItemSlashBlade.damageItem(player.getHeldItemMainhand(), 1, player);

        ReflectionAccessHelper.setVelocity(target,0,0,0);
        target.setDead();
    }
}
