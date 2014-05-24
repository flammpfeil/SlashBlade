package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

@Mod(name=SlashBlade.modname,modid=SlashBlade.modid,version="1.7.2 r9.8")
public class SlashBlade implements IFuelHandler{


	public static final String modname = "SlashBlade";
	public static final String modid = "flammpfeil.slashblade";

	public static final String BrokenBladeWhiteStr = "BrokenBladeWhite";
	public static final String HundredKillSilverBambooLightStr = "HundredKillSilverBambooLight";

	public static ItemSlashBlade weapon;
	public static ItemSlashBladeDetune bladeWood;
	public static ItemSlashBladeDetune bladeBambooLight;
	public static ItemSlashBladeDetune bladeSilverBambooLight;
    public static ItemSlashBladeDetune bladeWhiteSheath;
    public static ItemSlashBladeNamed bladeNamed;

    public static ItemSlashBladeWrapper wrapBlade = null;

	public static Item proudSoul;

//	public static float offsetX,offsetY,offsetZ;

	public static Map<String,Boolean> attackDisabled = new HashMap<String,Boolean>();

	public static Configuration mainConfiguration;

	public static ConfigEntityListManager manager;


	public static final String ProudSoulStr = "proudsoul";
	public static final String IngotBladeSoulStr = "ingot_bladesoul";
    public static final String SphereBladeSoulStr = "sphere_bladesoul";
    public static final String TinyBladeSoulStr = "tiny_bladesoul";

