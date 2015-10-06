package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import java.util.EnumSet;

/**
 * Created by Furia on 2015/10/04.
 */
public class SoulEater {
    static final String tag = "SB.KilledPos";
    static final String tag2 = "SB.KilledCount";
    static final String tag3 = "SB.KilledExp";

    public static void entityKilled(ItemStack stack, EntityLivingBase target,EntityLivingBase player){
        if(!isAveilable(stack)) return;

        NBTTagCompound nbtTag = ItemSlashBlade.getItemTagCompound(stack);
        int exp = ItemSlashBlade.PrevExp.get(nbtTag);
        exp = Math.max(0,exp);

        if(!isNoMove(player)){
            player.getEntityData().setInteger(tag, getPosHash(player));
            player.getEntityData().setInteger(tag3, exp);

            if(!player.worldObj.isRemote)
                player.getEntityData().setInteger(tag2, 1);
        }else{
            player.getEntityData().setInteger(tag, getPosHash(player));
            if(!player.worldObj.isRemote){
                int count = player.getEntityData().getInteger(tag2);
                count += 1;
                player.getEntityData().setInteger(tag2, count);
            }

            int sumexp = player.getEntityData().getInteger(tag3);
            sumexp += exp;
            player.getEntityData().setInteger(tag3, sumexp);
        }
    }

    public static void fire(ItemStack stack ,EntityLivingBase player){
        if(!player.getEntityData().hasKey(tag)) return;

        if(!isNoMove(player)) return;
        player.getEntityData().removeTag(tag);

        int exp = player.getEntityData().getInteger(tag3);
        player.getEntityData().removeTag(tag3);

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        ItemSlashBlade.ProudSoul.add(tag,exp);

        StylishRankManager.addRankPoint(player, StylishRankManager.AttackTypes.KillNoutou);


        if(!isSoulEaterAveilable(stack)) return;

        int curDamage = stack.getItemDamage();
        int repair = Math.max(exp, 1);
        stack.setItemDamage(Math.max(0, curDamage - repair));


        if(player.getHealth() != player.getMaxHealth()){
            if(!player.worldObj.isRemote) {
                float count = player.getEntityData().getInteger(tag2);
                count = Math.min(count, player.getMaxHealth() / 10.0f);
                player.getEntityData().removeTag(tag2);

                player.heal(count);
            }

            if(player instanceof EntityPlayer){
                AchievementList.triggerAchievement((EntityPlayer) player, "soulEater");
                ((EntityPlayer) player).onEnchantmentCritical(player);
                ((EntityPlayer) player).addExhaustion(1.0f);
            }
        }
    }

    static int getPosHash(EntityLivingBase entity){
        return  (int) ((entity.posX + entity.posY + entity.posZ) * 10.0);
    }

    public static boolean isNoMove(EntityLivingBase player){
        int posHash = player.getEntityData().getInteger(tag);
        int nowHash = getPosHash(player);

        return posHash == nowHash;
    }

    static boolean isAveilable(ItemStack stack){
        if(stack == null) return false;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return false;
        return true;
    }

    static boolean isSoulEaterAveilable(ItemStack stack){
        ItemSlashBlade blade = (ItemSlashBlade) stack.getItem();
        EnumSet<ItemSlashBlade.SwordType> st = blade.getSwordType(stack);
        return st.contains(ItemSlashBlade.SwordType.SoulEeater);
    }
}
