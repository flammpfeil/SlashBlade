package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RecipeAwakeBlade extends ShapedOreRecipe {

    boolean isYoutou = false;
    ItemStack requiredStateBlade = null;

    public RecipeAwakeBlade(ItemStack result, boolean isYoutou, ItemStack requiredStateBlade, Object... recipe) {
        super(result, recipe);
        this.isYoutou = isYoutou;
        this.requiredStateBlade = requiredStateBlade;
    }

    int tagValueCompareInteger(String key,NBTTagCompound reqTag,NBTTagCompound srcTag){
        return Integer.compare(reqTag.getInteger(key) , srcTag.getInteger(key));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {

        boolean result = super.matches(inv, world);

        if(result && requiredStateBlade != null){
            for(int idx = 0; idx < inv.getSizeInventory(); idx++){
                ItemStack curIs = inv.getStackInSlot(idx);
                if(curIs != null
                        && curIs.getItem() instanceof ItemSlashBlade
                        && curIs.hasTagCompound()){



                    Map<Integer,Integer> oldItemEnchants = EnchantmentHelper.getEnchantments(requiredStateBlade);
                    for(Map.Entry<Integer,Integer> enchant: oldItemEnchants.entrySet())
                    {
                        int level = EnchantmentHelper.getEnchantmentLevel(enchant.getKey(),curIs);
                        if(level < enchant.getValue()){
                            return false;
                        }
                    }

                    NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(requiredStateBlade);
                    NBTTagCompound srcTag = ItemSlashBlade.getItemTagCompound(curIs);

                    if(!curIs.getUnlocalizedName().equals(requiredStateBlade.getUnlocalizedName()))
                        return false;

                    if(0 < tagValueCompareInteger(ItemSlashBlade.proudSoulStr,reqTag,srcTag))
                        return false;
                    if(0 < tagValueCompareInteger(ItemSlashBlade.killCountStr,reqTag,srcTag))
                        return false;
                    if(0 < tagValueCompareInteger(ItemSlashBlade.RepairCounterStr,reqTag,srcTag))
                        return false;



                    break;
                }
            }
        }

        return result;
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

                {
                    NBTTagCompound newTag;
                    newTag = ItemSlashBlade.getItemTagCompound(result);

                    if(newTag.hasKey(ItemSlashBladeNamed.CurrentItemNameStr)){
                        ItemStack tmp;
                        String key = newTag.getString(ItemSlashBladeNamed.CurrentItemNameStr);
                        tmp = ItemSlashBladeNamed.getCustomBlade(key);
                        if(tmp == null)
                            tmp = ItemSlashBladeNamed.getCustomBlade(key + ".youtou");

                        if(tmp != null)
                            result = tmp;
                    }
                }

                if(isYoutou)
                    result.setStackDisplayName(result.getDisplayName());

                NBTTagCompound newTag;
                newTag = ItemSlashBlade.getItemTagCompound(result);

                newTag.setInteger(ItemSlashBlade.killCountStr,oldTag.getInteger(ItemSlashBlade.killCountStr));
                newTag.setInteger(ItemSlashBlade.proudSoulStr,oldTag.getInteger(ItemSlashBlade.proudSoulStr));
                newTag.setInteger(ItemSlashBlade.RepairCounterStr,oldTag.getInteger(ItemSlashBlade.RepairCounterStr));

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
