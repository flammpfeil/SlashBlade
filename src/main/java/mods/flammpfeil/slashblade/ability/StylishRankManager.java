package mods.flammpfeil.slashblade.ability;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.util.Map;

/**
 * Created by Furia on 14/07/29.*/

public class StylishRankManager {

    /**
     * 0 < Relative
     * 0 > Absolute
     */
    public static class AttackTypes{
        public static String Saya1 = registerAttackType("Saya1", 0.3f);
        public static String Saya2 = registerAttackType("Saya2", 0.3f);
        public static String Battou = registerAttackType("Battou", 0.5f);

        public static String Kiriage = registerAttackType("Kiriage", 0.3f);
        public static String Kiriorosi = registerAttackType("Kiriorosi", 0.4f);

        public static String Iai = registerAttackType("Iai", 0.3f);
        public static String JumpBattou = registerAttackType("JumpBattou", 0.4f);

        public static String SlashDim = registerAttackType("SlashDim", 0.6f);
        public static String Drive = registerAttackType("Drive", 0.5f);
        public static String Spear = registerAttackType("Spear", -0.2f);
        public static String WaveEdge = registerAttackType("WaveEdge", -0.1f);

        public static String QuickDrive = registerAttackType("QuickDrive", 0.2f);

        public static String PhantomSword = registerAttackType("PhantomSword", 0.2f);

        public static String JustGuard = registerAttackType("JustGuard", 1.0f);
        public static String Noutou = registerAttackType("Noutou", -1.0f);
    }

    public static String rankText[] = {"D","C","B","A","S","SS","SSS"};

    public static int RankRange = 1000;

    public static TagPropertyAccessor.TagPropertyIntegerWithRange RankPoint = new TagPropertyAccessor.TagPropertyIntegerWithRange("SBRankPoint",0, (rankText.length * RankRange));
    public static TagPropertyAccessor.TagPropertyLong LastRankPointUpdate = new TagPropertyAccessor.TagPropertyLong("SBLastRPUpdate");
    public static TagPropertyAccessor.TagPropertyString AttackType = new TagPropertyAccessor.TagPropertyString("LastAttackType");


    public static Map<String,Float> attackTypes = Maps.newHashMap();

    public static final int initCooltime = 20;
    public static final int addCooltime = 10;
    public static final int maxCooltime = initCooltime + addCooltime;

    public static int getTotalRankPoint(EntityLivingBase e){
        int rank = RankPoint.get(e.getEntityData());

        long now = e.worldObj.getTotalWorldTime();
        long lastUpdate = LastRankPointUpdate.get(e.getEntityData());

        long descPoint = Math.max(0,lastUpdate - now);

        if(descPoint < RankRange*2)
            rank -= descPoint;
        else
            rank = 0;

        return Math.max(0,rank);
    }

    public static float getCurrentProgress(EntityLivingBase e){

        int rank = getTotalRankPoint(e);

        rank /= rankText.length;

        return rank / (float) RankRange;
    }

    public static int getStylishRank(EntityLivingBase e){
        return getStylishRank(getTotalRankPoint(e));
    }
    public static int getStylishRank(int totalRankPoint){
        int rank = totalRankPoint;

        rank = (int)Math.ceil(rank / RankRange);

        rank = Math.max(0,Math.min(rankText.length, rank));

        return rank;
    }

    public static String getRankText(EntityLivingBase e){
        return getRankText(getStylishRank(e));
    }
    public static String getRankText(int rank){
        return rank <= 0 ? "" : rankText[Math.max(rank - 1, rankText.length - 1)];
    }

    public static String registerAttackType(String key,float factor){
        attackTypes.put(key,factor);
        return key;
    }

    public static void setNextAttackType(EntityLivingBase e,String key){
        AttackType.set(e.getEntityData(), key);
    }

    public static void doAttack(EntityLivingBase e){
        NBTTagCompound tag = e.getEntityData();

        //stylePoint
        String attackType = AttackType.get(tag);
        int value = (int)(RankRange * attackTypes.get(attackType));

        if(value < 0){
            value = Math.abs(value);

        }else{
            //get last use AttackTypeTime stylePoint calcFactor
            String timerKey = "SBAttackTime" + attackType;
            long last = tag.getLong(timerKey);
            long now = e.worldObj.getTotalWorldTime();

            if(last < now){
                tag.setLong(timerKey,now + initCooltime);
            }else if((last - now) < initCooltime){
                value /= 2;
                tag.setLong(timerKey,Math.min(now + maxCooltime, last + addCooltime));
            }else{
                value = 1;
                tag.setLong(timerKey,now + maxCooltime);
            }

        }

        addRankPoint(e,value);
    }

    public static void addRankPoint(EntityLivingBase e,int amount){
        NBTTagCompound tag = e.getEntityData();

        int rankPoint = getTotalRankPoint(e);

        int lastRank = getStylishRank(rankPoint);

        rankPoint += amount;
        RankPoint.set(tag, rankPoint);
        LastRankPointUpdate.set(tag,e.worldObj.getTotalWorldTime());

        int postRank = getStylishRank(rankPoint);

        if(lastRank != postRank)
            onRiseInRank(e,postRank,rankPoint);
    }

    public static void onRiseInRank(EntityLivingBase e, int rank,int rankPoint){
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getRankText(rank)));
    }
}
