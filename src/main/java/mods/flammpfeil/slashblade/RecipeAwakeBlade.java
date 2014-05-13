package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;

public class RecipeAwakeBlade extends ShapedOreRecipe {

	public RecipeAwakeBlade(ItemStack result, Object... recipe) {
		super(result, recipe);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		ItemStack result = super.getCraftingResult(var1);

		for(int idx = 0; idx < var1.getSizeInventory(); idx++){
			ItemStack curIs = var1.getStackInSlot(idx);
			if(curIs != null
					&& curIs.getItem() instanceof ItemSlashBlade
					&& curIs.hasTagCompound()){

                NBTTagCompound oldTag = curIs.getTagCompound();
				oldTag = (NBTTagCompound)oldTag.copy();

                NBTTagCompound newTag;
                ItemStack trueBlade = ItemSlashBladeNamed.getCustomBlade(oldTag.getString(ItemSlashBladeNamed.TrueItemNameStr));
                newTag = ItemSlashBlade.getItemTagCompound(trueBlade);

                newTag.setInteger(ItemSlashBlade.killCountStr,oldTag.getInteger(ItemSlashBlade.killCountStr));
                newTag.setInteger(ItemSlashBlade.proudSoulStr,oldTag.getInteger(ItemSlashBlade.proudSoulStr));

                result = trueBlade;
			}
		}

		return result;
	}

}
