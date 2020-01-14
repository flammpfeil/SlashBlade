package mods.flammpfeil.slashblade.gui.config;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Furia on 2017/01/22.
 */
public class ConfigGuiFactory implements IModGuiFactory {

    public static class ConfigGui extends GuiConfig {
        public ConfigGui(GuiScreen parentScreen) {
            super(parentScreen,getConfigElements(),
                    SlashBlade.modid, false, false, "SlashBlade config", SlashBlade.mainConfigurationFile.getAbsolutePath());
        }

        private static List<IConfigElement> getConfigElements(){
            List<IConfigElement> list = new ArrayList<IConfigElement>();

            for(String categoryName : SlashBlade.mainConfiguration.getCategoryNames()){
                list.add(
                        new ConfigElement(SlashBlade.mainConfiguration.getCategory(categoryName))
                );
            }

            return list;
        }

    }

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ConfigGui(parentScreen);
    }


    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

}
