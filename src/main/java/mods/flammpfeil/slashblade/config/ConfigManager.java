package mods.flammpfeil.slashblade.config;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 2017/01/22.
 */
public class ConfigManager {
    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent event){
        if(!event.getModID().equals(SlashBlade.modid))
            return;

        SlashBlade.mainConfiguration.save();
    }
}
