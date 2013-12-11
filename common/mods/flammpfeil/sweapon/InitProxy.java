package mods.flammpfeil.sweapon;

import cpw.mods.fml.common.SidedProxy;

public class InitProxy {
	@SidedProxy(clientSide = "mods.flammpfeil.sweapon.InitProxyClient", serverSide = "mods.flammpfeil.sweapon.InitProxy")
	public static InitProxy proxy;


	public void initializeItemRenderer() {}

}
