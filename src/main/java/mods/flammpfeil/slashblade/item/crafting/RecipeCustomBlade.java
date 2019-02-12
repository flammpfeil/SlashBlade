package mods.flammpfeil.slashblade.item.crafting;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by Furia on 2016/02/10.
 */
public class RecipeCustomBlade extends ShapedOreRecipe {
    public RecipeCustomBlade(ItemStack result, Object... recipe) {
        super(new ResourceLocation(SlashBlade.modid, "customblade"), result, recipe);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {

        if(!super.matches(inv, world)) return false;

        ItemStack stack = inv.getStackInSlot(0);
        if(stack.isEmpty()) return false;


        if(this.getIngredients().size() == 0) return false;

        Ingredient ingradient = this.getIngredients().get(0);

        if(ingradient.getMatchingStacks() == null || ingradient.getMatchingStacks().length == 0) return false;
        ItemStack input = ingradient.getMatchingStacks()[0];

        if(input.getCount() != stack.getCount()) return false;

        return true;
    }
}
