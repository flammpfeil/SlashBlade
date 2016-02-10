package mods.flammpfeil.slashblade.item.crafting;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by Furia on 2016/02/10.
 */
public class RecipeCustomBlade extends ShapedOreRecipe {
    public RecipeCustomBlade(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {

        if(!super.matches(inv, world)) return false;

        ItemStack stack = inv.getStackInSlot(0);
        if(stack == null) return false;

        if(this.getInput().length == 0) return false;

        Object input = this.getInput()[0];
        if(!(input instanceof ItemStack)) return false;

        if(((ItemStack) input).stackSize != stack.stackSize) return false;

        return true;
    }
}
