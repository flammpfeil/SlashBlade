package mods.flammpfeil.slashblade.named;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.RecipeWrapBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.DummyAnvilRecipe;
import mods.flammpfeil.slashblade.util.SlashBladeAchievementCreateEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Furia on 2016/06/01.
 */
public class NamedBladeManager {
    static public final List<String> keyList = Lists.newArrayList();
    static public final Map<String,ItemStack> namedbladeSouls = Maps.newHashMap();

    private static ItemStack getSoulOf(int index){
        String key = keyList.get(index);
        if(key == null)
            return ItemStack.EMPTY;
        ItemStack stack = namedbladeSouls.get(key);
        if(!stack.isEmpty())
            stack = stack.copy();
        return stack;
    }

    public static ItemStack getNamedSoul(Random rand){
        if(rand == null)
            rand = new Random();
        int max = keyList.size();
        int index = rand.nextInt(max);
        return getSoulOf(index);
    }
    public static ItemStack getNamedSoulSequential(int num){
        int max = keyList.size();
        int index = Math.abs(num) % max;
        return getSoulOf(index);
    }

    public static void registerBladeSoul(NBTTagCompound tag, String name){
        ItemStack crystal = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);

        NBTTagCompound newTag = (NBTTagCompound)tag.copy();

        newTag.removeTag("ench");

        crystal.setTagCompound(newTag);

        crystal.setStackDisplayName(name + " soul");

        String keyName = ItemSlashBladeNamed.CurrentItemName.get(tag);
        keyList.add(keyName);
        namedbladeSouls.put(keyName, crystal);
    }

    public static void registerBladeSoul(NBTTagCompound tag, String name, boolean addCreativeTab){
        registerBladeSoul(tag,name);

        if(addCreativeTab){
            ItemStack blade = new ItemStack(SlashBlade.bladeNamed, 1, 0);
            blade.setTagCompound((NBTTagCompound)tag.copy());

            SlashBlade.registerCustomItemStack(blade.getUnlocalizedName(), blade);
            ItemSlashBladeNamed.NamedBlades.add(blade.getUnlocalizedName());
        }
    }

    @SubscribeEvent
    public void onRegisterSBAchievement(SlashBladeAchievementCreateEvent event){
        for(Map.Entry<String,ItemStack> entry : namedbladeSouls.entrySet()){
            ItemStack icon = SlashBlade.getCustomBlade(entry.getKey());

            if(icon.isEmpty()) {
                ItemStack soul = entry.getValue();
                NBTTagCompound matTag = soul.getTagCompound();

                ItemStack targetBlade = SlashBlade.findItemStack(SlashBlade.modid, "slashbladeNamed", 1);

                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(targetBlade);


                ItemSlashBladeNamed.CurrentItemName.set(tag, ItemSlashBladeNamed.CurrentItemName.get(matTag));

                if (ItemSlashBlade.BaseAttackModifier.exists(matTag))
                    ItemSlashBlade.setBaseAttackModifier(tag, ItemSlashBlade.BaseAttackModifier.get(matTag));

                TagPropertyAccessor[] accessors = {
                        ItemSlashBladeNamed.CustomMaxDamage,
                        ItemSlashBlade.TextureName,
                        ItemSlashBlade.ModelName,
                        ItemSlashBlade.SpecialAttackType,
                        ItemSlashBlade.StandbyRenderType,
                        ItemSlashBladeNamed.IsDefaultBewitched,
                        ItemSlashBladeNamed.TrueItemName,
                        ItemSlashBlade.SummonedSwordColor,
                        ItemSlashBlade.IsDestructable,
                        ItemSlashBlade.IsBroken
                };

                for (TagPropertyAccessor acc : accessors)
                    copyTag(acc, tag, matTag);

                icon = targetBlade;
            }

            if(!icon.isEmpty()){
                /*todo:advancement
                String achievementKey = entry.getKey().replaceFirst("flammpfeil.slashblade.named.","");
                achievementKey = achievementKey.replaceFirst("flammpfeil.slashblade.","");
                Achievement ach = AchievementList.registerCraftingAchievement(achievementKey, icon, net.minecraft.stats.AchievementList.BUILD_SWORD);
                */

                String contentKey = entry.getKey();
                ItemStack base = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeNamed",1);
                SlashBlade.addRecipe(contentKey, new DummyAnvilRecipe(icon, base, entry.getValue()));
                //todo:advancement
                //AchievementList.setContent(ach , contentKey);
            }

        }
    }
    public void copyTag(TagPropertyAccessor acc, NBTTagCompound dest , NBTTagCompound src){
        if(acc.exists(src))
            acc.set(dest, acc.get(src));
    }
}
