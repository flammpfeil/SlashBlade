package mods.flammpfeil.slashblade.entity;

import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/05/08.
 */
public class EntityHelmBrakerManager extends Entity implements IThrowableEntity {
    /**
     * ★撃った人
     */
    protected Entity thrower;

    protected ItemStack blade = null;

    /**
     * ★多段Hit防止用List
     */
    protected List<Entity> alreadyHitEntity = new ArrayList<Entity>();

    /**
     * ■コンストラクタ
     * @param par1World
     */
    public EntityHelmBrakerManager(World par1World)
    {
        super(par1World);

        this.isImmuneToFire = true;

        this.motionY = -1.0f;
        this.stepHeight = 1.0f;
    }

    public EntityHelmBrakerManager(World par1World, EntityLivingBase entityLiving, boolean isSingleHit){
        this(par1World, entityLiving);
        this.setIsSingleHit(isSingleHit);
    }

    public EntityHelmBrakerManager(World par1World, EntityLivingBase entityLiving)
    {
        this(par1World);

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
        setSize(2.0F, 2.0F);

        //■初期位置・初期角度等の設定
        setLocationAndAngles(thrower.posX,
                thrower.posY,
                thrower.posZ,
                thrower.rotationYaw,
                thrower.rotationPitch);
    }


    private static final int SINGLE_HIT = 12;
    private static final int LIFETIME = 13;
    private static final int THROWER_ENTITY_ID = 14;
    /**
     * ■イニシャライズ
     */
    @Override
    protected void entityInit() {

        //isMultiHit
        this.getDataWatcher().addObject(SINGLE_HIT, (byte)0);

        //lifetime
        this.getDataWatcher().addObject(LIFETIME, 20);

        //lifetime
        this.getDataWatcher().addObject(THROWER_ENTITY_ID, 0);

    }


    public boolean isSingleHit(){
        return this.getDataWatcher().getWatchableObjectByte(SINGLE_HIT) != 0;
    }
    public void setIsSingleHit(boolean isSingleHit){
        this.getDataWatcher().updateObject(SINGLE_HIT,(byte)(isSingleHit ? 1 : 0));
    }

    public int getLifeTime(){
        return this.getDataWatcher().getWatchableObjectInt(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataWatcher().updateObject(LIFETIME, lifetime);
    }

    public int getThrowerEntityId(){
        return this.getDataWatcher().getWatchableObjectInt(THROWER_ENTITY_ID);
    }
    public void setThrowerEntityId(int entityid){
        this.getDataWatcher().updateObject(THROWER_ENTITY_ID, entityid);
    }

    //■毎回呼ばれる。移動処理とか当り判定とかもろもろ。
    @Override
    public void onUpdate()
    {
        super.onUpdate();


        this.fallDistance = 30;
        this.moveEntity(motionX,motionY,motionZ);

        AxisAlignedBB bb = null;
        ItemSlashBlade.ComboSequence combo = ItemSlashBlade.ComboSequence.None;
        //super.onUpdate();
        if(this.getThrower() != null && this.getThrower() instanceof EntityLivingBase) {
            EntityLivingBase owner = (EntityLivingBase)this.getThrower();

            if(blade == null){
                blade = owner.getHeldItem();
                if(blade == null || !(blade.getItem() instanceof ItemSlashBlade))
                {
                    setDead();
                    return;
                }
            }

            ItemSlashBlade itemBlade = (ItemSlashBlade) blade.getItem();
            bb = itemBlade.getBBofCombo(blade, ItemSlashBlade.ComboSequence.HelmBraker,(EntityLivingBase) getThrower());

            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);

            combo = ItemSlashBlade.getComboSequence(tag);
        }


        //■死亡チェック
        if(ticksExisted >= getLifeTime()
                || combo != ItemSlashBlade.ComboSequence.HelmBraker
                || this.getThrower() == null
                || (this.getThrower() != null && (this.getThrower().onGround
                        || this.getThrower().isInWater()))) {

            alreadyHitEntity.clear();
            alreadyHitEntity = null;
            setDead();
            return;
        }

        if(1 < ticksExisted && getThrower() != null){
            getThrower().moveEntity(0,-1.0,0);

            getThrower().fallDistance = 0;
        }

        if(!worldObj.isRemote)
        {

            {
                if(this.getThrower() instanceof EntityLivingBase){
                    EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.DestructableSelector);

                    StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.DestructObject);

                    list.removeAll(alreadyHitEntity);
                    alreadyHitEntity.addAll(list);
                    for(Entity curEntity : list){
                        boolean isDestruction = true;

                        if(curEntity instanceof EntityFireball){
                            if((((EntityFireball)curEntity).shootingEntity != null && ((EntityFireball)curEntity).shootingEntity.getEntityId() == entityLiving.getEntityId())){
                                isDestruction = false;
                            }else{
                                isDestruction = !curEntity.attackEntityFrom(DamageSource.causeMobDamage(entityLiving), 1.0f);
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
                                this.worldObj.spawnParticle("explode"
                                        , curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8
                                        , curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8
                                        , curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8
                                        , var2, var4, var6);
                            }
                        }

                        StylishRankManager.doAttack(this.thrower);
                    }
                }
                if(isSingleHit() || this.ticksExisted % 3 == 0){
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.AttackableSelector);
                    list.removeAll(alreadyHitEntity);

                    if(isSingleHit())
                        alreadyHitEntity.addAll(list);

                    StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.HelmBraker);

                    if(blade != null){
                        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                        for(Entity curEntity : list){
                            curEntity.hurtResistantTime = 0;

                            if(thrower instanceof EntityPlayer){
                                ItemSlashBlade itemBlade = (ItemSlashBlade)blade.getItem();
                                itemBlade.attackTargetEntity(blade, curEntity, (EntityPlayer)thrower, true);
                            }
                            else{
                                DamageSource ds = new EntityDamageSource("mob", this.getThrower());
                                curEntity.attackEntityFrom(ds, 10);
                                if(blade != null && curEntity instanceof EntityLivingBase)
                                    ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)curEntity,(EntityLivingBase)thrower);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void updateFallState(double p_70064_1_, boolean p_70064_3_) {
        super.updateFallState(p_70064_1_, p_70064_3_);
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
     * ■Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {}

    /**
     * ■Called by portal blocks when an entity is within it.
     */
    @Override
    public void setInPortal() {
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

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }
}
