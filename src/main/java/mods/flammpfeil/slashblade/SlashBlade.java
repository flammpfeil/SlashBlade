package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(name=SlashBlade.modname,modid=SlashBlade.modid,version="1.7.2 r4")
public class SlashBlade implements IFuelHandler{


	public static final String modname = "SlashBlade";
	public static final String modid = "flammpfeil.slashblade";

	public static final String BrokenBladeWhiteStr = "BrokenBladeWhite";

    public static Set<Item> blades = new HashSet<Item>();

	public static ItemSlashBlade weapon;
	public static ItemSlashBladeDetune bladeWood;
	public static ItemSlashBladeDetune bladeBambooLight;
	public static ItemSlashBladeDetune bladeSilverBambooLight;
	public static ItemSlashBladeDetune bladeWhiteSheath;
	public static Item proudSoul;

//	public static float offsetX,offsetY,offsetZ;

	public static Map<String,Boolean> attackDisabled = new HashMap<String,Boolean>();

	public static Configuration mainConfiguration;

	public static ConfigEntityListManager manager;


	public static final String ProudSoulStr = "proudsoul";
	public static final String IngotBladeSoulStr = "ingot_bladesoul";
	public static final String SphereBladeSoulStr = "sphere_bladesoul";

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt){
		mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

		try{
			mainConfiguration.load();
/*
			Property propOffsets;
			propOffsets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "OffsetX", 0.0);
			offsetX = (float)propOffsets.getDouble(0.0);

			propOffsets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "OffsetY", 0.0);
			offsetY = (float)propOffsets.getDouble(0.0);

			propOffsets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "OffsetZ", 0.0);
			offsetZ = (float)propOffsets.getDouble(0.0);
*/
		}
		finally
		{
			mainConfiguration.save();
		}

		proudSoul = (new ItemSWaeponMaterial())
				.setUnlocalizedName("flammpfeil.slashblade.proudsoul")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabMaterials);
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

		weapon = (ItemSlashBlade)(new ItemSlashBlade(ToolMaterial.IRON, 4 + ToolMaterial.EMERALD.getDamageVsEntity()))
				.setRepairMaterial(new ItemStack(Items.iron_ingot))
				.setRepairMaterialOreDic("ingotSteel","nuggetSteel")
				.setUnlocalizedName("flammpfeil.slashblade")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);

		GameRegistry.registerItem(weapon, "slashblade");

		bladeWood = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.WOOD.getDamageVsEntity()))
				.setDestructable(true)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/wood.png"))
				.setRepairMaterialOreDic("logWood")
				.setMaxDamage(60)
				.setUnlocalizedName("flammpfeil.slashblade.wood")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeWood, "slashbladeWood");

		bladeBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.STONE.getDamageVsEntity()))
				.setDestructable(true)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/banboo.png"))
				.setRepairMaterialOreDic("bamboo")
				.setMaxDamage(50)
				.setUnlocalizedName("flammpfeil.slashblade.bamboo")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeBambooLight, "slashbladeBambooLight");

		bladeSilverBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.IRON.getDamageVsEntity()))
				.setDestructable(true)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/silverbanboo.png"))
				.setRepairMaterialOreDic("bamboo")
				.setMaxDamage(40)
				.setUnlocalizedName("flammpfeil.slashblade.silverbamboo")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeSilverBambooLight, "slashbladeSilverBambooLight");

		bladeWhiteSheath = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.IRON, 4 + ToolMaterial.IRON.getDamageVsEntity()))
				.setDestructable(false)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/white.png"))
				.setRepairMaterial(new ItemStack(Items.iron_ingot))
				.setRepairMaterialOreDic("ingotSteel","nuggetSteel")
				.setMaxDamage(70)
				.setUnlocalizedName("flammpfeil.slashblade.white")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeWhiteSheath, "slashbladeWhite");

        blades.add(weapon);
        blades.add(bladeWood);
        blades.add(bladeBambooLight);
        blades.add(bladeSilverBambooLight);
        blades.add(bladeWhiteSheath);

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeWood),
                "  #",
                " # ",
                "X  ",
                '#', "logWood",
                'X', new ItemStack(Items.wooden_sword, 1, 1)));


		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeBambooLight),
				"  #",
				" # ",
				"X  ",
				'#',"bamboo",
				'X', new ItemStack(bladeWood,1, OreDictionary.WILDCARD_VALUE)));


		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeSilverBambooLight),
				" TI",
				"SXK",
				"PS ",
				'T', Items.egg,
				'I', Items.iron_ingot,
				'S', Items.string,
				'X', new ItemStack(bladeBambooLight,1,OreDictionary.WILDCARD_VALUE),
				'K', "dyeBlack",
				'P', Items.paper //S
				));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeSilverBambooLight),
				" TI",
				"SXK",
				"PS ",
				'T', Items.egg,
				'I', "ingotSilver",
				'S', Items.string,
                'X', new ItemStack(bladeBambooLight,1,OreDictionary.WILDCARD_VALUE),
				'K', "dyeBlack",
				'P', Items.paper
				));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeWhiteSheath, 1, bladeWhiteSheath.getMaxDamage() / 3),
				"  #",
				" # ",
				"XG ",
				'#', Items.iron_ingot,
				'G', Items.gold_ingot,
				'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeWhiteSheath, 1, bladeWhiteSheath.getMaxDamage() / 4),
				"  #",
				" # ",
				"XG ",
				'#', "ingotSteel",
				'G', Items.gold_ingot,
				'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeWhiteSheath, 1),
				"  #",
				" # ",
				"XG ",
				'#', itemIngotBladeSoul,
				'G', Items.gold_ingot,
				'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));

		ItemStack brokenBladeWhite = new ItemStack(bladeWhiteSheath,1,0);
		brokenBladeWhite.setItemDamage(brokenBladeWhite.getMaxDamage());
		brokenBladeWhite.setStackDisplayName("BrokenBladeWhite");
		brokenBladeWhite.getTagCompound().setBoolean(ItemSlashBlade.isBrokenStr, true);
		GameRegistry.registerCustomItemStack(BrokenBladeWhiteStr, brokenBladeWhite);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(weapon),
				" BI",
				"L#C",
				"SG ",
				'L', Blocks.lapis_block,
				'C', Blocks.coal_block,
				'I', itemSphereBladeSoul,
				'B', Items.blaze_rod,
				'G', Items.gold_ingot,
				'S', Items.string,
				'#', brokenBladeWhite
				));

		GameRegistry.addRecipe(new ShapedOreRecipe(itemIngotBladeSoul,
				"PPP",
				"PIP",
				"PPP",
				'I', Items.iron_ingot,
				'P', itemProudSoul));

		GameRegistry.addRecipe(new ShapedOreRecipe(itemIngotBladeSoul,
				" P ",
				"PIP",
				" P ",
				'I', "ingotSteel",
				'P', itemProudSoul));

        GameRegistry.addSmelting(itemIngotBladeSoul , itemSphereBladeSoul, 2.0F);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.experience_bottle),
				"XXX",
				"XIX",
				"XXX",
				'I',Items.glass_bottle,
				'X',itemProudSoul));

        GameRegistry.addRecipe(new RecipeAdjustPos());

        RecipeInstantRepair recipeRepair = new RecipeInstantRepair();
        GameRegistry.addRecipe(recipeRepair);

        FMLCommonHandler.instance().bus().register(recipeRepair);

		GameRegistry.registerFuelHandler(this);

		InitProxy.proxy.initializeItemRenderer();

		manager = new ConfigEntityListManager();

        FMLCommonHandler.instance().bus().register(manager);
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		return (fuel.getItem() == this.proudSoul && fuel.getItemDamage() == 0) ? 20000 : 0;
	}


}
