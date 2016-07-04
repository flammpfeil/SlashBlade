package mods.flammpfeil.slashblade.entity;

import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.EntityDrive;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/05/08.
 */
public class EntityMaximumBetManager extends Entity implements IThrowableEntity {
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
    public EntityMaximumBetManager(World par1World)
    {
        super(par1World);
        ticksExisted = 0;
    }

    @Override
    protected void entityInit() {
        //entityid
        this.getDataWatcher().addObject(4, 0);
    }

    int getThrowerEntityID(){
        return this.getDataWatcher().getWatchableObjectInt(4);
    }

    void setThrowerEntityID(int id){
        this.getDataWatcher().updateObject(4,id);
    }

    public EntityMaximumBetManager(World par1World, EntityLivingBase entityLiving)
    {
        this(par1World);

        //■Y軸のオフセット設定
        yOffset = entityLiving.getEyeHeight()/2.0F;

        //■撃った人
        thrower = entityLiving;

        setThrowerEntityID(thrower.getEntityId());

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
        setSize(64.0F, 32.0F);

        //■初期位置・初期角度等の設定
        setLocationAndAngles(thrower.posX,
                thrower.posY,
                thrower.posZ,
                thrower.rotationYaw,
                thrower.rotationPitch);
    }

    //■毎回呼ばれる。移動処理とか当り判定とかもろもろ。
    @Override
    public void onUpdate()
    {
        //super.onUpdate();

        if(this.getThrower() != null){
            this.getThrower().motionY = 0;
        }

        if(this.thrower == null && this.getThrowerEntityID() != 0){
            this.thrower = this.worldObj.getEntityByID(this.getThrowerEntityID());
        }

        if(this.blade == null && this.getThrower() != null && this.getThrower() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer)this.getThrower();
            ItemStack stack = player.getHeldItem();
            if(stack.getItem() instanceof ItemSlashBlade)
                this.blade = stack;
        }

        if(this.ticksExisted == 1 && this.getThrower() != null) {
            this.getThrower().motionX = 0;
            this.getThrower().motionY = 0;
            this.getThrower().motionZ = 0;

            if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer){
                EntityPlayer player = (EntityPlayer)this.getThrower();
                player.playSound("flammpfeil.slashblade:swingblade", 1.0F, 0.5F + player.getRNG().nextFloat() * 0.05f);
            }

            doAttack(ItemSlashBlade.ComboSequence.SlashEdge);
        }

        //■死亡チェック
        if(ticksExisted >= 5) {

            if(blade != null){
                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                ItemSlashBlade bladeItem = (ItemSlashBlade) blade.getItem();

                ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.ReturnEdge);
                if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer) {
                    bladeItem.doSwingItem(blade, (EntityPlayer) this.getThrower());
                    EntityPlayer player = (EntityPlayer)this.getThrower();
                    player.playSound("flammpfeil.slashblade:swingblade", 1.0F, 0.5F + player.getRNG().nextFloat() * 0.05f);
                }

                doAttack(ItemSlashBlade.ComboSequence.ReturnEdge);
            }

            alreadyHitEntity.clear();
            alreadyHitEntity = null;
            setDead();
        }
    }

    public void doAttack(ItemSlashBlade.ComboSequence combo){
        if(blade == null) return;
        if(!(blade.getItem() instanceof  ItemSlashBlade)) return;

        ItemSlashBlade itemBlade = (ItemSlashBlade)blade.getItem();

        if(worldObj.isRemote) return;
        if(!(this.getThrower() instanceof EntityLivingBase)) return;
        EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();

        double dAmbit = 0.5D;
        AxisAlignedBB bb = itemBlade.getBBofCombo(blade, combo, entityLiving);

        bb = bb.expand(0,dAmbit,0);

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
                    this.worldObj.spawnParticle("explode", curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8, curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8, curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8, var2, var4, var6);
                }
            }

            StylishRankManager.doAttack(this.thrower);
        }


        list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.AttackableSelector);
        list.removeAll(alreadyHitEntity);


        StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.Spear);

        if(blade != null){
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
            for(Entity curEntity : list){
                curEntity.hurtResistantTime = 0;
                if(thrower instanceof EntityPlayer){
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

        float magicDamage = 0.5f;
        if(this.getThrower() != null){
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
            float baseModif = itemBlade.getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, blade);

            int rank = StylishRankManager.getStylishRank(getThrower());
            magicDamage = baseModif;
            if(5 <= rank)
                magicDamage += ItemSlashBlade.AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));
        }


        EntityDrive entityDrive = new EntityDrive(this.worldObj, entityLiving, magicDamage,true,90.0f - Math.abs(combo.swingDirection));
        if (entityDrive != null) {
            entityDrive.setInitialSpeed(1.25f);
            entityDrive.setLifeTime(20);
            this.worldObj.spawnEntityInWorld(entityDrive);
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

    /**
     * ■Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    @Override
    public void mountEntity(Entity par1Entity) {}

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
