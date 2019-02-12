package mods.flammpfeil.slashblade.item.crafting;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Map;

/**
 * Created by Furia on 14/08/24.
 */
public class RecipeBladeSoulUpgrade extends ShapedOreRecipe {
    public RecipeBladeSoulUpgrade(ResourceLocation loc, ItemStack result, Object... recipe) {
        super(loc,result, recipe);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean result = super.matches(inv, world);

        if(result){
            Map<Enchantment,Integer> all = Maps.newHashMap();

            int soulCount = 0;

            for(int idx = 0; idx < inv.getSizeInventory(); idx++){
                ItemStack stack = inv.getStackInSlot(idx);
                if(stack.isEmpty()) continue;
                if(!(stack.getItem() instanceof ItemProudSoul)) continue;

                soulCount++;

                Map<Enchantment,Integer> emap = EnchantmentHelper.getEnchantments(stack);

                for(Map.Entry<Enchantment,Integer> entry : emap.entrySet()){
                    if(all.containsKey(entry.getKey())){

                        int value = all.get(entry.getKey()).intValue() + entry.getValue();

                        all.put(entry.getKey(),value);
                    }else{
                        all.put(entry.getKey(),entry.getValue());
                    }
                }
            }

            result = all.size() == 1 || all.size() == 0;
            if(result){
                for(Map.Entry<Enchantment,Integer> entry : all.entrySet()){
                    result = entry.getValue() == soulCount;
                }
            }
        }

        return result;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        ItemStack result = super.getCraftingResult(var1);

        Map<Enchantment,Integer> all = Maps.newHashMap();

        for(int idx = 0; idx < var1.getSizeInventory(); idx++){
            ItemStack stack = var1.getStackInSlot(idx);
            if(stack.isEmpty()) continue;
            if(!(stack.getItem() instanceof ItemProudSoul)) continue;


            Map<Enchantment,Integer> emap = EnchantmentHelper.getEnchantments(stack);
            all.putAll(emap);
        }

        EnchantmentHelper.setEnchantments(all,result);

        return result;
    }
}
