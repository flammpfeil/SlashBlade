package mods.flammpfeil.slashblade.util;

import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 15/02/12.
 */
public class DummyAnvilRecipe extends DummyRecipeBase {

    public ItemStack left;
    public ItemStack right;
    public ItemStack output;

    public DummyAnvilRecipe(ItemStack output, ItemStack left, ItemStack right) {
        super(output);
        this.left = left;
        this.right = right;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.Anvil;
    }
}
