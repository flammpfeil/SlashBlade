package mods.flammpfeil.slashblade.core;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.BladeSpecialRender;
import mods.flammpfeil.slashblade.client.renderer.entity.*;
import mods.flammpfeil.slashblade.client.renderer.entity.layers.EntityLivingRenderHandler;
import mods.flammpfeil.slashblade.client.renderer.entity.layers.LayerSlashBlade;
import mods.flammpfeil.slashblade.event.ModelRegister;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.*;
import mods.flammpfeil.slashblade.util.KeyBindingEx;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import mods.flammpfeil.slashblade.ability.AvoidAction;
import mods.flammpfeil.slashblade.ability.client.StylishRankRenderer;
import mods.flammpfeil.slashblade.entity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.lwjgl.input.Keyboard;

import java.util.EnumSet;
import java.util.Map;

public class CoreProxyClient extends CoreProxy {

    static public KeyBindingEx lockon = null;
    static public KeyBindingEx camera = null;

    static public KeyBindingEx summonedsword = null;
    static public KeyBindingEx styleaction = null;

	@Override
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

        RenderingRegistry.registerEntityRenderingHandler(EntitySummonedSword.class, new IRenderFactory<EntitySummonedSword>() {
            @Override
            public Render<? super EntitySummonedSword> createRenderFor(RenderManager manager) {
                return new RenderPhantomSword(manager);
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
                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayerSP player = mc.player;
                if(player != null && !mc.isGamePaused() && mc.inGameHasFocus && mc.currentScreen == null){
                    ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                    if(!item.isEmpty() && item.getItem() instanceof ItemSlashBlade){

                        mc.playerController.updateController();

                        ((ItemSlashBlade)item.getItem()).doRangeAttack(item, player, MessageRangeAttack.RangeAttackState.UPKEY);
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
                    Minecraft mc = Minecraft.getMinecraft();
                    EntityPlayerSP player = mc.player;
                    if(player != null && !mc.isGamePaused() && mc.inGameHasFocus && mc.currentScreen == null){
                        ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                        if(!item.isEmpty() && item.getItem() instanceof ItemSlashBlade){

                            mc.playerController.updateController();

                            long currentTime = player.getEntityWorld().getTotalWorldTime();

                            long backKeyLastActiveTime = player.getEntityData().getLong("SB.MCS.B");
                            final int TypeAheadBuffer = 7;

                            MessageRangeAttack.RangeAttackState command;
                            if((currentTime - backKeyLastActiveTime) <= (ChargeTime + TypeAheadBuffer)
                                && player.movementInput.forwardKeyDown && (0 < (player.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK))){
                                command = MessageRangeAttack.RangeAttackState.HEAVY_RAIN;
                            }else if(player.movementInput.forwardKeyDown && (0 < (player.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK))){
                                command = MessageRangeAttack.RangeAttackState.BLISTERING;
                            }else if(player.movementInput.backKeyDown && (0 < (player.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK))){
                                command = MessageRangeAttack.RangeAttackState.STORM;
                            }else{
                                command = MessageRangeAttack.RangeAttackState.SPIRAL;
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
                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayerSP player = mc.player;
                if(player == null) return;
                if(mc.isGamePaused()) return;
                if(!mc.inGameHasFocus) return;
                if(mc.currentScreen != null) return;

                ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                if(item.isEmpty()) return;
                if(!(item.getItem() instanceof ItemSlashBlade)) return;

                if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward)
                        && (0 < (player.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK))){

                    mc.playerController.updateController();
                    NetworkManager.INSTANCE.sendToServer(new MessageSpecialAction((byte) 1));

                }else
                if(mc.player.moveStrafing != 0.0f || mc.player.moveForward != 0.0f){
                    AvoidAction.doAvoid();
                }
            }

            @Override
            public void presskey(int count) {
                super.presskey(count);

                Minecraft mc = Minecraft.getMinecraft();
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


                player.world.spawnParticle(EnumParticleTypes.PORTAL,
                        player.posX + (player.getRNG().nextDouble() - 0.5D) * (double)player.width,
                        player.posY + player.getRNG().nextDouble() * (double)player.height - 0.25D,
                        player.posZ + (player.getRNG().nextDouble() - 0.5D) * (double)player.width,
                        (player.getRNG().nextDouble() - 0.5D) * 2.0D, -player.getRNG().nextDouble(), (player.getRNG().nextDouble() - 0.5D) * 2.0D);
            }

            @Override
            public void upkey(int count) {
                super.upkey(count);

                Minecraft mc = Minecraft.getMinecraft();
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

                if(20 > count) return;

                mc.playerController.updateController();
                NetworkManager.INSTANCE.sendToServer(new MessageSpecialAction((byte) 3));


            }
        };

        lockon = new KeyBindingEx("Key.SlashBlade.LO", Keyboard.KEY_LSHIFT, "flammpfeil.slashblade"){
        };
        camera = new KeyBindingEx("Key.SlashBlade.CA", Keyboard.KEY_LCONTROL, "flammpfeil.slashblade"){
        };

	}


    @Override
    public void postInit() {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();

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
                List<LayerRenderer> layers = ReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)rle, "villagerLayers","field_177121_n");
                layers.add(new LayerSlashBlade(rle));

                layers = ReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)rle,"defaultLayers", "field_177122_o");
                layers.add(new LayerSlashBlade(rle));
            }*/

            rle.addLayer(new LayerSlashBlade(rle));

            /*
            Method addLayer = ReflectionHelper.findMethod(RenderLivingBase.class, (RenderLivingBase)rle, new String[]{"addLayer","func_177094_a"}, LayerRenderer.class);

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



        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                return stack.getItemDamage() != 4 ? -1 : 0xCCC0FF;
            }
        }, SlashBlade.proudSoul);
    }

    /*
    @Override
    public void getMouseOver(double len)
    {
        Minecraft mc = Minecraft.getMinecraft();

        float partialTicks = ReflectionAccessHelper.getPartialTicks();

        EntityRenderer er = mc.entityRenderer;
        Entity entity = mc.getRenderViewEntity();
        if (entity != null)
        {
            if (mc.theWorld != null)
            {
                mc.pointedEntity = null;
                double d0 = len;;
                mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
                double d1 = d0;
                Vec3d vec3 = entity.getPositionEyes(partialTicks);


                if (mc.objectMouseOver != null)
                {
                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3d vec31 = entity.getLook(partialTicks);
                Vec3d vec32 = vec3.addVector(vec31.x * d0, vec31.y * d0, vec31.z * d0);
                Entity pointedEntity = null;
                Vec3d vec33 = null;
                float f = 1.0F;
                List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().offset(vec31.x * d0, vec31.y * d0, vec31.z * d0).grow((double) f, (double) f, (double) f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                    public boolean apply(Entity p_apply_1_) {
                        return p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity1 = (Entity)list.get(i);

                    if (entity1.canBeCollidedWith())
                    {
                        float f2 = entity1.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double) f2, (double) f2, (double)f2);
                        RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.contains(vec3))
                        {
                            if (0.0D <= d2)
                            {
                                pointedEntity = entity1;
                                vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity1 == entity.getRidingEntity() && !entity1.canRiderInteract())
                                {
                                    if (d2 == 0.0D)
                                    {
                                        pointedEntity = entity1;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                }
                                else
                                {
                                    pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null))
                {
                    mc.objectMouseOver = new RayTraceResult(pointedEntity, vec33);

                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
                    {
                        mc.pointedEntity = pointedEntity;
                    }
                }
            }
        }
    }*/

    @Override
    public IMessage onMessage(MessageRankpointSynchronize message, MessageContext ctx) {

        if(ctx.getClientHandler() == null) return null;

        EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
        if(entityPlayer == null) return null;

        Minecraft.getMinecraft().addScheduledTask(() -> {

            StylishRankManager.setRankPoint(entityPlayer, message.rankpoint);
        });

        return null;
    }

    @Override
    public void setTEISR(Item item) {
        item.setTileEntityItemStackRenderer(BladeSpecialRender.getInstance());
    }
}
