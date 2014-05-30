package mods.flammpfeil.slashblade;

import net.minecraft.inventory.InventoryCrafting;
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

                ItemSlashBlade.IsBroken.remove(tag);
                ItemSlashBlade.IsNoScabbard.remove(tag);

				result.setTagCompound(tag);
			}
		}

		return result;
	}

}
