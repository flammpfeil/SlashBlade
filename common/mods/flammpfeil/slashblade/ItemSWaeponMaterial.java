package mods.flammpfeil.slashblade;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

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
	public Icon getIconFromDamage(int par1) {
		switch(par1){
		case 1:
			return Item.ingotIron.getIconFromDamage(0);
		case 2:
			return Item.snowball.getIconFromDamage(0);
		default:
			return super.getIconFromDamage(par1);
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
	}
}
