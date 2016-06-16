package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

/**
 * Created by Furia on 2016/06/18.
 */
public class TeleportCanceller {

    final static String TeleportCancelTimeout = "DisTeleportTimeout";
    final static int Amount = 200;

    static public void setCancel(Entity entity){
        if(entity != null && entity instanceof EntityEnderman)
            entity.getEntityData().setLong(TeleportCancelTimeout, entity.worldObj.getTotalWorldTime() + Amount);
    }

    static public boolean canTeleport(Entity entity){
        boolean result = false;
        if(entity != null && !entity.worldObj.isRemote){
            long current = entity.worldObj.getTotalWorldTime();
            long timeout = entity.getEntityData().getLong(TeleportCancelTimeout);

            result = timeout < current;
        }
        return result;
    }

    @SubscribeEvent
    public void onEnderTeleportEvent(EnderTeleportEvent event){
        if(!canTeleport(event.entityLiving)){
            event.setCanceled(true);
        }
    }
}
