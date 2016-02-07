package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.entity.EntitySpearManager;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Created by Furia on 14/07/07.
 */
public class Spear extends SpecialAttackBase {
    @Override
    public String toString() {
        return "spear";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.worldObj;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);


        double playerDist = 3.5;
        float attackDist = (float)(playerDist / 3.0);


        if(!player.onGround)
            playerDist *= 0.35f;
        player.motionX = -Math.sin(Math.toRadians(player.rotationYaw)) * playerDist;
        player.motionZ =  Math.cos(Math.toRadians(player.rotationYaw)) * playerDist;

        if(!world.isRemote){

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag,cost,false)){
                stack.damageItem(10, player);
            }

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            player.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(),10,0,true,false));

            EntitySpearManager entityDA = new EntitySpearManager(world, player, false);
            entityDA.setLifeTime(7);
            if (entityDA != null) {
                world.spawnEntityInWorld(entityDA);
            }
        }


        world.playSoundAtEntity(player, "random.explode", 1.0F, 1.0F);
        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.HiraTuki);
    }
}
