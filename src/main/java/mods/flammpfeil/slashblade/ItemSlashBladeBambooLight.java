package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 14/05/27.
 */
public class ItemSlashBladeBambooLight extends ItemSlashBladeDetune {
    public ItemSlashBladeBambooLight(ToolMaterial par2EnumToolMaterial, float baseAttackModifiers) {
        super(par2EnumToolMaterial, baseAttackModifiers);
    }

    @Override
    public boolean isDestructable(ItemStack stack) {
        NBTTagCompound tag = getItemTagCompound(stack);
        int killCount = ItemSlashBlade.KillCount.get(tag);
        if(100 <= killCount)
            return false;
        else
            return super.isDestructable(stack);
    }
}
