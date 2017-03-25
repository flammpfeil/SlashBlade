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
public class DummySmeltingRecipe extends DummyRecipeBase {

    public ItemStack input;

    public DummySmeltingRecipe(ItemStack input, ItemStack output) {
        super(output);
        this.input = input;
    }

    @Override
<<<<<<< HEAD
    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        return ItemStack.EMPTY;
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
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        return nonnulllist;
=======
    public RecipeType getRecipeType() {
        return RecipeType.Smelting;
>>>>>>> ea3c052... add: Achievement Recipe GUI (potion / anvil)
    }
}
