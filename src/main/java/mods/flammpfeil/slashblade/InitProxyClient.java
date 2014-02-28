package mods.flammpfeil.slashblade;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class InitProxyClient extends InitProxy{
	@Override

	public void initializeItemRenderer() {
		ItemRendererBaseWeapon renderer = new ItemRendererBaseWeapon();
		MinecraftForgeClient.registerItemRenderer(SlashBlade.weapon, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeWood, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeBambooLight, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeSilverBambooLight, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeWhiteSheath, renderer);
		MinecraftForge.EVENT_BUS.register(renderer);
	}
}
