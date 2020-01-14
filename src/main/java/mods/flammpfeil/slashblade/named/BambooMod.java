package mods.flammpfeil.slashblade.named;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.RecipeAwakeBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 14/11/11.
 */
public class BambooMod {
	public static final String namekatana = "wrap.BambooMod.katana";

	@SubscribeEvent
	public void InitKatana(LoadEvent.InitEvent event){
	     ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
	        NBTTagCompound tag = new NBTTagCompound();
	        customblade.setTagCompound(tag);
	        ItemSlashBladeNamed.CurrentItemName.set(tag, namekatana);
	        ItemSlashBladeNamed.CustomMaxDamage.set(tag, Integer.valueOf(45));
	        ItemSlashBlade.setBaseAttackModifier(tag, 4F);
            ItemSlashBlade.TextureName.set(tag,"BambooKatana");
	        SlashBlade.registerCustomItemStack(namekatana, customblade);
	        ItemSlashBladeNamed.NamedBlades.add(namekatana);
	}

	@SubscribeEvent
	public void InitRecipes(LoadEvent.PostInitEvent event){
	    ItemStack soul = SlashBlade.findItemStack("flammpfeil.slashblade", SlashBlade.ProudSoulStr, 1);

	    SlashBlade.addRecipe(namekatana, new RecipeAwakeBlade(new ResourceLocation("flammpfeil.slashblade",namekatana), SlashBlade.getCustomBlade(namekatana), SlashBlade.getCustomBlade("slashbladeWrapper"),new Object[]{
				"  S", " W ", "B  ",
				Character.valueOf('S'), soul,
				Character.valueOf('B'), SlashBlade.findItemStack("sakura","sakura_katana",1),
				Character.valueOf('W'), SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1)
		}));
	}

}
