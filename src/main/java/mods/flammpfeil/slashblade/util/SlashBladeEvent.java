package mods.flammpfeil.slashblade.util;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
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
    public static class OnEntityBladeStandUpdateEvent extends SlashBladeEvent{

        EntityBladeStand entityBladeStand;

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

}
