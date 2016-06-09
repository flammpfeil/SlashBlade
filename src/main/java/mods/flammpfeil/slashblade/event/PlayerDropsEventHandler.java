package mods.flammpfeil.slashblade.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
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

            if(isPlayer || item.getEntityItem().hasDisplayName() || item.getEntityItem().getRarity() != EnumRarity.common)
                item.getEntityData().setBoolean("SB.DeathDrop",true);
        }
    }
}
