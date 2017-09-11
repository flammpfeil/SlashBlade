package mods.flammpfeil.slashblade.util;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Furia on 2017/02/04.
 */
public abstract class DummyRecipeBase implements IRecipe {

    public static final ResourceLocation BackGroundResource = new ResourceLocationRaw("flammpfeil.slashblade","textures/gui/crafting_recipe.png");
    public static final ResourceLocation BackGroundResource2 = new ResourceLocationRaw("flammpfeil.slashblade","textures/gui/crafting_recipe2.png");

    public enum RecipeType{
        Crafting(BackGroundResource, 0),
        Smelting(BackGroundResource, 86),
        Anvil(BackGroundResource2, 0),
        Potion(BackGroundResource2, 86);

        public ResourceLocation backGround;
        public int yOffset;

        RecipeType(ResourceLocation backGround, int yOffset){
            this.backGround = backGround;
            this.yOffset = yOffset;
        }
    }

    public ItemStack output;

    public DummyRecipeBase(ItemStack output) {
        this.output = output;
    }

    public abstract RecipeType getRecipeType();


    @Override
    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        return null;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        return nonnulllist;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public IRecipe setRegistryName(ResourceLocation name) {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return null;
    }

    @Override
    public Class<IRecipe> getRegistryType() {
        return null;
    }
}
