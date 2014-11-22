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
    public void dropItemDestructed(Entity entity, ItemStack stack) {
        super.dropItemDestructed(entity, stack);

        if(!entity.worldObj.isRemote){
            NBTTagCompound tag = getItemTagCompound(stack);
            int killCount = ItemSlashBlade.KillCount.get(tag);
            if(100 <= killCount){
                if(entity instanceof EntityPlayer)
                    AchievementList.triggerAchievement((EntityPlayer)entity,"saya");
                ItemStack sheath = GameRegistry.findItemStack(SlashBlade.modid, "slashbladeWrapper", 1);
                if(sheath != null){
                    NBTTagCompound copyTag = (NBTTagCompound)tag.copy();
                    IsBroken.remove(copyTag);
                    sheath.setTagCompound(copyTag);
                    entity.entityDropItem(sheath, 0.0F);
                }
            }
        }
    }
}
