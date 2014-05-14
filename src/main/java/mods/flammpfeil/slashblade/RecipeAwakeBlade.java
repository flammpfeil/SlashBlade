package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

                if(oldTag.hasKey(ItemSlashBladeNamed.TrueItemNameStr)){
                    result = ItemSlashBladeNamed.getCustomBlade(oldTag.getString(ItemSlashBladeNamed.TrueItemNameStr));
                }

                NBTTagCompound newTag;
                newTag = ItemSlashBlade.getItemTagCompound(result);

                newTag.setInteger(ItemSlashBlade.killCountStr,oldTag.getInteger(ItemSlashBlade.killCountStr));
                newTag.setInteger(ItemSlashBlade.proudSoulStr,oldTag.getInteger(ItemSlashBlade.proudSoulStr));

                if(oldTag.hasKey(ItemSlashBlade.adjustXStr))
                    newTag.setFloat(ItemSlashBlade.adjustXStr,oldTag.getFloat(ItemSlashBlade.adjustXStr));

                if(oldTag.hasKey(ItemSlashBlade.adjustYStr))
                    newTag.setFloat(ItemSlashBlade.adjustYStr,oldTag.getFloat(ItemSlashBlade.adjustYStr));

                if(oldTag.hasKey(ItemSlashBlade.adjustZStr))
                    newTag.setFloat(ItemSlashBlade.adjustZStr,oldTag.getFloat(ItemSlashBlade.adjustZStr));

                {
                    Map<Integer,Integer> newItemEnchants = EnchantmentHelper.getEnchantments(result);
                    Map<Integer,Integer> oldItemEnchants = EnchantmentHelper.getEnchantments(curIs);
                    for(int enchantIndex : oldItemEnchants.keySet())
                    {
                        Enchantment enchantment = Enchantment.enchantmentsList[enchantIndex];

                        int destLevel = newItemEnchants.containsKey(enchantIndex) ? newItemEnchants.get(enchantIndex) : 0;
                        int srcLevel = oldItemEnchants.get(enchantIndex);

                        srcLevel = Math.max(srcLevel, destLevel);
                        srcLevel = Math.min(srcLevel, enchantment.getMaxLevel());


                        boolean canApplyFlag = enchantment.canApply(result);
                        if(canApplyFlag){
                            for(int curEnchantIndex : newItemEnchants.keySet()){
                                if (curEnchantIndex != enchantIndex && !enchantment.canApplyTogether(Enchantment.enchantmentsList[curEnchantIndex]))
                                {
                                    canApplyFlag = false;
                                    break;
                                }
                            }
                            if (canApplyFlag)
                                newItemEnchants.put(Integer.valueOf(enchantIndex), Integer.valueOf(srcLevel));
                        }
                    }
                    EnchantmentHelper.setEnchantments(newItemEnchants, result);
                }
			}
		}

		return result;
	}

}
