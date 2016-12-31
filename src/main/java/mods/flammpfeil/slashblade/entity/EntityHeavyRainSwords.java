package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ability.StunManager;
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

/**
 * Created by Furia on 2016/05/09.
 */
public class EntityHeavyRainSwords extends EntitySummonedSwordBase {
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
    private static final DataParameter<Float> INI_PITCH = EntityDataManager.<Float>createKey(EntityHeavyRainSwords.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> INI_YAW = EntityDataManager.<Float>createKey(EntityHeavyRainSwords.class, DataSerializers.FLOAT);

    @Override
    protected void entityInit() {
        super.entityInit();

        this.getDataManager().register(INI_PITCH, -90.0f);
        this.getDataManager().register(INI_YAW, 0.0f);
    }

    public float getIniPitch(){
        return this.getDataManager().get(INI_PITCH);
    }
    public void setIniPitch(float pitch){
        this.getDataManager().set(INI_PITCH,pitch);
    }
    public float getIniYaw(){
        return this.getDataManager().get(INI_YAW);
    }
    public void setIniYaw(float yaw){
        this.getDataManager().set(INI_YAW,yaw);
    }

    @Override
    protected boolean onImpact(RayTraceResult mop) {
        if(getInterval() + 1 < ticksExisted)
            return super.onImpact(mop);
        else
            return false;
    }

    @Override
    public void spawnParticle() {
        super.spawnParticle();

        if(!getEntityWorld().isRemote && this.ticksExisted == this.getInterval()){
            this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.NEUTRAL, 0.40F, 2.0F);
        }

        if(this.ticksExisted < this.getInterval() && this.ridingEntity2 == null){
            float trailLength;
            //for (int l = 0; l < 4; ++l)
            {
                trailLength = 0.25F;
                this.world.spawnParticle(EnumParticleTypes.PORTAL
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


    private void faceEntityStandby() {

        Vec3d lookVec = getThrower().getLook(1.0f);

        Entity target = null;
        if (getTargetEntityId() != 0)
            target = getEntityWorld().getEntityByID(getTargetEntityId());

        Vec3d pos = new Vec3d(0, 0, 0);

        if (target != null) {
            pos = pos.add(target.getPositionVector());
        } else {
            pos = pos.add(getThrower().getPositionVector());
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

        float f3 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
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

        if(!this.world.isRemote) {
            float magicDamage = Math.max(1.0f, AttackLevel);
            target.hurtResistantTime = 0;
            DamageSource ds = new EntityDamageSource("directMagic", this.getThrower()).setDamageBypassesArmor().setMagicDamage();
            target.attackEntityFrom(ds, magicDamage);

            if (!blade.isEmpty() && target instanceof EntityLivingBase && thrower != null && thrower instanceof EntityLivingBase) {
                StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.PhantomSword);
                ((ItemSlashBlade) blade.getItem()).hitEntity(blade, (EntityLivingBase) target, (EntityLivingBase) thrower);

                ReflectionAccessHelper.setVelocity(target, 0, 0, 0);
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

        if(!this.world.isRemote){
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
