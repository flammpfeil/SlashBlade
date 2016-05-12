package mods.flammpfeil.slashblade.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.MessageRangeAttack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 14/06/09.
 */
public class MessageRangeAttackHandler implements IMessageHandler<MessageRangeAttack,IMessage> {

    @Override
    public IMessage onMessage(MessageRangeAttack message, MessageContext ctx) {
        EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;

        ItemStack stack = entityPlayer.getHeldItem();
        if (stack != null && stack.getItem() instanceof ItemSlashBlade) {
            ((ItemSlashBlade) stack.getItem()).doRangeAttack(stack, entityPlayer, message.mode);
        }
        return null;
    }
}
