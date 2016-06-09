package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.EntityDrive;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Furia on 14/05/27.
 */
public class CircleSlash extends SpecialAttackBase{

    static public String AttackType = StylishRankManager.AttackTypes.registerAttackType("OverSlash", 0.5F);

    @Override
    public String toString() {
        return "circleslash";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.worldObj;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        player.worldObj.playSoundAtEntity(player, "mob.blaze.hit", 0.2F, 0.6F);

        if(!world.isRemote){

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag,cost,false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            {
                AxisAlignedBB bb = player.boundingBox.copy();
                bb = bb.expand(5.0f, 0.25f, 5.0f);

                List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, bb, ItemSlashBlade.AttackableSelector);

                for(Entity curEntity : list){
                    StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.CircleSlash);
                    blade.attackTargetEntity(stack, curEntity, player, true);
                    player.onCriticalHit(curEntity);
                }
            }

            float baseModif = blade.getBaseAttackModifiers(tag);
            int level = Math.max(1, EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack));
            float magicDamage = (baseModif/2.0f);

            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank)
                magicDamage += ItemSlashBlade.AttackAmplifier.get(tag) * (0.25f + (level /5.0f));


            for(int i = 0; i < 6;i++){
                EntityDrive entityDrive = new EntityDrive(world, player, magicDamage,false,0);
                entityDrive.setLocationAndAngles(player.posX,
                        player.posY + (double)player.getEyeHeight()/2D,
                        player.posZ,
                        player.rotationYaw + 60 * i /*+ (entityDrive.getRand().nextFloat() - 0.5f) * 60*/,
                        0);//(entityDrive.getRand().nextFloat() - 0.5f) * 60);
                entityDrive.setDriveVector(0.5f);
                entityDrive.setLifeTime(10);
                entityDrive.setIsMultiHit(false);
                entityDrive.setRoll(90.0f /*+ 120 * (entityDrive.getRand().nextFloat() - 0.5f)*/);
                if (entityDrive != null) {
                    world.spawnEntityInWorld(entityDrive);
                }
            }

        }


        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.Battou);
    }
}
