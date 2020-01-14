package mods.flammpfeil.slashblade.specialeffect;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.capability.BladeCapabilityProvider;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Furia on 15/06/19.
 */
public class HFCustom implements ISpecialEffect, IRemovable{
    private static final String EffectKey = "HFCustom";

    private boolean useBlade(ItemSlashBlade.ComboSequence sequence){
        if(sequence.useScabbard) return false;
        if(sequence == ItemSlashBlade.ComboSequence.None) return false;
        if(sequence == ItemSlashBlade.ComboSequence.Noutou) return false;
        return true;
    }


    public static final int startupCost = 1000;
    public static final int runningCost = 100;

    public static final int energyCostBase = 200;

    @SubscribeEvent
    public void onImpactEffectEvent(SlashBladeEvent.ImpactEffectEvent event){

        //if(event.user.world.isRemote) return;

        if(!useBlade(event.sequence)) return;

        if(!SpecialEffects.isPlayer(event.user)) return;
        EntityPlayer player = (EntityPlayer) event.user;

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

        ItemStack blade = event.blade;

        if(!blade.hasCapability(BladeCapabilityProvider.ENERGY, null)) return;
        IEnergyStorage storage = blade.getCapability(BladeCapabilityProvider.ENERGY, null);
        if(storage == null) return;

        if(!isEmpowered(blade)) return;

        EntityLivingBase target = event.target;

        //effected
        int effectLevel = ItemSlashBlade.getSpecialEffect(blade).getInteger(this.getEffectKey());
        int rank = StylishRankManager.getStylishRank(player);

        float damage = 0.5f * (1 + rank + effectLevel);

        int usage = (int)(energyCostBase * Math.pow(1.2, rank + effectLevel));
        if (storage.extractEnergy(usage,false) == usage)
            forceAttack(target, damage);
    }

    @SubscribeEvent
    public void onUpdateItemSlashBlade(SlashBladeEvent.OnUpdateEvent event){

        if(event.entity.world.isRemote) return;

        if(!SpecialEffects.isPlayer(event.entity)) return;
        EntityPlayer player = (EntityPlayer) event.entity;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(event.blade);

        ItemStack blade = event.blade;

        switch (SpecialEffects.isEffective(player,event.blade,this)){
            case None:
                return;
            case NonEffective:
                //if(player.getRNG().nextInt(4) != 0) return;
                break;
            case Effective:
                break;
        }

        updateEmpoweredState(player, blade);

        if(!isEmpowered(blade)) return;

        if(!blade.hasCapability(BladeCapabilityProvider.ENERGY, null)) return;
        IEnergyStorage storage = blade.getCapability(BladeCapabilityProvider.ENERGY, null);
        if(storage == null) return;

        if(player.world instanceof WorldServer && player.world.getTotalWorldTime() % 10 == 0 &&
                (player.getHeldItemMainhand() != blade
                        || !ItemSlashBlade.OnClick.get(tag)
                        || (!player.isSwingInProgress && player.getActiveItemStack() != null))){

            storage.extractEnergy(HFCustom.runningCost, false);
            if(storage.getEnergyStored() <= 0) {
                setEmpoweredState(player, blade, false);
                return;
            }

            Random rand = player.getRNG();

            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            ((WorldServer)player.world).spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                    false,
                    player.posX,
                    player.posY + player.height / 2.0f,
                    player.posZ,
                    1, d0, d1, d2, 0.25);
        }

        if(!useBlade(ItemSlashBlade.getComboSequence(tag))) return;

        PotionEffect haste = player.getActivePotionEffect(MobEffects.MINING_FATIGUE);
        int check = haste != null ? haste.getAmplifier() != 1 ? 3 : 4 : 2;

        if (player.swingProgressInt != check) return;

        //effected
        blade.damageItem(1, player);
    }

    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getDefaultRequiredLevel() {
        return 1;
    }

    @Override
    public String getEffectKey() {
        return EffectKey;
    }

    void forceAttack(EntityLivingBase target, float amount){
        float health = ((EntityLivingBase) target).getHealth();
        if(0 < health){
            health = Math.max(1.0f,health - amount);
            ((EntityLivingBase) target).setHealth(health);
        }
    }

    public static final String tagEmpowered = "isEmpowered";
    public static boolean isEmpowered(ItemStack blade){
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
        return tag.getBoolean(tagEmpowered);
    }


    public boolean canUse(ItemStack blade, EntityPlayer player){
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
        if(tag.hasUniqueId("Owner")){
            UUID ownerid = tag.getUniqueId("Owner");
            boolean isOwner = ownerid.equals(player.getUniqueID());

            if(!isOwner)
                return false;
        }

        return true;
    }
    public boolean canEmpowered(ItemStack blade){
        if(!blade.hasCapability(BladeCapabilityProvider.ENERGY,null))
            return false;

        IEnergyStorage storage = blade.getCapability(BladeCapabilityProvider.ENERGY, null);
        if(storage == null)
            return false;

        if (storage.getEnergyStored() < startupCost)
            return false;

        return true;
    }

    public boolean setEmpoweredState(EntityPlayer player, ItemStack blade, boolean state){
        if(isEmpowered(blade) == state)
            return state;

        state = state && canEmpowered(blade);

        if(state){
            IEnergyStorage storage = blade.getCapability(BladeCapabilityProvider.ENERGY, null);
            storage.extractEnergy(startupCost, false);
        }

        blade.setTagInfo(tagEmpowered, new NBTTagByte((byte)(state ? 1 : 0)));

        return state;
    }


    public void updateEmpoweredState(EntityPlayer player, ItemStack blade) {
        if(player.world.isRemote)
           return;

        boolean currentState = isEmpowered(blade);

        boolean newState;

        if(canUse(blade, player)){
            newState = currentState;

            if(player.getHeldItemMainhand() == blade){
                int imputState = player.getEntityData().getByte("SB.MCS");
                final int imputStateMask = MessageMoveCommandState.CAMERA + MessageMoveCommandState.STYLE;

                if(imputStateMask == (imputState & imputStateMask))
                    newState = MessageMoveCommandState.SNEAK != (imputState & MessageMoveCommandState.SNEAK);
            }

        }else{
            newState = false;
        }

        newState = setEmpoweredState(player, blade, newState);

        if(currentState == newState)
            return;

        if(newState){
            player.world.playSound((EntityPlayer) null,
                    player.prevPosX,
                    player.prevPosY,
                    player.prevPosZ, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundCategory.NEUTRAL, 1.0F, 1.2F);
        }
        player.world.playSound((EntityPlayer) null,
                player.prevPosX,
                player.prevPosY,
                player.prevPosZ, SoundEvents.UI_BUTTON_CLICK, SoundCategory.NEUTRAL, 0.25F, 2.0F);

    }

    @Override
    public boolean canCopy(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRemoval(ItemStack stack) {
        return false;
    }
}
