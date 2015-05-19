package mods.flammpfeil.slashblade;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.ability.client.StylishRankRenderer;
import mods.flammpfeil.slashblade.client.renderer.BladeStandRender;
import mods.flammpfeil.slashblade.client.renderer.ItemRendererSpecialMaterial;
import mods.flammpfeil.slashblade.client.renderer.RenderPhantomSwordBase;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntityPhantomSwordBase;
import mods.flammpfeil.slashblade.gui.AchievementsExtendedGuiHandler;
import mods.flammpfeil.slashblade.network.MessageSpecialAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

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

        IItemRenderer render = new ItemRendererSpecialMaterial();
        MinecraftForgeClient.registerItemRenderer(SlashBlade.proudSoul, render);

        FMLCommonHandler.instance().bus().register(new StylishRankRenderer());

        RenderDrive rd = new RenderDrive();
        RenderingRegistry.registerEntityRenderingHandler(EntityDrive.class, rd);
        RenderingRegistry.registerEntityRenderingHandler(EntityPhantomSword.class, new RenderPhantomSword());
        RenderingRegistry.registerEntityRenderingHandler(EntityDirectAttackDummy.class, rd);

        RenderingRegistry.registerEntityRenderingHandler(EntityPhantomSwordBase.class, new RenderPhantomSwordBase());

        RenderingRegistry.registerEntityRenderingHandler(EntityBladeStand.class, new BladeStandRender());

        KeyBinding keybind = new KeyBindingEx("Key.SlashBlade.PS",-98,"flammpfeil.slashblade"){
            @Override
            public void upkey(int count) {
                Minecraft mc = Minecraft.getMinecraft();
                EntityClientPlayerMP player = mc.thePlayer;
                if(player != null && !mc.isGamePaused() && mc.inGameHasFocus && mc.currentScreen == null){
                    ItemStack item = player.getHeldItem();
                    if(item != null && item.getItem() instanceof ItemSlashBlade){

                        mc.playerController.updateController();

                        ((ItemSlashBlade)item.getItem()).doRangeAttack(item, player, 1);
                    }
                }
            }
        };

        KeyBinding keybind2 = new KeyBindingEx("Key.SlashBlade.SA", Keyboard.KEY_V,"flammpfeil.slashblade"){
            @Override
            public void downkey() {
                Minecraft mc = Minecraft.getMinecraft();
                EntityClientPlayerMP player = mc.thePlayer;
                if(player == null) return;
                if(mc.isGamePaused()) return;
                if(!mc.inGameHasFocus) return;
                if(mc.currentScreen != null) return;

                ItemStack item = player.getHeldItem();
                if(item == null) return;
                if(!(item.getItem() instanceof ItemSlashBlade)) return;

                if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward)){

                    mc.playerController.updateController();
                    PacketHandler.INSTANCE.sendToServer(new MessageSpecialAction((byte) 1));

                }else
                if(mc.thePlayer.moveStrafing != 0.0f || mc.thePlayer.moveForward != 0.0f){
                    long now = mc.thePlayer.worldObj.getTotalWorldTime();

                    long interval = 3;

                    long timeout = mc.thePlayer.getEntityData().getLong("SB.AvoidTimeout");

                    if(interval < Math.abs(timeout - now)){

                        //avoid combo limit
                        {
                            long comboInterval = 10;
                            int comboLimit = 3;


                            int combo = mc.thePlayer.getEntityData().getInteger("SB.AvoidCombo");
                            long comboTimeout = mc.thePlayer.getEntityData().getLong("SB.AvoidComboTimeout");

                            if(comboInterval < Math.abs(comboTimeout - now)){
                                combo = 0;
                            }

                            if(comboLimit <= combo){
                                return;
                            }else{
                                combo++;
                                mc.thePlayer.getEntityData().setInteger("SB.AvoidCombo",combo);
                                mc.thePlayer.getEntityData().setLong("SB.AvoidComboTimeout",now+comboInterval);
                            }
                        }

                        float speedFactor;
                        if(mc.thePlayer.isSneaking())
                            speedFactor = 2.8f;
                        else
                            speedFactor = 0.8f;

                        mc.thePlayer.getEntityData().setLong("SB.AvoidTimeout",now + interval);

                        player.playSound("random.fizz", 0.3F, 10.0F);
                        mc.thePlayer.moveFlying(mc.thePlayer.moveStrafing,mc.thePlayer.moveForward,speedFactor);
                        mc.playerController.updateController();
                        PacketHandler.INSTANCE.sendToServer(new MessageSpecialAction((byte) 2));
                    }

                }
            }
        };

        AchievementsExtendedGuiHandler extendedGuiHandler = new AchievementsExtendedGuiHandler();
	}
}
