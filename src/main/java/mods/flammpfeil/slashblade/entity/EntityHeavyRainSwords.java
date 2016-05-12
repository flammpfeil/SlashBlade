package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.minecraft.world.World;

/**
 * Created by Furia on 2016/05/09.
 */
public class EntityHeavyRainSwords extends EntityPhantomSwordBase {
    public EntityHeavyRainSwords(World par1World) {
        super(par1World);
    }

    public EntityHeavyRainSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    static final int stayTime = 10;

    public EntityHeavyRainSwords(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll, int interval, int targetId) {
        super(par1World, entityLiving, AttackLevel, roll);

        {
            setInterval(stayTime + interval);
            setTargetEntityId(targetId);

            faceEntityStandby();

            this.setDriveVector(1.75f,true);
        }
    }
    private static final int INI_PITCH = 12;
    private static final int INI_YAW = 13;

    @Override
    protected void entityInit() {
        super.entityInit();

        this.getDataWatcher().addObject(INI_PITCH, -90.0f);
        this.getDataWatcher().addObject(INI_YAW, 0.0f);
    }

    public float getIniPitch(){
        return this.getDataWatcher().getWatchableObjectFloat(INI_PITCH);
    }
    public void setIniPitch(float pitch){
        this.getDataWatcher().updateObject(INI_PITCH,pitch);
    }
    public float getIniYaw(){
        return this.getDataWatcher().getWatchableObjectFloat(INI_YAW);
    }
    public void setIniYaw(float yaw){
        this.getDataWatcher().updateObject(INI_YAW,yaw);
    }

    @Override
    protected boolean onImpact(MovingObjectPosition mop) {
        if(getInterval() < ticksExisted)
            return super.onImpact(mop);
        else
            return false;
    }

    @Override
    public void spawnParticle() {
        super.spawnParticle();

        if(!worldObj.isRemote && this.ticksExisted == this.getInterval()){
            this.worldObj.playSoundEffect(this.prevPosX, this.prevPosY, this.prevPosZ, "mob.blaze.hit", 0.40F, 2.0F);
        }

        if(this.ticksExisted < this.getInterval() && this.ridingEntity2 == null){
            float trailLength;
            //for (int l = 0; l < 4; ++l)
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
    public boolean doTargeting() {
        return false;
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

    private void faceEntityStandby() {

        Vec3 lookVec = getLook(getThrower());

        Entity target = null;
        if (getTargetEntityId() != 0)
            target = worldObj.getEntityByID(getTargetEntityId());

        Vec3 pos = Vec3.createVectorHelper(0, 0, 0);

        if (target != null) {
            pos = pos.addVector(target.posX, target.posY, target.posZ);
        } else {
            pos = pos.addVector(getThrower().posX, getThrower().posY, getThrower().posZ);
            float len = 8.0f;
            pos = pos.addVector(lookVec.xCoord * len, 0, lookVec.zCoord * len);
        }

        float areaSize;
        if(stayTime < getInterval())
            areaSize = 1.5f;
        else
            areaSize = 0.1f;

        pos = pos.addVector((getRand().nextGaussian() - 0.5) * areaSize, 8.0, (getRand().nextGaussian() - 0.5) * areaSize);

        //■初期位置・初期角度等の設定
        setPosition(pos.xCoord,
                pos.yCoord,
                pos.zCoord);
        setRotation(getRand().nextFloat() * 360.0f,
                (float)(90.0 + (getRand().nextGaussian() - 0.5) * 8.0));

        setIniPitch(iniPitch = this.rotationPitch);
        setIniYaw(iniYaw = this.rotationYaw);
    }

    @Override
    public void setDriveVector(float fYVecOfst,boolean init)
    {
        fYVecOfst = 0.8f;

        if(Float.isNaN(iniYaw))
            iniYaw = getIniYaw();
        if(Float.isNaN(iniPitch))
            iniPitch = getIniPitch();

        //■角度 -> ラジアン 変換
        float fYawDtoR = (  iniYaw / 180F) * (float)Math.PI;
        float fPitDtoR = (iniPitch / 180F) * (float)Math.PI;

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

    @Override
    protected void attackEntity(Entity target){

        if(!this.worldObj.isRemote) {
            float magicDamage = Math.max(1.0f, AttackLevel);
            target.hurtResistantTime = 0;
            DamageSource ds = new EntityDamageSource("directMagic", this.getThrower()).setDamageBypassesArmor().setMagicDamage();
            target.attackEntityFrom(ds, magicDamage);

            if (blade != null && target instanceof EntityLivingBase && thrower != null && thrower instanceof EntityLivingBase) {
                StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.PhantomSword);
                ((ItemSlashBlade) blade.getItem()).hitEntity(blade, (EntityLivingBase) target, (EntityLivingBase) thrower);

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                target.addVelocity(0.0, 0.1D, 0.0);

                ((EntityLivingBase) target).hurtTime = 1;

                ((ItemSlashBlade) blade.getItem()).setDaunting(((EntityLivingBase) target));
                StunManager.setFreeze((EntityLivingBase) target, 20);
            }
        }

        mountEntity(target);
    }

    @Override
    protected void blastAttackEntity(Entity target) {
        super.blastAttackEntity(target);

        if(!this.worldObj.isRemote){
            if (target instanceof EntityLivingBase) {
                StunManager.setFreeze((EntityLivingBase) target, 20);
            }
        }
    }

    @Override
    public void mountEntity(Entity par1Entity) {
        if(par1Entity != null){
            this.hitYaw = this.rotationYaw - par1Entity.rotationYaw;
            this.hitPitch = this.rotationPitch - par1Entity.rotationPitch;
            this.hitX = this.lastTickPosX - par1Entity.posX;
            this.hitY = this.lastTickPosY - par1Entity.posY;
            this.hitZ = this.lastTickPosZ - par1Entity.posZ;
            this.ridingEntity2 = par1Entity;

            this.ticksExisted = 200 - (getLifeTime() - this.ticksExisted);
        }
    }
}
