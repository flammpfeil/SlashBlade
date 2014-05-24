package mods.flammpfeil.slashblade;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
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
        MinecraftForgeClient.registerItemRenderer(SlashBlade.wrapBlade, renderer);
        MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeNamed, renderer);
		MinecraftForge.EVENT_BUS.register(renderer);

        RenderingRegistry.registerEntityRenderingHandler(EntityDrive.class, new RenderDrive());
        RenderingRegistry.registerEntityRenderingHandler(EntityPhantomSword.class, new RenderPhantomSword());

        KeyBinding keybind = new KeyBindingEx("Key.SlashBlade.PS",-98,"PhantomSword"){
            @Override
            public void upkey(int count) {
                Minecraft mc = Minecraft.getMinecraft();
                EntityClientPlayerMP player = mc.thePlayer;
                if(player != null && !mc.isGamePaused() && mc.inGameHasFocus && mc.currentScreen == null){
                    player.sendChatMessage("/slashblade ps");
                }
            }
        };
	}
}
