package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 14/06/09.
 */
public class MessageRangeAttack implements IMessage {

    public byte mode;

    public MessageRangeAttack(){};

    public MessageRangeAttack(byte mode){
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
