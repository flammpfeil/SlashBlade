package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Created by Furia on 2016/05/24.
 */
public class AttackLimitter {
    public boolean enabled = false;

    public void loadConfig(Configuration config){
        try{
            config.load();

            Property prop = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "enabledAttackAttenuation" , true);
            enabled = prop.getBoolean();
        }finally {
            config.save();
        }
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        if(!enabled)
            return;

        if (!(event.source.getEntity() instanceof EntityLivingBase))
            return;

        if(!(event.source instanceof EntityDamageSource))
            return;

        EntityLivingBase attacker = (EntityLivingBase) event.source.getEntity();

        ItemStack stack = attacker.getHeldItem();
        if (stack == null)
            return;

        if (!(stack.getItem() instanceof ItemSlashBlade))
            return;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);


        final String LAST_ATTACK_TIME = "SB.LastAttackTime";
        long last = attacker.getEntityData().getLong(LAST_ATTACK_TIME);
        long now = attacker.worldObj.getTotalWorldTime();

        long actionTime = ItemSlashBlade.LastActionTime.get(tag);
        attacker.getEntityData().setLong(LAST_ATTACK_TIME,actionTime);

        long dist = now - last;

        float chargeTicks = 13;

        float charge = (dist / chargeTicks);
        charge = Math.max(0, charge);
        if (1.0f < charge)
            return;

        charge = charge * 0.8f + 0.2f;
        event.ammount *= charge;
    }
}
