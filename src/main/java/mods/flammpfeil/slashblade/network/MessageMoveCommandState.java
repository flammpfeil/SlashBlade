package mods.flammpfeil.slashblade.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

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

    @Override
    public void fromBytes(ByteBuf buf) {
        this.command = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.command);
    }
}
