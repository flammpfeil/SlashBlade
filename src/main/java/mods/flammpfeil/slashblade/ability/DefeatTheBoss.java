package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.NamedBladeManager;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 2015/11/19.
 */
public class DefeatTheBoss {
    public static void entityKilled(ItemStack stack, EntityLivingBase target,EntityLivingBase player) {
        if (!(target instanceof EntityLiving)) return;
        if (!(target instanceof IMob)) return;
        if (!isAveilable(stack)) return;

        EntityLiving living = (EntityLiving) target;

        if(!(living.hasCustomName() || !target.isNonBoss())) return;

        ItemStack tinySoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);

        tinySoul.addEnchantment(EnchantHelper.getEnchantmentRare(player.getRNG()),1);

        player.entityDropItem(tinySoul, 0.0F);

        if(!target.isNonBoss()){
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
