package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Furia on 2018/03/09.
 */
public class DamageLimitter {
    static float factor = 1.0f;
    static float limit = -1.0f;

    public static void setLimit(float limit) {
        DamageLimitter.limit = limit < 0 ? Float.MAX_VALUE : Math.max(1.0f , limit);
    }
    public static float getLimit() {
        return limit;
    }

    public static void setFactor(float factor) {
        DamageLimitter.factor = Math.max(0.01f, Math.min(factor, 10.0f));
    }

    public static float getFactor() {
        return factor;
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {

        EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();

        if(attacker == null)
            return;
        if (!(attacker instanceof EntityLivingBase))
            return;

        ItemStack stack = attacker.getHeldItem(EnumHand.MAIN_HAND);
        if (stack.isEmpty())
            return;

        if (!(stack.getItem() instanceof ItemSlashBlade))
            return;

        float amount = event.getAmount();

        if(amount <= 0)
            return;

        amount = amount * getFactor();
        amount = Math.max(1.0f, Math.min(amount, getLimit()));

        event.setAmount(amount);
    }
}
