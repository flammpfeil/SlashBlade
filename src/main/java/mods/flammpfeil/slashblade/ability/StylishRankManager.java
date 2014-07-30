package mods.flammpfeil.slashblade.ability;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
        public static Map<String,Float> types = Maps.newHashMap();
        public static String None = registerAttackType("None", 0);

        public static String Saya1 = registerAttackType("Saya1", 0.3f);
        public static String Saya2 = registerAttackType("Saya2", 0.3f);
        public static String Battou = registerAttackType("Battou", 0.5f);
        public static String IaiBattou = registerAttackType("IaiBattou", -0.5f);

        public static String Kiriage = registerAttackType("Kiriage", 0.3f);
        public static String Kiriorosi = registerAttackType("Kiriorosi", 0.4f);

        public static String Iai = registerAttackType("Iai", 0.3f);
        public static String JumpBattou = registerAttackType("JumpBattou", 0.4f);

        public static String SlashDim = registerAttackType("SlashDim", 0.6f);
        public static String SlashDimMagic = registerAttackType("SlashDimMagic", -0.1f);
        public static String Drive = registerAttackType("Drive", 0.5f);
        public static String Spear = registerAttackType("Spear", -0.2f);
        //public static String WaveEdge = registerAttackType("WaveEdge", -0.1f);

        public static String QuickDrive = registerAttackType("QuickDrive", 0.2f);

        public static String PhantomSword = registerAttackType("PhantomSword", 0.2f);
        public static String BreakPhantomSword = registerAttackType("BreakPhantomSword", 0.1f);

        public static String JustGuard = registerAttackType("JustGuard", 1.0f);
        public static String Noutou = registerAttackType("Noutou", -1.0f);

        public static String DestructObject = registerAttackType("DestructObject", -0.1f);

        public static String registerAttackType(String key,float factor){
            types.put(key,factor);
            return key;
        }
    }

    public static String rankText[] = {"D","C","B","A","S","SS","SSS"};

    public static int RankRange = 1000;

    public static TagPropertyAccessor.TagPropertyIntegerWithRange RankPoint = new TagPropertyAccessor.TagPropertyIntegerWithRange("SBRankPoint",0, (rankText.length * RankRange));
    public static TagPropertyAccessor.TagPropertyLong LastRankPointUpdate = new TagPropertyAccessor.TagPropertyLong("SBLastRPUpdate");
    public static TagPropertyAccessor.TagPropertyString AttackType = new TagPropertyAccessor.TagPropertyString("LastAttackType");


    public static final int initCooltime = 20;
    public static final int addCooltime = 10;
    public static final int maxCooltime = initCooltime + addCooltime;

    public static NBTTagCompound dummyTag = new NBTTagCompound();
    public static NBTTagCompound getTag(Entity e){
        if(e != null)
            return e.getEntityData();
        else
            return dummyTag;
    }

    public static int getTotalRankPoint(Entity e){
        NBTTagCompound tag = getTag(e);
        int rank = RankPoint.get(tag);

        long now = e.worldObj.getTotalWorldTime();
        long lastUpdate = LastRankPointUpdate.get(tag);

        long descPoint = Math.max(0,lastUpdate - now);

        if(descPoint < RankRange*2)
            rank -= descPoint;
        else
            rank = 0;

        return Math.max(0,rank);
    }

    public static float getCurrentProgress(Entity e){

        int rank = getTotalRankPoint(e);

        rank /= rankText.length;

        return rank / (float) RankRange;
    }

    public static int getStylishRank(Entity e){
        return getStylishRank(getTotalRankPoint(e));
    }
    public static int getStylishRank(int totalRankPoint){
        int rank = totalRankPoint;

        rank = (int)Math.ceil(rank / RankRange);

        rank = Math.max(0,Math.min(rankText.length, rank));

        return rank;
    }

    public static String getRankText(Entity e){
        return getRankText(getStylishRank(e));
    }
    public static String getRankText(int rank){
        return rank <= 0 ? "" : rankText[Math.max(rank - 1, rankText.length - 1)];
    }

    public static void setNextAttackType(Entity e,String key){
        NBTTagCompound tag = getTag(e);
        AttackType.set(tag, key);
    }

    public static void doAttack(Entity e){
        if(e == null) return;
        NBTTagCompound tag = getTag(e);

        //stylePoint
        String attackType = AttackType.get(tag);
        int value = 0;

        if(AttackTypes.types.containsKey(attackType))
            value =(int)(RankRange * AttackTypes.types.get(attackType));

        if(value == 0)
            return;
        else if(value < 0){
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

    public static void addRankPoint(Entity e,int amount){
        if(e == null) return;
        NBTTagCompound tag = getTag(e);

        int rankPoint = getTotalRankPoint(e);

        int lastRank = getStylishRank(rankPoint);

        rankPoint += amount;
        RankPoint.set(tag, rankPoint);
        LastRankPointUpdate.set(tag,e.worldObj.getTotalWorldTime());

        int postRank = getStylishRank(rankPoint);

        if(lastRank != postRank)
            onRiseInRank(e,postRank,rankPoint);
    }

    public static void onRiseInRank(Entity e, int rank,int rankPoint){
        if(e == null) return;

        if(e.worldObj.isRemote)
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(getRankText(rank)));
    }
}
