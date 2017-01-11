package mods.flammpfeil.slashblade.specialeffect;

import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 2017/01/11.
 */
public interface IRemovable {
    boolean canCopy(ItemStack stack);
    boolean canRemoval(ItemStack stack);
}
