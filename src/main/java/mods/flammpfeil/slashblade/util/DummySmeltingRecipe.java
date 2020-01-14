package mods.flammpfeil.slashblade.util;

import net.minecraft.item.ItemStack;

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
    public RecipeType getRecipeType() {
        return RecipeType.Smelting;
    }

}
