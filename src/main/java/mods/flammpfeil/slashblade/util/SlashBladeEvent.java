package mods.flammpfeil.slashblade.util;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
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
public class SlashBladeEvent extends Event{

    public ItemStack blade;

    public SlashBladeEvent(ItemStack blade){
        this.blade = blade;
    }

    @Cancelable
    public static class ImpactEffectEvent extends SlashBladeEvent{

        public ItemSlashBlade.ComboSequence sequence;
        public EntityLivingBase target;
        public EntityLivingBase user;

        public ImpactEffectEvent(ItemStack stack, EntityLivingBase target, EntityLivingBase user, ItemSlashBlade.ComboSequence sequence) {
            super(stack);
            this.target = target;
            this.user = user;
            this.sequence = sequence;
        }
    }

    @Cancelable
    public static class OnEntityBladeStandUpdateEvent extends SlashBladeEvent{

        public EntityBladeStand entityBladeStand;

        public OnEntityBladeStandUpdateEvent(EntityBladeStand entityItem) {
            super(entityItem.getBlade());
            this.entityBladeStand = entityItem;
        }
    }

    @Cancelable
    public static class OnUpdateEvent extends SlashBladeEvent{
        public World world;
        public Entity entity;
        public int indexOfMainSlot;
        public boolean isCurrent;

        public OnUpdateEvent(ItemStack blade,World world,Entity entity,int indexOfMainSlot,boolean isCurrent) {
            super(blade);

            this.world = world;
            this.entity = entity;
            this.indexOfMainSlot = indexOfMainSlot;
            this.isCurrent = isCurrent;
        }
    }

    @Cancelable
    public static class BladeStandAttack extends SlashBladeEvent{
        public EntityBladeStand entityBladeStand;
        public DamageSource damageSource;
        public float damage;

        public BladeStandAttack(EntityBladeStand entityBladeStand, DamageSource damageSource, float damage) {
            super(entityBladeStand.getBlade());
            this.entityBladeStand = entityBladeStand;
            this.damageSource = damageSource;
            this.damage = damage;
        }
    }

}
