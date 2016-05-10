package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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