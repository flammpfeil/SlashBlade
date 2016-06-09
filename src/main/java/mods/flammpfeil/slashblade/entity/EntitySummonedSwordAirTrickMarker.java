package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.AirTrick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Furia on 2016/05/09.
 */
public class EntitySummonedSwordAirTrickMarker extends EntityPhantomSwordBase {

    public EntitySummonedSwordAirTrickMarker(World par1World) {
        super(par1World);

        setInterval(0);
    }

    public EntitySummonedSwordAirTrickMarker(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    public EntitySummonedSwordAirTrickMarker(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll) {
        super(par1World, entityLiving, AttackLevel, roll);

        {
            Vec3 eyeDir = thrower.getLookVec();

            this.setLocationAndAngles(
                    thrower.posX + eyeDir.xCoord * 2,
                    thrower.posY + eyeDir.yCoord * 2 + thrower.getEyeHeight(),
                    thrower.posZ + eyeDir.zCoord * 2,
                    thrower.rotationYaw,
                    thrower.rotationPitch);

            this.setDriveVector(1.75f,true);
        }
    }

    @Override
    protected boolean onImpact(MovingObjectPosition mop) {
        boolean result = false;

        if (mop.entityHit != null){
            Entity target = mop.entityHit;

            if(getTargetEntityId() != 0 && target.getEntityId() != getTargetEntityId())
                return result;

            if(mop.hitInfo.equals(ItemSlashBlade.AttackableSelector)){
                attackEntity(target);
                result = true;
            }
        }else{
            if(!worldObj.getCollidingBoundingBoxes(this,this.boundingBox).isEmpty())
            {
                if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer)
                    ((EntityPlayer)this.getThrower()).onCriticalHit(this);
                this.setDead();
                result = true;
            }
        }

        return result;
    }

    @Override
    public void mountEntity(Entity par1Entity) {
        super.mountEntity(par1Entity);

        if(!this.worldObj.isRemote){
            if(this.getThrower() instanceof EntityPlayerMP)
                AirTrick.doAirTrick((EntityPlayerMP)this.getThrower());
        }
    }

    @Override
    public boolean doTargeting() {
        //boolean result = super.doTargeting();

        int targetid = this.getTargetEntityId();

        if(targetid != 0 && getInterval() < this.ticksExisted ){
            Entity target = worldObj.getEntityByID(targetid);

            if(target != null){

                if(Float.isNaN(iniPitch) && thrower != null){
                    iniYaw = thrower.rotationYaw;
                    iniPitch = thrower.rotationPitch;
                }

                faceEntity(this,target, 90f, 90.0f);

                double vec = 1.1 * (Vec3.createVectorHelper(this.motionX,this.motionY,this.motionZ)).lengthVector();

                setDriveVector((float)vec, false);
            }
        }

        return true;
    }
}
