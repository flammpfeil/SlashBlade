package mods.flammpfeil.slashblade;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name="SlashBlade",modid="flammpfeil.slashblade",useMetadata=false,version="@VERSION@")
public class SlashBlade implements IFuelHandler ,ITickHandler{


	public static Item weapon;
	public static Item proudSoul;

	public static int itemid = 22802;
	public static int itemid2 = 22803;

	public static float offsetX,offsetY,offsetZ;

	public static Map<String,Boolean> attackDisabled = new HashMap<String,Boolean>();

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
				.setUnlocalizedName("flammpfeil.slashblade:proudsoul")
				.setTextureName("flammpfeil.slashblade:proudsoul")
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


    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent evt)
    {
        TickRegistry.registerTickHandler(this, Side.CLIENT);
    }





    static boolean loaded = false;
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }


    static public Map<String,Boolean> attackableTargets = new HashMap<String,Boolean>();

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {

		try{
			mainConfiguration.load();

			ArrayList<String> targets = new ArrayList<String>();


			for(Object key : EntityList.classToStringMapping.keySet()){
				String name = (String)EntityList.classToStringMapping.get(key);
				if(name == null || name.length() == 0)
					continue;
				Entity instance = null;

				try{
					instance = EntityList.createEntityByName(name, null);
				}catch(Exception e){
				}

				if(instance == null || !(instance instanceof EntityLivingBase))
					continue;


				boolean attackable = true;

				if(instance instanceof IMob)
					attackable = true;
				else
				if(instance instanceof IAnimals
					||instance instanceof EntityOwnable
					||instance instanceof IMerchant)
					attackable = false;


				attackableTargets.put(name, attackable);
			}

			Property propAttackableTargets = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "AttackableTargets" ,new String[]{});

			for(String curEntry : propAttackableTargets.getStringList()){
				int spliterIdx = curEntry.lastIndexOf(":");
				String name = curEntry.substring(0, spliterIdx);
				name = name.replace("&","%amp;");
				String attackableStr = curEntry.substring(spliterIdx + 1, curEntry.length());

				boolean attackable = attackableStr.toLowerCase().equals("true");

				attackableTargets.put(name, attackable);
			}

			for(Object key : attackableTargets.keySet()){
				Boolean name = (Boolean)attackableTargets.get(key);

				String keyStr = (String)key;
				keyStr = keyStr.replace("&","%amp;");
				targets.add(String.format("%s:%b", keyStr ,name));
			}

			String[] data = targets.toArray(new String[]{});

			propAttackableTargets.set(data);
		}
		finally
		{
			mainConfiguration.save();
		}

		loaded = true;
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        if (!loaded)
        {
            return EnumSet.of(TickType.CLIENT);
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
