package mods.flammpfeil.slashblade;

import java.util.EnumSet;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityPhantomSword.class, new RenderPhantomSword());
        KeyBinding[] array = new KeyBinding[]{};
        array = (KeyBinding[])KeyBinding.keybindArray.toArray(array);
        boolean[] repeat = new boolean[array.length];
        for(int i=0;i<repeat.length;i++)
        	repeat[i] = false;

        KeyBindingRegistry.registerKeyBinding(new KeyHandler(array,repeat) {

			@Override
			public String getLabel() {
				return null;
			}

			@Override
			public EnumSet<TickType> ticks() {
		        return EnumSet.of(TickType.CLIENT, TickType.RENDER);
			}

			@Override
			public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
				if(kb.keyCode == -98 && tickEnd){
	                Minecraft mc = Minecraft.getMinecraft();
	                EntityClientPlayerMP player = mc.thePlayer;
	                mc.playerController.func_78768_b(player,player);
	                /*
	                if(player != null && mc.inGameHasFocus && mc.currentScreen == null){
	                    player.sendChatMessage("/slashblade ps");
	                }*/
				}
			}

			@Override
			public void keyDown(EnumSet<TickType> types, KeyBinding kb,
					boolean tickEnd, boolean isRepeat) {
				// TODO 自動生成されたメソッド・スタブ

			}
		});
	}
}
