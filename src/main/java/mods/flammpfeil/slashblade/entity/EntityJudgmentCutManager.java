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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 14/05/08.
 */
public class EntityJudgmentCutManager extends Entity implements IThrowableEntity {
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
    public EntityJudgmentCutManager(World par1World)
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

    public EntityJudgmentCutManager(World par1World, EntityLivingBase entityLiving)
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

        if(this.thrower == null && this.getThrowerEntityID() != 0){
            this.thrower = this.worldObj.getEntityByID(this.getThrowerEntityID());
        }

        if(this.blade == null && this.getThrower() != null && this.getThrower() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer)this.getThrower();
            ItemStack stack = player.getHeldItem();
            if(stack.getItem() instanceof ItemSlashBlade)
                this.blade = stack;
        }

        if(this.thrower != null) {
            this.thrower.motionX = 0;
            this.thrower.motionY = 0;
            this.thrower.motionZ = 0;

            if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer){
                EntityPlayer player = (EntityPlayer)this.getThrower();

                if(this.ticksExisted < 3)
                    player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.portal", 1.0F, 1.0F);

                if(this.ticksExisted < 8){
                    for(int i = 0;i < 20; i++)
                    {

                        double d0 = player.getRNG().nextGaussian() * 0.2D;
                        double d1 = player.getRNG().nextGaussian() * 0.2D;
                        double d2 = player.getRNG().nextGaussian() * 0.2D;
                        double d3 = 16.0D;
                        this.worldObj.spawnParticle("witchMagic"
                                , player.posX + (double)(player.getRNG().nextFloat() * player.width * 2.0F) - (double)player.width - d0 * d3
                                , player.posY // + (double)(this.itemRand.nextFloat() * par3Entity.height) - d1 * d3
                                , player.posZ + (double)(player.getRNG().nextFloat() * player.width * 2.0F) - (double)player.width - d2 * d3, d0, d1, d2);


                        /*this.worldObj.spawnParticle("portal",
                                this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width,
                                this.posY + this.rand.nextDouble() * (double)this.height - 0.25D,
                                this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width,
                                (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
                                */
                    }
                    player.worldObj.playSoundAtEntity(player, "mob.blaze.hit", 1.0F, 1.0F);
                }
            }
        }

        if(!worldObj.isRemote)
        {

            AxisAlignedBB bb = this.boundingBox.copy().offset(0, -this.height / 2, 0);

            //足止め
            if(this.ticksExisted == 2 && this.getThrower() != null){
                List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.AttackableSelector);
                list.removeAll(alreadyHitEntity);

                if(blade != null){
                    NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                    for(Entity curEntity : list){
                        if(curEntity instanceof EntityLivingBase){
                            int stanTicks = 40;

                            if(!curEntity.worldObj.isRemote){
                                ((EntityLivingBase) curEntity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), stanTicks, 30, true));
                                ((EntityLivingBase) curEntity).attackTime = stanTicks;
                            }

                            StunManager.setStun((EntityLivingBase) curEntity, stanTicks);
                            StunManager.setFreeze((EntityLivingBase) curEntity, stanTicks);


                            for(int i = 0; i<5; i++)
                                this.worldObj.spawnParticle("portal",
                                    curEntity.posX + (this.rand.nextDouble() - 0.5D) * (double)curEntity.width,
                                    curEntity.posY + this.rand.nextDouble() * (double)curEntity.height - 0.25D,
                                    curEntity.posZ + (this.rand.nextDouble() - 0.5D) * (double)curEntity.width,
                                    (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);

                        }
                    }
                }
            }

            if(this.ticksExisted == 25 && this.getThrower() != null){


                List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.getThrower(), bb, ItemSlashBlade.AttackableSelector);
                list.removeAll(alreadyHitEntity);

                StylishRankManager.setNextAttackType(this.getThrower(), StylishRankManager.AttackTypes.JudgmentCut);

                if(blade != null){
                    NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                    ItemSlashBlade bladeItem = (ItemSlashBlade)blade.getItem();

                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, blade);
                    float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);
                    for(Entity curEntity : list){
                        if(!(this.getThrower() instanceof EntityPlayer)) continue;

                        bladeItem.attackTargetEntity(blade, curEntity, (EntityPlayer)this.getThrower(), true);
                        ((EntityPlayer)this.getThrower()).onEnchantmentCritical(curEntity);


                        curEntity.hurtResistantTime = 0;

                        for(int i = 0; i < 5;i++){

                            EntityDrive entityDrive = new EntityDrive(this.worldObj, (EntityLivingBase)this.getThrower(), Math.min(1.0f,magicDamage), true,0);


                            float rotationYaw = curEntity.rotationYaw + 60 * i + (entityDrive.getRand().nextFloat() - 0.5f) * 60;
                            float rotationPitch = (entityDrive.getRand().nextFloat() - 0.5f) * 60;

                            float fYawDtoR = (  rotationYaw / 180F) * (float)Math.PI;
                            float fPitDtoR = (rotationPitch / 180F) * (float)Math.PI;
                            float fYVecOfst = 0.5f;

                            float motionX = -MathHelper.sin(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst * 2;
                            float motionY = -MathHelper.sin(fPitDtoR) * fYVecOfst;
                            float motionZ =  MathHelper.cos(fYawDtoR) * MathHelper.cos(fPitDtoR) * fYVecOfst * 2;

                            entityDrive.setLocationAndAngles(curEntity.posX - motionX,
                                    curEntity.posY + (double)curEntity.getEyeHeight()/2D - motionY,
                                    curEntity.posZ - motionZ,
                                    rotationYaw,
                                    rotationPitch);
                            entityDrive.setDriveVector(fYVecOfst);
                            entityDrive.setLifeTime(8);
                            entityDrive.setIsMultiHit(false);

                            int rank = StylishRankManager.getStylishRank(this.getThrower());
                            if(5 <= rank) {
                                EnumSet<ItemSlashBlade.SwordType> type = bladeItem.getSwordType(blade);
                                entityDrive.setIsSlashDimension(type.contains(ItemSlashBlade.SwordType.FiercerEdge));
                            }

                            entityDrive.setRoll(90.0f + 120 * (entityDrive.getRand().nextFloat() - 0.5f));
                            if (entityDrive != null) {
                                this.worldObj.spawnEntityInWorld(entityDrive);
                            }
                        }

                        if(!curEntity.worldObj.isRemote){
                            for(int i = 0; i< 2; i++) {
                                EntitySlashDimension dim = new EntitySlashDimension(curEntity.worldObj, (EntityLivingBase) getThrower(), 1);
                                if (dim != null) {
                                    dim.setPosition(curEntity.posX + (this.rand.nextFloat() - 0.5) * 5.0, curEntity.posY + curEntity.height * this.rand.nextFloat(), curEntity.posZ + (this.rand.nextFloat() - 0.5) * 5.0);
                                    dim.setLifeTime(10 + i * 3);
                                    dim.setIsSlashDimension(true);
                                    curEntity.worldObj.spawnEntityInWorld(dim);
                                }
                            }
                        }

                    }
                }
            }
        }


        //■死亡チェック
        if(ticksExisted >= 30) {

            if(blade != null){
                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                ItemSlashBlade bladeItem = (ItemSlashBlade)blade.getItem();

                ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.Battou);
                if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer)
                    bladeItem.doSwingItem(blade,(EntityPlayer)this.getThrower());

                blade.setItemDamage(blade.getMaxDamage() / 2);
            }

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
