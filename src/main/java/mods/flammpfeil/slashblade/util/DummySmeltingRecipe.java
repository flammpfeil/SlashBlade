package mods.flammpfeil.slashblade.util;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Created by Furia on 15/02/12.
 */
public class DummySmeltingRecipe implements IRecipe {

    public ItemStack input;
    public ItemStack output;

    public DummySmeltingRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        return ItemStack.field_190927_a;
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>func_191197_a(inv.getSizeInventory(), ItemStack.field_190927_a);
        return nonnulllist;
    }
}
