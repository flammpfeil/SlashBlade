package mods.flammpfeil.slashblade.util;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Furia on 14/12/25.
 */
public class SlashBladeHooks {
    static public EventBus EventBus = new EventBus();

    static public boolean onUpdateHooks(ItemStack blade,World world,Entity entity,int indexOfMainSlot,boolean isCurrent){
        return EventBus.post(new SlashBladeEvent.OnUpdateEvent(blade, world, entity, indexOfMainSlot, isCurrent));
    }
    static public boolean onEntityBladeStandUpdateHooks(EntityBladeStand entityBladeStand){
        return EventBus.post(new SlashBladeEvent.OnEntityBladeStandUpdateEvent(entityBladeStand));
    }
    static public boolean onImpactEffectHooks(ItemStack stack, EntityLivingBase target, EntityLivingBase user, ItemSlashBlade.ComboSequence sequence){
        return EventBus.post(new SlashBladeEvent.ImpactEffectEvent(stack, target, user, sequence));
    }

    public static boolean onBladeStandAttack(EntityBladeStand entityBladeStand, DamageSource damageSource, float damage) {
        return EventBus.post(new SlashBladeEvent.BladeStandAttack(entityBladeStand,damageSource,damage));
    }
}
