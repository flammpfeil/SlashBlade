package mods.flammpfeil.sweapon;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(name="SlashBlade",modid="flammpfeil.slashBlade",useMetadata=false,version="@VERSION@")
public class SlashBlade implements IFuelHandler{


	public static Item weapon;
	public static Item proudSoul;

	public static int itemid = 22802;
	public static int itemid2 = 22803;

	public static boolean PFLMFix = false;

	public static float offsetX,offsetY,offsetZ;


	public static Configuration mainConfiguration;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt){
		mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

		try{
			mainConfiguration.load();

			Property propShiftItemId;
			propShiftItemId = mainConfiguration.getItem(Configuration.CATEGORY_ITEM, "BaseWeapon", itemid);
			itemid= propShiftItemId.getInt();

			Property propShiftItemId2;
			propShiftItemId2 = mainConfiguration.getItem(Configuration.CATEGORY_ITEM, "ProudSoul", itemid2);
			itemid2= propShiftItemId2.getInt();

			Property propPFLMFix;
			propPFLMFix = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "PFLMFix", PFLMFix);
			PFLMFix = propPFLMFix.getBoolean(PFLMFix);

			Property propOffsets;
			propOffsets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "OffsetX", 0.0);
			offsetX = (float)propOffsets.getDouble(0.0);

			propOffsets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "OffsetY", 0.0);
			offsetY = (float)propOffsets.getDouble(0.0);

			propOffsets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "OffsetZ", 0.0);
			offsetZ = (float)propOffsets.getDouble(0.0);

		}
		finally
		{
			mainConfiguration.save();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent evt){

		weapon = (new ItemSlashBlade(itemid, EnumToolMaterial.IRON))
				.setUnlocalizedName("flammpfeil.slashblade:slashblade")
				.setTextureName("flammpfeil.slashblade:blade")
				.setCreativeTab(CreativeTabs.tabCombat);

		LanguageRegistry.instance().addName(weapon,"SlashBlade");
		LanguageRegistry.instance().addNameForObject(weapon,"ja_JP","太刀");

		proudSoul = (new ItemSWaeponMaterial(itemid2))
				.setUnlocalizedName("flammpfeil.sweapon:proudsoul")
				.setTextureName("flammpfeil.sweapon:proudsoul")
				.setCreativeTab(CreativeTabs.tabMaterials);

		LanguageRegistry.instance().addName(proudSoul,"ProudSoul");
		LanguageRegistry.instance().addNameForObject(proudSoul,"ja_JP","刀の魂片");


		LanguageRegistry.instance().addStringLocalization("flammpfeil.swaepon.info.bewitched", "bewitched");
		LanguageRegistry.instance().addStringLocalization("flammpfeil.swaepon.info.magic", "enchanted");
		LanguageRegistry.instance().addStringLocalization("flammpfeil.swaepon.info.noname", "sealed");
		LanguageRegistry.instance().addStringLocalization("flammpfeil.swaepon.info.bewitched","ja_JP", "妖");
		LanguageRegistry.instance().addStringLocalization("flammpfeil.swaepon.info.magic","ja_JP", "印");
		LanguageRegistry.instance().addStringLocalization("flammpfeil.swaepon.info.noname","ja_JP", "封");

		GameRegistry.addRecipe(new ItemStack(weapon),new Object[]{"#I","#I","ZX",
			'#',Block.blockLapis,
			'I',Item.ingotIron,
			'X',Item.swordIron});

		GameRegistry.addRecipe(new ShapelessOreRecipe(Item.expBottle,Item.glassBottle,proudSoul));

		GameRegistry.registerFuelHandler(this);

		InitProxy.proxy.initializeItemRenderer();
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		return fuel.itemID == this.proudSoul.itemID ? 20000 : 0;
	}
}
