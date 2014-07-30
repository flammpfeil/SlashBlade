package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/05/08.
 */
public class EntityPhantomSword extends Entity implements IThrowableEntity {
    /**
     * ★撃った人
     */
    protected Entity thrower;

    protected ItemStack blade = null;

    /**
     * ★多段Hit防止用List
     */
    protected List<Entity> alreadyHitEntity = new ArrayList<Entity>();

    protected float AttackLevel = 0.0f;

    /**
     * ■コンストラクタ
     * @param par1World
     */
    public EntityPhantomSword(World par1World)
    {
        super(par1World);
    }

    public EntityPhantomSword(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll){
        this(par1World,entityLiving,AttackLevel);
        this.setRoll(roll);
    }

    public EntityPhantomSword(World par1World, EntityLivingBase entityLiving, float AttackLevel)
    {
        this(par1World);

        this.AttackLevel = AttackLevel;

        //■Y軸のオフセット設定
        yOffset = entityLiving.getEyeHeight()/2.0F;

        //■撃った人
        thrower = entityLiving;

        blade = entityLiving.getHeldItem();
        if(blade != null && !(blade.getItem() instanceof ItemSlashBlade)){
            blade = null;
        }

        //■撃った人と、撃った人が（に）乗ってるEntityも除外
        alreadyHitEntity.clear();
        alreadyHitEntity.add(thrower);
        alreadyHitEntity.add(thrower.ridingEntity);
        alreadyHitEntity.add(thrower.riddenByEntity);

        //■生存タイマーリセット
        ticksExisted = 0;

        //■サイズ変更
        setSize(0.5F, 0.5F);

        {
            float dist = 2.0f;

            double ran = (rand.nextFloat() - 0.5) * 2.0;

            double yaw =  Math.toRadians(-thrower.rotationYaw + 90);

            double x = ran * Math.sin(yaw);
            double y = 1.0 - Math.abs(ran);
            double z = ran * Math.cos(yaw);

            x*=dist;
            y*=dist;
            z*=dist;

            //■初期位置・初期角度等の設定
            setLocationAndAngles(thrower.posX + x,
                    thrower.posY + y,
                    thrower.posZ + z,
                    thrower.rotationYaw,
                    thrower.rotationPitch);

            iniYaw = thrower.rotationYaw;
            iniPitch = thrower.rotationPitch;

            setDriveVector(1.75f);
        }
    }

    /**
     * ■イニシャライズ
     */
    @Override
    protected void entityInit() {
        //EntityId
        this.getDataWatcher().addObject(4, 0);

        //Roll
        this.getDataWatcher().addObject(5, 0.0f);

        //lifetime
        this.getDataWatcher().addObject(6, 20);

    }

    public int getTargetEntityId(){
        return this.getDataWatcher().getWatchableObjectInt(4);
    }
    public void setTargetEntityId(int entityid){
        this.getDataWatcher().updateObject(4,entityid);
    }

    public float getRoll(){
        return this.getDataWatcher().getWatchableObjectFloat(5);
    }
    public void setRoll(float roll){
        this.getDataWatcher().updateObject(5,roll);
    }

    public int getLifeTime(){
        return this.getDataWatcher().getWatchableObjectInt(6);
    }
    public void setLifeTime(int lifetime){
        this.getDataWatcher().updateObject(6,lifetime);
    }

    float speed = 0.0f;
    float iniYaw = Float.NaN;
    float iniPitch = Float.NaN;

    public void doTargeting(){
        int targetid = this.getTargetEntityId();
        if(targetid != 0){
            Entity target = worldObj.getEntityByID(targetid);

            if(target != null){

                if(Float.isNaN(iniPitch)){
                    iniYaw = thrower.rotationYaw;
                    iniPitch = thrower.rotationPitch;
                }
                faceEntity(this,target,ticksExisted * 1.0f,ticksExisted * 1.0f);
                setDriveVector(1.75F, false);
            }
        }
    }


