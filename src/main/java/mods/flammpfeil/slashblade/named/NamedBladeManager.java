package mods.flammpfeil.slashblade.named;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
}
