package mods.flammpfeil.sweapon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSWaeponMaterial extends Item {

	public ItemSWaeponMaterial(int par1) {
		super(par1);

	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		if(	par1ItemStack.itemID == SlashBlade.proudSoul.itemID){
			return true;
		}
		return super.hasEffect(par1ItemStack);
	}

}
