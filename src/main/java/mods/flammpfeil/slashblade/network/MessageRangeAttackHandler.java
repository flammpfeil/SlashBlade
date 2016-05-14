package mods.flammpfeil.slashblade.network;

import io.netty.buffer.ByteBuf;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Furia on 14/06/09.
 */
public class MessageRangeAttackHandler implements IMessageHandler<MessageRangeAttack,IMessage> {

    @Override
    public IMessage onMessage(MessageRangeAttack message, MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;

        ItemStack stack = entityPlayer.getHeldItem(EnumHand.MAIN_HAND);
        if (stack != null && stack.getItem() instanceof ItemSlashBlade) {
            ((ItemSlashBlade) stack.getItem()).doRangeAttack(stack, entityPlayer, message.mode);
        }
        return null;
    }
}
