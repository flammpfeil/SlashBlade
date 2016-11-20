package mods.flammpfeil.slashblade.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Furia on 14/05/08.
 */
public class EntityDrive extends Entity implements IThrowableEntity {
    /**
     * ★撃った人
     */
    protected Entity thrower;

    protected ItemStack blade = ItemStack.field_190927_a;

    /**
     * ★多段Hit防止用List
     */
    protected List<Entity> alreadyHitEntity = new ArrayList<Entity>();

    protected float AttackLevel = 0.0f;

    /**
     * ■コンストラクタ
     * @param par1World
     */
    public EntityDrive(World par1World)
    {
        super(par1World);
    }

    public EntityDrive(World par1World, EntityLivingBase entityLiving,float AttackLevel,boolean multiHit, float roll){
        this(par1World,entityLiving,AttackLevel,multiHit);
        this.setRoll(roll);
    }

    public EntityDrive(World par1World, EntityLivingBase entityLiving,float AttackLevel,boolean multiHit){
        this(par1World, entityLiving, AttackLevel);
        this.setIsMultiHit(multiHit);
    }

    public EntityDrive(World par1World, EntityLivingBase entityLiving,float AttackLevel)
    {
        this(par1World);

        this.AttackLevel = AttackLevel;

        //■撃った人
        thrower = entityLiving;

        blade = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
        if(!blade.func_190926_b() && !(blade.getItem() instanceof ItemSlashBlade)){
            blade = ItemStack.field_190927_a;
        }

        //■撃った人と、撃った人が（に）乗ってるEntityも除外
        alreadyHitEntity.clear();
        alreadyHitEntity.add(thrower);
        alreadyHitEntity.add(thrower.getRidingEntity());
        alreadyHitEntity.addAll(thrower.getPassengers());

        //■生存タイマーリセット
        ticksExisted = 0;

        //■サイズ変更
        setSize(1.0F, 2.0F);

        //■初期位置・初期角度等の設定
        setLocationAndAngles(thrower.posX,
                thrower.posY + (double) thrower.getEyeHeight() / 2D,
                thrower.posZ,
                thrower.rotationYaw,
                thrower.rotationPitch);

        //■初期ベクトル設定
        setDriveVector(0.75F);

        //■プレイヤー位置より一歩進んだ所に出現する
        Vec3d motion = thrower.getLookVec();
        if(motion == null) motion = new Vec3d(motionX,motionY,motionZ);
        motion = motion.normalize();
        setPosition(posX + motion.xCoord * 20, posY + motion.yCoord * 20, posZ + motion.zCoord * 20);
    }





