package mods.flammpfeil.slashblade.named;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.RecipeAwakeBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 14/07/09.
 */
public class RecipeAwakeBladeFox extends RecipeAwakeBlade {

    public RecipeAwakeBladeFox(ResourceLocation loc, ItemStack result, ItemStack requiredStateBlade, Object... recipe) {
        super(loc, result, requiredStateBlade, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        ItemStack blade =  super.getCraftingResult(var1);

        ItemStack katana = SlashBlade.findItemStack("BambooMod", "katana", 1);
        if(!katana.isEmpty())
            SlashBlade.wrapBlade.setWrapItem(blade,katana);

        return blade;
    }

    @Override
    public ItemStack getRecipeOutput() {
        ItemStack blade =  super.getRecipeOutput();

        ItemStack katana = SlashBlade.findItemStack("BambooMod","katana",1);
        if(!katana.isEmpty())
            SlashBlade.wrapBlade.setWrapItem(blade,katana);

        return blade;
    }
}