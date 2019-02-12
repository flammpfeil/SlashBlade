package mods.flammpfeil.slashblade.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Furia on 2017/09/30.
 */
public class BladeIngredient extends Ingredient {

    public BladeIngredient(ItemStack stack){
        super(stack);

    }


    public IntList getValidItemStacksPacked()
    {
        IntList matchingStacksPacked = new IntArrayList(1);

        for (ItemStack itemstack : this.getMatchingStacks())
        {
            if(itemstack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
                for(int dm = 0; dm < itemstack.getMaxDamage(); dm++){
                    matchingStacksPacked.add(this.pack(itemstack, dm));
                }
            }else
                matchingStacksPacked.add(this.pack(itemstack));
        }

        matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);

        return matchingStacksPacked;
    }

    private int pack(ItemStack stack)
    {
        Item item = stack.getItem();
        int i = stack.getItemDamage();//item.getHasSubtypes() ? stack.getMetadata() : 0;
        return Item.REGISTRY.getIDForObject(item) << 16 | i & 65535;
    }
    private int pack(ItemStack stack,int damageg)
    {
        Item item = stack.getItem();
        int i = damageg;//item.getHasSubtypes() ? stack.getMetadata() : 0;
        return Item.REGISTRY.getIDForObject(item) << 16 | i & 65535;
    }
}
