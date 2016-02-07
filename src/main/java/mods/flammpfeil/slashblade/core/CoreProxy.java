package mods.flammpfeil.slashblade.core;

import net.minecraftforge.fml.common.SidedProxy;

public class CoreProxy {
	@SidedProxy(clientSide = "mods.flammpfeil.slashblade.core.CoreProxyClient", serverSide = "mods.flammpfeil.slashblade.core.CoreProxy")
	public static CoreProxy proxy;


	public void initializeItemRenderer() {}

	public void postInit(){}

    public void getMouseOver(double len){}
}
