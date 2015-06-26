package mods.flammpfeil.slashblade.specialeffect;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/06/21.
 */
public class CrystalHealing implements ISpecialEffect {
    static final String EffectKey = "CrystalHealing";

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){

        if(!SpecialEffects.isPlayer(event.entityLiving)) return;
        EntityPlayer player = (EntityPlayer)event.entityLiving;

        if(player.worldObj.isRemote) return;
        if((player.worldObj.getTotalWorldTime() & 0xF) != 0xF) return; //16tick cicle

        ItemStack blade = player.getHeldItem();
        if(!SpecialEffects.isBlade(blade)) return;

        switch (SpecialEffects.isEffective(player,blade,this)){
            case Effective:
                break;
            default:
                return;
        }

        if(!player.isUsingItem()) return;
        if(player.getItemInUseDuration() < ItemSlashBlade.RequiredChargeTick) return;

        boolean hasBeaconEffect = false;
        for(Object current : player.getActivePotionEffects()){
            PotionEffect effect = (PotionEffect)current;

            if(effect.getIsAmbient()){
                hasBeaconEffect = true;
                break;
            }
        }

        if(!hasBeaconEffect) return;

        player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(),8,2));

    }

    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getDefaultRequiredLevel() {
        return 30;
    }

    @Override
    public String getEffectKey() {
        return EffectKey;
    }
}
