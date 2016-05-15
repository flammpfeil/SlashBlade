package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by Furia on 2016/05/09.
 */
public class EntityBlisteringSwords extends EntitySummonedSwordBase {
    public EntityBlisteringSwords(World par1World) {
        super(par1World);

        setInterval(0);
    }

    public EntityBlisteringSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    public EntityBlisteringSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll, int interval) {
        super(par1World, entityLiving, AttackLevel, roll);

        {
            interval = Math.max(0,Math.min(interval,7));
            setInterval(interval);

            float dist = 1.0f;

            //side, height, width
            int[][] pattern = {
                    {1,1,0},
                    {-1,1,0},
                    {1,0,1},
                    {-1,0,1},
                    {1,-1,2},
                    {-1,-1,2},
                    {1,-2,3},
                    {-1,-2,3}
            };

            double yaw =  Math.toRadians(-getThrower().rotationYaw + 90 * pattern[interval][0]);


            double x = Math.sin(yaw);
            double y = pattern[interval][1] * 0.25f;
            double z = Math.cos(yaw);

            x*=(dist + 0.1 * pattern[interval][2]);
            //y*=dist;
            z*=(dist + 0.1 * pattern[interval][2]);

            Vec3d vec = getThrower().getLookVec();

            if(vec != null){
                x -= vec.xCoord;
                y -= vec.yCoord;
                z -= vec.zCoord;
            }

            //■初期位置・初期角度等の設定
            setLocationAndAngles(getThrower().posX + x,
                    getThrower().posY + getThrower().getEyeHeight()/2.0f + y,
                    getThrower().posZ + z,
                    getThrower().rotationYaw,
                    getThrower().rotationPitch);

            setRotation( getThrower().rotationYaw, getThrower().rotationPitch);

            this.setDriveVector(1.75f,true);

            mountEntity(getThrower());
        }
    }
    private static final DataParameter<Boolean> HAS_FIRED = EntityDataManager.<Boolean>createKey(EntityBlisteringSwords.class, DataSerializers.BOOLEAN);

    @Override
    protected void entityInit() {
        super.entityInit();

        this.getDataManager().register(HAS_FIRED, false);
    }

    public boolean hasFired(){
        return this.getDataManager().get(HAS_FIRED);
    }
    public void setHasFired(boolean hasFired){
        this.getDataManager().set(HAS_FIRED,hasFired);
    }

    @Override
    public void updateRidden() {

        if(hasFired()) {
            if(ridingEntity2 == thrower){
                Vec3d vec = ridingEntity2.getLookVec();
                this.ridingEntity2 = null;
                this.ticksExisted = 0;

                if(vec != null)
                    this.setPosition(this.posX - vec.xCoord,
                            this.posY - vec.yCoord,
                            this.posZ - vec.zCoord);
            }else {
                super.updateRidden();
            }
            return;
        }

        Entity ridingEntity = this.ridingEntity2;

        if(ridingEntity.isDead){
            this.setDead();
            return;
        }

        if(!getEntityWorld().isRemote){
            long holdlimit = ridingEntity.getEntityData().getLong("SB.BSHOLDLIMIT") + getInterval();
            long currentTime = getEntityWorld().getTotalWorldTime();
            if(holdlimit < currentTime) {
                setHasFired(true);
                this.ticksExisted = 0;
                return;
            }
        }

        int interval = Math.max(0,Math.min(getInterval(),7));

        float dist = 0.8f;

        //side, height, width
        int[][] pattern = {
                {1,1,0},
                {-1,1,0},
                {1,0,1},
                {-1,0,1},
                {1,-1,2},
                {-1,-1,2},
                {1,-2,3},
                {-1,-2,3}
        };

        double yaw =  Math.toRadians(-getThrower().rotationYaw + 90 * pattern[interval][0]);


        double x = Math.sin(yaw);
        double y = pattern[interval][1] * 0.25f;
        double z = Math.cos(yaw);

        x*=(dist + 0.15 * pattern[interval][2]);
        //y*=dist;
        z*=(dist + 0.15 * pattern[interval][2]);

        Vec3d vec = getThrower().getLookVec();

        if(vec != null){
            x -= vec.xCoord;
            y -= vec.yCoord;
            z -= vec.zCoord;
        }

        //■初期位置・初期角度等の設定
        setPosition(getThrower().posX + x,
                getThrower().posY + getThrower().getEyeHeight()/2.0f + y,
                getThrower().posZ + z);
        setRotation(-getThrower().rotationYaw,
                -getThrower().rotationPitch);

        doTargeting();
    }

    @Override
    protected void initRotation() {
        super.initRotation();

        if(ridingEntity2 == null && !hasFired() && getThrower() != null){
            if(getEntityWorld().isRemote)
                mountEntity(getThrower());
        }
    }

    @Override
    protected boolean onImpact(RayTraceResult mop) {
        boolean result = false;

        if (mop.entityHit != null){
            Entity target = mop.entityHit;

            if(mop.hitInfo.equals(EntitySelectorDestructable.getInstance())) {
                destructEntity(target);
                return true;
            }

            if(getTargetEntityId() != 0 && target.getEntityId() != getTargetEntityId())
                return result;

            if(mop.hitInfo.equals(EntitySelectorAttackable.getInstance())){
                attackEntity(target);
                result = true;
            }
        }else{
            if(ticksExisted < 10)
                return false;
            if(!worldObj.getCubes(this,this.getEntityBoundingBox()).isEmpty())
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
    public boolean doTargeting() {
        //boolean result = super.doTargeting();

        int targetid = this.getTargetEntityId();

        if(targetid != 0){
            Entity target = worldObj.getEntityByID(targetid);

            if(target != null){

                if(Float.isNaN(iniPitch) && getThrower() != null){
                    iniYaw = getThrower().rotationYaw;
                    iniPitch = getThrower().rotationPitch;
                }

                faceEntity(this,target, 10.0f, 10.0f);

                setDriveVector(1.75f, false);
            }
        }else if(!hasFired()){
            if(Float.isNaN(iniPitch) && getThrower() != null){
                iniYaw = getThrower().rotationYaw;
                iniPitch = getThrower().rotationPitch;
            }
            faceEntityV(getThrower(),10,10);
            setDriveVector(1.75f, false);
        }

        return true;
    }



    public void faceEntityV(Entity viewer, float yawStep, float pitchStep)
    {
        Vec3d lookVec = viewer.getLookVec();

        if(lookVec == null) return;

        double d0 = lookVec.xCoord; //target.posX - viewer.posX;
        double d1 = lookVec.zCoord; //target.posZ - viewer.posZ;
        double d2 = lookVec.yCoord;

        /*
        if (target instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)target;
            d2 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (viewer.posY + (double)viewer.getEyeHeight());
        }
        else
        {
            AxisAlignedBB boundingBox = target.getEntityBoundingBox();
            d2 = (boundingBox.minY + boundingBox.maxY) / 2.0D - (viewer.posY + (double)viewer.getEyeHeight());
        }
        */

        double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));


        iniPitch = this.updateRotation(iniPitch, f3, pitchStep);
        iniYaw = this.updateRotation(iniYaw, f2, yawStep);


    }
    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }

}
