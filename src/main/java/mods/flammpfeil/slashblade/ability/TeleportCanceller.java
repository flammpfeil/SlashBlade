package mods.flammpfeil.slashblade.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 2016/06/18.
 */
public class TeleportCanceller {

    final static String TeleportCancelTimeout = "DisTeleportTimeout";
    final static int Amount = 200;

    static public void setCancel(Entity entity){
        if(entity != null && entity instanceof EntityEnderman)
            entity.getEntityData().setLong(TeleportCancelTimeout, entity.world.getGameTime() + Amount);
    }

    static public boolean canTeleport(Entity entity){
        boolean result = false;
        if(entity != null && !entity.world.isRemote){
            long current = entity.world.getGameTime();
            long timeout = entity.getEntityData().getLong(TeleportCancelTimeout);

            result = timeout < current;
        }
        return result;
    }

    @SubscribeEvent
    public void onEnderTeleportEvent(EnderTeleportEvent event){
        if(!canTeleport(event.getEntityLiving())){
            event.setCanceled(true);
        }
    }
}
