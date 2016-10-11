package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Furia on 2016/05/12.
 */
public class MessageRankpointSynchronizeHandler implements IMessageHandler<MessageRankpointSynchronize,IMessage> {

    @Override
    public IMessage onMessage(MessageRankpointSynchronize message, MessageContext ctx) {
        if(ctx.getClientHandler() == null) return null;

        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if(entityPlayer == null) return null;

        StylishRankManager.setRankPoint(entityPlayer, message.rankpoint);

        return null;
    }
}