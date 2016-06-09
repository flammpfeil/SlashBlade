package mods.flammpfeil.slashblade.named;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
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
        ItemStack crystal = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);

        NBTTagCompound newTag = (NBTTagCompound)tag.copy();

        newTag.removeTag("ench");

        crystal.setTagCompound(newTag);

        crystal.setStackDisplayName(name + " soul");

        namedbladeSouls.add(crystal);
    }

    public static void registerBladeSoul(NBTTagCompound tag, String name, boolean addCreativeTab){
        registerBladeSoul(tag,name);

        if(addCreativeTab){
            ItemStack blade = new ItemStack(SlashBlade.bladeNamed, 1, 0);
            blade.setTagCompound((NBTTagCompound)tag.copy());

            GameRegistry.registerCustomItemStack(blade.getUnlocalizedName(), blade);
            ItemSlashBladeNamed.NamedBlades.add(blade.getUnlocalizedName());
        }
    }
}
