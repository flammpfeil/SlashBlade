package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.SidedProxy;

public class InitProxy {
	@SidedProxy(clientSide = "mods.flammpfeil.slashblade.InitProxyClient", serverSide = "mods.flammpfeil.slashblade.InitProxy")
	public static InitProxy proxy;


	public void initializeItemRenderer() {}


    public void getMouseOver(double len){}
}
