package mods.flammpfeil.slashblade.named;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.init.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 14/07/07.
 */
public class Fox {
    static public final String nameWhite = "flammpfeil.slashblade.named.fox.white";
    static public final String nameBlack = "flammpfeil.slashblade.named.fox.black";

    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){

        {
            String name = nameWhite;

            ItemStack customblade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            SlashBlade.wrapBlade.removeWrapItem(customblade);

            customblade.addEnchantment(Enchantments.KNOCKBACK,2);
            customblade.addEnchantment(Enchantments.BANE_OF_ARTHROPODS,2);
            customblade.addEnchantment(Enchantments.UNBREAKING,3);
            customblade.addEnchantment(Enchantments.LOOTING,3);
            customblade.addEnchantment(Enchantments.FIRE_ASPECT,2);

            NBTTagCompound tag = customblade.getTagCompound();

            ItemStack innerBlade = SlashBlade.findItemStack("minecraft", "wooden_sword", 1);

            SlashBlade.wrapBlade.setWrapItem(customblade,innerBlade);

            ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);

            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.TrueItemName.set(tag, name);

            ItemSlashBlade.TextureName.set(tag, "named/sange/white");
            ItemSlashBlade.ModelName.set(tag, "named/sange/sange");

            ItemSlashBlade.SpecialAttackType.set(tag, 0); //0:次元斬
            ItemSlashBlade.StandbyRenderType.set(tag, 1);

            ItemSlashBladeNamed.IsDefaultBewitched.set(tag, true);

            NamedBladeManager.registerBladeSoul(tag , customblade.getDisplayName());
            SlashBlade.registerCustomItemStack(name, customblade);
        }

        {
            String name = nameBlack;

            ItemStack customblade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            SlashBlade.wrapBlade.removeWrapItem(customblade);

            customblade.addEnchantment(Enchantments.SMITE,4);
            customblade.addEnchantment(Enchantments.KNOCKBACK,2);
            customblade.addEnchantment(Enchantments.FIRE_ASPECT, 2);

            NBTTagCompound tag = customblade.getTagCompound();

            ItemStack innerBlade = SlashBlade.findItemStack("minecraft", "wooden_sword", 1);

            SlashBlade.wrapBlade.setWrapItem(customblade, innerBlade);

            ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);

            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.TrueItemName.set(tag, name);

            ItemSlashBlade.TextureName.set(tag, "named/sange/black");
            ItemSlashBlade.ModelName.set(tag, "named/sange/sange");

            ItemSlashBlade.SpecialAttackType.set(tag, 4); //4:シュンカ一段
            ItemSlashBlade.StandbyRenderType.set(tag, 1);

            ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

            NamedBladeManager.registerBladeSoul(tag , customblade.getDisplayName());
            SlashBlade.registerCustomItemStack(name, customblade);
        }
    }

	@SubscribeEvent
	public void InitFoxRecipes(LoadEvent.PostInitEvent event){
	    String nameWhite = "flammpfeil.slashblade.named.fox.white";
	    String nameBlack = "flammpfeil.slashblade.named.fox.black";
	    final String namekatana = "wrap.BambooMod.katana";
	    ItemStack foxbladeReqired =SlashBlade.getCustomBlade(namekatana);
	    foxbladeReqired.addEnchantment(Enchantments.LOOTING,1);
	    NBTTagCompound reqTag1 = ItemSlashBlade.getItemTagCompound(foxbladeReqired);
        ItemSlashBlade.KillCount.set(reqTag1,199);
        ItemSlashBlade.ProudSoul.set(reqTag1,1000);
        ItemSlashBlade.RepairCount.set(reqTag1,1);
	    ItemStack fox = SlashBlade.findItemStack(SlashBlade.modid,nameWhite, 1);

	    ItemStack wheat = (Loader.isModLoaded("tofucraft"))
	    		?SlashBlade.findItemStack("tofucraft","foodset", 1):new ItemStack(Items.WHEAT,1);
	    		if((Loader.isModLoaded("tofucraft")))wheat.setItemDamage(10);
	    SlashBlade.addRecipe(nameWhite, new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,nameWhite),fox, foxbladeReqired,
	    		new Object[]{"DAD", "DBD", "DHD",
	    				Character.valueOf('H'), wheat,
	    				Character.valueOf('A'), SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.ProudSoulStr,1),
	    				Character.valueOf('B'), foxbladeReqired,
	    				Character.valueOf('D'), SlashBlade.findItemStack("sakura","kitunebi",1),
	    		}));

	    ItemStack foxblade2Reqired =SlashBlade.getCustomBlade(namekatana);
	    foxblade2Reqired.addEnchantment(Enchantments.SMITE,1);
	    NBTTagCompound reqTag2 = ItemSlashBlade.getItemTagCompound(foxblade2Reqired);
        ItemSlashBlade.KillCount.set(reqTag2,199);
        ItemSlashBlade.ProudSoul.set(reqTag2,1000);
        ItemSlashBlade.RepairCount.set(reqTag2,1);
	    ItemStack fox2 = SlashBlade.findItemStack(SlashBlade.modid,"flammpfeil.slashblade.named.fox.black", 1);
	    SlashBlade.addRecipe(nameBlack, new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,nameBlack),fox2, foxblade2Reqired,
	    		new Object[]{"DAD", "DBD", "DHD",
	    				Character.valueOf('H'), wheat,
	    				Character.valueOf('A'), SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.ProudSoulStr,1),
	    				Character.valueOf('B'), foxbladeReqired,
	    				Character.valueOf('D'), SlashBlade.findItemStack("sakura","kitunebi",1),
	    		}));
	}
}
