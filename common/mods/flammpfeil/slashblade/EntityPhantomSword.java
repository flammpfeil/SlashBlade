package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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


        float dist = 2.5f;

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

        //■初期ベクトル設定
        setDriveVector(0.75F);

        //■プレイヤー位置より一歩進んだ所に出現する
        setPosition(posX + motionX, posY + motionY, posZ + motionZ);
    }

    /**
     * ■イニシャライズ
     */
    @Override
    protected void entityInit() {
        //Roll
        this.getDataWatcher().addObject(5, 0.0f);

        //lifetime
        this.getDataWatcher().addObject(6, 20);

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

    public void setInitialSpeed(float f){
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

        setDriveVector(f);
    }

    /**
     * ■初期ベクトルとかを決めてる。
     * ■移動速度設定
     * @param fYVecOfst
     */
    public void setDriveVector(float fYVecOfst)
    {
        //■角度 -> ラジアン 変換
        float fYawDtoR = (  rotationYaw / 180F) * (float)Math.PI;
        float fPitDtoR = (rotationPitch / 180F) * (float)Math.PI;

        //■単位ベクトル
        motionX = -MathHelper.sin(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst;
        motionY = -MathHelper.sin(fPitDtoR) * fYVecOfst;
        motionZ =  MathHelper.cos(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst;

        float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        prevRotationYaw = rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / Math.PI);
        prevRotationPitch = rotationPitch = (float)((Math.atan2(motionY, f3) * 180D) / Math.PI);
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
                DamageSource ds = DamageSourceAccessHelper.setDamageBypassesArmor(new EntityDamageSource("directMagic",this.getThrower())).setMagicDamage();
                ridingEntity.attackEntityFrom(ds, magicDamage);
                if(blade != null && ridingEntity instanceof EntityLivingBase)
                    ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)ridingEntity,(EntityLivingBase)thrower);
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
                AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(posX - dAmbit, posY - dAmbit, posZ - dAmbit, posX + dAmbit, posY + dAmbit, posZ + dAmbit);

                if(this.getThrower() instanceof EntityLivingBase){
                    EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb,ItemSlashBlade.DestructableSelector);

                    list.removeAll(alreadyHitEntity);
                    alreadyHitEntity.addAll(list);
                    for(Entity curEntity : list){
                        boolean isDestruction = true;

                        if(curEntity instanceof EntityFireball){
                            if((((EntityFireball)curEntity).shootingEntity != null && ((EntityFireball)curEntity).shootingEntity.entityId == entityLiving.entityId)){
                                isDestruction = false;
                            }else{
                                isDestruction = !curEntity.attackEntityFrom(DamageSource.causeMobDamage(entityLiving), this.AttackLevel);
                            }
                        }else if(curEntity instanceof EntityArrow){
                            if((((EntityArrow)curEntity).shootingEntity != null && ((EntityArrow)curEntity).shootingEntity.entityId == entityLiving.entityId)){
                                isDestruction = false;
                            }
                        }else if(curEntity instanceof IThrowableEntity){
                            if((((IThrowableEntity)curEntity).getThrower() != null && ((IThrowableEntity)curEntity).getThrower().entityId == entityLiving.entityId)){
                                isDestruction = false;
                            }
                        }else if(curEntity instanceof EntityThrowable){
                            if((((EntityThrowable)curEntity).getThrower() != null && ((EntityThrowable)curEntity).getThrower().entityId == entityLiving.entityId)){
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

                        this.setDead();
                        return;
                    }
                }

                {
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.AttackableSelector);
                    list.removeAll(alreadyHitEntity);

                    alreadyHitEntity.addAll(list);


                    Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
                    Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

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
                        DamageSource ds = DamageSourceAccessHelper.setDamageBypassesArmor(new EntityDamageSource("directMagic",this.getThrower())).setMagicDamage();
                        hitEntity.attackEntityFrom(ds, magicDamage);
                        if(blade != null && hitEntity instanceof EntityLivingBase)
                            ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)hitEntity,(EntityLivingBase)thrower);

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
                int blockId = worldObj.getBlockId(nPosX, nPosY, nPosZ);
                Block nBlock = Block.blocksList[blockId];
                if (nBlock != null && !nBlock.isAirBlock(worldObj,nPosX, nPosY, nPosZ) &&
                        nBlock.getCollisionBoundingBoxFromPool(worldObj, nPosX, nPosY, nPosZ) != null)
                {
                    this.setDead();
                }
            }

            if(7 < ticksExisted){
                posX += motionX;
                posY += motionY;
                posZ += motionZ;
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
