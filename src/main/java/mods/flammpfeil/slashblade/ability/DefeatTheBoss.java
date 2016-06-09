package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.NamedBladeManager;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;

import java.util.Random;

/**
 * Created by Furia on 2015/11/19.
 */
public class DefeatTheBoss {
    public static void entityKilled(ItemStack stack, EntityLivingBase target,EntityLivingBase player) {
        if (!(target instanceof EntityLiving)) return;
        if (!(target instanceof IMob)) return;
        if (!isAveilable(stack)) return;

        EntityLiving living = (EntityLiving) target;

        if(!(living.hasCustomNameTag() || target instanceof IBossDisplayData)) return;

        ItemStack tinySoul = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);

        tinySoul.addEnchantment(EnchantHelper.getEnchantmentRare(player.getRNG()),1);

        player.entityDropItem(tinySoul, 0.0F);

        if(target instanceof IBossDisplayData){
            ItemStack soul = NamedBladeManager.getNamedSoul(player.getRNG());
            player.entityDropItem(soul, 0.0F);
        }
    }

    static boolean isAveilable(ItemStack stack){
        if(stack == null) return false;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return false;
        return true;
    }
}
