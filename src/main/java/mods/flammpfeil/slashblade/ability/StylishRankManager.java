package mods.flammpfeil.slashblade.ability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Furia on 14/07/29.*/

public class StylishRankManager {

    static private String unescape(String source){
        return source.replace("\"", "").replace("\\quot;", "\"").replace("\\r;","\r").replace("\\n;","\n").replace("\\\\", "\\");
    }

    public StylishRankManager(){
        try{
            SlashBlade.mainConfiguration.load();

            {
                Property propIgnoreDamageType = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "RankDownIgnoreDamageTypes" ,new String[]{});

                String[] strs = propIgnoreDamageType.getStringList();
                ArrayList<String> tmp = Lists.newArrayList();
                for(String str : strs){
                    ignoreDamageTypes.add(unescape(str));
                }
            }

        }
        finally
        {
            SlashBlade.mainConfiguration.save();
        }

    }

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
        public static String JudgmentCut= registerAttackType("JudgmentCut", 0.1f);

        public static String Drive = registerAttackType("Drive", 0.5f);
        public static String Spear = registerAttackType("Spear", -0.2f);
        //public static String WaveEdge = registerAttackType("WaveEdge", -0.1f);
        public static String CircleSlash = registerAttackType("CircleSlash", 0.3f);

        public static String QuickDrive = registerAttackType("QuickDrive", 0.2f);

        public static String PhantomSword = registerAttackType("PhantomSword", 0.2f);
        public static String BreakPhantomSword = registerAttackType("BreakPhantomSword", 0.1f);

        public static String JustGuard = registerAttackType("JustGuard", 1.0f);
        public static String Noutou = registerAttackType("Noutou", -1.5f);

        public static String KillNoutou = registerAttackType("KillNoutou", -0.5f);

        public static String DestructObject = registerAttackType("DestructObject", -0.1f);

        public static String AttackAvoidance = registerAttackType("AttackAvoidance", -0.3f);


        public static String SlashEdge = registerAttackType("SlashEdge", 0.2f);
        public static String ReturnEdge = registerAttackType("ReturnEdge", 0.2f);
        public static String SSlashEdge = registerAttackType("SSlashEdge", 0.3f);
        public static String SReturnEdge = registerAttackType("SReturnEdge", 0.3f);
        public static String SIai = registerAttackType("SIai", 0.3f);
        public static String SSlashBlade = registerAttackType("SSlashBlade", -0.2f);

        public static String ASlashEdge = registerAttackType("ASlashEdge", 0.3f);
        public static String AKiriorosi = registerAttackType("AKiriorosi", 0.3f);
        public static String AKiriage = registerAttackType("AKiriage", 0.4f);
        public static String AKiriorosiFinish = registerAttackType("AKiriorosiFinish", 0.5f);

        public static String Calibur = registerAttackType("Calibur", 0.5f);

        public static String HelmBraker = registerAttackType("HelmBraker", 0.5f);

        public static String RapidSlash = registerAttackType("RapidSlash", 0.3f);
        public static String RisingStar = registerAttackType("RisingStar", 0.3f);

        public static String registerAttackType(String key,float factor){
            types.put(key,factor);
            return key;
        }
    }

    /**
     * 0:
     * 1:D
     * 2:C
     * 3:B
     * 4:A
     * 5:S
     * 6: 5.5:SS
     * 7: 5.75:SSS
     */
    public static String rankText[] = {"D","C","B","A","S","SS","SSS"};

    public static int RankRange = 100;

    public static TagPropertyAccessor.TagPropertyIntegerWithRange RankPoint = new TagPropertyAccessor.TagPropertyIntegerWithRange("SBRankPoint",0, (6 * RankRange)-1);
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

        int currentProgress = rank % RankRange;
        long descPoint = Math.max(0, Math.min(currentProgress, now - lastUpdate));

        rank -= descPoint;

        rank = Math.max(0,rank);

        return rank;
    }

    public static float getCurrentProgress(Entity e){

        int rank = getTotalRankPoint(e);

        rank %= RankRange;

        return rank / (float) RankRange;
    }

    public static int getStylishRank(Entity e){
        return getStylishRank(getTotalRankPoint(e));
    }
    public static int getStylishRank(int totalRankPoint){
        int rank = totalRankPoint;

        rank = (int)Math.floor(rank / (float)RankRange);

        //ss
        if(RankRange * 5.5 < totalRankPoint){
            rank++;
        }

        //sss
        if(RankRange * 5.75 < totalRankPoint){
            rank++;
        }

        rank = Math.max(0,Math.min(rankText.length, rank));

        return rank;
    }

    public static String getRankText(Entity e){
        return getRankText(getStylishRank(e));
    }
    public static String getRankText(int rank){
        return rank <= 0 ? "" : rankText[Math.min(rank - 1, rankText.length - 1)];
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
        addRankPoint(e, attackType);
    }

    public static void addRankPoint(Entity e, String attackType){
        if(e == null) return;
        NBTTagCompound tag = getTag(e);

        //stylePoint
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

        int currentRank = getStylishRank(e);

        if(2 < currentRank){
            currentRank = Math.min(rankText.length-2,currentRank);
            currentRank -=2;

            //b - s : 3 - 5

            do{
                value *= 0.8f;
            }while(0 < --currentRank);

            value = Math.max(1, value);
        }

        addRankPoint(e,value);
    }

    public static void addRankPoint(Entity e,int amount){
        if(e == null) return;
        if(e.worldObj.isRemote) return;
        NBTTagCompound tag = getTag(e);

        int rankPoint = getTotalRankPoint(e);

        int lastRank = getStylishRank(rankPoint);

        rankPoint += amount;
        RankPoint.set(tag, rankPoint);
        LastRankPointUpdate.set(tag,e.worldObj.getTotalWorldTime());

        int postRank = getStylishRank(rankPoint);

        if(lastRank < postRank){
            if(e instanceof EntityPlayer)
                AchievementList.triggerAchievement((EntityPlayer)e,"rank"+getRankText(postRank));
        }

        onRiseInRank(e, postRank, rankPoint);
    }

    public static final String MessageHeader = "///RankUpdate ";

    public static void onRiseInRank(Entity e, int rank,int rankPoint){
        if(e == null) return;

        NBTTagCompound tag = getTag(e);

        if(e instanceof EntityPlayer){
            //((EntityPlayer)e).addChatMessage(new ChatComponentText(getRankText(rank) + ":" + rankPoint + ":" + AttackType.get(tag)));
            ((EntityPlayer)e).addChatMessage(new ChatComponentText(MessageHeader + rankPoint));
        }
    }

    static public Set<String> ignoreDamageTypes = Sets.newHashSet("thorns","fall");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void LivingHurtEvent(LivingHurtEvent e){
        String type = e.source.getDamageType();
        if(e.isCanceled()) return;
        if(e.entity == null) return;
        if(!(e.entity instanceof EntityPlayer)) return;

        //guard不可でかつ、mobからの攻撃ではない
        if(e.source.isUnblockable() && e.source.getEntity() != null) return;

        //反射攻撃ではないこと
        if(e.source.getEntity() != null && e.source.getEntity() instanceof EntityLivingBase){
            EntityLivingBase attacker = (EntityLivingBase)e.source.getEntity();

            if(attacker.func_142015_aE() == attacker.ticksExisted)
                return;
        }

        if(ignoreDamageTypes.contains(type)) return;

        NBTTagCompound tag = getTag(e.entity);

        long lastUpdate = LastRankPointUpdate.get(tag);
        long now = e.entity.worldObj.getTotalWorldTime();

        if(RankRange * 2 < now - lastUpdate){
            RankPoint.set(tag, 0);
            onRiseInRank(e.entity, 0, 0);
        }else{
            RankPoint.add(tag, -RankRange * 2);
            onRiseInRank(e.entity, 0, RankPoint.get(tag));
        }
        LastRankPointUpdate.set(tag, now);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void ClientChatReceivedEvent(net.minecraftforge.client.event.ClientChatReceivedEvent e){
        String text = e.message.getUnformattedText();
        if(text.startsWith(MessageHeader)){

            String value = text.substring(MessageHeader.length());
            int rankPoint;
            try{
                rankPoint = Integer.parseInt(value);
            }catch(Exception ex){
                rankPoint = 0;
            }

            Entity el = net.minecraft.client.Minecraft.getMinecraft().thePlayer;
            NBTTagCompound tag = getTag(el);
            RankPoint.set(tag,rankPoint);
            LastRankPointUpdate.set(tag,el.worldObj.getTotalWorldTime());

            //Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("receive :" + rankPoint + ":" + e.message.getUnformattedText()));

            e.setCanceled(true);
        }
    }
}
