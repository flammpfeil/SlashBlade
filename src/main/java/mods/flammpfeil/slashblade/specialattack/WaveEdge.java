package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.EntityDrive;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Furia on 14/05/27.
 */
public class WaveEdge extends SpecialAttackBase {
    @Override
    public String toString() {
        return "waveedge";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.worldObj;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(!world.isRemote){

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag,cost,false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            float baseModif = blade.getBaseAttackModifiers(tag);
            int level = Math.max(1, EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack));
            float magicDamage = (baseModif/2.0f);

            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank)
                magicDamage += ItemSlashBlade.AttackAmplifier.get(tag) * (0.25f + (level /5.0f));

            final float[] speeds = {0.25f,0.3f,0.35f};
            for(int i = 0; i < speeds.length;i++){
                EntityDrive entityDrive = new EntityDrive(world, player, magicDamage,false,0);
                entityDrive.setInitialSpeed(speeds[i]);
                if (entityDrive != null) {
                    world.spawnEntityInWorld(entityDrive);
                }
            }
            {
                EntityDrive entityDrive = new EntityDrive(world, player, magicDamage,true,0);
                entityDrive.setInitialSpeed(0.225f);
                if (entityDrive != null) {
                    world.spawnEntityInWorld(entityDrive);
                }
            }
        }

        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.Kiriage);
    }
}
