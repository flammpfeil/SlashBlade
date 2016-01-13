package mods.flammpfeil.slashblade;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ability.*;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.gui.AchievementsExtendedGuiHandler;
import mods.flammpfeil.slashblade.item.TossEventHandler;
import mods.flammpfeil.slashblade.named.*;
import mods.flammpfeil.slashblade.named.BladeMaterials;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.DummySmeltingRecipe;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import mods.flammpfeil.slashblade.util.PotionManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.*;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

@Mod(name = SlashBlade.modname, modid = SlashBlade.modid, version = SlashBlade.version)
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

	public static Item proudSoul;

	public static Configuration mainConfiguration;

	public static ConfigEntityListManager manager;


	public static final String ProudSoulStr = "proudsoul";
	public static final String IngotBladeSoulStr = "ingot_bladesoul";
    public static final String SphereBladeSoulStr = "sphere_bladesoul";
    public static final String TinyBladeSoulStr = "tiny_bladesoul";

    public static final SlashBladeTab tab = new SlashBladeTab("flammpfeil.slashblade");

    public static final EventBus InitEventBus = new EventBus();

    //ability
    public static JustGuard abilityJustGuard;
    public static StylishRankManager stylishRankManager;
    public static SneakMove abilitySneakMove;
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
        addRecipe(key, value, false);
    }
    public static void addRecipe(String key, IRecipe value, boolean isDummy) {
        if(!isDummy)
            GameRegistry.addRecipe(value);
        recipeMultimap.put(key, value);
    }

    @EventHandler
	public void preInit(FMLPreInitializationEvent evt){
		mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

		try{
			mainConfiguration.load();
		}
		finally
		{
			mainConfiguration.save();
		}


		proudSoul = (new ItemSWaeponMaterial())
				.setUnlocalizedName("flammpfeil.slashblade.proudsoul")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(tab);
		GameRegistry.registerItem(proudSoul,"proudsoul");

		ItemStack itemProudSoul = new ItemStack(proudSoul,1,0);
		itemProudSoul.setRepairCost(-10);
		GameRegistry.registerCustomItemStack(ProudSoulStr , itemProudSoul);
		ItemStack itemIngotBladeSoul = new ItemStack(proudSoul,1,1);
		itemIngotBladeSoul.setRepairCost(-25);
		GameRegistry.registerCustomItemStack(IngotBladeSoulStr , itemIngotBladeSoul);
        ItemStack itemSphereBladeSoul = new ItemStack(proudSoul,1,2);
        itemSphereBladeSoul.setRepairCost(-50);
        GameRegistry.registerCustomItemStack(SphereBladeSoulStr , itemSphereBladeSoul);
        ItemStack itemTinyBladeSoul = new ItemStack(proudSoul,1,3);
        GameRegistry.registerCustomItemStack(TinyBladeSoulStr , itemTinyBladeSoul);

        //==================================================================================================================================

		weapon = (ItemSlashBlade)(new ItemSlashBlade(ToolMaterial.IRON, 4 + ToolMaterial.EMERALD.getDamageVsEntity()))
				.setRepairMaterial(new ItemStack(Items.iron_ingot))
				.setRepairMaterialOreDic("ingotSteel","nuggetSteel")
				.setUnlocalizedName("flammpfeil.slashblade")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(tab);

		GameRegistry.registerItem(weapon, "slashblade");

        //==================================================================================================================================

        bladeWood = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.WOOD.getDamageVsEntity()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/wood.png"))
                .setRepairMaterialOreDic("logWood")
                .setMaxDamage(60)
                .setUnlocalizedName("flammpfeil.slashblade.wood")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(bladeWood, "slashbladeWood");

        bladeBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.STONE.getDamageVsEntity()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/banboo.png"))
                .setRepairMaterialOreDic("bamboo")
                .setMaxDamage(50)
                .setUnlocalizedName("flammpfeil.slashblade.bamboo")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(bladeBambooLight, "slashbladeBambooLight");

        bladeSilverBambooLight = (ItemSlashBladeBambooLight)(new ItemSlashBladeBambooLight(ToolMaterial.WOOD, 4 + ToolMaterial.IRON.getDamageVsEntity()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/silverbanboo.png"))
                .setRepairMaterialOreDic("bamboo")
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.silverbamboo")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(bladeSilverBambooLight, "slashbladeSilverBambooLight");

        bladeWhiteSheath = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.IRON, 4 + ToolMaterial.IRON.getDamageVsEntity()))
                .setDestructable(false)
                .setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/white.png"))
                .setRepairMaterial(new ItemStack(Items.iron_ingot))
                .setRepairMaterialOreDic("ingotSteel","nuggetSteel")
                .setMaxDamage(70)
                .setUnlocalizedName("flammpfeil.slashblade.white")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(bladeWhiteSheath, "slashbladeWhite");



        //==================================================================================================================================

        wrapBlade = (ItemSlashBladeWrapper)(new ItemSlashBladeWrapper(ToolMaterial.IRON))
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.wrapper")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(wrapBlade, "slashbladeWrapper");




        bladeNamed = (ItemSlashBladeNamed)(new ItemSlashBladeNamed(ToolMaterial.IRON, 4.0f))
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.named")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(bladeNamed, "slashbladeNamed");


		GameRegistry.registerFuelHandler(this);

		InitProxy.proxy.initializeItemRenderer();

		manager = new ConfigEntityListManager();

        FMLCommonHandler.instance().bus().register(manager);

        PacketHandler.init();

        RecipeSorter.register("flammpfeil.slashblade:upgrade", RecipeUpgradeBlade.class, SHAPED, "after:forge:shaped");
        RecipeSorter.register("flammpfeil.slashblade:wrap", RecipeWrapBlade.class, SHAPED, "after:forge:shaped");
        RecipeSorter.register("flammpfeil.slashblade:adjust", RecipeAdjustPos.class, SHAPED, "after:forge:shaped");
        RecipeSorter.register("flammpfeil.slashblade:repair", RecipeInstantRepair.class, SHAPED, "after:forge:shaped");
        RecipeSorter.register("flammpfeil.slashblade:awake", RecipeAwakeBlade.class, SHAPED, "after:forge:shaped");


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
    }

    StatManager statManager;

    @EventHandler
    public void init(FMLInitializationEvent evt){

        GameRegistry.addRecipe(new RecipeWrapBlade());

        GameRegistry.addRecipe(new RecipeAdjustPos());

        RecipeInstantRepair recipeRepair = new RecipeInstantRepair();
        GameRegistry.addRecipe(recipeRepair);

        FMLCommonHandler.instance().bus().register(recipeRepair);


        int entityId = 1;
        EntityRegistry.registerModEntity(EntityDrive.class, "Drive", entityId++, this, 250, 1, true);
        EntityRegistry.registerModEntity(EntityPhantomSword.class, "PhantomSword", entityId++, this, 250, 1, true);
        EntityRegistry.registerModEntity(EntityDirectAttackDummy.class, "DirectAttackDummy", entityId++, this, 250, 1, true);

        EntityRegistry.registerModEntity(EntityPhantomSwordBase.class, "PhantomSwordBase", entityId++, this, 250, 1, true);

        EntityRegistry.registerModEntity(EntityJudgmentCutManager.class, "JudgmentCutManager", entityId++, this, 250, 1, true);
        EntityRegistry.registerModEntity(EntitySakuraEndManager.class, "SakuraEndManager", entityId++, this, 250, 1, true);

        EntityRegistry.registerModEntity(EntityJustGuardManager.class, "JustGuardManager", entityId++, this, 250, 1, true);

        EntityRegistry.registerModEntity(EntityBladeStand.class, "BladeStand", 100, this, 250, 1000, false);


        MinecraftForge.EVENT_BUS.register(new DropEventHandler());

        MinecraftForge.EVENT_BUS.register(new SlashBladeItemDestroyEventHandler());
        MinecraftForge.EVENT_BUS.register(new TossEventHandler());

        //ability
        abilityJustGuard = new JustGuard();
        MinecraftForge.EVENT_BUS.register(abilityJustGuard);

        abilitySneakMove = new SneakMove();
        MinecraftForge.EVENT_BUS.register(abilitySneakMove);

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


        stylishRankManager = new StylishRankManager();
        MinecraftForge.EVENT_BUS.register(stylishRankManager);


        abilityStun = new StunManager();
        MinecraftForge.EVENT_BUS.register(abilityStun);

        abilityProjectileBarrier = new ProjectileBarrier();
        MinecraftForge.EVENT_BUS.register(abilityProjectileBarrier);

        new PotionManager();

        statManager = new StatManager();
        MinecraftForge.EVENT_BUS.register(statManager);

        statManager.registerItemStat(weapon, weapon, "SlashBlade");
        statManager.registerItemStat(bladeWood, weapon, "SlashBlade");
        statManager.registerItemStat(bladeBambooLight, weapon, "SlashBlade");
        statManager.registerItemStat(bladeSilverBambooLight, weapon, "SlashBlade");
        statManager.registerItemStat(bladeWhiteSheath, weapon, "SlashBlade");
        statManager.registerItemStat(wrapBlade, weapon, "SlashBlade");
        statManager.registerItemStat(bladeNamed, weapon, "SlashBlade");

        InitEventBus.post(new LoadEvent.InitEvent(evt));
    }

    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent evt)
    {
        ArrayList<ItemStack> items = OreDictionary.getOres("bamboo");
        if(0 == items.size()){
            ItemStack itemSphereBladeSoul =
                    GameRegistry.findItemStack(modid, SphereBladeSoulStr, 1);

            GameRegistry.addRecipe(new ShapedOreRecipe(wrapBlade,
                    "RBL",
                    "CIC",
                    "LBR",
                    'C', Blocks.coal_block,
                    'R', Blocks.lapis_block,
                    'B', Blocks.obsidian,
                    'I', itemSphereBladeSoul,
                    'L', "logWood"));
        }

        InitEventBus.post(new LoadEvent.PostInitEvent(evt));

        EnchantHelper.initEnchantmentList();

        SpecialEffects.init();

        AchievementList.init();
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


    public static ItemStack getCustomBlade(String modid,String name){
        ItemStack blade = GameRegistry.findItemStack(modid, name, 1);

        if(blade != null){
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);

            if(ItemSlashBladeNamed.IsDefaultBewitched.get(tag)){
                blade.setStackDisplayName(blade.getDisplayName());
            }
        }

        return blade;
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
}
