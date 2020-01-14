package mods.flammpfeil.slashblade.specialeffect;

import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

/**
 * Created by Furia on 15/06/19.
 */
public class WitherEdge implements ISpecialEffect, IRemovable{
    private static final String EffectKey = "WitherEdge";

    private boolean useBlade(ItemSlashBlade.ComboSequence sequence){
        if(sequence.useScabbard) return false;
        if(sequence == ItemSlashBlade.ComboSequence.None) return false;
        if(sequence == ItemSlashBlade.ComboSequence.Noutou) return false;
        return true;
    }

    @SubscribeEvent
    public void onImpactEffectEvent(SlashBladeEvent.ImpactEffectEvent event){

        if(!useBlade(event.sequence)) return;

        if(!SpecialEffects.isPlayer(event.user)) return;
        EntityPlayer player = (EntityPlayer) event.user;

        switch (SpecialEffects.isEffective(player, event.blade, this)){
            case None:
                return;
            case Effective:
                if(event.target.getRNG().nextInt(2) != 0) return;
                break;
            case NonEffective:
                if(event.target.getRNG().nextInt(5) != 0) return;
                break;
        }

        event.target.addPotionEffect(new PotionEffect(MobEffects.WITHER,20 * 5,1));
        player.onEnchantmentCritical(event.target);

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

        player.addPotionEffect(new PotionEffect(MobEffects.WITHER,20 * 5,1));

    }

    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getDefaultRequiredLevel() {
        return 20;
    }

    @Override
    public String getEffectKey() {
        return EffectKey;
    }

    @Override
    public boolean canCopy(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canRemoval(ItemStack stack) {
        if(stack.getUnlocalizedName().equals("item.flammpfeil.slashblade.named.koseki"))
            return false;
        return true;
    }
}
