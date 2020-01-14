package mods.flammpfeil.slashblade.core;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.crafting.RecipeCustomBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 2016/02/10.
 */
public class ConfigCustomBladeManager {
    String[] lines = {};

    public void loadConfig(Configuration config){
        Property propCustomBlade = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "CustomBlade" ,new String[]{"dios"});
        lines = propCustomBlade.getStringList();
        propCustomBlade.setShowInGui(false);
    }

    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){
    	String line;int i;
        for(i=0;i<lines.length;i++){
        	line = lines[i];
            String key = "custom_"+line;

            ItemStack customBlade = new ItemStack(SlashBlade.bladeNamed,1,0);

            NBTTagCompound tag = new NBTTagCompound();
            customBlade.setTagCompound(tag);

            ItemSlashBladeNamed.CurrentItemName.set(tag, key);
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, 50);
            ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getAttackDamage());
            ItemSlashBlade.TextureName.set(tag, "custom/"+line+"/texture");
            ItemSlashBlade.ModelName.set(tag, "custom/"+line+"/model");
            ItemSlashBlade.StandbyRenderType.set(tag, 2);

            ItemStack tiny = SlashBlade.getCustomBlade(SlashBlade.TinyBladeSoulStr);
            tiny.setCount(i+1);

            IRecipe recipe = new RecipeCustomBlade(customBlade,
                    "P  ",
                    " B ",
                    "  S",
                    'S', SlashBlade.getCustomBlade(SlashBlade.SphereBladeSoulStr),
                    'B', new ItemStack(SlashBlade.bladeNamed,1,0),
                    'P', tiny
            ).setMirrored(false);

            SlashBlade.addRecipe(key,recipe);
            SlashBlade.registerCustomItemStack(key, customBlade);
            ItemSlashBladeNamed.NamedBlades.add(key);
        }
    }
}
