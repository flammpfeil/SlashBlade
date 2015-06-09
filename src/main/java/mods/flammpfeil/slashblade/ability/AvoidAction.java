package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.PacketHandler;
import mods.flammpfeil.slashblade.network.MessageSpecialAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

        ItemStack stack = target.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;


        UntouchableTime.setUntouchableTime(target,2,true);
    }

    @SideOnly(Side.CLIENT)
    public static void doAvoid() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

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

            player.playSound("random.fizz", 0.3F, 10.0F);
            mc.thePlayer.moveFlying(mc.thePlayer.moveStrafing,mc.thePlayer.moveForward,speedFactor);
            mc.playerController.updateController();
            PacketHandler.INSTANCE.sendToServer(new MessageSpecialAction((byte) 2));
        }
    }
}
