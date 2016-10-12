package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.core.CoreProxy;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Furia on 2016/05/12.
 */
public class MessageRankpointSynchronizeHandler implements IMessageHandler<MessageRankpointSynchronize,IMessage> {

    @Override
    public IMessage onMessage(MessageRankpointSynchronize message, MessageContext ctx) {
        return CoreProxy.proxy.onMessage(message, ctx);
    }
}