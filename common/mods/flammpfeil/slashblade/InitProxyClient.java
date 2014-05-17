package mods.flammpfeil.slashblade;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class InitProxyClient extends InitProxy{
	@Override

	public void initializeItemRenderer() {
		ItemRendererBaseWeapon renderer = new ItemRendererBaseWeapon();
		MinecraftForgeClient.registerItemRenderer(SlashBlade.weapon.itemID, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeWood.itemID, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeBambooLight.itemID, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeSilverBambooLight.itemID, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeWhiteSheath.itemID, renderer);
        MinecraftForgeClient.registerItemRenderer(SlashBlade.wrapBlade.itemID, renderer);
        MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeNamed.itemID, renderer);
		MinecraftForge.EVENT_BUS.register(renderer);

        RenderingRegistry.registerEntityRenderingHandler(EntityDrive.class, new RenderDrive());
	}
}
