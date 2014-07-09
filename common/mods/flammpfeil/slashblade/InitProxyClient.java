package mods.flammpfeil.slashblade;

import java.util.EnumSet;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
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

        RenderDrive rd = new RenderDrive();
        RenderingRegistry.registerEntityRenderingHandler(EntityDrive.class, rd);
        RenderingRegistry.registerEntityRenderingHandler(EntityPhantomSword.class, new RenderPhantomSword());
        RenderingRegistry.registerEntityRenderingHandler(EntityDirectAttackDummy.class, rd);
        KeyBinding[] array = new KeyBinding[]{Minecraft.getMinecraft().gameSettings.keyBindPickBlock};
        boolean[] repeat = new boolean[]{false};

        TickRegistry.registerTickHandler(new KeyHandler(array,repeat) {

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
					if(tickEnd){
		                Minecraft mc = Minecraft.getMinecraft();
		                EntityClientPlayerMP player = mc.thePlayer;
		                if(player != null && mc.inGameHasFocus && mc.currentScreen == null){
		                	ItemStack item = player.getHeldItem();
		                	if(item != null){
		                		if(item.getItem() instanceof ItemSlashBlade)
		                			mc.playerController.func_78768_b(player,player);
		                	}
		                }
					}
				}

				@Override
				public void keyDown(EnumSet<TickType> types, KeyBinding kb,
						boolean tickEnd, boolean isRepeat) {
					// TODO 自動生成されたメソッド・スタブ

				}
			}, Side.CLIENT);
        /*KeyBindingRegistry.registerKeyBinding();*/
	}
}
