package mods.flammpfeil.slashblade.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.crafting.RecipeCustomBlade;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.SlashBladeAchievementCreateEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Furia on 2016/02/10.
 */
public class ConfigCustomBladeManager {
    String[] lines = {};

    static private String escape(String source){
        return String.format("\"%s\"", source.replace("\\","\\\\").replace("\"","\\quot;").replace("\r", "\\r;").replace("\n", "\\n;"));
    }
    static private String unescape(String source){
        return source.replace("\"", "").replace("\\quot;", "\"").replace("\\r;","\r").replace("\\n;","\n").replace("\\\\", "\\");
    }

    public void loadConfig(Configuration config){
        Property propCustomBlade = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "CustomBlade" ,new String[]{"dios"});
        lines = propCustomBlade.getStringList();
    }

    @SubscribeEvent
    public void onSlashBladeAchievementCreateEvent(SlashBladeAchievementCreateEvent event){
        int x = 1;
        int y = 13;
        for(String line : lines){
            String key = "custom_"+line;

            ItemStack customBlade = new ItemStack(SlashBlade.bladeNamed,1,0);

            NBTTagCompound tag = new NBTTagCompound();
            customBlade.setTagCompound(tag);

            ItemSlashBladeNamed.CurrentItemName.set(tag, key);
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, 50);
            ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getDamageVsEntity());
            ItemSlashBlade.TextureName.set(tag, "custom/"+line+"/texture");
            ItemSlashBlade.ModelName.set(tag, "custom/"+line+"/model");
            ItemSlashBlade.StandbyRenderType.set(tag, 2);

            ItemStack tiny = SlashBlade.getCustomBlade(SlashBlade.TinyBladeSoulStr);
            tiny.stackSize = x;

            IRecipe recipe = new RecipeCustomBlade(customBlade,
                    "P##",
                    "#B#",
                    "##S",
                    'S', SlashBlade.getCustomBlade(SlashBlade.SphereBladeSoulStr),
                    'B', new ItemStack(SlashBlade.bladeNamed,1,0),
                    'P', tiny
            ).setMirrored(false);

            SlashBlade.addRecipe(key,recipe);
            GameRegistry.registerCustomItemStack(key, customBlade);

            Achievement achievement = AchievementList.registerCraftingAchievement(
                    key, -3 + x++, y, SlashBlade.getCustomBlade(key), AchievementList.getAchievement("noname"));

            AchievementList.setContent(achievement, key);

            ItemSlashBladeNamed.NamedBlades.add(key);
        }
    }
}
