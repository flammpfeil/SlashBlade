package mods.flammpfeil.slashblade.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by Furia on 15/05/15.
 */
public class MessageMoveCommandState implements IMessage {

    public byte command;

    public static final int FORWARD = 1;
    public static final int BACK = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int SNEAK = 0x10;
    public static final int CAMERA = 0x20;

    public MessageMoveCommandState(){};

    public MessageMoveCommandState(MovementInput input){
        command = 0;
        if(input.forwardKeyDown)
            command += FORWARD;
        if(input.backKeyDown)
            command += BACK;
        if(input.leftKeyDown)
            command += LEFT;
        if(input.rightKeyDown)
            command += RIGHT;
        if(input.sneak)
            command += SNEAK;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.command = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.command);
    }
}
