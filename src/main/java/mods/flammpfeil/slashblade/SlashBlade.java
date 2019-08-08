package mods.flammpfeil.slashblade;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import mods.flammpfeil.slashblade.capability.MobEffect.CapabilityMobEffectHandler;
import mods.flammpfeil.slashblade.capability.MobEffect.CapabilityMobEffectRegister;
import mods.flammpfeil.slashblade.capability.MobEffect.MobEffect;
import mods.flammpfeil.slashblade.capability.MobEffect.MobEffectCapabilityProvider;
import mods.flammpfeil.slashblade.config.ConfigManager;
import mods.flammpfeil.slashblade.core.ConfigCustomBladeManager;
import mods.flammpfeil.slashblade.core.CoreProxy;
import mods.flammpfeil.slashblade.event.*;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.core.ConfigEntityListManager;
import mods.flammpfeil.slashblade.item.crafting.RecipeBladeSoulUpgrade;
import mods.flammpfeil.slashblade.item.crafting.RecipeCustomBlade;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.minecraft.command.FunctionObject;
import net.minecraft.util.ResourceLocation;
import mods.flammpfeil.slashblade.util.DummyRecipeBase;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ability.*;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.item.TossEventHandler;
import mods.flammpfeil.slashblade.named.*;
import mods.flammpfeil.slashblade.named.BladeMaterials;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.util.DummySmeltingRecipe;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.io.File;
import java.util.*;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

@Mod(name = SlashBlade.modname, modid = SlashBlade.modid, version = SlashBlade.version,
    guiFactory = "mods.flammpfeil.slashblade.gui.config.ConfigGuiFactory")
public class SlashBlade implements IFuelHandler{


	public static final String modname = "SlashBlade";
    public static final String modid = "flammpfeil.slashblade";
    public static final String version = "@VERSION@";

	public static final String BrokenBladeWhiteStr = "BrokenBladeWhite";

	public static ItemSlashBlade weapon;
	public static ItemSlashBladeDetune bladeWood;
	public static ItemSlashBladeDetune bladeBambooLight;
	public static ItemSlashBladeDetune bladeSilverBambooLight;
    public static ItemSlashBladeDetune bladeWhiteSheath;
    public static ItemSlashBladeNamed bladeNamed;

    public static ItemSlashBladeWrapper wrapBlade = null;

	public static ItemProudSoul proudSoul;

    public static Configuration mainConfiguration;
    public static File mainConfigurationFile;

	public static ConfigEntityListManager manager;

    public static boolean SafeDrop = true;
    public static boolean MobSafeDrop = false;

    public static boolean SneakForceLockOn = false;

    public static boolean UseRenderLivingEvent = false;

    public static boolean RenderEnchantEffect = true;
    public static boolean RenderNFCSEffect = true;

	public static final String ProudSoulStr = "proudsoul";
	public static final String IngotBladeSoulStr = "ingot_bladesoul";
    public static final String SphereBladeSoulStr = "sphere_bladesoul";
    public static final String TinyBladeSoulStr = "tiny_bladesoul";
    public static final String CrystalBladeSoulStr = "crystal_bladesoul";
    public static final String TrapezohedronBladeSoulStr = "trapezohedron_bladesoul";

    public static final SlashBladeTab tab = new SlashBladeTab("flammpfeil.slashblade");

    public static final EventBus InitEventBus = new EventBus();

    //ability
    public static JustGuard abilityJustGuard;
    public static StylishRankManager stylishRankManager;
    public static ChargeFloating abilityChargeFloating;
    public static FireResistance abilityFireResistance;
    public static WaterBreathing abilityWaterBreathing;
    public static UntouchableTime abilityUntouchableTime;
    public static AvoidAction abilityAvoidAction;
    public static EnemyStep abilityEnemyStep;
    public static AerialRave abilityAerialRave;
    public static StunManager abilityStun;
    public static ProjectileBarrier abilityProjectileBarrier;

    public static Multimap<String,IRecipe> recipeMultimap = HashMultimap.create();

