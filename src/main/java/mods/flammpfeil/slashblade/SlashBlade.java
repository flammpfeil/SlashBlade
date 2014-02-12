package mods.flammpfeil.slashblade;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(name="SlashBlade",modid="flammpfeil.slashblade",useMetadata=false,version="1.7.2 r3")
public class SlashBlade implements IFuelHandler{


	public static Item weapon;
	public static Item proudSoul;

//	public static float offsetX,offsetY,offsetZ;

	public static Map<String,Boolean> attackDisabled = new HashMap<String,Boolean>();

	public static Configuration mainConfiguration;

	public static ConfigEntityListManager manager;

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



		weapon = (new ItemSlashBlade(Item.ToolMaterial.IRON))
				.setUnlocalizedName("flammpfeil.slashblade")
				.setTextureName("flammpfeil.slashblade:blade")
				.setCreativeTab(CreativeTabs.tabCombat);

		GameRegistry.registerItem(weapon, "slashblade");

		proudSoul = (new ItemSWaeponMaterial())
				.setUnlocalizedName("flammpfeil.slashblade.proudsoul")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabMaterials);

		GameRegistry.registerItem(proudSoul,"proudsoul");

		GameRegistry.addRecipe(new ItemStack(weapon),new Object[]{"#I","#I","ZX",
			'#',Blocks.lapis_block,
			'I',Items.iron_ingot,
			'X',Items.iron_sword});

		GameRegistry.addRecipe(new ShapelessOreRecipe(Items.experience_bottle,Items.glass_bottle,proudSoul));

		GameRegistry.registerFuelHandler(this);

		InitProxy.proxy.initializeItemRenderer();

		manager = new ConfigEntityListManager();

        FMLCommonHandler.instance().bus().register(manager);

        GameRegistry.addRecipe(new RecipeAdjustPos());
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		return fuel.getItem() == this.proudSoul ? 20000 : 0;
	}


}
