package mods.flammpfeil.slashblade.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.InitProxyClient;
import mods.flammpfeil.slashblade.PacketHandler;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;

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


        MessageMoveCommandState message = new MessageMoveCommandState();

        message.command = 0;
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        if(gameSettings.keyBindForward.getIsKeyPressed())
            message.command += MessageMoveCommandState.FORWARD;
        if(gameSettings.keyBindBack.getIsKeyPressed())
            message.command += MessageMoveCommandState.BACK;
        if(gameSettings.keyBindLeft.getIsKeyPressed())
            message.command += MessageMoveCommandState.LEFT;
        if(gameSettings.keyBindRight.getIsKeyPressed())
            message.command += MessageMoveCommandState.RIGHT;
        if(player.movementInput.sneak || InitProxyClient.lockon.getIsKeyPressed())
            message.command += MessageMoveCommandState.SNEAK;

        if(InitProxyClient.camera.getIsKeyPressed())
            message.command += MessageMoveCommandState.CAMERA;


        byte lastCommand = player.getEntityData().getByte("SB.MCS");

        long currentTime = player.getEntityWorld().getTotalWorldTime();

        int forwardState = MessageMoveCommandState.FORWARD | MessageMoveCommandState.SNEAK;
        if(forwardState == (player.getEntityData().getByte("SB.MCS") & forwardState))
            player.getEntityData().setLong("SB.MCS.F",currentTime);

        int backState = MessageMoveCommandState.BACK | MessageMoveCommandState.SNEAK;
        if(backState == (player.getEntityData().getByte("SB.MCS") & backState))
            player.getEntityData().setLong("SB.MCS.B",currentTime);

        if(lastCommand != message.command){
            player.getEntityData().setByte("SB.MCS",message.command);
            PacketHandler.INSTANCE.sendToServer(message);
        }

    }
}
