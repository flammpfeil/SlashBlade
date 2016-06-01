package mods.flammpfeil.slashblade.named;

import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 2016/06/01.
 */
public class NamedBladeManager {
    static public List<ItemStack> namedbladeSouls = Lists.newArrayList();

    public static ItemStack getNamedSoul(Random rand){
        return namedbladeSouls.get(rand.nextInt(namedbladeSouls.size())).copy();
    }
    public static ItemStack getNamedSoulSequential(int num){
        return namedbladeSouls.get(Math.abs(num) % namedbladeSouls.size()).copy();
    }

    public static void registerBladeSoul(NBTTagCompound tag, String name){
        ItemStack crystal = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);

        NBTTagCompound newTag = (NBTTagCompound)tag.copy();

        newTag.removeTag("ench");

        crystal.setTagCompound(newTag);

        crystal.setStackDisplayName(name + " soul");

        namedbladeSouls.add(crystal);
    }
}
