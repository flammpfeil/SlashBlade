package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 14/05/27.
 */
public class ItemSlashBladeBambooLight extends ItemSlashBladeDetune {
    public ItemSlashBladeBambooLight(int id,EnumToolMaterial par2EnumToolMaterial, float baseAttackModifiers) {
        super(id,par2EnumToolMaterial, baseAttackModifiers);
    }

    @Override
    public void dropItemDestructed(Entity entity, ItemStack stack) {
        super.dropItemDestructed(entity, stack);

        if(!entity.worldObj.isRemote){
            NBTTagCompound tag = getItemTagCompound(stack);
            int killCount = ItemSlashBlade.KillCount.get(tag);
            if(100 <= killCount){
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