    public static void addSmelting(String key, ItemStack input, ItemStack output, float xp){
        GameRegistry.addSmelting(input,output,xp);
        recipeMultimap.put(key,new DummySmeltingRecipe(input,output));
    }
    public static void addRecipe(String key, IRecipe value) {
        addRecipe(key, value, value instanceof DummyRecipeBase);
    }
    public static void addRecipe(String key, IRecipe value, boolean isDummy) {
        if(!isDummy) {
            if(value.getRegistryName() == null)
                value.setRegistryName(new ResourceLocation(value.getGroup()));
            ForgeRegistries.RECIPES.register(value);
        }
        recipeMultimap.put(key, value);
    }

    @EventHandler
	public void preInit(FMLPreInitializationEvent evt){
		mainConfiguration = new Configuration(this.mainConfigurationFile = evt.getSuggestedConfigurationFile());

		try {
            mainConfiguration.load();


            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_CLIENT, "SneakForceLockOn", SlashBlade.SneakForceLockOn);
                SlashBlade.SneakForceLockOn = prop.getBoolean();
                prop.setShowInGui(true);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_CLIENT, "UseRenderLivingEvent", SlashBlade.UseRenderLivingEvent);
                SlashBlade.UseRenderLivingEvent = prop.getBoolean();
                prop.setShowInGui(true);
            }
            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_CLIENT, "EnchantVisualEffect", true);
                SlashBlade.RenderEnchantEffect = prop.getBoolean();
                prop.setShowInGui(true);
            }
            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_CLIENT, "NFCSVisualEffect", true);
                SlashBlade.RenderNFCSEffect = prop.getBoolean();
                prop.setShowInGui(true);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "FastLeavesDecay", false);
                EntityLumberManager.BlockHarvestDropsEventHandler.fastLeavesDecay = prop.getBoolean(false);
                prop.setShowInGui(true);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "SafeDrop", true, "true:bladestand / false:all ways EntityItem drop");
                SafeDrop = prop.getBoolean(true);
                prop.setShowInGui(true);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "SafeDrop", true, "true:bladestand / false:all ways EntityItem drop");
                SafeDrop = prop.getBoolean(true);
                prop.setShowInGui(true);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "SafeDrop", MobSafeDrop, "true:bladestand / false:all ways EntityItem drop");
                MobSafeDrop = prop.getBoolean(MobSafeDrop);
                prop.setShowInGui(true);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "RankpointRange", 100);
                prop.setComment("decrement speed factor up 50<def:100<500 down");
                int range = Math.max(50, Math.min(500, prop.getInt()));
                StylishRankManager.setRankRange(range);
            }
            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "RankpointUpRateTaunt", 150);
                prop.setComment("percentage 1%<def:150%<200%");
                float range = Math.max(1, Math.min(200, prop.getInt()));
                StylishRankManager.AttackTypes.registerAttackType(StylishRankManager.AttackTypes.Noutou, range / 100.0f);
            }
            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "RankpointUpRate", 100);
                prop.setComment("percentage 1%<def:100%<200%");
                float range = Math.max(1, Math.min(200, prop.getInt()));
                StylishRankManager.setRankRate(range / 100.0f);
            }
            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "WhiffsRankDownRate", 10);
                prop.setComment("rankpoint change factor percentage 0% <= value <= 100% (0% = disable)");
                int value = prop.getInt();
                value = Math.max(0, Math.min(100, value));
                StylishRankManager.whiffsRankDownDisabled = (value == 0);
                StylishRankManager.whiffsRankDownFactor = value / 100.0f;
            }

            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "DamageMultiplier", 100);
                prop.setComment("blade damage multiplier factor 0% <= value <= 1000% (default=100%, 0%=allways1damage)");
                float value = prop.getInt();
                value = Math.max(0, Math.min(value,1000)) / 100.0f;
                DamageLimitter.setFactor(value);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "DamageLimit", -1);
                prop.setComment("blade damage limit -1:Limitless | 0 <= value <= XX (0=allways1damage)");
                DamageLimitter.setLimit(prop.getInt());
            }


            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "DamageAPMultiplier", 100);
                prop.setComment("ArmorPiercing damage multiplier factor 0% <= value <= 1000% (default=100%, 0%=allways1damage)");
                float value = prop.getInt();
                value = Math.max(0, Math.min(value,1000)) / 100.0f;
                ArmorPiercing.setFactor(value);
            }

            {
                Property prop = SlashBlade.mainConfiguration.get("difficulty", "DamageAPLimit", -1);
                prop.setComment("ArmorPiercing damage limit -1:Limitless | 0 <= value <= XX (0=allways1damage)");
                ArmorPiercing.setLimit(prop.getInt());
            }


            MinecraftForge.EVENT_BUS.register(new ConfigManager());
		}
		finally
		{
			mainConfiguration.save();
		}


		proudSoul = (ItemProudSoul)(new ItemProudSoul())
				.setUnlocalizedName("flammpfeil.slashblade.proudsoul")
				.setCreativeTab(tab)
                .setRegistryName("proudsoul");
		ForgeRegistries.ITEMS.register(proudSoul);


		ItemStack itemProudSoul = new ItemStack(proudSoul,1,0);
		itemProudSoul.setRepairCost(-10);
		registerCustomItemStack(ProudSoulStr , itemProudSoul);

		ItemStack itemIngotBladeSoul = new ItemStack(proudSoul,1,1);
		itemIngotBladeSoul.setRepairCost(-25);
		registerCustomItemStack(IngotBladeSoulStr , itemIngotBladeSoul);

        ItemStack itemSphereBladeSoul = new ItemStack(proudSoul,1,2);
        itemSphereBladeSoul.setRepairCost(-50);
        registerCustomItemStack(SphereBladeSoulStr , itemSphereBladeSoul);

        ItemStack itemTinyBladeSoul = new ItemStack(proudSoul,1,3);
        registerCustomItemStack(TinyBladeSoulStr , itemTinyBladeSoul);

        ItemStack itemCrystalBladeSoul = new ItemStack(proudSoul,1,4);
        itemCrystalBladeSoul.setRepairCost(-65);
        registerCustomItemStack(CrystalBladeSoulStr , itemCrystalBladeSoul);

        ItemStack itemTrapezohedronBladeSoul = new ItemStack(proudSoul,1,5);
        itemCrystalBladeSoul.setRepairCost(-80);
        registerCustomItemStack(TrapezohedronBladeSoulStr , itemTrapezohedronBladeSoul);


        ItemStack steelIngot = new ItemStack(proudSoul,1, ItemProudSoul.EnumSoulType.STEEL_INGOT.getMetadata());
        OreDictionary.registerOre("ingotSteel", steelIngot);
        ItemStack ingotSilver = new ItemStack(proudSoul,1, ItemProudSoul.EnumSoulType.SILVER_INGOT.getMetadata());
        OreDictionary.registerOre("ingotSilver", ingotSilver);

        //==================================================================================================================================

		weapon = (ItemSlashBlade)(new ItemSlashBlade(ToolMaterial.IRON, 4 + ToolMaterial.DIAMOND.getAttackDamage()))
				.setRepairMaterial(new ItemStack(Items.IRON_INGOT))
				.setRepairMaterialOreDic("ingotSteel", "nuggetSteel")
				.setUnlocalizedName("flammpfeil.slashblade")
				.setCreativeTab(tab)
                .setRegistryName("slashblade");

        ForgeRegistries.ITEMS.register(weapon);

        //==================================================================================================================================

        bladeWood = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.WOOD.getAttackDamage()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocationRaw("flammpfeil.slashblade", "model/wood.png"))
                .setRepairMaterialOreDic("logWood")
                .setMaxDamage(60)
                .setUnlocalizedName("flammpfeil.slashblade.wood")
                .setCreativeTab(tab)
                .setRegistryName("slashbladeWood");
        ForgeRegistries.ITEMS.register(bladeWood);

        bladeBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.STONE.getAttackDamage()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocationRaw("flammpfeil.slashblade", "model/banboo.png"))
                .setRepairMaterialOreDic("bamboo")
                .setMaxDamage(50)
                .setUnlocalizedName("flammpfeil.slashblade.bamboo")
                .setCreativeTab(tab)
                .setRegistryName("slashbladeBambooLight");
        ForgeRegistries.ITEMS.register(bladeBambooLight);

        bladeSilverBambooLight = (ItemSlashBladeBambooLight)(new ItemSlashBladeBambooLight(ToolMaterial.WOOD, 4 + ToolMaterial.IRON.getAttackDamage()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocationRaw("flammpfeil.slashblade", "model/silverbanboo.png"))
                .setRepairMaterialOreDic("bamboo")
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.silverbamboo")
                .setCreativeTab(tab)
                .setRegistryName("slashbladeSilverBambooLight");
        ForgeRegistries.ITEMS.register(bladeSilverBambooLight);

        bladeWhiteSheath = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.IRON, 4 + ToolMaterial.IRON.getAttackDamage()))
                .setDestructable(false)
                .setModelTexture(new ResourceLocationRaw("flammpfeil.slashblade", "model/white.png"))
                .setRepairMaterial(new ItemStack(Items.IRON_INGOT))
                .setRepairMaterialOreDic("ingotSteel", "nuggetSteel")
                .setMaxDamage(70)
                .setUnlocalizedName("flammpfeil.slashblade.white")
                .setCreativeTab(tab)
                .setRegistryName("slashbladeWhite");
        ForgeRegistries.ITEMS.register(bladeWhiteSheath);



        //==================================================================================================================================

        wrapBlade = (ItemSlashBladeWrapper)(new ItemSlashBladeWrapper(ToolMaterial.IRON))
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.wrapper")
                .setCreativeTab(tab)
                .setRegistryName("slashbladeWrapper");
        ForgeRegistries.ITEMS.register(wrapBlade);




        bladeNamed = (ItemSlashBladeNamed)(new ItemSlashBladeNamed(ToolMaterial.IRON, 4.0f))
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.named")
                .setCreativeTab(tab)
                .setRegistryName("slashbladeNamed");
        ForgeRegistries.ITEMS.register(bladeNamed);


		GameRegistry.registerFuelHandler(this);

		CoreProxy.proxy.initializeItemRenderer();

		manager = new ConfigEntityListManager();

        MinecraftForge.EVENT_BUS.register(manager);

        NetworkManager.init();

        InitEventBus.register(new BladeMaterials());
        InitEventBus.register(new SimpleBlade());

        InitEventBus.register(new Tagayasan());
        InitEventBus.register(new Yamato());
        InitEventBus.register(new Tukumo());

        InitEventBus.register(new Agito());

        InitEventBus.register(new PSSange());
        InitEventBus.register(new PSYasha());
        InitEventBus.register(new Fox());
        InitEventBus.register(new Tizuru());
        InitEventBus.register(new Doutanuki());
        InitEventBus.register(new BambooMod());

        InitEventBus.register(new Koseki());

        ConfigCustomBladeManager ccb = new ConfigCustomBladeManager();
        ccb.loadConfig(mainConfiguration);
        InitEventBus.register(ccb);


        CapabilityMobEffectHandler.register();
    }

    //StatManager statManager;

    @EventHandler
    public void init(FMLInitializationEvent evt){

        SlashBlade.addRecipe("wrap", new RecipeWrapBlade());

        SlashBlade.addRecipe("adjust", new RecipeAdjustPos());

        RecipeInstantRepair recipeRepair = new RecipeInstantRepair();
        SlashBlade.addRecipe("repair", recipeRepair);

        //MinecraftForge.EVENT_BUS.register(recipeRepair);


        int entityId = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"Drive"), EntityDrive.class, "Drive", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SummonedSword"), EntitySummonedSword.class, "PhantomSword", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SpearManager"), EntitySpearManager.class, "DirectAttackDummy", entityId++, this, 250, 10, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SummonedSwordBase"), EntitySummonedSwordBase.class, "SummonedSwordBase", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"WitherSword"), EntityWitherSword.class, "WitherSword", entityId++, this, 250, 10, false);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"JudgmentCutManager"), EntityJudgmentCutManager.class, "JudgmentCutManager", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SakuraEndManager"), EntitySakuraEndManager.class, "SakuraEndManager", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"MaximumBetManager"), EntityMaximumBetManager.class, "MaximumBetManager", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SpearManager"), EntitySpearManager.class, "SpearManager", entityId++, this, 250, 10, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"JustGuardManager"), EntityJustGuardManager.class, "JustGuardManager", entityId++, this, 250, 10, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"BladeStand"), EntityBladeStand.class, "BladeStand", entityId++, this, 250, 20, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SummonedBlade"), EntitySummonedBlade.class, "SummonedBlade", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SummonedSwordAirTrickMarker"), EntitySummonedSwordAirTrickMarker.class, "SummonedSwordATM", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"BlisteringSwords"), EntityBlisteringSwords.class, "BlisteringSwords", entityId++, this, 250, 200, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"HeavyRainSwords"), EntityHeavyRainSwords.class, "HeavyRainSwords", entityId++, this, 250, 200, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SpiralSwords"), EntitySpiralSwords.class, "SpiralSwords", entityId++, this, 250, 200, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"StormSwords"), EntityStormSwords.class, "StormSwords", entityId++, this, 250, 200, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"RapidSlashManager"), EntityRapidSlashManager.class, "RapidSlashManager", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"HelmBrakerManager"), EntityHelmBrakerManager.class, "HelmbrakerManager", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"CaliburManager"), EntityCaliburManager.class, "CaliburManager", entityId++, this, 250, 10, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"GrimGrip"), EntityGrimGrip.class, "GrimGrip", entityId++, this, 250, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(modid,"GrimGripKey"), EntityGrimGripKey.class, "GrimGripKey", entityId++, this, 250, 200, false);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SlashDimension"), EntitySlashDimension.class, "SlashDimension", entityId++, this, 250, 200, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"LumberManager"), EntityLumberManager.class, "LumberManager", entityId++, this, 250, 200, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"StingerManager"), EntityStingerManager.class, "StingerManager", entityId++, this, 250, 10, true);

        EntityRegistry.registerModEntity(new ResourceLocation(modid,"SpinningSword"), EntitySpinningSword.class, "SpinningSword", entityId++, this, 250, 10, true);

        MinecraftForge.EVENT_BUS.register(new EntityLumberManager.BlockHarvestDropsEventHandler());


        MinecraftForge.EVENT_BUS.register(new DropEventHandler());
        MinecraftForge.EVENT_BUS.register(new AnvilEventHandler());

        //MinecraftForge.EVENT_BUS.register(new SlashBladeItemDestroyEventHandler());
        MinecraftForge.EVENT_BUS.register(new TossEventHandler());

        //ability
        abilityJustGuard = new JustGuard();
        MinecraftForge.EVENT_BUS.register(abilityJustGuard);

        abilityChargeFloating = new ChargeFloating();
        MinecraftForge.EVENT_BUS.register(abilityChargeFloating);

        abilityFireResistance = new FireResistance();
        MinecraftForge.EVENT_BUS.register(abilityFireResistance);

        abilityWaterBreathing = new WaterBreathing ();
        MinecraftForge.EVENT_BUS.register(abilityWaterBreathing);

        abilityUntouchableTime = new UntouchableTime();
        MinecraftForge.EVENT_BUS.register(abilityUntouchableTime);

        abilityAvoidAction = new AvoidAction();
        MinecraftForge.EVENT_BUS.register(abilityAvoidAction);

        abilityEnemyStep = new EnemyStep();
        MinecraftForge.EVENT_BUS.register(abilityEnemyStep);

        abilityAerialRave = new AerialRave();
        MinecraftForge.EVENT_BUS.register(abilityAerialRave);

        MinecraftForge.EVENT_BUS.register(new TeleportCanceller());


        stylishRankManager = new StylishRankManager();
        MinecraftForge.EVENT_BUS.register(stylishRankManager);


        abilityStun = new StunManager();
        MinecraftForge.EVENT_BUS.register(abilityStun);

        abilityProjectileBarrier = new ProjectileBarrier();
        MinecraftForge.EVENT_BUS.register(abilityProjectileBarrier);

        MinecraftForge.EVENT_BUS.register(new PlayerDropsEventHandler());

        MinecraftForge.EVENT_BUS.register(new MoveImputHandler());
        MinecraftForge.EVENT_BUS.register(new IllegalActionEnabler());

        InitEventBus.register(new NamedBladeManager());

        MinecraftForge.EVENT_BUS.register(new ClickCanceller());

        MinecraftForge.EVENT_BUS.register(new Taunt());


        MinecraftForge.EVENT_BUS.register(new DamageLimitter());

        //statManager = new StatManager();
        //MinecraftForge.EVENT_BUS.register(statManager);

        /*
        statManager.registerItemStat(weapon, weapon, "SlashBlade");
        statManager.registerItemStat(bladeWood, weapon, "SlashBlade");
        statManager.registerItemStat(bladeBambooLight, weapon, "SlashBlade");
        statManager.registerItemStat(bladeSilverBambooLight, weapon, "SlashBlade");
        statManager.registerItemStat(bladeWhiteSheath, weapon, "SlashBlade");
        statManager.registerItemStat(wrapBlade, weapon, "SlashBlade");
        statManager.registerItemStat(bladeNamed, weapon, "SlashBlade");
*/

        InitEventBus.post(new LoadEvent.InitEvent(evt));

        FMLInterModComms.sendMessage("BetterAchievements", SlashBlade.modname, SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yamato"));
    }

    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent evt)
    {
        List<ItemStack> items = OreDictionary.getOres("bamboo");
        if(0 == items.size()){
            ItemStack itemSphereBladeSoul =
                    SlashBlade.findItemStack(modid, SphereBladeSoulStr, 1);

            SlashBlade.addRecipe("sheath", new ShapedOreRecipe(new ResourceLocation(modid,"recipe")
                    ,wrapBlade,
                    "RBL",
                    "CIC",
                    "LBR",
                    'C', Blocks.COAL_BLOCK,
                    'R', Blocks.LAPIS_BLOCK,
                    'B', Blocks.OBSIDIAN,
                    'I', itemSphereBladeSoul,
                    'L', "logWood"));
        }

        InitEventBus.post(new LoadEvent.PostInitEvent(evt));

        EnchantHelper.initEnchantmentList();

        SpecialEffects.init();

        //AchievementList.init();

        CoreProxy.proxy.postInit();

        MinecraftForge.EVENT_BUS.register(ScheduleEntitySpawner.getInstance());

        FMLCommonHandler.instance().resetClientRecipeBook();
    }


	@Override
	public int getBurnTime(ItemStack fuel) {
		return (fuel.getItem() == this.proudSoul && fuel.getItemDamage() == 0) ? 10000 : 0;
	}


