package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Furia on 2016/05/09.
 */
public class EntitySummonedBlade extends EntityPhantomSwordBase {
    public long hitTime = 0;
    public float hitStopFactor = 0;

    public EntitySummonedBlade(World par1World) {
        super(par1World);

        setInterval(0);

        hitStopFactor = rand.nextFloat();
    }

    public EntitySummonedBlade(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    public EntitySummonedBlade(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll) {
        super(par1World, entityLiving, AttackLevel, roll);

        {
            float dist = 1.0f;

            int dirFactor = rand.nextInt(6);

            float rotBase = 30.0f;
            float[] rolls = {180.0f+rotBase, -180, 180-rotBase,-rotBase, 0 ,rotBase};

            setRoll(rolls[dirFactor]);

            int[][] pattern = {
                    {1,1},
                    {1,0},
                    {1,-1},
                    {-1,1},
                    {-1,0},
                    {-1,-1}
            };

            double yaw =  Math.toRadians(-thrower.rotationYaw + 90 * pattern[dirFactor][0]);



            double x = Math.sin(yaw);
            double y = pattern[dirFactor][1] * 0.5f;
            double z = Math.cos(yaw);

            x*=dist;
            y*=dist;
            z*=dist;

            Vec3 vec = thrower.getLookVec();

            if(vec != null){
                x -= vec.xCoord;
                y -= vec.yCoord;
                z -= vec.zCoord;
            }

            //■初期位置・初期角度等の設定
            setLocationAndAngles(thrower.posX + x,
                    thrower.posY + /*thrower.getEyeHeight()/2.0f + */y,
                    thrower.posZ + z,
                    thrower.rotationYaw,
                    thrower.rotationPitch);

            iniYaw = thrower.rotationYaw;
            iniPitch = thrower.rotationPitch;

            setDriveVector(1.75f);
        }
    }

    @Override
    protected boolean onImpact(MovingObjectPosition mop) {
        boolean result = true;

        if (mop.entityHit != null){
            Entity target = mop.entityHit;

            if(mop.hitInfo.equals(ItemSlashBlade.AttackableSelector)){

                attackEntity(target);

            }else{ //(mop.hitInfo.equals(ItemSlashBlade.getInstance)){

                destructEntity(target);
            }


            hitTime = this.worldObj.getTotalWorldTime();
        }else{
            result = false;
        }

        return result;
    }

    @Override
    public void calculateSpeed() {
        //super.calculateSpeed();
    }

    @Override
    public boolean doTargeting() {
        //boolean result = super.doTargeting();

        int targetid = this.getTargetEntityId();
        if(targetid == 0){
            double expandFactor = 15;
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this
                    , this.boundingBox.expand(expandFactor, expandFactor, expandFactor));
            list.removeAll(alreadyHitEntity);

            double tmpDistance = 15;

            Entity pointedEntity = null;

            EntityLivingBase viewer = null;
            if(getThrower() != null && getThrower() instanceof EntityLivingBase)
                viewer = (EntityLivingBase)getThrower();

            if(viewer != null) {
                for (Entity entity : list) {
                    if (entity == null || !entity.canBeCollidedWith())
                        continue;

                    if (!ItemSlashBlade.AttackableSelector.isEntityApplicable(entity))
                        continue;

                    if(!viewer.canEntityBeSeen(entity))
                        continue;

                    double d3 = this.getDistanceToEntity(entity);

                    if (d3 < tmpDistance || tmpDistance == 0.0D) {
                        if (entity == this.getRidingEntity() && !entity.canRiderInteract()) {
                            if (tmpDistance == 0.0D) {
                                pointedEntity = entity;
                            }
                        } else {
                            pointedEntity = entity;
                            tmpDistance = d3;
                        }
                    }
                }
            }


            if(pointedEntity != null)
                this.setTargetEntityId( pointedEntity.getEntityId());
        }

        if(targetid != 0 && getInterval() < this.ticksExisted ){
            Entity target = worldObj.getEntityByID(targetid);

            if(target != null){

                if(Float.isNaN(iniPitch) && thrower != null){
                    iniYaw = thrower.rotationYaw;
                    iniPitch = thrower.rotationPitch;
                }

                float lastYaw = iniYaw;
                float lastPitch = iniPitch;

                faceEntity(this,target, 10.0f, 10.0f);

                float lastSpeed = (float)(Vec3.createVectorHelper(this.motionX, this.motionY, this.motionZ)).lengthVector();

                float speedFactor = Math.abs(iniYaw - lastYaw) / 10f + Math.abs(iniPitch - lastPitch)/10f;
                speedFactor = 1.0f - Math.min(speedFactor, 0.75f);
                speedFactor = ((0.75f * speedFactor) + lastSpeed * 9f) / 10.0f;

                setDriveVector(speedFactor, false);
            }
        }

        return true;
    }
}
