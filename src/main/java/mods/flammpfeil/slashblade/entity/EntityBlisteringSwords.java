package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Furia on 2016/05/09.
 */
public class EntityBlisteringSwords extends EntityPhantomSwordBase {
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

            faceEntityStandby();

            this.setDriveVector(1.75f,true);

            mountEntity(getThrower());
        }
    }
    private static final int HAS_FIRED = 12;
    private static final int IS_JUDGMENT = 13;

    @Override
    protected void entityInit() {
        super.entityInit();

        this.getDataWatcher().addObject(HAS_FIRED, (byte)0);
        this.getDataWatcher().addObject(IS_JUDGMENT, (byte)0);
    }

    public boolean hasFired(){
        return this.getDataWatcher().getWatchableObjectByte(HAS_FIRED) != 0;
    }
    public void setHasFired(boolean hasFired){
        this.getDataWatcher().updateObject(HAS_FIRED,(byte)(hasFired ? 1 : 0));
    }
    public boolean isJudgement(){
        return this.getDataWatcher().getWatchableObjectByte(IS_JUDGMENT) != 0;
    }
    public void setIsJudgement(boolean isJudgement){
        this.getDataWatcher().updateObject(IS_JUDGMENT,(byte)(isJudgement ? 1 : 0));
    }

    public Vec3 getLook(Entity target)
    {
        float f1;
        float f2;
        float f3;
        float f4;

        f1 = MathHelper.cos(-target.rotationYaw * 0.017453292F - (float)Math.PI);
        f2 = MathHelper.sin(-target.rotationYaw * 0.017453292F - (float)Math.PI);
        f3 = -MathHelper.cos(-target.rotationPitch * 0.017453292F);
        f4 = MathHelper.sin(-target.rotationPitch * 0.017453292F);
        return Vec3.createVectorHelper((double)(f2 * f3), (double)f4, (double)(f1 * f3));
    }

    @Override
    public void updateRidden() {

        if(hasFired()) {
            if(ridingEntity2 == thrower){
                Vec3 vec = getLook(this.ridingEntity2);
                this.ridingEntity2 = null;
                this.ticksExisted = 0;


                if(worldObj.isRemote && vec != null) {
                    vec = vec.normalize();
                    vec.xCoord *= -0.25;
                    vec.yCoord *= -0.25;
                    vec.zCoord *= -0.25;
                    vec = vec.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
                    this.setPosition(vec.xCoord,
                            vec.yCoord,
                            vec.zCoord);
                }
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

        if(!worldObj.isRemote){
            long holdlimit = ridingEntity.getEntityData().getLong("SB.BSHOLDLIMIT") + getInterval();
            long currentTime = worldObj.getTotalWorldTime();
            if(holdlimit < currentTime) {
                setHasFired(true);

                this.worldObj.updateEntityWithOptionalForce(this,true);

                this.ticksExisted = 0;

                this.worldObj.playSoundEffect(this.prevPosX, this.prevPosY, this.prevPosZ, "mob.enderdragon.wings", 0.35F, 0.2F);

                return;
            }
        }

        faceEntityStandby();

        doTargeting();
    }

    @Override
    protected void initRotation() {
        super.initRotation();

        if(ridingEntity2 == null && !hasFired() && getThrower() != null){
            if(worldObj.isRemote)
                mountEntity(getThrower());
        }
    }

    @Override
    public void spawnParticle() {
        super.spawnParticle();

        if(hasFired()){
            float trailLength;
            for (int l = 0; l < 4; ++l)
            {
                trailLength = 0.25F;
                this.worldObj.spawnParticle("portal"
                        , this.posX - this.motionX * (double)trailLength
                        , this.posY - this.motionY * (double)trailLength
                        , this.posZ - this.motionZ * (double)trailLength
                        , this.motionX, this.motionY, this.motionZ);
            }
        }
    }

    @Override
    protected boolean onImpact(MovingObjectPosition mop) {
        boolean result = false;

        if (mop.entityHit != null){
            Entity target = mop.entityHit;

            if(mop.hitInfo.equals(ItemSlashBlade.DestructableSelector)) {
                destructEntity(target);
                return true;
            }

            if(getTargetEntityId() != 0 && target.getEntityId() != getTargetEntityId())
                return result;

            if(mop.hitInfo.equals(ItemSlashBlade.AttackableSelector)){
                attackEntity(target);
                result = true;
            }
        }else{
            if(ticksExisted < 10)
                return false;
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

            if(getThrower() != null)
                faceEntityV(getThrower(),10,10);
            setDriveVector(1.75f, false);
        }

        return true;
    }

    @Override
    protected void attackEntity(Entity target) {
        if(target instanceof EntityLivingBase && isJudgement()){
            float health = ((EntityLivingBase) target).getHealth();
            if(0 < health){
                this.AttackLevel /= 2;
                health = Math.max(1,health - this.AttackLevel);
                ((EntityLivingBase) target).setHealth(health);
            }
        }
        super.attackEntity(target);
    }

    private void faceEntityStandby(){

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

        Vec3 vec = getThrower().getLookVec();

        if(vec != null){
            x -= vec.xCoord;
            y -= vec.yCoord;
            z -= vec.zCoord;
        }

        //■初期位置・初期角度等の設定
        if(!worldObj.isRemote)
            setPosition(getThrower().posX + x,
                getThrower().posY + getThrower().getEyeHeight() + y,
                getThrower().posZ + z);
        else
            setPosition(getThrower().posX + x,
                    getThrower().posY + /*-getThrower().height/2.0f + */ y,
                    getThrower().posZ + z);

        setRotation(-getThrower().rotationYaw,
                -getThrower().rotationPitch);
    }


    public void faceEntityV(Entity viewer, float yawStep, float pitchStep)
    {
        Vec3 lookVec = getLook(viewer);

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
