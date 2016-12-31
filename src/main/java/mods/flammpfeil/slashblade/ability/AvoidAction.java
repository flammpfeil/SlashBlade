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
        EntityLivingBase target = event.getEntityLiving();
        if(target == null) return;

        ItemStack stack = target.getHeldItem(EnumHand.MAIN_HAND);
        if(stack.isEmpty()) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;


        UntouchableTime.setUntouchableTime(target,2,true);
    }

    @SideOnly(Side.CLIENT)
    public static void doAvoid() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        long now = mc.player.world.getTotalWorldTime();

        long interval = 2;

        long timeout = mc.player.getEntityData().getLong("SB.AvoidTimeout");

        if(interval < Math.abs(timeout - now)){

            //avoid combo limit
            {
                long comboInterval = 10;
                int comboLimit = 3;


                int combo = mc.player.getEntityData().getInteger("SB.AvoidCombo");
                long comboTimeout = mc.player.getEntityData().getLong("SB.AvoidComboTimeout");

                if(comboInterval < Math.abs(comboTimeout - now)){
                    combo = 0;
                }

                if(comboLimit <= combo){
                    return;
                }else{
                    combo++;
                    mc.player.getEntityData().setInteger("SB.AvoidCombo",combo);
                    mc.player.getEntityData().setLong("SB.AvoidComboTimeout",now+comboInterval);
                }
            }

            float speedFactor;
            if(mc.player.isSneaking())
                speedFactor = 2.8f;
            else
                speedFactor = 0.8f;

            mc.player.getEntityData().setLong("SB.AvoidTimeout",now + interval);

            player.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.4F);
            mc.player.moveRelative(mc.player.moveStrafing,mc.player.moveForward,speedFactor);
            mc.playerController.updateController();
            NetworkManager.INSTANCE.sendToServer(new MessageSpecialAction((byte) 2));
        }
    }
}
