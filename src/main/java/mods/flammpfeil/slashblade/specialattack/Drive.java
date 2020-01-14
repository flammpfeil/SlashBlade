package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
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

    private final String name;
    public Drive(String name, float speed,int lifetime,boolean multihit, ItemSlashBlade.ComboSequence setCombo){
        this.name = name;
        this.speed = speed;
        this.lifetime = lifetime;
        this.multihit = multihit;
        this.setCombo = setCombo;
    }

    public Drive(float speed,int lifetime,boolean multihit, ItemSlashBlade.ComboSequence setCombo){
        this("drive", speed, lifetime, multihit, setCombo);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.world;
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(!world.isRemote){

            final int cost = -10;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 5, player);
            }

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            float baseModif = blade.getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            float magicDamage = baseModif;

            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank)
                magicDamage += ItemSlashBlade.AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));

            EntityDrive entityDrive = new EntityDrive(world, player, magicDamage,multihit,90.0f - setCombo.swingDirection);
            if (entityDrive != null) {
                entityDrive.setInitialSpeed(speed);
                entityDrive.setLifeTime(lifetime);
                world.spawnEntity(entityDrive);
            }
        }

        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                0.8F, 0.01F);
        ItemSlashBlade.setComboSequence(tag, setCombo);
    }
}
