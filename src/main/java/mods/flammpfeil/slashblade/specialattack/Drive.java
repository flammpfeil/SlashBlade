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
public class Drive extends SpecialAttackBase {

    private final float speed;
    private final int lifetime;
    private final boolean multihit;
    private final ItemSlashBlade.ComboSequence setCombo;

    public Drive(float speed,int lifetime,boolean multihit, ItemSlashBlade.ComboSequence setCombo){
        this.speed = speed;
        this.lifetime = lifetime;
        this.multihit = multihit;
        this.setCombo = setCombo;
    }

    @Override
    public String toString() {
        return "drive";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.worldObj;
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(!world.isRemote){

            final int cost = -10;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 5, player);
            }

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            float baseModif = blade.getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            float magicDamage = baseModif;

            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank)
                magicDamage += ItemSlashBlade.AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));

            EntityDrive entityDrive = new EntityDrive(world, player, magicDamage,multihit,90.0f - setCombo.swingDirection);
            if (entityDrive != null) {
                entityDrive.setInitialSpeed(speed);
                entityDrive.setLifeTime(lifetime);
                world.spawnEntityInWorld(entityDrive);
            }
        }

        ItemSlashBlade.setComboSequence(tag, setCombo);
    }
}
