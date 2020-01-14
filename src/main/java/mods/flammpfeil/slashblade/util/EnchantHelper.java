package mods.flammpfeil.slashblade.util;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/08/24.
 */
public class EnchantHelper {

    public static List<Enchantment> normal = Lists.newArrayList();

    public static List<Enchantment> rare = Lists.newArrayList(
            Enchantments.POWER
            ,Enchantments.PUNCH
            ,Enchantments.THORNS
            ,Enchantments.FIRE_PROTECTION
            ,Enchantments.FEATHER_FALLING
            ,Enchantments.FORTUNE
            ,Enchantments.RESPIRATION
            ,Enchantments.UNBREAKING);

    public static void initEnchantmentList(){
        //func_181077_c : get locationEnchantments.keySet()
        for(ResourceLocation key : Enchantment.REGISTRY.getKeys()){
            Enchantment ench = Enchantment.getEnchantmentByLocation(key.toString());
            if(ench == null) continue;

            if(ench.type == null) continue;

            if(ench.type.canEnchantItem(Items.IRON_SWORD)){
                normal.add(ench);
            }
        }

        normal.addAll(rare);
    }

    public static Enchantment getEnchantmentNormal(Random rand){
        int idx = rand.nextInt(normal.size());
        return normal.get(idx);
    }
    public static Enchantment getEnchantmentRare(Random rand){
        int idx = rand.nextInt(rare.size());
        return rare.get(idx);
    }
}
