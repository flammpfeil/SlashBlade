package mods.flammpfeil.slashblade.entity.ai;

import mods.flammpfeil.slashblade.capability.MobEffect.CapabilityMobEffectHandler;
import mods.flammpfeil.slashblade.capability.MobEffect.IMobEffectHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Created by Furia on 15/06/20.
 */
public class EntityAIStun extends EntityAIBase {

    private EntityLivingBase owner;

    public EntityAIStun(EntityLivingBase owner){
        this.owner = owner;
        this.setMutexBits(0xffff);
    }

    static public final String StunTimeout = "StunTimeout";
    static public final long timeoutLimit = 200;

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting();
    }

    @Override
    public boolean shouldExecute() {
        if(this.owner == null) return false;
        if(this.owner.world == null) return false;

        IMobEffectHandler mobEffect = this.owner.getCapability(CapabilityMobEffectHandler.MOB_EFFECT, null);
        if(mobEffect == null) return false;

        if(mobEffect.isStun(this.owner.world.getTotalWorldTime(), timeoutLimit)){
            return true;
        }else{
            return false;
        }

        /*
        long timeout = this.owner.getEntityData().getLong(StunTimeout);
        if(timeout == 0) return false;
        timeout = timeout - this.owner.world.getTotalWorldTime();
        if(timeout <= 0 || timeoutLimit < timeout){
            this.owner.getEntityData().removeTag(StunTimeout);
            return false;
        }

        return true;

        */
    }

    @Override
    public void updateTask() {
    }
}