    public void faceEntity(Entity viewer, Entity target, float yawStep, float pitchStep)
    {
        double d0 = target.posX - viewer.posX;
        double d1 = target.posZ - viewer.posZ;
        double d2;

        if (target instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)target;
            d2 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (viewer.posY + (double)viewer.getEyeHeight());
        }
        else
        {
            d2 = (target.boundingBox.minY + target.boundingBox.maxY) / 2.0D - (viewer.posY + (double)viewer.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));


        iniPitch = this.updateRotation(iniPitch, f3, pitchStep);
        iniYaw = this.updateRotation(iniYaw, f2, yawStep);



        /**/

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

    public void setDriveVector(float fYVecOfset){
        setDriveVector(fYVecOfset,true);
    }

    /**
     * ■初期ベクトルとかを決めてる。
     * ■移動速度設定
     * @param fYVecOfst
     */
    public void setDriveVector(float fYVecOfst,boolean init)
    {
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
    public void updateRidden() {

        Entity ridingEntity = this.ridingEntity2;

        if(ridingEntity.isDead){
            this.setDead();
            return;
        }

        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;


        posX = ridingEntity.posX + (this.hitX * Math.cos(Math.toRadians(ridingEntity.rotationYaw)) - this.hitZ * Math.sin(Math.toRadians(ridingEntity.rotationYaw)));
        posY = ridingEntity.posY + this.hitY;
        posZ = ridingEntity.posZ + (this.hitX * Math.sin(Math.toRadians(ridingEntity.rotationYaw)) + this.hitZ * Math.cos(Math.toRadians(ridingEntity.rotationYaw)));

        rotationPitch = ridingEntity.rotationPitch + this.hitPitch;
        rotationYaw = ridingEntity.rotationYaw + this.hitYaw;

        setPosition(posX, posY, posZ);

        setRotation(rotationYaw,rotationPitch);

        //■死亡チェック
        if(ticksExisted >= getLifeTime()) {

            if(!ridingEntity.isDead){
                float magicDamage = Math.max(1.0f, AttackLevel / 2);
                ridingEntity.hurtResistantTime = 0;
                DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
                ridingEntity.attackEntityFrom(ds, magicDamage);
                if(blade != null && ridingEntity instanceof EntityLivingBase){
                    StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.BreakPhantomSword);
                    ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)ridingEntity,(EntityLivingBase)thrower);
                }
            }

