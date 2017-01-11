package mods.flammpfeil.slashblade.compat;

import mods.flammpfeil.slashblade.compat.enderio.AnvilRecipe;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by Furia on 2017/01/11.
 */
public class CompatManager {
    public void init(){
        if(Loader.isModLoaded("EnderIO")){
            (new AnvilRecipe()).register();
        }
    }
}
