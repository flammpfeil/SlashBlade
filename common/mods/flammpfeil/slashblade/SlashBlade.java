package mods.flammpfeil.slashblade;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.spi.RegisterableService;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name=SlashBlade.modname,modid=SlashBlade.modid,version="@VERSION@")
@NetworkMod(clientSideRequired=true)
public class SlashBlade implements IFuelHandler ,ITickHandler{

	public static final String modname = "SlashBlade";
	public static final String modid = "flammpfeil.slashblade";

	public static final String BrokenBladeWhiteStr = "BrokenBladeWhite";

	public static ItemSlashBlade weapon;
	public static ItemSlashBladeDetune bladeWood;
	public static ItemSlashBladeDetune bladeBambooLight;
	public static ItemSlashBladeDetune bladeSilverBambooLight;
	public static ItemSlashBladeDetune bladeWhiteSheath;

    public static ItemSlashBladeWrapper wrapBlade;

	public static Item proudSoul;

	public static int itemid = 22802;
	public static int itemid2 = 22803;

	public static int itemidWood = 22804;
	public static int itemidBamboo = 22805;
	public static int itemidSilverBamboo = 22806;
	public static int itemidWhite = 22807;

	public static int itemidWrap = 22808;

	public static Map<String,Boolean> attackDisabled = new HashMap<String,Boolean>();

	public static Configuration mainConfiguration;

	public static final String ProudSoulStr = "proudsoul";
	public static final String IngotBladeSoulStr = "ingot_bladesoul";
	public static final String SphereBladeSoulStr = "sphere_bladesoul";
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

		weapon = (ItemSlashBlade)(new ItemSlashBlade(itemid, EnumToolMaterial.IRON, 4 + EnumToolMaterial.EMERALD.getDamageVsEntity()))
				.setRepairMaterial(new ItemStack(Item.ingotIron))
				.setRepairMaterialOreDic("ingotSteel","nuggetSteel")
				.setUnlocalizedName("flammpfeil.slashblade")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(weapon, "slashblade");

