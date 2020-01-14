package mods.flammpfeil.slashblade;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * Created by Furia on 14/05/16.
 */
public class SlashBladeTab extends CreativeTabs {

    public SlashBladeTab(String label){
        super(label);
    }
    @Override
    public ItemStack getTabIconItem() {
        return  SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yamato");
    }
}
