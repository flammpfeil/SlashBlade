package mods.flammpfeil.slashblade.entity;

import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.AirTrick;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/05/08.
 */
public class EntityPhantomSwordBase extends Entity implements IProjectile,IThrowableEntity {

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
    public EntityPhantomSwordBase(World par1World)
    {
        super(par1World);

        this.noClip = true;
    }

    public EntityPhantomSwordBase(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll){
        this(par1World,entityLiving,AttackLevel);
        this.setRoll(roll);
    }

    public EntityPhantomSwordBase(World par1World, EntityLivingBase entityLiving, float AttackLevel)
    {
        this(par1World);

        this.AttackLevel = AttackLevel;

        //■Y軸のオフセット設定
        yOffset = entityLiving.getEyeHeight()/2.0F;

        //■撃った人
        setThrower(entityLiving);

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

    static final int THROWER_ENTITY_ID = 11;

    /**
     * ■イニシャライズ
     */
    @Override
    protected void entityInit() {

        //thrower
        this.getDataWatcher().addObject(11, 0);

        //EntityId
        this.getDataWatcher().addObject(4, 0);

        //Roll
        this.getDataWatcher().addObject(5, 0.0f);

        //lifetime
        this.getDataWatcher().addObject(6, 20);

        //interval
        this.getDataWatcher().addObject(7, 7);

        //color
        this.getDataWatcher().addObject(10, 0x3333FF);
    }

    public int getThrowerEntityId(){
        return this.getDataWatcher().getWatchableObjectInt(THROWER_ENTITY_ID);
    }
    public void setThrowerEntityId(int entityid){
        this.getDataWatcher().updateObject(THROWER_ENTITY_ID, entityid);
    }

    public int getTargetEntityId(){
        return this.getDataWatcher().getWatchableObjectInt(4);
    }
    public void setTargetEntityId(int entityid){
        this.getDataWatcher().updateObject(4, entityid);
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

    public int getInterval(){
        return this.getDataWatcher().getWatchableObjectInt(7);
    }
    public void setInterval(int value){
        this.getDataWatcher().updateObject(7,value);
    }

    public int getColor(){
        return this.getDataWatcher().getWatchableObjectInt(10);
    }
    public void setColor(int value){
        this.getDataWatcher().updateObject(10,value);
    }

    float speed = 0.0f;
    float iniYaw = Float.NaN;
    float iniPitch = Float.NaN;

    public boolean doTargeting(){

        if(this.ticksExisted > getInterval()) return false;

        int targetid = this.getTargetEntityId();

        Entity owner = this.thrower;
        if(this.thrower == null)
            owner = this;

        if(targetid == 0){

            Entity rayEntity = getRayTrace(owner, 30.0f); //最長３０
            if(rayEntity != null){
                targetid = rayEntity.getEntityId();
                this.setTargetEntityId( rayEntity.getEntityId());
            }
        }

        //視線中に無かった場合近傍Entityに拡張検索
        if(targetid == 0){
            Entity rayEntity = getRayTrace(owner, 30.0f,5.0f,5.0f); //最長３０、視線外10幅まで探索拡張
            if(rayEntity != null){
                targetid = rayEntity.getEntityId();
                this.setTargetEntityId( rayEntity.getEntityId());
            }
        }

        if(targetid != 0){
            Entity target = worldObj.getEntityByID(targetid);

            if(target != null){

                if(Float.isNaN(iniPitch) && thrower != null){
                    iniYaw = thrower.rotationYaw;
                    iniPitch = thrower.rotationPitch;
                }
                faceEntity(this,target,ticksExisted * 1.0f,ticksExisted * 1.0f);
                setDriveVector(1.75F, false);
            }
        }

        return true;
    }

    public Entity getRayTrace(Entity owner, double reachMax){
        return this.getRayTrace(owner, reachMax,1.0f,0.0f);
    }

    public Entity getRayTrace(Entity owner, double reachMax, float expandFactor, float expandBorder){
        Entity pointedEntity;
        float par1 = 1.0f;

        MovingObjectPosition objectMouseOver = rayTrace(owner,reachMax, par1);
        double reachMin = reachMax;
        Vec3 entityPos = getPosition(owner);

        if (objectMouseOver != null)
        {
            reachMin = objectMouseOver.hitVec.distanceTo(entityPos);
        }

        Vec3 lookVec = getLook(owner, par1);
        Vec3 reachVec = entityPos.addVector(lookVec.xCoord * reachMax, lookVec.yCoord * reachMax, lookVec.zCoord * reachMax);
        pointedEntity = null;
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(lookVec.xCoord * reachMax, lookVec.yCoord * reachMax, lookVec.zCoord * reachMax).expand((double)expandFactor, (double)expandFactor, (double)expandFactor));
        list.removeAll(alreadyHitEntity);

        double tmpDistance = reachMin;

        EntityLivingBase viewer = (owner instanceof EntityLivingBase) ? (EntityLivingBase) owner : null;

        for (Entity entity : list) {
            if (entity == null || !entity.canBeCollidedWith()) continue;

            if(!ItemSlashBlade.AttackableSelector.isEntityApplicable(entity))
                continue;

            if(viewer != null && !viewer.canEntityBeSeen(entity))
                continue;

            float borderSize = entity.getCollisionBorderSize() + expandBorder; //視線外10幅まで判定拡張
            AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)borderSize, (double)borderSize, (double)borderSize);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(entityPos, reachVec);

            if (axisalignedbb.isVecInside(entityPos))
            {
                if (0.0D < tmpDistance || tmpDistance == 0.0D)
                {
                    pointedEntity = entity;
                    tmpDistance = 0.0D;
                }
            }
            else if (movingobjectposition != null)
            {
                double d3 = entityPos.distanceTo(movingobjectposition.hitVec);

                if (d3 < tmpDistance || tmpDistance == 0.0D)
                {
                    if (entity == this.ridingEntity && !entity.canRiderInteract())
                    {
                        if (tmpDistance == 0.0D)
                        {
                            pointedEntity = entity;
                        }
                    }
                    else
                    {
                        pointedEntity = entity;
                        tmpDistance = d3;
                    }
                }
            }
        }

