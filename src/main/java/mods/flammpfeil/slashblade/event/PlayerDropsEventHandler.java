package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 2016/03/20.
 */
public class PlayerDropsEventHandler {
    @SubscribeEvent
    public void onPlayerDropsEvent(LivingDropsEvent event){
        boolean isPlayer = event.entityLiving instanceof EntityPlayer;
        for(EntityItem item : event.drops){
            if(!(item.getEntityItem().getItem() instanceof ItemSlashBlade))
                continue;

            if(isPlayer || item.hasCustomName() || item.getEntityItem().getRarity() != EnumRarity.COMMON)
                item.addTag("SB.DeathDrop");
        }
    }
}
