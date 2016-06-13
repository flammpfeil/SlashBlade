package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

/**
 * Created by Furia on 2016/05/09.
 */
public class EntitySpiralSwords extends EntityPhantomSwordBase {
    public EntitySpiralSwords(World par1World) {
        super(par1World);

        setInterval(0);
    }

    public EntitySpiralSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    public EntitySpiralSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll, float rotOffset) {
        super(par1World, entityLiving, AttackLevel, roll);

        {
            setRotOffset(rotOffset);

            faceEntityStandby();

            this.setDriveVector(1.0f,true);

            mountEntity(getThrower());
        }
    }
    private static final int HAS_FIRED  = 12;
    private static final int ROT_OFFSET = 13;
    private static final int HOLDID     = 14;
    private static final int ROT_PITCH = 15;
    private static final int ROT_YAW = 16;
    private static final int ROT_TICKS = 17;

    @Override
    protected void entityInit() {
        super.entityInit();

        this.getDataWatcher().addObject(HAS_FIRED, (byte)0);

        this.getDataWatcher().addObject(ROT_OFFSET, 0.0f);

        this.getDataWatcher().addObject(HOLDID, 0);

        this.getDataWatcher().addObject(ROT_PITCH, 0.0f);
        this.getDataWatcher().addObject(ROT_YAW, 0.0f);

        this.getDataWatcher().addObject(ROT_TICKS, 30);

    }

    public boolean hasFired(){
        return this.getDataWatcher().getWatchableObjectByte(HAS_FIRED) != 0;
    }
    public void setHasFired(boolean hasFired){
        this.getDataWatcher().updateObject(HAS_FIRED,(byte)(hasFired ? 1 : 0));
    }

    public float getRotOffset(){
        return this.getDataWatcher().getWatchableObjectFloat(ROT_OFFSET);
    }
    public void setRotOffset(float rotOffset){
        this.getDataWatcher().updateObject(ROT_OFFSET,rotOffset);
    }

    public int getHoldId(){
        return this.getDataWatcher().getWatchableObjectInt(HOLDID);
    }
    public void setHoldId(int id){
        this.getDataWatcher().updateObject(HOLDID,id);
    }

    public int getRotTicks(){
        return this.getDataWatcher().getWatchableObjectInt(ROT_TICKS);
    }
    public void setRotTicks(int ticks){
        this.getDataWatcher().updateObject(ROT_TICKS,ticks);
    }

    public float getRotPitch(){
        return this.getDataWatcher().getWatchableObjectFloat(ROT_PITCH);
    }
    public void setRotPitch(float rotPitch){
        this.getDataWatcher().updateObject(ROT_PITCH,rotPitch);
    }

    public float getRotYaw(){
        return this.getDataWatcher().getWatchableObjectFloat(ROT_YAW);
    }
    public void setRotYaw(float rotYaw){
        this.getDataWatcher().updateObject(ROT_YAW,rotYaw);
    }

    static final int waitTime = 7;

    @Override
    public void updateRidden() {

        if(getLifeTime() < this.ticksExisted){
            setInvisible(true);
            isDead = true;
        }

        if(hasFired()) {
            if (ridingEntity2 == thrower) {

                this.ridingEntity2 = null;
                //this.ticksExisted = 0;

                /*
                Vec3d vec = this.getLook(1.0f);
                vec = vec.normalize().scale(-0.25).add(getPositionVector());
                this.setPosition(vec.xCoord, vec.yCoord, vec.zCoord);
                */
            } else {
                super.updateRidden();
            }
            return;
        }

        Entity ridingEntity = this.ridingEntity2;

        faceEntityStandby();

        if(ridingEntity.isDead){
            setInterval(ticksExisted + waitTime);
            setLifeTime(ticksExisted + 30);
            setHasFired(true);
            this.worldObj.updateEntityWithOptionalForce(this,true);
            return;
        }

        if(!worldObj.isRemote){
            long holdid = ridingEntity.getEntityData().getLong("SB.SPHOLDID");
            if(holdid != getHoldId()){
                setInterval(ticksExisted + waitTime);
                setLifeTime(ticksExisted + 30);

                this.worldObj.playSoundEffect(this.prevPosX, this.prevPosY, this.prevPosZ, "mob.enderdragon.wings", 0.35F, 0.2F);

                setHasFired(true);
                this.worldObj.updateEntityWithOptionalForce(this,true);
                return;
            }else if(getInterval() < ticksExisted) {
                setHasFired(true);

                this.worldObj.updateEntityWithOptionalForce(this, true);

                //this.ticksExisted = 0;
                return;
            }
        }

        setMotionVector(1.0f, false);

        MovingObjectPosition movingobjectposition = getMovingObjectPosition();

        if (movingobjectposition != null)
        {
            if(onImpact(movingobjectposition))
                return;
        }

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

            if(mop.hitInfo.equals(ItemSlashBlade.AttackableSelector)){
                attackEntity(target);
            }
        }else{
            if(this.ticksExisted < getInterval())
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
        return false;

        /*
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

        */
    }


    private void faceEntityStandby() {

        int ticks = this.ticksExisted;
        if ((getInterval() - waitTime) < ticks) {
            ticks = getInterval() - waitTime;
        }

        double rotParTick = 360.0 / (double)getRotTicks();
        double offset = getRotOffset();
        double degYaw = ticks * rotParTick + offset;
        double yaw = Math.toRadians(degYaw);

        /*
        Vec3 pos = Vec3.createVectorHelper(
                Math.sin(yaw),
                Math.sin(yaw + ticks / 10.0) * 0.1f,
                Math.cos(yaw));
        */

        Matrix4d rotMat = new Matrix4d();
        rotMat.setIdentity();


        {//yaw
            double thRot = 0;
            if (getThrower() != null)
                thRot = getThrower().rotationYaw;

            Matrix4d rotA = new Matrix4d();
            rotA.rotY(Math.toRadians(getRotYaw() - thRot));
            rotMat.mul(rotA);
        }
        {//ptich
            Matrix4d rotA = new Matrix4d();
            rotA.rotX(Math.toRadians(-getRotPitch()));
            rotMat.mul(rotA);
        }

        final double pitch = 7.5;
        {
            Matrix4d rotA = new Matrix4d();
            rotA.rotY(-Math.toRadians((ticks * 5.0) % 360.0));
            rotMat.mul(rotA);
        }
        {
            Matrix4d rot = new Matrix4d();
            rot.rotZ(Math.toRadians(pitch));
            rotMat.mul(rot);
        }
        {
            Matrix4d rot = new Matrix4d();
            rot.rotY(yaw);
            rotMat.mul(rot);
        }

        Vector3d vector3d = new Vector3d(0,0,1);
        rotMat.transform(vector3d);

        vector3d.normalize();
        vector3d.scale(1.5);

        Vec3 pos = Vec3.createVectorHelper(vector3d.x, vector3d.y, vector3d.z);

        if (getThrower() != null) {
            pos = pos.addVector(getThrower().posX, getThrower().posY, getThrower().posZ);
            if(!worldObj.isRemote)
                pos = pos.addVector(0, getThrower().height / 2.0, 0);
            else
                pos = pos.addVector(0, -getThrower().height / 3.0, 0);
        }

        Vector3d rot = new Vector3d();
        rotate(rotMat, rot);

        prevRotationYaw = this.rotationYaw;
        prevRotationPitch = this.rotationPitch;


        //■初期位置・初期角度等の設定
        setPosition(pos.xCoord, pos.yCoord, pos.zCoord);

        setRotation((float)Math.toDegrees(rot.y), (float)Math.toDegrees(rot.x));
//        setRotation((float) (-degYaw + (ticks * 5.0)), (float)(-(pitch) * Math.sin(yaw))/**/);
    }

    public final void rotate(Matrix4d m , Vector3d rot) {
        Vector3d unit = new Vector3d(0,0,1);

        Vector3d vector3d = new Vector3d(0,0,1);
        m.transform(vector3d);
        vector3d.normalize();

        Vector3d yawBase = new Vector3d(vector3d);
        yawBase.y = 0;

        double yaw = 0.0;
        if(0.0 != yawBase.length()){
            yawBase.normalize();
            yaw = unit.dot(yawBase);
            yaw = Math.acos(yaw);

            Vector3d cx = new Vector3d();
            cx.cross(unit, yawBase);
            yaw = Math.signum(cx.y) * yaw;

            if(Math.abs(yaw) < 0.3){
                Vector3d xUnit = new Vector3d(1,0,0);

                yaw = xUnit.dot(yawBase);
                yaw = Math.acos(yaw);

                cx.cross(xUnit, yawBase);
                yaw = Math.signum(cx.y) * yaw + (Math.PI / 2.0);
            }



            Matrix4d invYaw = new Matrix4d();
            invYaw.rotY(-yaw);
            invYaw.transform(vector3d);

            vector3d.x = 0;
            vector3d.normalize();
        }

        double pitch = unit.dot(vector3d);
        pitch = Math.acos(pitch);
        {
            Vector3d cx = new Vector3d();
            cx.cross(unit, vector3d);
            pitch = Math.signum(cx.x) * pitch;

            if(Math.abs(pitch) < 0.3){
                Vector3d yUnit = new Vector3d(0,1,0);

                pitch = yUnit.dot(vector3d);
                pitch = Math.acos(pitch);

                cx.cross(yUnit, vector3d);
                pitch = Math.signum(cx.x) * pitch - (Math.PI / 2.0);
            }

        }

        rot.y = -yaw;
        rot.x = pitch;
    }

    public void setMotionVector(float fYVecOfst,boolean init)
    {
        //■角度 -> ラジアン 変換
        float fYawDtoR = (  rotationYaw / 180F) * (float)Math.PI;
        float fPitDtoR = (rotationPitch / 180F) * (float)Math.PI;

        //■単位ベクトル
        motionX = -MathHelper.sin(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst;
        motionY = -MathHelper.sin(fPitDtoR) * fYVecOfst;
        motionZ =  MathHelper.cos(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst;

        float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / Math.PI);
        rotationPitch = (float)((Math.atan2(motionY, f3) * 180D) / Math.PI);
        if(init){
            speed = fYVecOfst;
            prevRotationYaw = rotationYaw;
            prevRotationPitch = rotationPitch;
        }
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

    int life = 2;

    @Override
    protected void attackEntity(Entity target){

        if(!this.worldObj.isRemote){

            if(ticksExisted % 3 != 0) return; //hit 4ticks cycle



            float magicDamage = Math.max(1.0f, AttackLevel);
            target.hurtResistantTime = 0;
            DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
            target.attackEntityFrom(ds, magicDamage);

            if(blade != null && target instanceof EntityLivingBase && thrower != null && thrower instanceof EntityLivingBase){
                StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.PhantomSword);
                ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)target,(EntityLivingBase)thrower);

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                //target.addVelocity(0.0, 0.5D, 0.0);

                ((EntityLivingBase) target).hurtTime = 1;

                ((ItemSlashBlade)blade.getItem()).setDaunting(((EntityLivingBase) target));
            }

            if(--life <= 0) {
                setDead();
            }
        }
    }
}
