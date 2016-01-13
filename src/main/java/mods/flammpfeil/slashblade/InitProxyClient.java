package mods.flammpfeil.slashblade;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.ability.AvoidAction;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.ability.client.StylishRankRenderer;
import mods.flammpfeil.slashblade.client.renderer.BladeStandRender;
import mods.flammpfeil.slashblade.client.renderer.ItemRendererSpecialMaterial;
import mods.flammpfeil.slashblade.client.renderer.RenderPhantomSwordBase;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.gui.AchievementsExtendedGuiHandler;
import mods.flammpfeil.slashblade.network.MessageSpecialAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.util.Timer;

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
        RenderingRegistry.registerEntityRenderingHandler(EntityJudgmentCutManager.class, rd);
        RenderingRegistry.registerEntityRenderingHandler(EntitySakuraEndManager.class, rd);
        RenderingRegistry.registerEntityRenderingHandler(EntityJustGuardManager.class, rd);

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
                    AvoidAction.doAvoid();
                }
            }

            @Override
            public void presskey(int count) {
                super.presskey(count);

                Minecraft mc = Minecraft.getMinecraft();
                EntityClientPlayerMP player = mc.thePlayer;
                if(player == null) return;
                if(mc.isGamePaused()) return;
                if(!mc.inGameHasFocus) return;
                if(mc.currentScreen != null) return;

                ItemStack item = player.getHeldItem();
                if(item == null) return;
                if(!(item.getItem() instanceof ItemSlashBlade)) return;

                ItemSlashBlade bladeItem = (ItemSlashBlade)item.getItem();

                EnumSet<ItemSlashBlade.SwordType> types = bladeItem.getSwordType(item);

                if(!types.containsAll(ItemSlashBlade.SwordType.BewitchedPerfect)) return;
                if(!types.contains(ItemSlashBlade.SwordType.FiercerEdge)) return;


                player.worldObj.spawnParticle("portal",
                        player.posX + (player.getRNG().nextDouble() - 0.5D) * (double)player.width,
                        player.posY + player.getRNG().nextDouble() * (double)player.height - 0.25D,
                        player.posZ + (player.getRNG().nextDouble() - 0.5D) * (double)player.width,
                        (player.getRNG().nextDouble() - 0.5D) * 2.0D, -player.getRNG().nextDouble(), (player.getRNG().nextDouble() - 0.5D) * 2.0D);
            }

            @Override
            public void upkey(int count) {
                super.upkey(count);

                Minecraft mc = Minecraft.getMinecraft();
                EntityClientPlayerMP player = mc.thePlayer;
                if(player == null) return;
                if(mc.isGamePaused()) return;
                if(!mc.inGameHasFocus) return;
                if(mc.currentScreen != null) return;

                ItemStack item = player.getHeldItem();
                if(item == null) return;
                if(!(item.getItem() instanceof ItemSlashBlade)) return;

                ItemSlashBlade bladeItem = (ItemSlashBlade)item.getItem();

                EnumSet<ItemSlashBlade.SwordType> types = bladeItem.getSwordType(item);

                if(!types.containsAll(ItemSlashBlade.SwordType.BewitchedPerfect)) return;
                if(!types.contains(ItemSlashBlade.SwordType.FiercerEdge)) return;

                if(20 > count) return;

                mc.playerController.updateController();
                PacketHandler.INSTANCE.sendToServer(new MessageSpecialAction((byte) 3));


            }
        };

        AchievementsExtendedGuiHandler extendedGuiHandler = new AchievementsExtendedGuiHandler();
	}

    @Override
    public void getMouseOver(double len)
    {
        Minecraft mc = Minecraft.getMinecraft();
        float p_78473_1_ = ((Timer)ReflectionHelper.getPrivateValue(Minecraft.class,mc,"timer","field_71428_T")).renderPartialTicks;

        EntityRenderer er = mc.entityRenderer;
        if (mc.renderViewEntity != null)
        {
            if (mc.theWorld != null)
            {
                mc.pointedEntity = null;
                double d0 = len;
                mc.objectMouseOver = mc.renderViewEntity.rayTrace(d0, p_78473_1_);
                double d1 = d0;
                Vec3 vec3 = mc.renderViewEntity.getPosition(p_78473_1_);


                if (mc.objectMouseOver != null)
                {
                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = mc.renderViewEntity.getLook(p_78473_1_);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                Entity pointedEntity = null;
                Vec3 vec33 = null;
                float f1 = 1.0F;
                List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
                double d2 = d1;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = (Entity)list.get(i);

                    if (entity.canBeCollidedWith())
                    {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.isVecInside(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity == mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
                                {
                                    if (d2 == 0.0D)
                                    {
                                        pointedEntity = entity;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                }
                                else
                                {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null))
                {
                    mc.objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
                    {
                        mc.pointedEntity = pointedEntity;
                    }
                }
            }
        }
    }
}
