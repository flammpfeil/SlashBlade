package mods.flammpfeil.slashblade.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Created by Furia on 15/05/15.
 */
public class MessageSpecialAction implements IMessage {

    public byte mode;

    public MessageSpecialAction(){};

    public MessageSpecialAction(byte mode){
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.mode = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.mode);
    }
}
