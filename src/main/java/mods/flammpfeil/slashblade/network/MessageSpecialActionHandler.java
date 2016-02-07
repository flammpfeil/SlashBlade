package mods.flammpfeil.slashblade.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.AirTrick;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 15/05/15.
 */
public class MessageSpecialActionHandler implements IMessageHandler<MessageSpecialAction,IMessage> {

    @Override
    public IMessage onMessage(MessageSpecialAction message, MessageContext ctx) {
        if(ctx.getServerHandler() == null) return null;

        EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;

        if(entityPlayer == null) return null;

        ItemStack stack = entityPlayer.getHeldItem();
        if(stack == null) return null;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return null;

        switch(message.mode){
            case 3:
                {
                    ItemSlashBlade itemBlade = (ItemSlashBlade)stack.getItem();
                    SpecialAttackBase base = itemBlade.getSpecialAttack(stack);
                    if(base instanceof ISuperSpecialAttack){
                        ((ISuperSpecialAttack) base).doSuperSpecialAttack(stack, entityPlayer);
                    }else if(ItemSlashBlade.defaultSA instanceof ISuperSpecialAttack){
                        ((ISuperSpecialAttack) ItemSlashBlade.defaultSA).doSuperSpecialAttack(stack, entityPlayer);
                    }

                    break;
                }
            case 2:
                {
                    UntouchableTime.setUntouchableTime(entityPlayer,3,true);
                    break;
                }
            default:
                {
                    AirTrick.SummonOrDo(entityPlayer);
                }
                break;
        }
        return null;
    }
}
