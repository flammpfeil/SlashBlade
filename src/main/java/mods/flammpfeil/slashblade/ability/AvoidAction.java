package mods.flammpfeil.slashblade.ability;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.NetworkManager;
import mods.flammpfeil.slashblade.network.MessageSpecialAction;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Furia on 15/05/19.
 */
public class AvoidAction {
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;

        ItemStack stack = target.getHeldItem(EnumHand.MAIN_HAND);
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;


        UntouchableTime.setUntouchableTime(target,2,true);
    }

    @SideOnly(Side.CLIENT)
    public static void doAvoid() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        long now = mc.thePlayer.worldObj.getTotalWorldTime();

        long interval = 2;

        long timeout = mc.thePlayer.getEntityData().getLong("SB.AvoidTimeout");

        if(interval < Math.abs(timeout - now)){

            //avoid combo limit
            {
                long comboInterval = 10;
                int comboLimit = 3;


                int combo = mc.thePlayer.getEntityData().getInteger("SB.AvoidCombo");
                long comboTimeout = mc.thePlayer.getEntityData().getLong("SB.AvoidComboTimeout");

                if(comboInterval < Math.abs(comboTimeout - now)){
                    combo = 0;
                }

                if(comboLimit <= combo){
                    return;
                }else{
                    combo++;
                    mc.thePlayer.getEntityData().setInteger("SB.AvoidCombo",combo);
                    mc.thePlayer.getEntityData().setLong("SB.AvoidComboTimeout",now+comboInterval);
                }
            }

            float speedFactor;
            if(mc.thePlayer.isSneaking())
                speedFactor = 2.8f;
            else
                speedFactor = 0.8f;

            mc.thePlayer.getEntityData().setLong("SB.AvoidTimeout",now + interval);

            player.playSound(SoundEvents.entity_generic_extinguish_fire, 0.7F, 1.6F + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.4F);
            mc.thePlayer.moveFlying(mc.thePlayer.moveStrafing,mc.thePlayer.moveForward,speedFactor);
            mc.playerController.updateController();
            NetworkManager.INSTANCE.sendToServer(new MessageSpecialAction((byte) 2));
        }
    }
}
