package mods.flammpfeil.slashblade.core;

import mods.flammpfeil.slashblade.network.MessageRankpointSynchronize;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CoreProxy {
	@SidedProxy(clientSide = "mods.flammpfeil.slashblade.core.CoreProxyClient", serverSide = "mods.flammpfeil.slashblade.core.CoreProxy")
	public static CoreProxy proxy;


	public void initializeItemRenderer() {}

	public void postInit(){}

    //public void getMouseOver(double len){}

	public IMessage onMessage(MessageRankpointSynchronize message, MessageContext ctx) {
		return null;
	}
}
