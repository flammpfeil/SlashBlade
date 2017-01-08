package mods.flammpfeil.slashblade.specialeffect;

import com.google.common.collect.Multimap;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.event.DropEventHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Random;

/**
 * Created by Furia on 15/06/19.
 */
public class BlastEdge implements ISpecialEffect{
    private static final String EffectKey = "BlastEdge";

    private boolean useBlade(ItemSlashBlade.ComboSequence sequence){
        if(sequence.useScabbard) return false;
        if(sequence == ItemSlashBlade.ComboSequence.None) return false;
        if(sequence == ItemSlashBlade.ComboSequence.Noutou) return false;
        return true;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event){

        EntityLivingBase target = event.getEntityLiving();

        PotionEffect effect = target.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);
        int currentLevel = effect != null ? effect.getAmplifier() : 0;

        if(!target.worldObj.isRemote){
            if(target.isWet() && currentLevel < 0){
                target.removePotionEffect(MobEffects.FIRE_RESISTANCE);
                target.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.4F);
            }
        }

        if(target.worldObj instanceof WorldServer &&  currentLevel < 0 && target.ticksExisted % 20 == 0){
            currentLevel = Math.abs(currentLevel);
            Random rand = target.getRNG();
            for (int i = 0; i < currentLevel; ++i)
            {
                double d0 = rand.nextGaussian() * 0.5D;
                double d1 = rand.nextGaussian() * 0.02D;
                double d2 = rand.nextGaussian() * 0.5D;
                ((WorldServer)target.worldObj).spawnParticle(EnumParticleTypes.REDSTONE,
                        false,
                        target.posX,
                        target.posY + target.getEyeHeight(),
                        target.posZ,
                        currentLevel, d0, d1, d2, 0.0);
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event){
        if(!event.getSource().isFireDamage()) return;

        EntityLivingBase target = event.getEntityLiving();

        PotionEffect effect = target.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);
        int currentLevel = effect != null ? effect.getAmplifier() : 0;

        if(currentLevel < 0){
            target.removePotionEffect(MobEffects.FIRE_RESISTANCE);

            target.worldObj.createExplosion(null, target.posX , target.posY, target.posZ, 1, false);
        }

    }

    @SubscribeEvent
    public void onImpactEffectEvent(SlashBladeEvent.ImpactEffectEvent event){

        if(event.user.worldObj.isRemote) return;

        if(!useBlade(event.sequence)) return;

        if(!SpecialEffects.isPlayer(event.user)) return;
        EntityPlayer player = (EntityPlayer) event.user;

        int effectLevel = 0;

        switch (SpecialEffects.isEffective(player, event.blade, this)){
            case None:
                return;
                /*
            case Effective:
                if(event.target.getRNG().nextInt(2) != 0) return;
                break;
            case NonEffective:
                if(event.target.getRNG().nextInt(5) != 0) return;
                break;
                */
            default:
                break;
        }

        EntityLivingBase target = event.target;

        PotionEffect effect = target.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);
        int currentLevel = effect != null ? effect.getAmplifier() : 0;

        if(currentLevel <= -3){
            target.removePotionEffect(MobEffects.FIRE_RESISTANCE);
            Explosion blast = target.worldObj.createExplosion(player, target.posX , target.posY + player.getEyeHeight() + 0.1f, target.posZ, 0.5f, false);
            target.hurtResistantTime = 0;
            int level = 1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, event.blade);
            target.attackEntityFrom(DamageSource.causeExplosionDamage(blast), (float)level * 2);
        }else{
            currentLevel = Math.min(0, currentLevel);
            target.removePotionEffect(MobEffects.FIRE_RESISTANCE);
            target.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10,currentLevel - 1, true, false));
        }

        player.onEnchantmentCritical(target);

        Random rand = target.getRNG();
        for (int i = 0; i < 3; ++i)
        {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            target.worldObj.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                    target.posX + (double)(rand.nextFloat() * target.width * 2.0F) - (double)target.width,
                    target.posY + 1.0D + (double)(rand.nextFloat() * target.height),
                    target.posZ + (double)(rand.nextFloat() * target.width * 2.0F) - (double)target.width, d0, d1, d2, new int[0]);
        }
    }

    @SubscribeEvent
    public void onUpdateItemSlashBlade(SlashBladeEvent.OnUpdateEvent event){

        if(!SpecialEffects.isPlayer(event.entity)) return;
        EntityPlayer player = (EntityPlayer) event.entity;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(event.blade);
        if(!useBlade(ItemSlashBlade.getComboSequence(tag))) return;

        switch (SpecialEffects.isEffective(player,event.blade,this)){
            case None:
                return;
            case NonEffective:
                if(player.getRNG().nextInt(4) != 0) return;
                break;
            case Effective:
                return;
        }

        PotionEffect haste = player.getActivePotionEffect(MobEffects.MINING_FATIGUE);
        int check = haste != null ? haste.getAmplifier() != 1 ? 3 : 4 : 2;

        if (player.swingProgressInt != check) return;


        PotionEffect effect = player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);
        int currentLevel = effect != null ? effect.getAmplifier() : 0;

        if(currentLevel <= -5){
            player.removePotionEffect(MobEffects.FIRE_RESISTANCE);
            player.worldObj.createExplosion(null, player.posX , player.posY + player.getEyeHeight() + 0.1f, player.posZ, 1.0f, false);
        }else{
            currentLevel = Math.min(0, currentLevel);
            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10,-5, false, false));
        }

    }

    @Override
    public void register() {
        SlashBladeHooks.EventBus.register(this);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @Override
    public int getDefaultRequiredLevel() {
        return 5;
    }

    @Override
    public String getEffectKey() {
        return EffectKey;
    }


    @SubscribeEvent
    public void LivingDrops(LivingDropsEvent event){
        String key = EntityList.getEntityString(event.getEntityLiving());

        if(key.equals("Creeper")){

            NBTTagCompound tag = event.getEntityLiving().writeToNBT(new NBTTagCompound());
            if(tag.getBoolean("powered")){

                EntityLivingBase target =event.getEntityLiving().getAITarget();
                if(target == null) return;

                ItemStack attackItem = target.getHeldItem(EnumHand.MAIN_HAND);
                if(attackItem == null) return;
                if(!(attackItem.getItem() instanceof ItemSlashBlade)) return;

                ItemStack bladeSoulCrystal = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);
                SpecialEffects.addEffect(bladeSoulCrystal, this);

                event.getEntityLiving().entityDropItem(bladeSoulCrystal,1);
            }
        }
    }
}
