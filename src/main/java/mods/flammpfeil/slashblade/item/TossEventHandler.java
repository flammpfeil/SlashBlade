package mods.flammpfeil.slashblade.item;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;

/**
 * Created by Furia on 14/11/20.
 */
public class TossEventHandler {

    @SubscribeEvent
    public void itemTossEvent(ItemTossEvent event){
        if(!event.getPlayer().isSneaking())
            return;

        if(!(event.getEntityItem().getEntityItem().getItem() instanceof ItemSlashBlade))
            return;

        event.getEntityItem().getEntityData().setBoolean("noBladeStand",true);
    }
}