/*
    ICommand command;
    @EventHandler
    public void serverStarting(FMLServerStartingEvent evt)
    {
        command = new CommandHandler();
        evt.registerServerCommand(command);
    }
*/

    static public Map<ResourceLocationRaw, ItemStack> BladeRegistry = Maps.newHashMap();

    static public void registerCustomItemStack(String name, ItemStack stack){
        BladeRegistry.put(new ResourceLocationRaw(modid, name),stack);
    }

    static public ItemStack findItemStack(String modid, String name, int count){
        ResourceLocationRaw key = new ResourceLocationRaw(modid, name);
        ItemStack stack = ItemStack.EMPTY;

        if(BladeRegistry.containsKey(key)) {
            stack = BladeRegistry.get(new ResourceLocationRaw(modid, name)).copy();

        }else {
            Item item = Item.REGISTRY.getObject(new ResourceLocationRaw(modid, name));
            if (item != null)
                stack = new ItemStack(item);

        }

        if(!stack.isEmpty()) {
            stack.setCount(count);
        }

        return stack;
    }



    public static ItemStack getCustomBlade(String modid,String name){
        return SlashBlade.findItemStack(modid, name, 1);
    }
    public static ItemStack getCustomBlade(String key){
        String modid;
        String name;
        {
            String str[] = key.split(":",2);
            if(str.length == 2){
                modid = str[0];
                name = str[1];
            }else{
                modid = SlashBlade.modid;
                name = key;
            }
        }

        return getCustomBlade(modid,name);
    }


    @EventHandler
    public void instanced(FMLConstructionEvent event){
        MinecraftForge.EVENT_BUS.register(new CapabilityMobEffectRegister());
    }
}
