package mods.flammpfeil.slashblade.item.crafting;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.ItemSWaeponMaterial;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Map;

/**
 * Created by Furia on 14/08/24.
 */
public class RecipeBladeSoulUpgrade extends ShapedOreRecipe {
    public RecipeBladeSoulUpgrade(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean result = super.matches(inv, world);

        if(result){
            Map<Integer,Integer> all = Maps.newHashMap();

            int soulCount = 0;

            for(int idx = 0; idx < inv.getSizeInventory(); idx++){
                ItemStack stack = inv.getStackInSlot(idx);
                if(stack == null) continue;
                if(!(stack.getItem() instanceof ItemSWaeponMaterial)) continue;

                soulCount++;

                Map<Integer,Integer> emap = EnchantmentHelper.getEnchantments(stack);

                for(Map.Entry<Integer,Integer> entry : emap.entrySet()){
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
                for(Map.Entry<Integer,Integer> entry : all.entrySet()){
                    result = entry.getValue() == soulCount;
                }
            }
        }

        return result;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        ItemStack result = super.getCraftingResult(var1);

        Map<Integer,Integer> all = Maps.newHashMap();

        for(int idx = 0; idx < var1.getSizeInventory(); idx++){
            ItemStack stack = var1.getStackInSlot(idx);
            if(stack == null) continue;
            if(!(stack.getItem() instanceof ItemSWaeponMaterial)) continue;


            Map<Integer,Integer> emap = EnchantmentHelper.getEnchantments(stack);
            all.putAll(emap);
        }

        EnchantmentHelper.setEnchantments(all,result);

        return result;
    }
}
