package mods.flammpfeil.slashblade.specialeffect;

import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 15/06/20.
 */
public interface ISpecialEffect {
    void register();

    int getDefaultRequiredLevel();
    String getEffectKey();
}
