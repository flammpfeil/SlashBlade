package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 2016/11/20.
 */
public class IllegalActionEnabler {

    @SubscribeEvent()
    public void onGetCollisionBoxes(GetCollisionBoxesEvent event){
        if(event.getEntity() == null || !(event.getEntity() instanceof EntityPlayerMP))
            return;

        EntityPlayerMP playerEntity = (EntityPlayerMP) event.getEntity();

        if(playerEntity instanceof FakePlayer) return;

        if(!(playerEntity.getHeldItemMainhand().getItem() instanceof ItemSlashBlade))
            return;

        if(event.getAabb().equals(playerEntity.getBoundingBox().grow(0.0625D)))
            event.getCollisionBoxesList().add(event.getAabb().offset(0, playerEntity.height * 2, 0));
    }
}
