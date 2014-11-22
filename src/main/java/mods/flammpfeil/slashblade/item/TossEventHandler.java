package mods.flammpfeil.slashblade.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraftforge.event.entity.item.ItemTossEvent;

/**
 * Created by Furia on 14/11/20.
 */
public class TossEventHandler {

    @SubscribeEvent
    public void itemTossEvent(ItemTossEvent event){
        if(!event.player.isSneaking())
            return;

        if(!(event.entityItem.getEntityItem().getItem() instanceof ItemSlashBlade))
            return;

        event.entityItem.getEntityData().setBoolean("noBladeStand",true);
    }
}
