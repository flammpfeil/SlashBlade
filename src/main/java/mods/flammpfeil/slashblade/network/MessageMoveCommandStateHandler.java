package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.ability.AirTrick;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
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
        EntityPlayerMP entityPlayer = ctx.getServerHandler().player;
        if(entityPlayer == null) return null;

        entityPlayer.getServerWorld().addScheduledTask(() -> {
            ItemStack stack = entityPlayer.getHeldItem(EnumHand.MAIN_HAND);
            if(stack.isEmpty()) return ;
            if(!(stack.getItem() instanceof ItemSlashBlade)) return ;

            entityPlayer.getEntityData().setByte("SB.MCS",message.command);
        });

        return null;
    }
}