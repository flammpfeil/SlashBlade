package mods.flammpfeil.slashblade.entity;

import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.TeleportCanceller;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/05/08.
 */
public class EntitySlashDimension extends Entity implements IThrowableEntity {
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
    public EntitySlashDimension(World par1World)
    {
        super(par1World);
        ticksExisted = 0;

        getEntityData().setInteger("seed", rand.nextInt(50));
    }

    public EntitySlashDimension(World par1World, EntityLivingBase entityLiving, float AttackLevel, boolean multiHit){
        this(par1World, entityLiving, AttackLevel);
        this.setIsSingleHit(multiHit);
    }

    public EntitySlashDimension(World par1World, EntityLivingBase entityLiving, float AttackLevel)
    {
        this(par1World);

        this.AttackLevel = AttackLevel;

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
        setSize(4.0F, 4.0F);

    }




    private static final int LIFETIME = 3;
    private static final int SINGLE_HIT = 4;
    private static final int IS_SLASH_DIMENSION = 5;

    private static final int THROWER_ENTITY_ID = 6;
    private static final int INTERVAL = 7;
    private static final int COLOR = 8;

    /**
     * ■イニシャライズ
     */
    @Override
    protected void entityInit() {
        //lifetime
        this.getDataWatcher().addObject(LIFETIME, 20);

        //isMultiHit
        this.getDataWatcher().addObject(SINGLE_HIT, (byte)0);

        //lifetime
        this.getDataWatcher().addObject(IS_SLASH_DIMENSION,  (byte)0);

        //EntityId
        this.getDataWatcher().addObject(THROWER_ENTITY_ID, 0);

        //interval
        this.getDataWatcher().addObject(INTERVAL, 7);

        //color
        this.getDataWatcher().addObject(COLOR, 0x3333FF);

    }

    public boolean getIsSingleHit(){
        return this.getDataWatcher().getWatchableObjectByte(SINGLE_HIT) != 0;
    }
    public void setIsSingleHit(boolean isSingleHit){
        this.getDataWatcher().updateObject(SINGLE_HIT, isSingleHit ? (byte)1 : (byte)0);
    }

    public int getLifeTime(){
        return this.getDataWatcher().getWatchableObjectInt(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataWatcher().updateObject(LIFETIME,lifetime);
    }

    public boolean getIsSlashDimension(){
        return this.getDataWatcher().getWatchableObjectByte(IS_SLASH_DIMENSION) != 0;
    }
    public void setIsSlashDimension(boolean isSlashDimension){
        this.getDataWatcher().updateObject(IS_SLASH_DIMENSION, isSlashDimension ? (byte)1 : (byte)0);
    }

    public int getInterval(){
        return this.getDataWatcher().getWatchableObjectInt(INTERVAL);
    }
    public void setInterval(int value){
        this.getDataWatcher().updateObject(INTERVAL,value);
    }

    public int getColor(){
        return this.getDataWatcher().getWatchableObjectInt(COLOR);
    }
    public void setColor(int value){
        this.getDataWatcher().updateObject(COLOR,value);
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

        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;

        if(!worldObj.isRemote)
        {
            if(ticksExisted < 8 && ticksExisted % 2 == 0) {
                this.playSound("mob.wither.hurt", 0.2F, 0.5F + 0.25f * this.rand.nextFloat());
            }

            {
                AxisAlignedBB bb = this.boundingBox;

                if(this.getThrower() instanceof EntityLivingBase){
                    EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.DestructableSelector);

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

                if(getIsSingleHit() || this.ticksExisted % 2 == 0){
                    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.AttackableSelector);
                    list.removeAll(alreadyHitEntity);

                    if(getIsSingleHit())
                        alreadyHitEntity.addAll(list);

                    float magicDamage = Math.max(1.0f, AttackLevel);

                    StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.SlashDimMagic);

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

                        Vec3 pos = Vec3.createVectorHelper(curEntity.posX, curEntity.posY, curEntity.posZ);//.getPositionVector();

                        TeleportCanceller.setCancel(curEntity);

                        curEntity.hurtResistantTime = 0;
                        DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage().setProjectile();


                        if(blade != null && curEntity instanceof EntityLivingBase)
                            ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)curEntity,(EntityLivingBase)thrower);

                        /*
                        if(!(curEntity.getPositionVector().equals(pos)))
                            curEntity.setPositionAndUpdate(pos.xCoord,pos.yCoord,pos.zCoord);
                        */

                        curEntity.motionX = 0;
                        curEntity.motionY = 0;
                        curEntity.motionZ = 0;

                        if(3 < this.ticksExisted){
                            if(blade != null && curEntity instanceof EntityLivingBase) {
                                if(getIsSlashDimension()){
                                    curEntity.addVelocity(
                                            0,
                                            0.5D,
                                            0);

                                }else{
                                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, blade);
                                    if(0 < level){
                                        curEntity.addVelocity(
                                                (double) (Math.sin(getThrower().rotationYaw * (float) Math.PI / 180.0F) * (float) level * 0.5F),
                                                0.2D,
                                                (double) (-Math.cos(getThrower().rotationYaw * (float) Math.PI / 180.0F) * (float) level * 0.5F));
                                    }else{
                                        curEntity.addVelocity(
                                                (double) (-Math.sin(getThrower().rotationYaw * (float) Math.PI / 180.0F) * 0.5),
                                                0.2D,
                                                (double) (Math.cos(getThrower().rotationYaw * (float) Math.PI / 180.0F)) * 0.5);

                                    }
                                }
                            }
                        }

                    }
                }
            }


            //地形衝突で消失
            /*
            if(!worldObj.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                this.setDead();
            }
            */

        }

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
}
