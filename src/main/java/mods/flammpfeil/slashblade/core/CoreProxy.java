package mods.flammpfeil.slashblade.core;

import mods.flammpfeil.slashblade.network.S2CRankpointSynchronize;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.SidedProvider;

public class CoreProxy {
	public static CoreProxy proxy = new CoreProxy();

	public void initializeItemRenderer() {}

	public void postInit(){}

	public void onMessage(S2CRankpointSynchronize message, EntityPlayerMP sender) {}
}
