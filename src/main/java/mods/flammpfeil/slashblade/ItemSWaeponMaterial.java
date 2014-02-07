package mods.flammpfeil.slashblade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSWaeponMaterial extends Item {

	public ItemSWaeponMaterial() {
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {

		if(	par1ItemStack.getItem() == SlashBlade.proudSoul){
			return true;
		}
		return super.hasEffect(par1ItemStack, pass);
	}

}
