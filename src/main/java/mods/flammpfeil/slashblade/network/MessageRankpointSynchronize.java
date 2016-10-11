package mods.flammpfeil.slashblade.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by Furia on 15/05/15.
 */
public class MessageRankpointSynchronize implements IMessage {

    public int rankpoint;

    public MessageRankpointSynchronize(){}
    public MessageRankpointSynchronize(int point){
        this.rankpoint = point;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.rankpoint = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.rankpoint);
    }
}
