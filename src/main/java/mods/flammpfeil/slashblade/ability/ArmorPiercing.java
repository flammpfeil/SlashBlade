package mods.flammpfeil.slashblade.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by Furia on 2018/03/10.
 */
public class ArmorPiercing {
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


    static public float doAPAttack(Entity entity, float amount){
        float result = 0;

        if(entity == null) return result;
        if(!(entity instanceof EntityLivingBase)) return result;
        if(!entity.isEntityAlive()) return result;
        if(amount < 0) return result;

        amount = amount * getFactor();
        amount = Math.max(1.0f, Math.min(amount, getLimit()));

        EntityLivingBase target = (EntityLivingBase)entity;

        float health = ( target).getHealth();
        if(0 < health){
            float postHealth = Math.max(1,health - amount);
            target.setHealth(postHealth);

            result = health - postHealth;
        }
        return result;
    }
}
