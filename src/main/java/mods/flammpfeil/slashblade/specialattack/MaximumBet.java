package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.entity.EntityMaximumBetManager;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Furia on 14/07/07.
 */
public class MaximumBet extends SpecialAttackBase {
    @Override
    public String toString() {
        return "maximumbet";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.world;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(!world.isRemote){

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag,cost,false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            EntityMaximumBetManager entityDA = new EntityMaximumBetManager(world, player);
            if (entityDA != null) {
                world.spawnEntity(entityDA);
            }
        }

        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashEdge);
    }
}
