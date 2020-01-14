package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayerMP;
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

        if(ctx.getServerHandler() == null) return null;
        EntityPlayerMP entityPlayer = ctx.getServerHandler().player;
        if(entityPlayer == null) return null;

        entityPlayer.getServerWorld().addScheduledTask(() -> {
            ItemStack stack = entityPlayer.getHeldItem(EnumHand.MAIN_HAND);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemSlashBlade) {
                ((ItemSlashBlade) stack.getItem()).doRangeAttack(stack, entityPlayer, message.mode);
            }
        });

        return null;
    }
}