        return pointedEntity;
    }
    public static MovingObjectPosition rayTrace(Entity owner, double par1, float par3)
    {
        Vec3 vec3 = getPosition(owner);
        Vec3 vec31 = getLook(owner, par3);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        return owner.worldObj.func_147447_a(vec3, vec32, false, false, true);
    }
    public static Vec3 getPosition(Entity owner)
    {
        return Vec3.createVectorHelper(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ);
    }
    public static Vec3 getLook(Entity owner, float rotMax)
    {
        float f1;
        float f2;
        float f3;
        float f4;

        if (rotMax == 1.0F)
        {
            f1 =  MathHelper.cos(-owner.rotationYaw   * 0.017453292F - (float)Math.PI);
            f2 =  MathHelper.sin(-owner.rotationYaw   * 0.017453292F - (float)Math.PI);
            f3 = -MathHelper.cos(-owner.rotationPitch * 0.017453292F);
            f4 =  MathHelper.sin(-owner.rotationPitch * 0.017453292F);
            return Vec3.createVectorHelper((double)(f2 * f3), (double)f4, (double)(f1 * f3));
        }
        else
        {
            f1 = owner.prevRotationPitch + (owner.rotationPitch - owner.prevRotationPitch) * rotMax;
            f2 = owner.prevRotationYaw + (owner.rotationYaw - owner.prevRotationYaw) * rotMax;
            f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
            f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            return Vec3.createVectorHelper((double)(f4 * f5), (double)f6, (double)(f3 * f5));
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

        this.prevRotationPitch = rotationPitch;
        this.prevRotationYaw = rotationYaw;
        rotationPitch = ridingEntity.rotationPitch + this.hitPitch;
        rotationYaw = ridingEntity.rotationYaw + this.hitYaw;

        setPosition(posX, posY, posZ);

        setRotation(rotationYaw,rotationPitch);

        //■死亡チェック
        if(ticksExisted >= 200/*getLifeTime()*/) {

            if(!ridingEntity.isDead){
                if(!worldObj.isRemote){
                    float magicDamage = Math.max(1.0f, AttackLevel / 2);
                    ridingEntity.hurtResistantTime = 0;
                    DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
                    ridingEntity.attackEntityFrom(ds, magicDamage);
                    if(blade != null && ridingEntity instanceof EntityLivingBase){
                        if(thrower != null){
                            StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.BreakPhantomSword);
                            ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)ridingEntity,(EntityLivingBase)thrower);
                        }

                        ridingEntity.motionX = 0;
                        ridingEntity.motionY = 0;
                        ridingEntity.motionZ = 0;
                        ridingEntity.addVelocity(0.0, 0.1D, 0.0);

                        ((EntityLivingBase) ridingEntity).hurtTime = 1;
                    }
                }
            }

            setDead();
        }
    }

    /**
     * 向き初期化
     */
    protected void initRotation(){

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI);
        }
    }

    /**
     *
     * @return hitInfo : IEntitySelector (Destructable / Attackable)
     */
    protected MovingObjectPosition getMovingObjectPosition(){
        Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
        vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (movingobjectposition != null) {
            int x = MathHelper.floor_double(movingobjectposition.hitVec.xCoord);
            int y = MathHelper.floor_double(movingobjectposition.hitVec.yCoord);
            int z = MathHelper.floor_double(movingobjectposition.hitVec.zCoord);

            int offset = -1;
            Block block = worldObj.getBlock(x, y + offset, z);

            if (block != null)
                if (block.getCollisionBoundingBoxFromPool(worldObj, x, y + offset, z) == null)
                    movingobjectposition = null;
                else
                    vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
        }

        Entity entity = null;

        AxisAlignedBB bb = this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D);

        IEntitySelector[] selectors = {ItemSlashBlade.DestructableSelector,ItemSlashBlade.AttackableSelector};
        for(IEntitySelector selector : selectors){
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, bb, selector);
            list.removeAll(alreadyHitEntity);

            if(selector.equals(ItemSlashBlade.AttackableSelector) && getTargetEntityId() != 0){
                Entity target = worldObj.getEntityByID(getTargetEntityId());
                if(target != null){
                    if(target.boundingBox.intersectsWith(bb))
                        list.add(target);
                }
            }

            double d0 = 0.0D;
            int i;
            float f1;

            for (i = 0; i < list.size(); ++i)
            {
                Entity entity1 = (Entity)list.get(i);

                if(entity1 instanceof EntityPhantomSwordBase)
                    if(((EntityPhantomSwordBase) entity1).getThrower() == this.getThrower())
                        continue;

                if (entity1.canBeCollidedWith())
                {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double)f1, (double)f1, (double)f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

                    if (movingobjectposition1 != null)
                    {
                        double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null)
            {
                movingobjectposition = new MovingObjectPosition(entity);
                movingobjectposition.hitInfo = selector;
                break;
            }
        }


        if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;

            if (entityplayer.capabilities.disableDamage || (this.getThrower() != null && this.getThrower() instanceof EntityPlayer && !((EntityPlayer)this.getThrower()).canAttackPlayer(entityplayer)))
            {
                movingobjectposition = null;
            }
        }

        return movingobjectposition;
    }

    public void doRotation(){

        if(doTargeting()) return;

        float f2;
        f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f2) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {
            ;
        }
    }

    public void normalizeRotation(){

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }
    }

    protected void destructEntity(Entity target){

        if(this.thrower == null) return;

        StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.DestructObject);

        boolean isDestruction = true;

        if(target instanceof EntityFireball){
            if((((EntityFireball)target).shootingEntity != null && ((EntityFireball)target).shootingEntity.getEntityId() == this.thrower.getEntityId())){
                isDestruction = false;
            }else if(this.thrower instanceof  EntityLivingBase){
                isDestruction = !target.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this.thrower), this.AttackLevel);
            }
        }else if(target instanceof EntityArrow){
            if((((EntityArrow)target).shootingEntity != null && ((EntityArrow)target).shootingEntity.getEntityId() == this.thrower.getEntityId())){
                isDestruction = false;
            }
        }else if(target instanceof EntityThrowable){
            if((((EntityThrowable)target).getThrower() != null && ((EntityThrowable)target).getThrower().getEntityId() == this.thrower.getEntityId())){
                isDestruction = false;
            }
        }

        if(isDestruction && target instanceof IThrowableEntity){
            if((((IThrowableEntity)target).getThrower() != null && ((IThrowableEntity)target).getThrower().getEntityId() == this.thrower.getEntityId())){
                isDestruction = false;
            }
        }

        if(isDestruction){
            target.motionX = 0;
            target.motionY = 0;
            target.motionZ = 0;
            target.setDead();

            for (int var1 = 0; var1 < 10; ++var1)
            {
                Random rand = this.getRand();
                double var2 = rand.nextGaussian() * 0.02D;
                double var4 = rand.nextGaussian() * 0.02D;
                double var6 = rand.nextGaussian() * 0.02D;
                double var8 = 10.0D;
                this.worldObj.spawnParticle("explode", target.posX + (double)(rand.nextFloat() * target.width * 2.0F) - (double)target.width - var2 * var8, target.posY + (double)(rand.nextFloat() * target.height) - var4 * var8, target.posZ + (double)(rand.nextFloat() * target.width * 2.0F) - (double)target.width - var6 * var8, var2, var4, var6);
            }
        }

        StylishRankManager.doAttack(this.thrower);

        this.setDead();
    }

    protected void attackEntity(Entity target){

        if(this.thrower != null)
            this.thrower.getEntityData().setInteger("LastHitSummonedSwords",this.getEntityId());

        mountEntity(target);

        if(!this.worldObj.isRemote){
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
                target.addVelocity(0.0, 0.1D, 0.0);

                ((EntityLivingBase) target).hurtTime = 1;

                ((ItemSlashBlade)blade.getItem()).setDaunting(((EntityLivingBase) target));
            }
        }
    }

    protected void blastAttackEntity(Entity target){
        if(!this.worldObj.isRemote){
            float magicDamage = 1;
            target.hurtResistantTime = 0;
            DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
            target.attackEntityFrom(ds, magicDamage);

            if(blade != null && target instanceof EntityLivingBase && thrower != null && thrower instanceof EntityLivingBase){
                StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.PhantomSword);
                ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)target,(EntityLivingBase)thrower);

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                target.addVelocity(0.0, 0.1D, 0.0);

                ((EntityLivingBase) target).hurtTime = 1;

                ((ItemSlashBlade)blade.getItem()).setDaunting(((EntityLivingBase) target));
            }
        }
    }

    protected boolean onImpact(MovingObjectPosition mop)
    {

        boolean result = true;

        if (mop.entityHit != null){
            Entity target = mop.entityHit;

            if(mop.hitInfo.equals(ItemSlashBlade.AttackableSelector)){

                attackEntity(target);

            }else{ //(mop.hitInfo.equals(ItemSlashBlade.DestructableSelector)){

                destructEntity(target);
            }
        }else{
            if(!worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
            {
                /*
                if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer)
                    ((EntityPlayer)this.getThrower()).onCriticalHit(this);
                */
                //this.setDead();
                result = true;
            }
        }

        return result;
    }

    public void spawnParticle(){
        if (this.isInWater())
        {
            float trailLength;
            for (int l = 0; l < 4; ++l)
            {
                trailLength = 0.25F;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)trailLength, this.posY - this.motionY * (double)trailLength, this.posZ - this.motionZ * (double)trailLength, this.motionX, this.motionY, this.motionZ);
            }
        }
    }

    public void calculateSpeed(){
        float speedReductionFactor = 1.10F;

        if (this.isInWater())
            speedReductionFactor = 1.0F;

        this.motionX *= (double)speedReductionFactor;
        this.motionY *= (double)speedReductionFactor;
        this.motionZ *= (double)speedReductionFactor;
        //this.motionY -= (double)fallingFactor;

    }

    //■毎回呼ばれる。移動処理とか当り判定とかもろもろ。
    @Override
    public void onUpdate()
    {
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;
        super.onUpdate();

        if(this.ridingEntity2 != null){
            updateRidden();
        }else{

            if (this.ticksExisted >= getLifeTime())
            {
                this.setDead();
            }

            initRotation();

            MovingObjectPosition movingobjectposition = getMovingObjectPosition();

            if (movingobjectposition != null)
            {
                if(onImpact(movingobjectposition))
                    return;
            }

            calculateSpeed();

            doRotation();

            if(getInterval() < this.ticksExisted)
                moveEntity(this.motionX, this.motionY, this.motionZ);

            normalizeRotation();

            spawnParticle();

        }
    }

    @Override
    public void setDead() {
        if(this.thrower != null && this.thrower instanceof EntityPlayer)
            ((EntityPlayer)thrower).onCriticalHit(this);
        /*
        if(!this.worldObj.isRemote)
            System.out.println("dead" + this.ticksExisted);
            */

        this.worldObj.playSoundEffect(this.prevPosX, this.prevPosY, this.prevPosZ, "dig.glass", 0.25F, 1.6F);

        AxisAlignedBB bb = this.boundingBox.expand(1.0D, 1.0D, 1.0D);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, bb, ItemSlashBlade.AttackableSelector);
        list.removeAll(alreadyHitEntity);
        for(Entity target : list){
            if(target == null) continue;
            blastAttackEntity(target);
        }

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
    public void moveEntity(double par1, double par3, double par5) {
        super.moveEntity(par1, par3, par5);
    }


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


    public Entity ridingEntity2 = null;

    public Entity getRidingEntity(){
        return this.ridingEntity2;
    }

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

            this.ticksExisted = 0;
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


    //IProjectile
    @Override
    public void setThrowableHeading(double v, double v2, double v3, float v4, float v5) {

    }

    //IThrowableEntity
    @Override
    public Entity getThrower() {
        if(this.thrower == null){
            int id = getThrowerEntityId();
            if(id != 0){
                this.thrower = this.worldObj.getEntityByID(id);
            }
        }

        return this.thrower;
    }

    @Override
    public void setThrower(Entity entity) {
        if(entity != null)
            setThrowerEntityId(entity.getEntityId());
        this.thrower = entity;
    }
}
