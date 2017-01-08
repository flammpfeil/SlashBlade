package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 2017/01/08.
 */
public class ClickCanceller {
    @SubscribeEvent
    public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event){
        if(!(event.getTarget() instanceof EntityLivingBase))
            return;
        if(event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemSlashBlade)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event){
        if(!(event.getTarget() instanceof EntityLivingBase))
            return;
        if(event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemSlashBlade)
            event.setCanceled(true);
    }
}
