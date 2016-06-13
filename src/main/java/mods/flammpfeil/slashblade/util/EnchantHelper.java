package mods.flammpfeil.slashblade.util;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;

import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/08/24.
 */
public class EnchantHelper {

    public static List<Enchantment> normal = Lists.newArrayList();

    public static List<Enchantment> rare = Lists.newArrayList(
            Enchantment.power
            ,Enchantment.punch
            ,Enchantment.thorns
            ,Enchantment.fireProtection
            ,Enchantment.featherFalling
            ,Enchantment.fortune
            ,Enchantment.respiration
            ,Enchantment.unbreaking);

    public static void initEnchantmentList(){
        for(Enchantment ench : Enchantment.enchantmentsList){
            if(ench == null) continue;

            if(ench.type == null) continue;

            if(ench.type.canEnchantItem(Items.iron_sword)){
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
