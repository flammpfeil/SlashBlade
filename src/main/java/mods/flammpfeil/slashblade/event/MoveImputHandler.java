package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.core.CoreProxyClient;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
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


        MessageMoveCommandState message = new MessageMoveCommandState();

        message.command = 0;
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        if(gameSettings.keyBindForward.isKeyDown())
            message.command += MessageMoveCommandState.FORWARD;
        if(gameSettings.keyBindBack.isKeyDown())
            message.command += MessageMoveCommandState.BACK;
        if(gameSettings.keyBindLeft.isKeyDown())
            message.command += MessageMoveCommandState.LEFT;
        if(gameSettings.keyBindRight.isKeyDown())
            message.command += MessageMoveCommandState.RIGHT;
        if(player.movementInput.sneak || CoreProxyClient.lockon.isKeyDown())
            message.command += MessageMoveCommandState.SNEAK;

        if(CoreProxyClient.camera.isKeyDown())
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
            NetworkManager.INSTANCE.sendToServer(message);
        }

    }
}
