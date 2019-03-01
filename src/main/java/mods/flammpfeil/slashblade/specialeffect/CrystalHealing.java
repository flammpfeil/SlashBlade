package mods.flammpfeil.slashblade.specialeffect;

import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
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

        if(!SpecialEffects.isPlayer(event.getEntityLiving())) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        if(player.world.isRemote) return;
        if((player.world.getGameTime() & 0xF) != 0xF) return; //16tick cicle

        ItemStack blade = player.getHeldItem(EnumHand.MAIN_HAND);
        if(!SpecialEffects.isBlade(blade)) return;

        switch (SpecialEffects.isEffective(player,blade,this)){
            case Effective:
                break;
            default:
                return;
        }

        if(player.getActiveItemStack().isEmpty()) return;
        if(player.getItemInUseMaxCount() < ItemSlashBlade.RequiredChargeTick) return;

        boolean hasBeaconEffect = false;
        for(Object current : player.getActivePotionEffects()){
            PotionEffect effect = (PotionEffect)current;

            if(effect.getIsAmbient()){
                hasBeaconEffect = true;
                break;
            }
        }

        if(!hasBeaconEffect) return;

        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION,8,2));

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
