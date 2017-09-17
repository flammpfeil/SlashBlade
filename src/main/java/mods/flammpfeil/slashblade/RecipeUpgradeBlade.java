package mods.flammpfeil.slashblade;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeUpgradeBlade extends ShapedOreRecipe {

	public RecipeUpgradeBlade(ResourceLocation loc, ItemStack result, Object... recipe) {
		super(loc, result, recipe);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		ItemStack result = super.getCraftingResult(var1);

		for(int idx = 0; idx < var1.getSizeInventory(); idx++){
			ItemStack curIs = var1.getStackInSlot(idx);
			if(!curIs.isEmpty()
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