            setDead();
        }
    }

    //■毎回呼ばれる。移動処理とか当り判定とかもろもろ。
    @Override
    public void onUpdate()
    {
        if(this.ridingEntity2 != null){
            updateRidden();
        }else{

            lastTickPosX = posX;
            lastTickPosY = posY;
            lastTickPosZ = posZ;

            //super.onUpdate();

            {
                double dAmbit = 0.75D;
                AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(posX - dAmbit, posY - dAmbit, posZ - dAmbit, posX + dAmbit, posY + dAmbit, posZ + dAmbit);

                if(this.getThrower() instanceof EntityLivingBase){
                    EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb,ItemSlashBlade.DestructableSelector);

                    StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.DestructObject);

                    list.removeAll(alreadyHitEntity);
                    alreadyHitEntity.addAll(list);
                    for(Entity curEntity : list){
                        boolean isDestruction = true;

                        if(curEntity instanceof EntityFireball){
                            if((((EntityFireball)curEntity).shootingEntity != null && ((EntityFireball)curEntity).shootingEntity.getEntityId() == entityLiving.getEntityId())){
                                isDestruction = false;
                            }else{
                                isDestruction = !curEntity.attackEntityFrom(DamageSource.causeMobDamage(entityLiving), this.AttackLevel);
                            }
                        }else if(curEntity instanceof EntityArrow){
                            if((((EntityArrow)curEntity).shootingEntity != null && ((EntityArrow)curEntity).shootingEntity.getEntityId() == entityLiving.getEntityId())){
                                isDestruction = false;
                            }
                        }else if(curEntity instanceof IThrowableEntity){
                            if((((IThrowableEntity)curEntity).getThrower() != null && ((IThrowableEntity)curEntity).getThrower().getEntityId() == entityLiving.getEntityId())){
                                isDestruction = false;
                            }
                        }else if(curEntity instanceof EntityThrowable){
                            if((((EntityThrowable)curEntity).getThrower() != null && ((EntityThrowable)curEntity).getThrower().getEntityId() == entityLiving.getEntityId())){
                                isDestruction = false;
                            }
                        }

                        if(!isDestruction)
                            continue;
                        else{
                            curEntity.motionX = 0;
                            curEntity.motionY = 0;
                            curEntity.motionZ = 0;
                            curEntity.setDead();

                            for (int var1 = 0; var1 < 10; ++var1)
                            {
                                Random rand = this.getRand();
                                double var2 = rand.nextGaussian() * 0.02D;
                                double var4 = rand.nextGaussian() * 0.02D;
                                double var6 = rand.nextGaussian() * 0.02D;
                                double var8 = 10.0D;
                                this.worldObj.spawnParticle("explode", curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8, curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8, curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8, var2, var4, var6);
                            }
                        }

                        StylishRankManager.doAttack(this.thrower);

                        this.setDead();
                        return;
                    }
                }

                {
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.AttackableSelector);
                    list.removeAll(alreadyHitEntity);

                    if(getTargetEntityId() != 0){
                        Entity target = worldObj.getEntityByID(getTargetEntityId());
                        if(target != null){
                            if(target.boundingBox.intersectsWith(bb))
                                list.add(target);
                        }
                    }
                    alreadyHitEntity.addAll(list);

                    Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
                    Vec3 vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

                    double d0 = 10.0D;
                    int i;
                    float f1;

                    Entity hitEntity = null;

                    for(Entity curEntity : list){
                        if (curEntity.canBeCollidedWith())
                        {
                            double d1 = curEntity.getDistanceToEntity(this);

                            if (d1 < d0 || d0 == 0.0D)
                            {
                                hitEntity = curEntity;
                                d0 = d1;
                            }
                        }
                    }

                    if(hitEntity != null){
                        float magicDamage = Math.max(1.0f, AttackLevel);
                        hitEntity.hurtResistantTime = 0;
                        DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
                        hitEntity.attackEntityFrom(ds, magicDamage);
                        if(blade != null && hitEntity instanceof EntityLivingBase){
                            StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.PhantomSword);
                            ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)hitEntity,(EntityLivingBase)thrower);
                        }

                        mountEntity(hitEntity);
                    }
                }
            }

            //■ブロック
            int nPosX = MathHelper.floor_double(posX);
            int nPosY = MathHelper.floor_double(posY);
            int nPosZ = MathHelper.floor_double(posZ);

            /*
            for (int idx = nPosX - 1; idx <= nPosX + 1; idx++) {
                for (int idy = nPosY - 1; idy <= nPosY + 1; idy++) {
                    for (int idz = nPosZ - 1; idz <= nPosZ + 1; idz++) {
                        //▼
                        Block nBlock = worldObj.getBlock(idx, idy, idz);

                        //■
                        if (nBlock.getMaterial() == Material.leaves
                                || nBlock.getMaterial() == Material.web
                                || nBlock.getMaterial() == Material.plants)
                        {
                            if(thrower instanceof EntityPlayerMP){
                                ((EntityPlayerMP)thrower).theItemInWorldManager.tryHarvestBlock(idx, idy, idz);
                            }
                        }
                    }
                }
            }
            */

            if(this.ridingEntity2 == null)
            {
                //■消滅処理
                Block nBlock = worldObj.getBlock(nPosX, nPosY, nPosZ);
                if (!nBlock.isAir(worldObj,nPosX, nPosY, nPosZ) &&
                        nBlock.getCollisionBoundingBoxFromPool(worldObj, nPosX, nPosY, nPosZ) != null)
                {
                    this.setDead();
                    return;
                }
            }

            if(7 < ticksExisted){
                posX += motionX;
                posY += motionY;
                posZ += motionZ;
            }else{
                doTargeting();
            }
            setPosition(posX, posY, posZ);

            //■死亡チェック
            if(ticksExisted >= getLifeTime()) {
                setDead();
            }

        }
    }

    @Override
    public void setDead() {
        if(this.thrower instanceof EntityPlayer)
            ((EntityPlayer)thrower).onCriticalHit(this);
        /*
        if(!this.worldObj.isRemote)
            System.out.println("dead" + this.ticksExisted);
            */
        super.setDead();
    }

    /**
     * ■Random
     * @return
     */
    public Random getRand()
    {
        return this.rand;
    }

    /**
     * ■Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     * Liquid = 流体
     */
    @Override
    public boolean isOffsetPositionInLiquid(double par1, double par3, double par5)
    {
        //AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(par1, par3, par5);
        //List list = this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb);
        //return !list.isEmpty() ? false : !this.worldObj.isAnyLiquid(axisalignedbb);
        return false;
    }

    /**
     * ■Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    @Override
    public void moveEntity(double par1, double par3, double par5) {}

    /**
     * ■Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    @Override
    protected void dealFireDamage(int par1) {}

    /**
     * ■Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    @Override
    public boolean handleWaterMovement()
    {
        return false;
    }

    /**
     * ■Checks if the current block the entity is within of the specified material type
     */
    @Override
    public boolean isInsideOfMaterial(Material par1Material)
    {
        return false;
    }

    /**
     * ■Whether or not the current entity is in lava
     */
    @Override
    public boolean handleLavaMovement()
    {
        return false;
    }

    /**
     * ■環境光による暗さの描画（？）
     *    EntityXPOrbのぱくり
     */
    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender(float par1)
    {
        float f1 = 0.5F;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        int i = super.getBrightnessForRender(par1);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f1 * 15.0F * 16.0F);

        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }

    /**
     * ■Gets how bright this entity is.
     *    EntityPortalFXのぱくり
     */
    @Override
    public float getBrightness(float par1)
    {
        float f1 = super.getBrightness(par1);
        float f2 = 0.9F;
        f2 = f2 * f2 * f2 * f2;
        return f1 * (1.0F - f2) + f2;
        //return super.getBrightness(par1);
    }

    /**
     * ■NBTの読込
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    /**
     * ■NBTの書出
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

    /**
     * ■影のサイズ
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getShadowSize()
    {
        return 0.0F;
    }

    double hitX;
    double hitY;
    double hitZ;
    float hitYaw;
    float hitPitch;


    Entity ridingEntity2 = null;
    /**
     * ■Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    @Override
    public void mountEntity(Entity par1Entity) {
        if(par1Entity != null){
            this.hitYaw = this.rotationYaw - par1Entity.rotationYaw;
            this.hitPitch = this.rotationPitch - par1Entity.rotationPitch;
            this.hitX = this.posX - par1Entity.posX;
            this.hitY = this.posY - par1Entity.posY;
            this.hitZ = this.posZ - par1Entity.posZ;
            this.ridingEntity2 = par1Entity;

            this.ticksExisted = Math.max(0,getLifeTime() - 20);
        }
    }

    /**
     * ■Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {}

    /**
     * ■Called by portal blocks when an entity is within it.
     */
    @Override
    public void setInPortal() {}

    /**
     * ■Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    /**
     * ■Sets the Entity inside a web block.
     */
    @Override
    public void setInWeb() {}


    @Override
    public Entity getThrower() {
        return this.thrower;
    }

    @Override
    public void setThrower(Entity entity) {
        this.thrower = entity;
    }
}
