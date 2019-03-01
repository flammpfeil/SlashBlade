package mods.flammpfeil.slashblade.core;

import com.google.common.collect.Iterables;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.AvoidAction;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.client.StylishRankRenderer;
import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.entity.*;
import mods.flammpfeil.slashblade.client.renderer.entity.layers.EntityLivingRenderHandler;
import mods.flammpfeil.slashblade.client.renderer.entity.layers.LayerSlashBlade;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.event.ModelRegister;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.*;
import mods.flammpfeil.slashblade.util.KeyBindingEx;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.input.Keyboard;

import java.util.EnumSet;

public class CoreProxyClient extends CoreProxy  {

    static public KeyBindingEx lockon = null;
    static public KeyBindingEx camera = null;

    static public KeyBindingEx summonedsword = null;
    static public KeyBindingEx styleaction = null;

	public void initializeItemRenderer() {
        //resource reload event
        MinecraftForge.EVENT_BUS.register(BladeModelManager.getInstance());

        MinecraftForge.EVENT_BUS.register(new EntityLivingRenderHandler());

        new ModelRegister();

        //MinecraftForge.EVENT_BUS.register(BladeFirstPersonRender.getInstance());

        MinecraftForge.EVENT_BUS.register(new LockonCircleRender());

		/*
        MinecraftForgeClient.registerItemRenderer(SlashBlade.weapon, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeWood, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeBambooLight, renderer);
		MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeSilverBambooLight, renderer);
        MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeWhiteSheath, renderer);
        MinecraftForgeClient.registerItemRenderer(SlashBlade.wrapBlade, renderer);
        MinecraftForgeClient.registerItemRenderer(SlashBlade.bladeNamed, renderer);
        */

        /*
        {
            List<ResourceLocation> variants = Lists.newArrayList();
            variants.add(new ModelResourceLocation(SlashBlade.modid + ":" + "proudsoul", "inventory"));
            variants.add(new ModelResourceLocation(Item.itemRegistry.getNameForObject(Items.iron_ingot).toString()));
            variants.add(new ModelResourceLocation(Item.itemRegistry.getNameForObject(Items.snowball).toString()));
            variants.add(new ModelResourceLocation(SlashBlade.modid + ":" + "tinyps", "inventory"));

            for(Map.Entry<String, Integer> entry : AchievementList.achievementIcons.entrySet()) {
                variants.add(new ModelResourceLocation(SlashBlade.modid + ":" + entry.getKey(), "inventory"));
            }

            ModelBakery.registerItemVariants(SlashBlade.proudSoul, variants.toArray(new ResourceLocation[]{}));
        }
        */

        {
            StateMapperBase propertyStringMapper = new StateMapperBase() {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                    return new ModelResourceLocation("minecraft:stone");
                }
            };

            for(ItemProudSoul.EnumSoulType type : ItemProudSoul.EnumSoulType.values()){
                ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, type.getMetadata(), new ModelResourceLocation(SlashBlade.modid + ":" + "material", propertyStringMapper.getPropertyString(SlashBlade.proudSoul.getStateFromMeta(type.getMetadata()).getProperties())));
            }
        }


        //ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 0, new ModelResourceLocation(SlashBlade.modid + ":" + "proudsoul"));
        ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 0, new ModelResourceLocation(SlashBlade.modid + ":" + "soul.obj"));

        //ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 1, new ModelResourceLocation(((ResourceLocation)Item.REGISTRY.getNameForObject(Items.IRON_INGOT)).toString()));
        ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 1, new ModelResourceLocation(SlashBlade.modid + ":" + "ingot.obj"));

        //ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 2, new ModelResourceLocation(((ResourceLocation)Item.REGISTRY.getNameForObject(Items.SNOWBALL)).toString()));
        ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 2, new ModelResourceLocation(SlashBlade.modid + ":" + "sphere.obj"));

        //ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 3, new ModelResourceLocation(SlashBlade.modid + ":" + "tinyps"));
        ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 3, new ModelResourceLocation(SlashBlade.modid + ":" + "tiny.obj"));

        //ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 4, new ModelResourceLocation(((ResourceLocation)Item.REGISTRY.getNameForObject(Items.NETHER_STAR)).toString()));
        ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 4, new ModelResourceLocation(SlashBlade.modid + ":" + "crystal.obj"));

        ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, 5, new ModelResourceLocation(SlashBlade.modid + ":" + "trapezohedron.obj"));
        OBJLoader.INSTANCE.addDomain(SlashBlade.modid);

        /* todo:advancement
        for(Map.Entry<String, Integer> entry : AchievementList.achievementIcons.entrySet()) {
            ModelLoader.setCustomModelResourceLocation(SlashBlade.proudSoul, entry.getValue(), new ModelResourceLocation(SlashBlade.modid + ":" + entry.getKey()));
        }
        */


        MinecraftForge.EVENT_BUS.register(new StylishRankRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntityDrive.class, new IRenderFactory<EntityDrive>() {
            @Override
            public Render<? super EntityDrive> createRenderFor(RenderManager manager) {
                return new RenderDrive(manager);
            }
        });


        RenderingRegistry.registerEntityRenderingHandler(EntitySpearManager.class, new IRenderFactory<EntitySpearManager>() {
            @Override
            public Render<? super EntitySpearManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityJudgmentCutManager.class, new IRenderFactory<EntityJudgmentCutManager>() {
            @Override
            public Render<? super EntityJudgmentCutManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntitySakuraEndManager.class, new IRenderFactory<EntitySakuraEndManager>() {
            @Override
            public Render<? super EntitySakuraEndManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityMaximumBetManager.class, new IRenderFactory<EntityMaximumBetManager>() {
            @Override
            public Render<? super EntityMaximumBetManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityJustGuardManager.class, new IRenderFactory<EntityJustGuardManager>() {
            @Override
            public Render<? super EntityJustGuardManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityRapidSlashManager.class, new IRenderFactory<EntityRapidSlashManager>() {
            @Override
            public Render<? super EntityRapidSlashManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityHelmBrakerManager.class, new IRenderFactory<EntityHelmBrakerManager>() {
            @Override
            public Render<? super EntityHelmBrakerManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityCaliburManager.class, new IRenderFactory<EntityCaliburManager>() {
            @Override
            public Render<? super EntityCaliburManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityLumberManager.class, new IRenderFactory<EntityLumberManager>() {
            @Override
            public Render<? super EntityLumberManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityStingerManager.class, new IRenderFactory<EntityStingerManager>() {
            @Override
            public Render<? super EntityStingerManager> createRenderFor(RenderManager manager) {
                return new InvisibleRender(manager);
            }
        });


        RenderingRegistry.registerEntityRenderingHandler(EntitySummonedSwordBase.class, new IRenderFactory<EntitySummonedSwordBase>() {
            @Override
            public Render<? super EntitySummonedSwordBase> createRenderFor(RenderManager manager) {
                return new RenderPhantomSwordBase(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntitySummonedSwordAirTrickMarker.class, new IRenderFactory<EntitySummonedSwordBase>() {
            @Override
            public Render<? super EntitySummonedSwordBase> createRenderFor(RenderManager manager) {
                return new RenderPhantomSwordBase(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityBlisteringSwords.class, new IRenderFactory<EntitySummonedSwordBase>() {
            @Override
            public Render<? super EntitySummonedSwordBase> createRenderFor(RenderManager manager) {
                return new RenderPhantomSwordBase(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityHeavyRainSwords.class, new IRenderFactory<EntitySummonedSwordBase>() {
            @Override
            public Render<? super EntitySummonedSwordBase> createRenderFor(RenderManager manager) {
                return new RenderPhantomSwordBase(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntitySpiralSwords.class, new IRenderFactory<EntitySummonedSwordBase>() {
            @Override
            public Render<? super EntitySummonedSwordBase> createRenderFor(RenderManager manager) {
                return new RenderPhantomSwordBase(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityStormSwords.class, new IRenderFactory<EntitySummonedSwordBase>() {
            @Override
            public Render<? super EntitySummonedSwordBase> createRenderFor(RenderManager manager) {
                return new RenderPhantomSwordBase(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityBladeStand.class, new IRenderFactory<EntityBladeStand>() {
            @Override
            public Render<? super EntityBladeStand> createRenderFor(RenderManager manager) {
                return new BladeStandRender(manager);
            }
        });

        RenderingRegistry.registerEntityRenderingHandler(EntitySummonedBlade.class, new IRenderFactory<EntitySummonedSwordBase>() {
            @Override
            public Render<? super EntitySummonedSwordBase> createRenderFor(RenderManager manager) {
                return new RenderSummonedBlade(manager);
            }
        });

        RenderingRegistry.registerEntityRenderingHandler(EntitySpinningSword.class, new IRenderFactory<EntitySpinningSword>() {
            @Override
            public Render<? super EntitySpinningSword> createRenderFor(RenderManager manager) {
                return new RenderSpinningSword(manager);
            }
        });



        RenderingRegistry.registerEntityRenderingHandler(EntityGrimGrip.class, new IRenderFactory<EntityGrimGrip>() {
            @Override
            public Render<? super EntityGrimGrip> createRenderFor(RenderManager manager) {
                return new GrimGripRender(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityGrimGripKey.class, new IRenderFactory<EntityGrimGripKey>() {
            @Override
            public Render<? super EntityGrimGripKey> createRenderFor(RenderManager manager) {
                return new GrimGripKeyRender(manager);
            }
        });


        RenderingRegistry.registerEntityRenderingHandler(EntitySlashDimension.class, new IRenderFactory<EntitySlashDimension>() {
            @Override
            public Render<? super EntitySlashDimension> createRenderFor(RenderManager manager) {
                return new RenderSlashDimension(manager);
            }
        });


        summonedsword = new KeyBindingEx("Key.SlashBlade.PS",-98,"flammpfeil.slashblade"){
            @Override
            public void upkey(int count) {
                charged = false;
                Minecraft mc = Minecraft.getInstance();
                EntityPlayerSP player = mc.player;
                if(player != null && !mc.isGamePaused() && mc.inGameHasFocus && mc.currentScreen == null){
                    ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                    if(!item.isEmpty() && item.getItem() instanceof ItemSlashBlade){

                        mc.playerController.updateController();

                        ((ItemSlashBlade)item.getItem()).doRangeAttack(item, player, C2SRangeAttack.RangeAttackState.UPKEY);
                    }
                }
            }

            boolean charged = false;

            @Override
            public void downkey() {
                super.downkey();

                charged = false;
            }

            @Override
            public void presskey(int count) {
                super.presskey(count);

                final int ChargeTime = 7;
                if(ChargeTime < count && !charged){
                    charged = true;
                    Minecraft mc = Minecraft.getInstance();
                    EntityPlayerSP player = mc.player;
                    if(player != null && !mc.isGamePaused() && mc.inGameHasFocus && mc.currentScreen == null){
                        ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                        if(!item.isEmpty() && item.getItem() instanceof ItemSlashBlade){

                            mc.playerController.updateController();

                            long currentTime = player.getEntityWorld().getGameTime();

                            long backKeyLastActiveTime = player.getEntityData().getLong("SB.MCS.B");
                            final int TypeAheadBuffer = 7;

                            C2SRangeAttack.RangeAttackState command;
                            if((currentTime - backKeyLastActiveTime) <= (ChargeTime + TypeAheadBuffer)
                                && player.movementInput.forwardKeyDown && (0 < (player.getEntityData().getByte("SB.MCS") & C2SMoveCommandState.SNEAK))){
                                command = C2SRangeAttack.RangeAttackState.HEAVY_RAIN;
                            }else if(player.movementInput.forwardKeyDown && (0 < (player.getEntityData().getByte("SB.MCS") & C2SMoveCommandState.SNEAK))){
                                command = C2SRangeAttack.RangeAttackState.BLISTERING;
                            }else if(player.movementInput.backKeyDown && (0 < (player.getEntityData().getByte("SB.MCS") & C2SMoveCommandState.SNEAK))){
                                command = C2SRangeAttack.RangeAttackState.STORM;
                            }else{
                                command = C2SRangeAttack.RangeAttackState.SPIRAL;
                            }
                            ((ItemSlashBlade)item.getItem()).doRangeAttack(item, player, command);
                        }
                    }
                }
            }
        };

        styleaction = new KeyBindingEx("Key.SlashBlade.SA", Keyboard.KEY_V,"flammpfeil.slashblade"){
            @Override
            public void downkey() {
                Minecraft mc = Minecraft.getInstance();
                EntityPlayerSP player = mc.player;
                if(player == null) return;
                if(mc.isGamePaused()) return;
                if(!mc.inGameHasFocus) return;
                if(mc.currentScreen != null) return;

                ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                if(item.isEmpty()) return;
                if(!(item.getItem() instanceof ItemSlashBlade)) return;

                if(GameSettings.isKeyDown(Minecraft.getInstance().gameSettings.keyBindForward)
                        && (0 < (player.getEntityData().getByte("SB.MCS") & C2SMoveCommandState.SNEAK))){

                    mc.playerController.updateController();
                    NetworkManager.channel.sendToServer(new C2SSpecialAction((byte) 1));

                }else
                if(mc.player.moveStrafing != 0.0f || mc.player.moveForward != 0.0f){
                    AvoidAction.doAvoid();
                }
            }

            @Override
            public void presskey(int count) {
                super.presskey(count);

                Minecraft mc = Minecraft.getInstance();
                EntityPlayerSP player = mc.player;
                if(player == null) return;
                if(mc.isGamePaused()) return;
                if(!mc.inGameHasFocus) return;
                if(mc.currentScreen != null) return;

                ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                if(item.isEmpty()) return;
                if(!(item.getItem() instanceof ItemSlashBlade)) return;

                ItemSlashBlade bladeItem = (ItemSlashBlade)item.getItem();

                EnumSet<ItemSlashBlade.SwordType> types = bladeItem.getSwordType(item);

                if(!types.containsAll(ItemSlashBlade.SwordType.BewitchedPerfect)) return;
                if(!types.contains(ItemSlashBlade.SwordType.FiercerEdge)) return;


                player.world.spawnParticle(Particles.PORTAL,
                        player.posX + (player.getRNG().nextDouble() - 0.5D) * (double)player.width,
                        player.posY + player.getRNG().nextDouble() * (double)player.height - 0.25D,
                        player.posZ + (player.getRNG().nextDouble() - 0.5D) * (double)player.width,
                        (player.getRNG().nextDouble() - 0.5D) * 2.0D, -player.getRNG().nextDouble(), (player.getRNG().nextDouble() - 0.5D) * 2.0D);
            }

            @Override
            public void upkey(int count) {
                super.upkey(count);

                Minecraft mc = Minecraft.getInstance();
                EntityPlayerSP player = mc.player;
                if(player == null) return;
                if(mc.isGamePaused()) return;
                if(!mc.isGameFocused()) return;
                if(mc.currentScreen != null) return;

                ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                if(item.isEmpty()) return;
                if(!(item.getItem() instanceof ItemSlashBlade)) return;

                ItemSlashBlade bladeItem = (ItemSlashBlade)item.getItem();

                EnumSet<ItemSlashBlade.SwordType> types = bladeItem.getSwordType(item);

                if(!types.containsAll(ItemSlashBlade.SwordType.BewitchedPerfect)) return;
                if(!types.contains(ItemSlashBlade.SwordType.FiercerEdge)) return;

                if(20 > count) return;

                mc.playerController.updateController();
                NetworkManager.channel.sendToServer(new C2SSpecialAction((byte) 3));


            }
        };

        lockon = new KeyBindingEx("Key.SlashBlade.LO", Keyboard.KEY_LSHIFT, "flammpfeil.slashblade"){
        };
        camera = new KeyBindingEx("Key.SlashBlade.CA", Keyboard.KEY_LCONTROL, "flammpfeil.slashblade"){
        };

	}


    @Override
    public void postInit() {
        RenderManager rm = Minecraft.getInstance().getRenderManager();

        for(Render render : Iterables.concat(rm.getSkinMap().values(), rm.entityRenderMap.values())){
            if(!(render instanceof RenderLivingBase))
                continue;

            RenderLivingBase rle = (RenderLivingBase) render;


            /*
            if(!(rle.getMainModel() instanceof ModelBiped))
                continue;
            */
/*
            if(rle instanceof RenderZombie){
                List<LayerRenderer> layers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)rle, "villagerLayers","field_177121_n");
                layers.add(new LayerSlashBlade(rle));

                layers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)rle,"defaultLayers", "field_177122_o");
                layers.add(new LayerSlashBlade(rle));
            }*/

            rle.addLayer(new LayerSlashBlade(rle));

            /*
            Method addLayer = ObfuscationReflectionHelper.findMethod(RenderLivingBase.class, (RenderLivingBase)rle, new String[]{"addLayer","func_177094_a"}, LayerRenderer.class);

            try {
                addLayer.setAccessible(true);
                addLayer.invoke(rle, new LayerSlashBlade(rle));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            */
        }



        Minecraft.getInstance().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                return stack.getItemDamage() != 4 ? -1 : 0xCCC0FF;
            }
        }, SlashBlade.proudSoul);
    }

    public void onMessage(S2CRankpointSynchronize message, EntityPlayerMP sender) {
        if(sender == null) return;
        StylishRankManager.setRankPoint(sender, message.rankpoint);
    }
}
