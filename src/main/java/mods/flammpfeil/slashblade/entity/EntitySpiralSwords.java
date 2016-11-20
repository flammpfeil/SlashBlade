package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.vecmath.*;

/**
 * Created by Furia on 2016/05/09.
 */
public class EntitySpiralSwords extends EntitySummonedSwordBase {
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
    private static final DataParameter<Boolean> HAS_FIRED = EntityDataManager.<Boolean>createKey(EntitySpiralSwords.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ROT_OFFSET = EntityDataManager.<Float>createKey(EntitySpiralSwords.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> HOLDID = EntityDataManager.<Integer>createKey(EntitySpiralSwords.class, DataSerializers.VARINT);
    private static final DataParameter<Float> ROT_PITCH = EntityDataManager.<Float>createKey(EntitySpiralSwords.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> ROT_YAW = EntityDataManager.<Float>createKey(EntitySpiralSwords.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> ROT_TICKS = EntityDataManager.<Integer>createKey(EntitySpiralSwords.class, DataSerializers.VARINT);

    @Override
    protected void entityInit() {
        super.entityInit();

        this.getDataManager().register(HAS_FIRED, false);

        this.getDataManager().register(ROT_OFFSET, 0.0f);

        this.getDataManager().register(HOLDID, 0);

        this.getDataManager().register(ROT_PITCH, 0.0f);
        this.getDataManager().register(ROT_YAW, 0.0f);

        this.getDataManager().register(ROT_TICKS, 30);

    }

    public boolean hasFired(){
        return this.getDataManager().get(HAS_FIRED);
    }
    public void setHasFired(boolean hasFired){
        this.getDataManager().set(HAS_FIRED,hasFired);
    }

    public float getRotOffset(){
        return this.getDataManager().get(ROT_OFFSET);
    }
    public void setRotOffset(float rotOffset){
        this.getDataManager().set(ROT_OFFSET,rotOffset);
    }

    public int getHoldId(){
        return this.getDataManager().get(HOLDID);
    }
    public void setHoldId(int id){
        this.getDataManager().set(HOLDID,id);
    }

    public int getRotTicks(){
        return this.getDataManager().get(ROT_TICKS);
    }
    public void setRotTicks(int ticks){
        this.getDataManager().set(ROT_TICKS,ticks);
    }

    public float getRotPitch(){
        return this.getDataManager().get(ROT_PITCH);
    }
    public void setRotPitch(float rotPitch){
        this.getDataManager().set(ROT_PITCH,rotPitch);
    }

    public float getRotYaw(){
        return this.getDataManager().get(ROT_YAW);
    }
    public void setRotYaw(float rotYaw){
        this.getDataManager().set(ROT_YAW,rotYaw);
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
            this.getEntityWorld().updateEntityWithOptionalForce(this,true);
            return;
        }

        if(!getEntityWorld().isRemote){
            long holdid = ridingEntity.getEntityData().getLong("SB.SPHOLDID");
            if(holdid != getHoldId()){
                setInterval(ticksExisted + waitTime);
                setLifeTime(ticksExisted + 30);

                this.worldObj.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.NEUTRAL, 0.35F, 0.2F);

                setHasFired(true);
                this.getEntityWorld().updateEntityWithOptionalForce(this,true);
                return;
            }else if(getInterval() < ticksExisted) {
                setHasFired(true);

                this.getEntityWorld().updateEntityWithOptionalForce(this, true);

                //this.ticksExisted = 0;
                return;
            }
        }

        setMotionVector(1.0f, false);

        RayTraceResult movingobjectposition = getRayTraceResult();

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
            if(getEntityWorld().isRemote)
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
                this.worldObj.spawnParticle(EnumParticleTypes.PORTAL
                        , this.posX - this.motionX * (double)trailLength
                        , this.posY - this.motionY * (double)trailLength
                        , this.posZ - this.motionZ * (double)trailLength
                        , this.motionX, this.motionY, this.motionZ);
            }
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

            if(mop.hitInfo.equals(EntitySelectorAttackable.getInstance())){
                attackEntity(target);
            }
        }else{
            if(this.ticksExisted < getInterval())
                return false;
            if(!worldObj.getCollisionBoxes(this,this.getEntityBoundingBox()).isEmpty())
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

        Vec3d pos = new Vec3d(vector3d.x, vector3d.y, vector3d.z);

        if (getThrower() != null) {
            pos = pos.add(getThrower().getPositionVector());
            pos = pos.addVector(0, getThrower().getEyeHeight() / 3.0, 0);
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
        float f3 = MathHelper.wrapDegrees(par2 - par1);

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

            if(!blade.func_190926_b() && target instanceof EntityLivingBase && thrower != null && thrower instanceof EntityLivingBase){
                StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.PhantomSword);
                ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)target,(EntityLivingBase)thrower);

                ReflectionAccessHelper.setVelocity(target, 0, 0, 0);
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