    public static final SlashBladeTab tab = new SlashBladeTab("flammpfeil.slashblade");

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt){
		mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

		try{
			mainConfiguration.load();
            /*
            {
                Property prop;
                prop = mainConfiguration.get(Configuration.CATEGORY_GENERAL,"useDettuneBlades",useDetuneBlades);
                useDetuneBlades = prop.getBoolean(useDetuneBlades);
            }
            {
                Property prop;
                prop = mainConfiguration.get(Configuration.CATEGORY_GENERAL,"useWrapBlades",useWrapBlades);
                useWrapBlades = prop.getBoolean(useWrapBlades);
            }
            {
                Property prop;
                prop = mainConfiguration.get(Configuration.CATEGORY_GENERAL,"scabbardRecipeLevel",scabbardRecipeLevel,"value:0 - 2 , only work useWrapBlades:true");
                scabbardRecipeLevel = prop.getInt();
            }
            */
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

        bladeSilverBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(ToolMaterial.WOOD, 4 + ToolMaterial.IRON.getDamageVsEntity()))
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

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(bladeWood),
                "  #",
                " # ",
                "X  ",
                '#', "logWood",
                'X', new ItemStack(Items.wooden_sword, 1, 1)));


        GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeBambooLight),
                "  #",
                " # ",
                "X  ",
                '#',"bamboo",
                'X', new ItemStack(bladeWood,1, OreDictionary.WILDCARD_VALUE)));


        GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeSilverBambooLight),
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
        GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeSilverBambooLight),
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


        ItemStack hundredKillSilverBambooLight = new ItemStack(bladeSilverBambooLight,1,0);
        hundredKillSilverBambooLight.setItemDamage(hundredKillSilverBambooLight.getMaxDamage());
        hundredKillSilverBambooLight.setStackDisplayName("HundredKillSBL");
        hundredKillSilverBambooLight.getTagCompound().setInteger(ItemSlashBlade.killCountStr, 100);
        GameRegistry.registerCustomItemStack(HundredKillSilverBambooLightStr, hundredKillSilverBambooLight);

        GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeWhiteSheath, 1, bladeWhiteSheath.getMaxDamage() / 3),
                "  #",
                " # ",
                "XG ",
                '#', Items.iron_ingot,
                'G', Items.gold_ingot,
                'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeWhiteSheath, 1, bladeWhiteSheath.getMaxDamage() / 4),
                "  #",
                " # ",
                "XG ",
                '#', "ingotSteel",
                'G', Items.gold_ingot,
                'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeWhiteSheath, 1),
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

        GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(weapon),
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

        //==================================================================================================================================

        wrapBlade = (ItemSlashBladeWrapper)(new ItemSlashBladeWrapper(ToolMaterial.IRON))
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.wrapper")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(CreativeTabs.tabCombat);
        GameRegistry.registerItem(wrapBlade, "slashbladeWrapper");


        GameRegistry.addRecipe(new RecipeWrapBlade());

        //==================================================================================================================================


        GameRegistry.addRecipe(new ShapelessOreRecipe(GameRegistry.findItemStack(modid,TinyBladeSoulStr,2),
                itemProudSoul));

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

        //==================================================================================================================================


        bladeNamed = (ItemSlashBladeNamed)(new ItemSlashBladeNamed(ToolMaterial.IRON, 4.0f))
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.named")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(bladeNamed, "slashbladeNamed");

        {
            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            customblade.addEnchantment(Enchantment.unbreaking,3);
            customblade.addEnchantment(Enchantment.smite,3);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.tagayasan");
            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr,70);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr,4 + ToolMaterial.IRON.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr,"named/tagayasan");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr,1);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,1);

            tag.setString(ItemSlashBladeNamed.RepairMaterialNameStr,"iron_ingot");

            String name = "flammpfeil.slashblade.named.tagayasan";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);


            {
                ItemStack reqiredBlade = new ItemStack(bladeWood);
                NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                reqTag.setInteger(ItemSlashBlade.killCountStr,1000);

                reqiredBlade.setStackDisplayName("thousandkill woodblade");

                name = "flammpfeil.slashblade.tagayasan.reqired";
                GameRegistry.registerCustomItemStack(name, reqiredBlade);
                ItemSlashBladeNamed.BladeNames.add(name);

                GameRegistry.addRecipe(new RecipeAwakeBlade(customblade,
                        true,
                        reqiredBlade,
                        "XEX",
                        "PBP",
                        "XEX",
                        'X',itemSphereBladeSoul,
                        'B',reqiredBlade,
                        'P',new ItemStack(Items.ender_pearl),
                        'E',new ItemStack(Items.ender_eye)));
            }
        }

        {

            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            customblade.addEnchantment(Enchantment.thorns, 1);
            customblade.addEnchantment(Enchantment.featherFalling, 4);
            customblade.addEnchantment(Enchantment.power, 5);
            customblade.addEnchantment(Enchantment.punch, 2);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr, "flammpfeil.slashblade.named.yamato");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr,40);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.EMERALD.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr, "named/yamato");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/yamato");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr, 0);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,1);

            tag.setInteger(ItemSlashBlade.killCountStr, 1000);
            tag.setInteger(ItemSlashBlade.proudSoulStr, 1000);

            String name = "flammpfeil.slashblade.named.yamato.youtou";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);

        }

        {

            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);
            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.yamato.broken");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr,40);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.EMERALD.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr, "named/yamato");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/yamato");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr, 0);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,1);

            tag.setBoolean(ItemSlashBlade.isBrokenStr,true);
            tag.setBoolean(ItemSlashBlade.isNoScabbardStr,true);
            tag.setBoolean(ItemSlashBlade.isSealedStr,true);
            tag.setString(ItemSlashBladeNamed.TrueItemNameStr, "slashblade.named.yamato");

            String name = "flammpfeil.slashblade.named.yamato.broken";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);

            {
                ItemStack reqiredBlade = customblade.copy();
                NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                reqTag.setInteger(ItemSlashBlade.proudSoulStr,1000);

                reqiredBlade.setStackDisplayName("thousandProudSouls");

                name = "flammpfeil.slashblade.named.yamato.reqired";
                GameRegistry.registerCustomItemStack(name, reqiredBlade);
                ItemSlashBladeNamed.BladeNames.add(name);

                ItemStack yamato = GameRegistry.findItemStack(modid,"flammpfeil.slashblade.named.yamato.youtou",1);
                GameRegistry.addRecipe(new RecipeAwakeBlade(yamato,
                        true,
                        reqiredBlade,
                        "XXX",
                        "XBX",
                        "XXX",
                        'X',itemSphereBladeSoul,
                        'B',reqiredBlade));
            }
        }

        {
            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            customblade.addEnchantment(Enchantment.sharpness, 4);
            customblade.addEnchantment(Enchantment.unbreaking, 3);
            customblade.addEnchantment(Enchantment.fireAspect, 2);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.yuzukitukumo");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr,40);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.EMERALD.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr,"named/a_tukumo");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/agito");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr,3);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,1);

            String name = "flammpfeil.slashblade.named.yuzukitukumo.youtou";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);
        }
        {
            ItemStack customblade = new ItemStack(weapon,1,0);

            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            customblade.addEnchantment(Enchantment.fireAspect, 1);

            tag.setInteger(ItemSlashBlade.killCountStr, 1000);

            String name = "flammpfeil.slashblade.thousandkill.youtou";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);

            GameRegistry.addRecipe(new RecipeAwakeBlade(ItemSlashBladeNamed.getCustomBlade("flammpfeil.slashblade.named.yuzukitukumo.youtou"),
                    true,
                    customblade,
                    "ESD",
                    "RBL",
                    "ISG",
                    'E', new ItemStack(Blocks.emerald_block),
                    'D', new ItemStack(Blocks.diamond_block),
                    'R', new ItemStack(Blocks.redstone_block),
                    'L', new ItemStack(Blocks.lapis_block),
                    'I', new ItemStack(Blocks.iron_block),
                    'G', new ItemStack(Blocks.gold_block),
                    'S', GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1),
                    'B', customblade));

            //GameRegistry.addRecipe(new RecipeAwakeTukumoBlade());
        }

        ItemSlashBladeNamed.BladeNames.add(SlashBlade.BrokenBladeWhiteStr);
        ItemSlashBladeNamed.BladeNames.add(SlashBlade.HundredKillSilverBambooLightStr);


        //==================================================================================================================================


        //------------- false
        {
            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.agito");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr, 60);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.IRON.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr,"named/agito_false");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/agito");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr,2);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,2);

            String name = "flammpfeil.slashblade.named.agito";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);
        }

        {
            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.agito.rust");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr, 60);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.STONE.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr,"named/agito_rust");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/agito");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr,2);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,2);

            tag.setBoolean(ItemSlashBlade.isSealedStr,true);

            tag.setString(ItemSlashBladeNamed.TrueItemNameStr,"flammpfeil.slashblade.named.agito");

            String name = "flammpfeil.slashblade.named.agito.rust";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);


            {
                ItemStack reqiredBlade = customblade.copy();
                NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                reqTag.setInteger(ItemSlashBlade.killCountStr,100);
                reqTag.setInteger(ItemSlashBlade.RepairCounterStr,1);

                reqiredBlade.setStackDisplayName("agito rust");

                name = "flammpfeil.slashblade.named.agito.reqired";
                GameRegistry.registerCustomItemStack(name, reqiredBlade);
                ItemSlashBladeNamed.BladeNames.add(name);

                ItemStack destBlade = GameRegistry.findItemStack(modid,tag.getString(ItemSlashBladeNamed.TrueItemNameStr),1);
                GameRegistry.addRecipe(new RecipeAwakeBlade(destBlade,
                        true,
                        reqiredBlade,
                        " X ",
                        "XBX",
                        " X ",
                        'X',itemProudSoul,
                        'B',reqiredBlade));
            }
        }
        //------------- true
        {
            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.orotiagito");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr, 60);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.EMERALD.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr,"named/orotiagito");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/agito");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr,2);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,2);

            String name = "flammpfeil.slashblade.named.orotiagito";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);
        }

        {
            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.orotiagito.seald");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr, 60);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.IRON.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr,"named/agito_true");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/agito");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr,2);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,2);

            tag.setString(ItemSlashBladeNamed.TrueItemNameStr,"flammpfeil.slashblade.named.orotiagito");

            String name = "flammpfeil.slashblade.named.orotiagito.seald";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);


            {
                ItemStack reqiredBlade = customblade.copy();
                NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                reqTag.setInteger(ItemSlashBlade.killCountStr,1000);
                reqTag.setInteger(ItemSlashBlade.proudSoulStr,1000);
                reqTag.setInteger(ItemSlashBlade.RepairCounterStr,10);

                reqiredBlade.setStackDisplayName("orotiagito seald");

                name = "flammpfeil.slashblade.named.orotiagito.reqired";
                GameRegistry.registerCustomItemStack(name, reqiredBlade);
                ItemSlashBladeNamed.BladeNames.add(name);

                ItemStack destBlade = GameRegistry.findItemStack(modid,tag.getString(ItemSlashBladeNamed.TrueItemNameStr),1);
                GameRegistry.addRecipe(new RecipeAwakeBlade(destBlade,
                        true,
                        reqiredBlade,
                        "PXP",
                        "XBX",
                        "PXP",
                        'X',itemSphereBladeSoul,
                        'P',itemProudSoul,
                        'B',reqiredBlade));
            }
        }

        {
            ItemStack customblade = new ItemStack(bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            tag.setString(ItemSlashBladeNamed.CurrentItemNameStr,"flammpfeil.slashblade.named.orotiagito.rust");

            tag.setInteger(ItemSlashBladeNamed.CustomMaxDamageStr, 60);
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + ToolMaterial.STONE.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr, "named/agito_rust_true");
            tag.setString(ItemSlashBlade.ModelNameStr,"named/agito");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr, 2);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,2);

            tag.setBoolean(ItemSlashBlade.isSealedStr, true);

            tag.setString(ItemSlashBladeNamed.TrueItemNameStr, "flammpfeil.slashblade.named.orotiagito.seald");

            String name = "flammpfeil.slashblade.named.orotiagito.rust";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);

            {
                ItemStack reqiredBlade = customblade.copy();
                NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                reqTag.setInteger(ItemSlashBlade.killCountStr,100);
                reqTag.setInteger(ItemSlashBlade.RepairCounterStr,1);

                reqiredBlade.setStackDisplayName("orotiagito rust");

                name = "flammpfeil.slashblade.named.orotiagito.seald.reqired";
                GameRegistry.registerCustomItemStack(name, reqiredBlade);
                ItemSlashBladeNamed.BladeNames.add(name);

                ItemStack destBlade = GameRegistry.findItemStack(modid,tag.getString(ItemSlashBladeNamed.TrueItemNameStr),1);
                GameRegistry.addRecipe(new RecipeAwakeBlade(destBlade,
                        true,
                        reqiredBlade,
                        " X ",
                        "XBX",
                        " X ",
                        'X',itemProudSoul,
                        'B',reqiredBlade));
            }
        }

        //==================================================================================================================================

        GameRegistry.addRecipe(new RecipeAdjustPos());

        RecipeInstantRepair recipeRepair = new RecipeInstantRepair();
        GameRegistry.addRecipe(recipeRepair);

        FMLCommonHandler.instance().bus().register(recipeRepair);

		GameRegistry.registerFuelHandler(this);

		InitProxy.proxy.initializeItemRenderer();

		manager = new ConfigEntityListManager();

        FMLCommonHandler.instance().bus().register(manager);


        RecipeSorter.register("flammpfeil.slashblade:upgrade", RecipeUpgradeBlade.class, SHAPED, "after:forge:shaped");
        RecipeSorter.register("flammpfeil.slashblade:wrap", RecipeWrapBlade.class, SHAPED, "after:forge:shaped");
        RecipeSorter.register("flammpfeil.slashblade:adjust", RecipeAdjustPos.class, SHAPED, "after:forge:shaped");
        RecipeSorter.register("flammpfeil.slashblade:repair", RecipeInstantRepair.class, SHAPED, "after:forge:shaped");
    }

    StatManager statManager;

    @EventHandler
    public void init(FMLInitializationEvent evt){
        EntityRegistry.registerModEntity(EntityDrive.class, "Drive", 1, this, 250, 1, true);
        EntityRegistry.registerModEntity(EntityPhantomSword.class, "PhantomSword", 2, this, 250, 1, true);


        MinecraftForge.EVENT_BUS.register(new DropEventHandler());

        statManager = new StatManager();
        MinecraftForge.EVENT_BUS.register(statManager);

        statManager.registerItemStat(weapon, weapon, "SlashBlade");
        statManager.registerItemStat(bladeWood, weapon, "SlashBlade");
        statManager.registerItemStat(bladeBambooLight, weapon, "SlashBlade");
        statManager.registerItemStat(bladeSilverBambooLight, weapon, "SlashBlade");
        statManager.registerItemStat(bladeWhiteSheath, weapon, "SlashBlade");
        statManager.registerItemStat(wrapBlade, weapon, "SlashBlade");
        statManager.registerItemStat(bladeNamed, weapon, "SlashBlade");

        DropEventHandler.registerEntityDrop("EnderDragon", 1.0f, GameRegistry.findItemStack(modid, "flammpfeil.slashblade.named.yamato.broken", 1));
        DropEventHandler.registerEntityDrop("TwilightForest.Hydra", 0.3f, GameRegistry.findItemStack(modid, "flammpfeil.slashblade.named.orotiagito.rust", 1));
        DropEventHandler.registerEntityDrop("TwilightForest.Naga",0.3f,GameRegistry.findItemStack(modid,"flammpfeil.slashblade.named.agito.rust",1));
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
    }


	@Override
	public int getBurnTime(ItemStack fuel) {
		return (fuel.getItem() == this.proudSoul && fuel.getItemDamage() == 0) ? 20000 : 0;
	}



    ICommand command;
    @EventHandler
    public void serverStarting(FMLServerStartingEvent evt)
    {
        command = new CommandHandler();
        evt.registerServerCommand(command);
    }


}
