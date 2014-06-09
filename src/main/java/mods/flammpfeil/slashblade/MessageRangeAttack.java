package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 14/06/09.
 */
public class MessageRangeAttack implements IMessage, IMessageHandler<MessageRangeAttack,IMessage> {

    private byte mode;

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

    @Override
    public IMessage onMessage(MessageRangeAttack message, MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;

        ItemStack stack = entityPlayer.getHeldItem();
        if(stack != null && stack.getItem() instanceof ItemSlashBlade){
            ((ItemSlashBlade)stack.getItem()).doRangeAttack(stack,entityPlayer,message.mode);
        }
        return null;
    }
}