		bladeWood = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidWood, EnumToolMaterial.WOOD, 4 + EnumToolMaterial.WOOD.getDamageVsEntity()))
				.setDestructable(true)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/wood.png"))
				.setRepairMaterialOreDic("logWood")
				.setMaxDamage(60)
				.setUnlocalizedName("flammpfeil.slashblade.wood")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeWood, "slashbladeWood");

		bladeBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidBamboo, EnumToolMaterial.WOOD, 4 + EnumToolMaterial.STONE.getDamageVsEntity()))
				.setDestructable(true)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/banboo.png"))
				.setRepairMaterialOreDic("bamboo")
				.setMaxDamage(50)
				.setUnlocalizedName("flammpfeil.slashblade.bamboo")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeBambooLight, "slashbladeBambooLight");

		bladeSilverBambooLight = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidSilverBamboo, EnumToolMaterial.WOOD, 4 + EnumToolMaterial.IRON.getDamageVsEntity()))
				.setDestructable(true)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/silverbanboo.png"))
				.setRepairMaterialOreDic("bamboo")
				.setMaxDamage(40)
				.setUnlocalizedName("flammpfeil.slashblade.silverbamboo")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeSilverBambooLight, "slashbladeSilverBambooLight");


		bladeWhiteSheath = (ItemSlashBladeDetune)(new ItemSlashBladeDetune(itemidWhite, EnumToolMaterial.IRON, 4 + EnumToolMaterial.IRON.getDamageVsEntity()))
				.setDestructable(false)
				.setModelTexture(new ResourceLocation("flammpfeil.slashblade","model/white.png"))
				.setRepairMaterial(new ItemStack(Item.ingotIron))
				.setRepairMaterialOreDic("ingotSteel","nuggetSteel")
				.setMaxDamage(70)
				.setUnlocalizedName("flammpfeil.slashblade.white")
				.setTextureName("flammpfeil.slashblade:proudsoul")
				.setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(bladeWhiteSheath, "slashbladeWhite");


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


        wrapBlade = (ItemSlashBladeWrapper)(new ItemSlashBladeWrapper(itemidWrap,EnumToolMaterial.IRON))
                .setRepairMaterial(new ItemStack(Item.swordIron))
                .setRepairMaterialOreDic("ingotSteel", "nuggetSteel")
                .setMaxDamage(40)
                .setUnlocalizedName("flammpfeil.slashblade.wrapper")
                .setTextureName("flammpfeil.slashblade:proudsoul")
                .setCreativeTab(CreativeTabs.tabCombat);
        GameRegistry.registerItem(wrapBlade, "slashbladeWrapper");


        GameRegistry.addRecipe(new RecipeWrapBlade());



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

        GameRegistry.addRecipe(new RecipeAdjustPos());

        RecipeInstantRepair recipeRepair = new RecipeInstantRepair();
        GameRegistry.addRecipe(recipeRepair);
        GameRegistry.registerCraftingHandler(recipeRepair);


		GameRegistry.registerFuelHandler(this);

		InitProxy.proxy.initializeItemRenderer();
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		return (fuel.itemID == this.proudSoul.itemID && fuel.getItemDamage() == 0) ? 20000 : 0;
	}


    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent evt)
    {
        TickRegistry.registerTickHandler(this, Side.SERVER);

        ArrayList<ItemStack> items = OreDictionary.getOres("bamboo");
        if(0 == items.size()){
        	ItemStack itemSphereBladeSoul = GameRegistry.findItemStack(this.modid, SphereBladeSoulStr, 1);

            GameRegistry.addRecipe(new ShapedOreRecipe(wrapBlade,
                    "RBL",
                    "CIC",
                    "LBR",
                    'C', Block.coalBlock,
                    'R', Block.blockLapis,
                    'B', Block.obsidian,
                    'I', itemSphereBladeSoul,
                    'L', "logWood"));
        }else{
            for(int idx = Block.blocksList.length; idx < Item.itemsList.length; idx++){
            	Item curItem = Item.itemsList[idx];
            	if(curItem != null){
            		if(curItem.getClass().getName() == "ruby.bamboo.item.ItemKatana"){
            			GameRegistry.registerItem(curItem, "katana", "BambooMod");
            			break;
            		}
            	}
            }
        }
        /*
        GameRegistry.registerItem(Item.swordWood, "wood_sword","Minecraft");
        /**/
    }





    static boolean loaded = false;
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }


    static public Map<String,Boolean> attackableTargets = new HashMap<String,Boolean>();
    static public Map<String,Boolean> destructableTargets = new HashMap<String,Boolean>();


    /**
     * [\] -> [\\]
     * ["] -> [\quot;]
     * 改行 -> [\r;\r;]
     * 全文を""でquotationする
     * 上記のとおり、エスケープされます。直接configを修正するときに覚えておくべき。
     * @param source
     * @return
     */
    static private String escape(String source){
		return String.format("\"%s\"", source.replace("\\","\\\\").replace("\"","\\quot;").replace("\r", "\\r;").replace("\n", "\\n;"));
    }
    static private String unescape(String source){
    	return source.replace("\"", "").replace("\\quot;", "\"").replace("\\r;","\r").replace("\\n;","\n").replace("\\\\", "\\");
    }


    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    	if (type.contains(TickType.WORLD)){

			try{
				mainConfiguration.load();



				for(Object key : EntityList.classToStringMapping.keySet()){
					Class cls = (Class)key;

					String name = (String)EntityList.classToStringMapping.get(key);
					if(name == null || name.length() == 0)
						continue;

					Entity instance = null;

					try{
						instance = EntityList.createEntityByName(name, (World)tickData[0]);
					}catch(Throwable e){
						instance = null;
					}


					if(EntityLivingBase.class.isAssignableFrom(cls))
					{
						boolean attackable = true;

						if(instance == null){
							attackable = true;

						}else if(IMob.class.isAssignableFrom(cls)){//instance instanceof IMob){
							attackable = true;

						}else if(instance instanceof IAnimals
								||instance instanceof EntityOwnable
								||instance instanceof IMerchant){
							attackable = false;

						}
						attackableTargets.put(name, attackable);
					}else{

						boolean destructable = false;

						if(instance instanceof IProjectile
								|| instance instanceof EntityTNTPrimed
								|| instance instanceof EntityFireball
								|| instance instanceof IThrowableEntity){
							//allways destruction
						}else{
							destructableTargets.put(cls.getSimpleName(), destructable);

						}

					}


				}

				{
					Property propAttackableTargets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "AttackableTargets" ,new String[]{});

					for(String curEntry : propAttackableTargets.getStringList()){
						curEntry = unescape(curEntry);
						int spliterIdx = curEntry.lastIndexOf(":");
						String name = curEntry.substring(0, spliterIdx);
						String attackableStr = curEntry.substring(spliterIdx + 1, curEntry.length());

						boolean attackable = attackableStr.toLowerCase().equals("true");

						attackableTargets.put(name, attackable);
					}

					ArrayList<String> profAttackableTargets = new ArrayList<String>();
					for(Object key : attackableTargets.keySet()){
						Boolean name = (Boolean)attackableTargets.get(key);

						String keyStr = (String)key;
						profAttackableTargets.add(escape(String.format("%s:%b", keyStr ,name)));
					}
					String[] data = profAttackableTargets.toArray(new String[]{});

					propAttackableTargets.set(data);
				}


				{
					Property propDestructableTargets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "DestructableTargets" ,new String[]{});

					for(String curEntry : propDestructableTargets.getStringList()){
						curEntry = unescape(curEntry);
						int spliterIdx = curEntry.lastIndexOf(":");
						String name = curEntry.substring(0, spliterIdx);
						String attackableStr = curEntry.substring(spliterIdx + 1, curEntry.length());

						boolean destructable = attackableStr.toLowerCase().equals("true");

						destructableTargets.put(name, destructable);
					}

					ArrayList<String> profDestructableTargets = new ArrayList<String>();
					for(Object key : destructableTargets.keySet()){
						Boolean name = (Boolean)destructableTargets.get(key);

						String keyStr = (String)key;
						profDestructableTargets.add(escape(String.format("%s:%b", keyStr ,name)));
					}
					String[] data2 = profDestructableTargets.toArray(new String[]{});

					propDestructableTargets.set(data2);
				}




			}
			finally
			{
				mainConfiguration.save();
			}

			loaded = true;

    	}
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        if (!loaded)
        {
            return EnumSet.of(TickType.WORLD);
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getLabel()
    {
        return null;
    }
}
