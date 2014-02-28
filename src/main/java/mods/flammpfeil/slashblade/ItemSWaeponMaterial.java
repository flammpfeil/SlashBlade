package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

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

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		String s = super.getUnlocalizedName(par1ItemStack);
		switch(par1ItemStack.getItemDamage()){
		case 1:
			s += ".ingot";
			break;
		case 2:
			s += ".sphere";
			break;
		}
		return s;
	}

	@Override
	public IIcon getIconFromDamage(int par1) {
		switch(par1){
		case 1:
			return Items.iron_ingot.getIconFromDamage(0);
		case 2:
			return Items.snowball.getIconFromDamage(0);
		default:
			return super.getIconFromDamage(par1);
		}
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
	}
}