    private static final DataParameter<Float> ROLL = EntityDataManager.<Float>createKey(EntityDrive.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> LIFETIME = EntityDataManager.<Integer>createKey(EntityDrive.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_MULTI_HIT = EntityDataManager.<Boolean>createKey(EntityDrive.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SLASH_DIMENSION = EntityDataManager.<Boolean>createKey(EntityDrive.class, DataSerializers.BOOLEAN);

    /**
     * ■イニシャライズ
     */
    @Override
    protected void entityInit() {
        //Roll
        this.getDataManager().register(ROLL, 0.0f);

        //lifetime
        this.getDataManager().register(LIFETIME, 20);

        //isMultiHit
        this.getDataManager().register(IS_MULTI_HIT, false);

        //lifetime
        this.getDataManager().register(IS_SLASH_DIMENSION, false);

    }

    public boolean getIsMultiHit(){
        return this.getDataManager().get(IS_MULTI_HIT);
    }
    public void setIsMultiHit(boolean isMultiHit){
        this.getDataManager().set(IS_MULTI_HIT,isMultiHit);
    }

    public float getRoll(){
        return this.getDataManager().get(ROLL);
    }
    public void setRoll(float roll){
        this.getDataManager().set(ROLL, roll);
    }

    public int getLifeTime(){
        return this.getDataManager().get(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataManager().set(LIFETIME,lifetime);
    }

    public boolean getIsSlashDimension(){
        return this.getDataManager().get(IS_SLASH_DIMENSION);
    }
    public void setIsSlashDimension(boolean isSlashDimension){
        this.getDataManager().set(IS_SLASH_DIMENSION, isSlashDimension);
    }

    public void setInitialSpeed(float f){
        setLocationAndAngles(thrower.posX,
                thrower.posY + (double)thrower.getEyeHeight()/2D,
                thrower.posZ,
                thrower.rotationYaw,
                thrower.rotationPitch);
        setDriveVector(f);

        //■プレイヤー位置より一歩進んだ所に出現する
        Vec3d motion = thrower.getLookVec();
        if(motion == null) motion = new Vec3d(motionX,motionY,motionZ);
        motion = motion.normalize();
        setPosition(posX + motion.xCoord * 1, posY + motion.yCoord * 1, posZ + motion.zCoord * 1);
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

    //■毎回呼ばれる。移動処理とか当り判定とかもろもろ。
    @Override
    public void onUpdate()
    {
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;

        //super.onUpdate();

        if(!worldObj.isRemote)
        {

            {
                double dAmbit = 1.5D;
                AxisAlignedBB bb = new AxisAlignedBB(posX - dAmbit, posY - dAmbit, posZ - dAmbit, posX + dAmbit, posY + dAmbit, posZ + dAmbit);

                if(this.getThrower() instanceof EntityLivingBase){
                    EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();
                    List<Entity> list = this.worldObj.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorDestructable.getInstance());

                    StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.DestructObject);

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
                            ReflectionAccessHelper.setVelocity(curEntity, 0, 0, 0);
                            curEntity.setDead();

                            for (int var1 = 0; var1 < 10; ++var1)
                            {
                                Random rand = this.getRand();
                                double var2 = rand.nextGaussian() * 0.02D;
                                double var4 = rand.nextGaussian() * 0.02D;
                                double var6 = rand.nextGaussian() * 0.02D;
                                double var8 = 10.0D;
                                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL
                                        , curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8
                                        , curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8
                                        , curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8
                                        , var2, var4, var6);
                            }
                        }

                        StylishRankManager.doAttack(this.thrower);
                    }
                }

                if(!getIsMultiHit() || this.ticksExisted % 2 == 0){
                    List<Entity> list = this.worldObj.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorAttackable.getInstance());
                    list.removeAll(alreadyHitEntity);

                    if(!getIsMultiHit())
                        alreadyHitEntity.addAll(list);

                    float magicDamage = Math.max(1.0f, AttackLevel);

                    if(getIsMultiHit())
                        StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.QuickDrive);
                    else
                        StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.Drive);

                    for(Entity curEntity : list){

                        if(getIsSlashDimension()){
                            if(curEntity instanceof EntityLivingBase){
                                float health = ((EntityLivingBase) curEntity).getHealth();
                                if(0 < health){
                                    health = Math.max(1,health - magicDamage);
                                    ((EntityLivingBase) curEntity).setHealth(health);
                                }
                            }
                        }

                        curEntity.hurtResistantTime = 0;
                        DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
                        curEntity.attackEntityFrom(ds, magicDamage);


                        if(!blade.func_190926_b() && curEntity instanceof EntityLivingBase)
                            ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)curEntity,(EntityLivingBase)thrower);
                    }
                }
            }

            /*
            //■ブロック
            int nPosX = MathHelper.floor_double(posX);
            int nPosY = MathHelper.floor_double(posY);
            int nPosZ = MathHelper.floor_double(posZ);

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

            //地形衝突で消失
            if(!worldObj.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                //todo: 突き刺し一定時間保持（DummyEntityに刺して止めるなど
                this.setDead();
            }

        }

        motionX *= 1.05f;
        motionY *= 1.05f;
        motionZ *= 1.05f;

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        setPosition(posX, posY, posZ);

        //■死亡チェック
        if(ticksExisted >= getLifeTime()) {
            alreadyHitEntity.clear();
            alreadyHitEntity = null;
            setDead();
        }
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
    public void moveEntity(MoverType moverType, double par1, double par3, double par5) {}

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
    public boolean isInLava() {
        return false;
    }

    /**
     * ■環境光による暗さの描画（？）
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
     * ■Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {}

    /**
     * ■Called by portal blocks when an entity is within it.
     */
    @Override
    public void setPortal(BlockPos pos) {
        //super.setPortal(pos);
    }

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
