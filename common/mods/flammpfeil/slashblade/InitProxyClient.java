package mods.flammpfeil.slashblade;

import net.minecraftforge.client.MinecraftForgeClient;

public class InitProxyClient extends InitProxy{
	@Override

	public void initializeItemRenderer() {
		ItemRendererBaseWeapon renderer = new ItemRendererBaseWeapon();
		MinecraftForgeClient.registerItemRenderer(SlashBlade.weapon.itemID, renderer);
		//MinecraftForge.EVENT_BUS.register(renderer);
	}
}
