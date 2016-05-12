package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import mods.flammpfeil.slashblade.network.MessageSpecialAction;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Furia on 2016/05/12.
 */
public class MoveImputHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent()
    public void onPlayerPostTick(TickEvent.PlayerTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;

        if(!(event.player instanceof EntityPlayerSP)) return;

        EntityPlayerSP player = (EntityPlayerSP)event.player;

        MessageMoveCommandState message = new MessageMoveCommandState(player.movementInput);

        byte lastCommand = player.getEntityData().getByte("SB.MCS");

        if(lastCommand != message.command){
            player.getEntityData().setByte("SB.MCS",message.command);
            NetworkManager.INSTANCE.sendToServer(message);
        }

    }
}
