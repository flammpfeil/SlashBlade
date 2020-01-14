package mods.flammpfeil.slashblade.network;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import java.util.Map;

/**
 * Created by Furia on 14/06/09.
 */
public class MessageRangeAttack implements IMessage {

    public RangeAttackState mode;

    public enum RangeAttackState{
        UPKEY(1),
        BLISTERING(2),
        STORM(3),
        SPIRAL(4),
        HEAVY_RAIN(5);

        private final byte data;


        public static RangeAttackState getState(byte data){
            return stateMap.get(data);
        }
        private static final Map<Byte,RangeAttackState> stateMap = Maps.newHashMap();

        static {
            for (RangeAttackState value : values())
            {
                stateMap.put(value.data, value);
            }
        }

        RangeAttackState(int value){
            data = (byte)value;
        }

        public byte getData() {
            return data;
        }
    }

    public MessageRangeAttack(){};

    public MessageRangeAttack(RangeAttackState mode){
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.mode = RangeAttackState.getState(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.mode.getData());
    }
}
