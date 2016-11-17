package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
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

/**
 * Created by Furia on 2016/05/09.
 */
public class EntityStormSwords extends EntitySummonedSwordBase {
    public EntityStormSwords(World par1World) {
        super(par1World);

        setInterval(0);
    }

    public EntityStormSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    public EntityStormSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll, float rotOffset,int targetEntityId) {
        super(par1World, entityLiving, AttackLevel, roll);

        {
            setTargetEntityId(targetEntityId);

            setRotOffset(rotOffset);

            if(getTargetEntityId() != 0){
                Entity target = par1World.getEntityByID(getTargetEntityId());
                mountEntity(target);
            }

            faceEntityStandby();

            this.setDriveVector(1.0f,true);
        }
    }
    private static final DataParameter<Boolean> HAS_FIRED = EntityDataManager.<Boolean>createKey(EntityStormSwords.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ROT_OFFSET = EntityDataManager.<Float>createKey(EntityStormSwords.class, DataSerializers.FLOAT);

    @Override
    protected void entityInit() {
        super.entityInit();

        this.getDataManager().register(HAS_FIRED, false);

        this.getDataManager().register(ROT_OFFSET, 0.0f);
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

    static final int waitTime = 7;

    @Override
    public void updateRidden() {

        if(hasFired()) {
            if (ridingEntity2.getEntityId() == this.getTargetEntityId()) {

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

        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        faceEntityStandby();

        if(ridingEntity.isDead){
            setInterval(ticksExisted + waitTime);
            setLifeTime(ticksExisted + 30);
            setHasFired(true);
            this.getEntityWorld().updateEntityWithOptionalForce(this,true);
            return;
        }

        if(!getEntityWorld().isRemote){

            if(getInterval() < ticksExisted) {
                setHasFired(true);

                this.getEntityWorld().updateEntityWithOptionalForce(this,true);

                this.worldObj.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.NEUTRAL, 0.35F, 0.2F);

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

        if(ridingEntity2 == null && !hasFired() && getTargetEntityId() != 0){
            if(getEntityWorld().isRemote) {
                Entity target = getEntityWorld().getEntityByID(getTargetEntityId());
                mountEntity(target);
            }
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


            if(this.ticksExisted < getInterval())
                return false;

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
        if((getInterval() - waitTime) < ticks){
            ticks = getInterval() - waitTime;
        }

        double rotParTick = 360.0 / 40;
        double offset = getRotOffset();
        double degYaw = ticks * rotParTick + offset;
        double yaw = Math.toRadians(degYaw);

        Vec3d pos = new Vec3d(
                Math.sin(yaw),
                Math.sin(yaw + ticks / 10.0) * 0.1f,
                Math.cos(yaw));

        pos = pos.scale(2.5);

        if (ridingEntity2 != null) {
            pos = pos.add(ridingEntity2.getPositionVector());
            pos = pos.addVector(0, ridingEntity2.getEyeHeight() / 3.0, 0);
        }

        //■初期位置・初期角度等の設定
        setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
        setRotation((float) (180.0 - degYaw), 0);
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

            if(this.alreadyHitEntity.contains(target)) return;
            this.alreadyHitEntity.add(target);

            float magicDamage = Math.max(1.0f, AttackLevel);
            target.hurtResistantTime = 0;
            DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
            target.attackEntityFrom(ds, magicDamage);

            if(!blade.func_190926_b() && target instanceof EntityLivingBase && thrower != null && thrower instanceof EntityLivingBase){
                StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.PhantomSword);
                ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)target,(EntityLivingBase)thrower);

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                target.addVelocity(0.0, 0.8D, 0.0);

                ((EntityLivingBase) target).hurtTime = 1;

                ((ItemSlashBlade)blade.getItem()).setDaunting(((EntityLivingBase) target));
            }

            if(--life <= 0) {
                setDead();
            }
        }
    }
}
