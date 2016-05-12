package mods.flammpfeil.slashblade.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 2016/05/12.
 */
public class MessageMoveCommandStateHandler implements IMessageHandler<MessageMoveCommandState,IMessage> {

    @Override
    public IMessage onMessage(MessageMoveCommandState message, MessageContext ctx) {
        if(ctx.getServerHandler() == null) return null;

        EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;

        if(entityPlayer == null) return null;

        ItemStack stack = entityPlayer.getHeldItem();
        if(stack == null) return null;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return null;

        entityPlayer.getEntityData().setByte("SB.MCS",message.command);

        return null;
    }
}