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

        int posHash = player.getEntityData().getInteger(tag);
        int nowHash = getPosHash(player);

        player.getEntityData().removeTag(tag);

        if(posHash != nowHash){
            player.getEntityData().setInteger(tag, nowHash);
            player.getEntityData().setInteger(tag2, 1);
            player.getEntityData().setInteger(tag3, exp);
        }else{
            int count = player.getEntityData().getInteger(tag2);
            count += 1;
            player.getEntityData().setInteger(tag2, count);

            int sumexp = player.getEntityData().getInteger(tag3);
            sumexp += exp;
            player.getEntityData().setInteger(tag3, sumexp);
        }
    }

    public static void fire(ItemStack stack ,EntityLivingBase player){
        if(!player.getEntityData().hasKey(tag)) return;

        int posHash = player.getEntityData().getInteger(tag);
        int nowHash = getPosHash(player);

        player.getEntityData().removeTag(tag);

        if(posHash != nowHash) return;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        int exp = player.getEntityData().getInteger(tag3);
        player.getEntityData().removeTag(tag3);

        ItemSlashBlade.ProudSoul.add(tag,exp / 2);

        int curDamage = stack.getItemDamage();
        int repair = Math.max(exp, 1);
        stack.setItemDamage(Math.max(0, curDamage - repair));

        if(!player.worldObj.isRemote) {
            float count = player.getEntityData().getInteger(tag2);
            count = count / 3.0f;
            player.getEntityData().removeTag(tag2);
            player.heal(count);
        }

        if(player instanceof EntityPlayer){
            AchievementList.triggerAchievement((EntityPlayer) player, "soulEater");
            ((EntityPlayer) player).onEnchantmentCritical(player);
            ((EntityPlayer) player).addExhaustion(1.0f);
        }
    }

    static int getPosHash(EntityLivingBase entity){
        return  (int) ((entity.posX + entity.posY + entity.posZ) * 10.0);
    }

    static boolean isAveilable(ItemStack stack){
        if(stack == null) return false;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return false;

        ItemSlashBlade blade = (ItemSlashBlade) stack.getItem();
        EnumSet<ItemSlashBlade.SwordType> st = blade.getSwordType(stack);
        if(!st.contains(ItemSlashBlade.SwordType.SoulEeater)) return false;

        return true;
    }
}
