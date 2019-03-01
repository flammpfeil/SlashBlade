package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.core.CoreProxyClient;
import mods.flammpfeil.slashblade.network.C2SMoveCommandState;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Furia on 2016/05/12.
 */
public class MoveImputHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent()
    public void onPlayerPostTick(TickEvent.PlayerTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;

        if(!(event.player instanceof EntityPlayerSP)) return;

        EntityPlayerSP player = (EntityPlayerSP)event.player;


        C2SMoveCommandState message = new C2SMoveCommandState();

        message.command = 0;
        if(player.movementInput.forwardKeyDown)
            message.command += C2SMoveCommandState.FORWARD;
        if(player.movementInput.backKeyDown)
            message.command += C2SMoveCommandState.BACK;
        if(player.movementInput.leftKeyDown)
            message.command += C2SMoveCommandState.LEFT;
        if(player.movementInput.rightKeyDown)
            message.command += C2SMoveCommandState.RIGHT;

        if((player.movementInput.sneak && SlashBlade.SneakForceLockOn)
                || CoreProxyClient.lockon.isKeyDown())
            message.command += C2SMoveCommandState.SNEAK;

        if(CoreProxyClient.camera.isKeyDown())
            message.command += C2SMoveCommandState.CAMERA;

        if(CoreProxyClient.styleaction.isKeyDown())
            message.command += C2SMoveCommandState.STYLE;


        byte lastCommand = player.getEntityData().getByte("SB.MCS");

        long currentTime = player.getEntityWorld().getGameTime();

        if(player.movementInput.forwardKeyDown &&  (0 < (player.getEntityData().getByte("SB.MCS") & C2SMoveCommandState.SNEAK)))
            player.getEntityData().setLong("SB.MCS.F",currentTime);
        if(player.movementInput.backKeyDown &&  (0 < (player.getEntityData().getByte("SB.MCS") & C2SMoveCommandState.SNEAK)))
            player.getEntityData().setLong("SB.MCS.B",currentTime);

        if(lastCommand != message.command){
            player.getEntityData().setByte("SB.MCS",message.command);
            NetworkManager.channel.sendToServer(message);
        }

    }
}
