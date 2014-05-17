package mods.flammpfeil.slashblade;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeUpgradeBlade extends ShapedOreRecipe {

	public RecipeUpgradeBlade(ItemStack result, Object... recipe) {
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

				NBTTagCompound tag = curIs.getTagCompound();
				tag = (NBTTagCompound)tag.copy();

                tag.removeTag(ItemSlashBlade.isBrokenStr);
                tag.removeTag(ItemSlashBlade.isNoScabbardStr);

				result.setTagCompound(tag);
			}
		}

		return result;
	}

}
