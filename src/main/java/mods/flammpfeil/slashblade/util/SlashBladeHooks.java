package mods.flammpfeil.slashblade.util;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Furia on 14/12/25.
 */
public class SlashBladeHooks {
    static public EventBus EventBus = new EventBus();

    static public boolean onUpdateHooks(ItemStack blade,World world,Entity entity,int indexOfMainSlot,boolean isCurrent){
        return EventBus.post(new SlashBladeEvent.OnUpdateEvent(blade, world, entity, indexOfMainSlot, isCurrent));
    }
    static public boolean onEntityItemUpdateHooks(EntityItem entityItem){
        return EventBus.post(new SlashBladeEvent.OnEntityItemUpdateEvent(entityItem));
    }
}
