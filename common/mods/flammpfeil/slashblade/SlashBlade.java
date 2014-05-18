package mods.flammpfeil.slashblade;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.spi.RegisterableService;

import net.minecraft.block.Block;import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name=SlashBlade.modname,modid=SlashBlade.modid,version="@VERSION@")
@NetworkMod(clientSideRequired=true)
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

	public static int itemid = 22802;
	public static int itemid2 = 22803;

	public static int itemidWood = 22804;
	public static int itemidBamboo = 22805;
	public static int itemidSilverBamboo = 22806;
	public static int itemidWhite = 22807;

	public static int itemidWrap = 22808;
	public static int itemidNamed = 22809;

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

			{
				Property propShiftItemId;
				propShiftItemId = mainConfiguration.getItem(Configuration.CATEGORY_ITEM, "BaseWeapon", itemid);
				itemid= propShiftItemId.getInt();
			}

			Property propShiftItemId2;
			propShiftItemId2 = mainConfiguration.getItem(Configuration.CATEGORY_ITEM, "ProudSoul", itemid2);
			itemid2= propShiftItemId2.getInt();


			{
				Property propShiftItemId;
				propShiftItemId = mainConfiguration.getItem("BladeWood", itemidWood);
				itemidWood = propShiftItemId.getInt();
			}
			{
				Property propShiftItemId;
				propShiftItemId = mainConfiguration.getItem("BladeBambooLight", itemidBamboo);
				itemidBamboo = propShiftItemId.getInt();
			}
			{
				Property propShiftItemId;
				propShiftItemId = mainConfiguration.getItem("BladeSilverBambooLight", itemidSilverBamboo);
				itemidSilverBamboo = propShiftItemId.getInt();
			}
			{
				Property propShiftItemId;
				propShiftItemId = mainConfiguration.getItem("BladeWhiteSheath", itemidWhite);
				itemidWhite = propShiftItemId.getInt();
			}

			{
				Property propShiftItemId;
				propShiftItemId = mainConfiguration.getItem("BladeSheath", itemidWrap);
				itemidWrap = propShiftItemId.getInt();
			}


			{
				Property propShiftItemId;
				propShiftItemId = mainConfiguration.getItem("BladeNamed", itemidNamed);
				itemidNamed = propShiftItemId.getInt();
			}

		}
		finally
		{
			mainConfiguration.save();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent evt){

		proudSoul = (new ItemSWaeponMaterial(itemid2))
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

		weapon = (ItemSlashBlade)(new ItemSlashBlade(itemid, EnumToolMaterial.IRON, 4 + EnumToolMaterial.EMERALD.getDamageVsEntity()))
				.setRepairMaterial(new ItemStack(Item.ingotIron))
				.setRepairMaterialOreDic("ingotSteel","nuggetSteel")
				.setUnlocalizedName("flammpfeil.slashblade")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(tab);
		GameRegistry.registerItem(weapon, "slashblade");

        //==================================================================================================================================

		bladeWood = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidWood, EnumToolMaterial.WOOD, 4 + EnumToolMaterial.WOOD.getDamageVsEntity()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/wood.png"))
                .setRepairMaterialOreDic("logWood")
                .setMaxDamage(60)
                .setUnlocalizedName("flammpfeil.slashblade.wood")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
		GameRegistry.registerItem(bladeWood, "slashbladeWood");

		bladeBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidBamboo, EnumToolMaterial.WOOD, 4 + EnumToolMaterial.STONE.getDamageVsEntity()))
                .setDestructable(true)
                .setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/banboo.png"))
                .setRepairMaterialOreDic("bamboo")
                .setMaxDamage(50)
                .setUnlocalizedName("flammpfeil.slashblade.bamboo")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
        GameRegistry.registerItem(bladeBambooLight, "slashbladeBambooLight");

		bladeSilverBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidSilverBamboo, EnumToolMaterial.WOOD, 4 + EnumToolMaterial.IRON.getDamageVsEntity()))
				.setDestructable(true)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/silverbanboo.png"))
				.setRepairMaterialOreDic("bamboo")
				.setMaxDamage(40)
				.setUnlocalizedName("flammpfeil.slashblade.silverbamboo")
				.setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(tab);
		GameRegistry.registerItem(bladeSilverBambooLight, "slashbladeSilverBambooLight");


		bladeWhiteSheath = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidWhite, EnumToolMaterial.IRON, 4 + EnumToolMaterial.IRON.getDamageVsEntity()))
				.setDestructable(false)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/white.png"))
				.setRepairMaterial(new ItemStack(Item.ingotIron))
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
				'#',"logWood",
				'X',new ItemStack(Item.swordWood,1,1)));


		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeBambooLight),
				"  #",
				" # ",
				"X  ",
				'#',"bamboo",
				'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));


		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeSilverBambooLight),
				" TI",
				"SXK",
				"PS ",
				'T', Item.egg,
				'I', Item.ingotIron,
				'S', Item.silk,
				'X', new ItemStack(bladeBambooLight,1,OreDictionary.WILDCARD_VALUE),
				'K', "dyeBlack",
				'P', Item.paper //S
				));
		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeSilverBambooLight),
				" TI",
				"SXK",
				"PS ",
				'T', Item.egg,
				'I', "dustSilver",
				'S', Item.silk, 'X', new ItemStack(bladeBambooLight,1,OreDictionary.WILDCARD_VALUE),
				'K', "dyeBlack",
				'P', Item.paper
				));
		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeSilverBambooLight),
				" TI",
				"SXK",
				"PS ",
				'T', Item.egg,
				'I', "ingotSilver",
				'S', Item.silk, 'X', new ItemStack(bladeBambooLight,1,OreDictionary.WILDCARD_VALUE),
				'K', "dyeBlack",
				'P', Item.paper
				));

		ItemStack hundredKillSilverBambooLight = new ItemStack(bladeSilverBambooLight,1,0);
		hundredKillSilverBambooLight.setItemDamage(hundredKillSilverBambooLight.getMaxDamage());
		hundredKillSilverBambooLight.setItemName("HundredKillSBL");
		hundredKillSilverBambooLight.getTagCompound().setInteger(ItemSlashBlade.killCountStr, 100);
		GameRegistry.registerCustomItemStack(HundredKillSilverBambooLightStr, hundredKillSilverBambooLight);


		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeWhiteSheath, 1, bladeWhiteSheath.getMaxDamage() / 3),
				"  #",
				" # ",
				"XG ",
				'#', Item.ingotIron,
				'G', Item.ingotGold,
				'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeWhiteSheath, 1, bladeWhiteSheath.getMaxDamage() / 4),
				"  #",
				" # ",
				"XG ",
				'#', "ingotSteel",
				'G', Item.ingotGold,
				'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(bladeWhiteSheath, 1),
				"  #",
				" # ",
				"XG ",
				'#', itemIngotBladeSoul,
				'G', Item.ingotGold,
				'X', new ItemStack(bladeWood,1,OreDictionary.WILDCARD_VALUE)));

		ItemStack brokenBladeWhite = new ItemStack(bladeWhiteSheath,1,0);
		brokenBladeWhite.setItemDamage(brokenBladeWhite.getMaxDamage());
		brokenBladeWhite.setItemName("BrokenBladeWhite");
		brokenBladeWhite.getTagCompound().setBoolean(ItemSlashBlade.isBrokenStr, true);
		GameRegistry.registerCustomItemStack(BrokenBladeWhiteStr, brokenBladeWhite);

		GameRegistry.addRecipe(new RecipeUpgradeBlade(new ItemStack(weapon),
				" BI",
				"L#C",
				"SG ",
				'L', Block.blockLapis,
				'C', Block.coalBlock,
				'I', itemSphereBladeSoul,
				'B', Item.blazeRod,
				'G', Item.ingotGold,
				'S', Item.silk,
				'#', brokenBladeWhite
				));

        //==================================================================================================================================

        wrapBlade = (ItemSlashBladeWrapper)(new ItemSlashBladeWrapper(itemidWrap,EnumToolMaterial.IRON))
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
				'I', Item.ingotIron,
				'P', itemProudSoul));

		GameRegistry.addRecipe(new ShapedOreRecipe(itemIngotBladeSoul,
				" P ",
				"PIP",
				" P ",
				'I', "ingotSteel",
				'P', itemProudSoul));

        FurnaceRecipes.smelting().addSmelting(itemIngotBladeSoul.itemID, itemIngotBladeSoul.getItemDamage() , itemSphereBladeSoul, 2.0F);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.expBottle),
				"XXX",
				"XIX",
				"XXX",
				'I',Item.glassBottle,
				'X',itemProudSoul));

        //==================================================================================================================================


        bladeNamed = (ItemSlashBladeNamed)(new ItemSlashBladeNamed(itemidNamed,EnumToolMaterial.IRON, 4.0f))
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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr,4 + EnumToolMaterial.IRON.getDamageVsEntity());
            tag.setFloat(ItemSlashBlade.attackAmplifierStr,0.01f);
            tag.setString(ItemSlashBlade.TextureNameStr,"named/tagayasan");
            tag.setInteger(ItemSlashBlade.SpecialAttackTypeStr,1);
            tag.setInteger(ItemSlashBlade.StandbyRenderTypeStr,1);

            tag.setString(ItemSlashBladeNamed.RepairMaterialNameStr,"iron_ingot");
            tag.setString(ItemSlashBladeNamed.RepairOreDicMaterialStr,"ingotIron");

            String name = "flammpfeil.slashblade.named.tagayasan";
            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.BladeNames.add(name);


            {
                ItemStack reqiredBlade = new ItemStack(bladeWood);
                NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                reqTag.setInteger(ItemSlashBlade.killCountStr,1000);

                reqiredBlade.setItemName("thousandkill woodblade");

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
                        'P',new ItemStack(Item.enderPearl),
                        'E',new ItemStack(Item.eyeOfEnder)));
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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.EMERALD.getDamageVsEntity());
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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.EMERALD.getDamageVsEntity());
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

                reqiredBlade.setItemName("thousandProudSouls");

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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.EMERALD.getDamageVsEntity());
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
                    'E', new ItemStack(Block.blockEmerald),
                    'D', new ItemStack(Block.blockDiamond),
                    'R', new ItemStack(Block.blockRedstone),
                    'L', new ItemStack(Block.blockLapis),
                    'I', new ItemStack(Block.blockIron),
                    'G', new ItemStack(Block.blockGold),
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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.IRON.getDamageVsEntity());
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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.STONE.getDamageVsEntity());
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

                reqiredBlade.setItemName("agito rust");

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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.EMERALD.getDamageVsEntity());
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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.IRON.getDamageVsEntity());
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

                reqiredBlade.setItemName("orotiagito seald");

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
            tag.setFloat(ItemSlashBladeNamed.BaseAttackModifiersStr, 4 + EnumToolMaterial.STONE.getDamageVsEntity());
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

                reqiredBlade.setItemName("orotiagito rust");

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
        GameRegistry.registerCraftingHandler(recipeRepair);

		GameRegistry.registerFuelHandler(this);

		InitProxy.proxy.initializeItemRenderer();

		manager = new ConfigEntityListManager();

        TickRegistry.registerTickHandler(manager, Side.SERVER);

        EntityRegistry.registerModEntity(EntityDrive.class, "Drive", 1, this, 250, 1, true);


        MinecraftForge.EVENT_BUS.register(new DropEventHandler());

        DropEventHandler.registerEntityDrop("EnderDragon", 1.0f, GameRegistry.findItemStack(modid, "flammpfeil.slashblade.named.yamato.broken", 1));
        DropEventHandler.registerEntityDrop("TwilightForest.Hydra", 0.3f, GameRegistry.findItemStack(modid, "flammpfeil.slashblade.named.orotiagito.rust", 1));
        DropEventHandler.registerEntityDrop("TwilightForest.Naga",0.3f,GameRegistry.findItemStack(modid,"flammpfeil.slashblade.named.agito.rust",1));

	}


    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent evt)
    {

    	if(OreDictionary.getOres("ingotIron").size() == 0){
    		OreDictionary.registerOre("ingotIron", Item.ingotIron);
    	}

        ArrayList<ItemStack> items = OreDictionary.getOres("bamboo");
        if(0 == items.size()){
            ItemStack itemSphereBladeSoul =
                    GameRegistry.findItemStack(modid, SphereBladeSoulStr, 1);

            GameRegistry.addRecipe(new ShapedOreRecipe(wrapBlade,
                    "RBL",
                    "CIC",
                    "LBR",
                    'C', Block.coalBlock,
                    'R', Block.blockLapis,
                    'B', Block.obsidian,
                    'I', itemSphereBladeSoul,
                    'L', "logWood"));
        }
    }


	@Override
	public int getBurnTime(ItemStack fuel) {
		return (fuel.itemID == this.proudSoul.itemID && fuel.getItemDamage() == 0) ? 20000 : 0;
	}



}
