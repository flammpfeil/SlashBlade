package mods.flammpfeil.slashblade.specialattack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 14/05/27.
 */
public abstract class SpecialAttackBase {
    @Override
    public abstract String toString();

    public abstract void doSpacialAttack(ItemStack stack, EntityPlayer player);
}
