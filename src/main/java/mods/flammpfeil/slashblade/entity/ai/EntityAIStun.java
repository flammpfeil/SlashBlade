package mods.flammpfeil.slashblade.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * Created by Furia on 15/06/20.
 */
public class EntityAIStun extends EntityAIBase {

    private EntityLivingBase owner;

    public EntityAIStun(EntityLivingBase owner){
        this.owner = owner;
        setMutexBits(0xFFFFFF);
    }

    static public final String StunTimeout = "StunTimeout";
    static public final long timeoutLimit = 200;

    @Override
    public boolean shouldExecute() {
        if(this.owner == null) return false;
        if(this.owner.worldObj == null) return false;
        long timeout = this.owner.getEntityData().getLong(StunTimeout);
        if(timeout == 0) return false;
        timeout = timeout - this.owner.worldObj.getTotalWorldTime();
        if(timeout <= 0 || timeoutLimit < timeout){
            this.owner.getEntityData().removeTag(StunTimeout);
            return false;
        }

        return true;
    }

    @Override
    public void updateTask() {
    }
}
