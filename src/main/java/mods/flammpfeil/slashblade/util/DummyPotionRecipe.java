package mods.flammpfeil.slashblade.util;

import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 15/02/12.
 */
public class DummyPotionRecipe extends DummyRecipeBase {

    public ItemStack top;
    public ItemStack bottom;
    public ItemStack output;

    public DummyPotionRecipe(ItemStack output, ItemStack top, ItemStack bottom) {
        super(output);
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.Potion;
    }
}
