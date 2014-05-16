package mods.flammpfeil.slashblade;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Furia on 14/05/16.
 */
public class SlashBladeTab extends CreativeTabs {

    public SlashBladeTab(String label){
        super(label);
    }
    @Override
    public Item getTabIconItem() {
        return SlashBlade.proudSoul;
    }
}
