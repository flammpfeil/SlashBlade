package mods.flammpfeil.slashblade.specialeffect;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;

/**
 * Created by Furia on 15/06/20.
 */
public class ProjectileBarrier implements ISpecialEffect {

    private static final String EffectKey = "ProjectileBarrier";

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){
        if(!SpecialEffects.isPlayer(event.entityLiving)) return;
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        ItemStack blade = player.getHeldItem();
        if(!SpecialEffects.isBlade(blade)) return;

        if(!player.isUsingItem()) return;

        switch (SpecialEffects.isEffective(player, blade, this)){
            case Effective:
                expandBarrier(player);
                break;
        }
    }

    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getDefaultRequiredLevel() {
        return 30;
    }

    @Override
    public String getEffectKey() {
        return EffectKey;
    }

    private void expandBarrier(EntityPlayer player){
        AxisAlignedBB bb = player.boundingBox.expand(2,2,2);
        List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,bb, ItemSlashBlade.DestructableSelector);
        for(Entity target : list)
            destructEntity(player, target);
    }

    private void destructEntity(EntityPlayer player, Entity target){
        player.onEnchantmentCritical(target);
        target.motionX = 0;
        target.motionY = 0;
        target.motionZ = 0;
        target.setDead();
    }
}
