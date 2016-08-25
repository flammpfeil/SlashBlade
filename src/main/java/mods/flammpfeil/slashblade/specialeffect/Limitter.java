package mods.flammpfeil.slashblade.specialeffect;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 15/06/21.
 */
public class Limitter implements ISpecialEffect {
    static final String EffectKey = "Limitter";

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){

        if(!SpecialEffects.isPlayer(event.getEntityLiving())) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        if(player.worldObj.isRemote) return;
        if((player.worldObj.getTotalWorldTime() & 0xF) != 0xF) return; //16tick cicle

        ItemStack blade = player.getHeldItem(EnumHand.MAIN_HAND);
        if(!SpecialEffects.isBlade(blade)) return;

        SpecialEffects.State state = SpecialEffects.isEffective(player,blade,this);
        if(state != SpecialEffects.State.NonEffective){
            return;
        }

        player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,20,-1));
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
