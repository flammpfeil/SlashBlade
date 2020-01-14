package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.core.CoreProxyClient;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.minecraft.client.entity.EntityPlayerSP;
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
        if(player.movementInput.forwardKeyDown)
            message.command += MessageMoveCommandState.FORWARD;
        if(player.movementInput.backKeyDown)
            message.command += MessageMoveCommandState.BACK;
        if(player.movementInput.leftKeyDown)
            message.command += MessageMoveCommandState.LEFT;
        if(player.movementInput.rightKeyDown)
            message.command += MessageMoveCommandState.RIGHT;

        if((player.movementInput.sneak && SlashBlade.SneakForceLockOn)
                || CoreProxyClient.lockon.isKeyDown())
            message.command += MessageMoveCommandState.SNEAK;

        if(CoreProxyClient.camera.isKeyDown())
            message.command += MessageMoveCommandState.CAMERA;

        if(CoreProxyClient.styleaction.isKeyDown())
            message.command += MessageMoveCommandState.STYLE;


        byte lastCommand = player.getEntityData().getByte("SB.MCS");

        long currentTime = player.getEntityWorld().getTotalWorldTime();

        if(player.movementInput.forwardKeyDown &&  (0 < (player.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK)))
            player.getEntityData().setLong("SB.MCS.F",currentTime);
        if(player.movementInput.backKeyDown &&  (0 < (player.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK)))
            player.getEntityData().setLong("SB.MCS.B",currentTime);

        if(lastCommand != message.command){
            player.getEntityData().setByte("SB.MCS",message.command);
            NetworkManager.INSTANCE.sendToServer(message);
        }

    }
}
