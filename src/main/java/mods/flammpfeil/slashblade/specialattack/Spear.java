package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.EntityDirectAttackDummy;
import mods.flammpfeil.slashblade.EntityDrive;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            player.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(),10,0,true));

            EntityDirectAttackDummy entityDA = new EntityDirectAttackDummy(world, player, false);
            entityDA.setLifeTime(7);
            if (entityDA != null) {
                world.spawnEntityInWorld(entityDA);
            }
        }


        world.playSoundAtEntity(player, "random.explode", 1.0F, 1.0F);
        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.HiraTuki);
    }
}
