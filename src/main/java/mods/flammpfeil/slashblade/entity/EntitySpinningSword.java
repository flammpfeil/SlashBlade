package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.Taunt;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 2017/02/02.
 */
public class EntitySpinningSword extends EntitySummonedSwordBase {

    public EntitySpinningSword(World par1World)
    {
        super(par1World);
    }

    public EntitySpinningSword(World par1World, EntityLivingBase entityLiving)
    {
        this(par1World);

        //■撃った人
        setThrower(entityLiving);

        blade = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
        if(blade != null && !(blade.getItem() instanceof ItemSlashBlade)){
            blade = null;
        }

        //■生存タイマーリセット
        ticksExisted = 0;

        //■サイズ変更
        setSize(2.0F, 2.0F);


        //■初期位置・初期角度等の設定
        Vec3d look = getVectorForRotation(0, thrower.rotationYaw + 20);
        setLocationAndAngles(
                thrower.posX + look.x * 1.0f,
                thrower.posY + thrower.height,
                thrower.posZ + look.z * 1.0f,
                0,
                0);

    }

    //■毎回呼ばれる。移動処理とか当り判定とかもろもろ。
    @Override
    public void onUpdate()
    {
        //super.onUpdate();


        if(this.getThrower() != null && this.getThrower() instanceof EntityLivingBase){
            EntityLivingBase owner = (EntityLivingBase)this.getThrower();

            byte command = owner.getEntityData().getByte("SB.MCS");
            if(2.5 < owner.getDistanceToEntity(this)
                    || !owner.onGround
                    || 0 == (command & MessageMoveCommandState.CAMERA)
                    || owner.getHeldItemMainhand() == null
                    || !(owner.getHeldItemMainhand().getItem() instanceof ItemSlashBlade)) {
                setDead();
                return;
            }

            if(blade == null){
                ItemStack stack = owner.getHeldItemMainhand();
                if(stack != null && stack.getItem() instanceof ItemSlashBlade)
                    blade = stack;
            }
        }

        if(!world.isRemote)
        {

            if(this.getThrower() != null){
                double dAmbit = 1.0D;
                AxisAlignedBB bb = new AxisAlignedBB(
                        this.posX - dAmbit, this.posY - dAmbit, this.posZ - dAmbit,
                        this.posX + dAmbit, this.posY + dAmbit, this.posZ + dAmbit);

                if(this.getThrower() instanceof EntityLivingBase){
                    EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();
                    List<Entity> list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorDestructable.getInstance());

                    StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.DestructObject);

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
                                Random rand = this.rand;
                                double var2 = rand.nextGaussian() * 0.02D;
                                double var4 = rand.nextGaussian() * 0.02D;
                                double var6 = rand.nextGaussian() * 0.02D;
                                double var8 = 10.0D;
                                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL
                                        , curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8
                                        , curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8
                                        , curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8
                                        , var2, var4, var6);
                            }
                        }

                        StylishRankManager.doAttack(this.thrower);
                    }
                }

                if(this.ticksExisted % 6 == 0){
                    this.world.playSound(null, this.posX, this.posY, this.posZ
                            , SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.NEUTRAL,0.35f,1.1f);

                    List<Entity> list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorAttackable.getInstance());

                    StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.RapidSlash);

                    if(blade != null){
                        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
                        for(Entity curEntity : list){
                            attackEntity(curEntity);
                        }
                    }
                }
            }
        }

        if(ticksExisted == 1){
            if(blade != null && getThrower() != null && getThrower() instanceof EntityLivingBase)
                Taunt.fire(blade, (EntityLivingBase)getThrower());
        }else if(getLifeTime() < ticksExisted) {
            Taunt.fire(blade, (EntityLivingBase)getThrower());

            AxisAlignedBB bb = getThrower().getEntityBoundingBox();
            bb = bb.grow(10, 5, 10);
            List<Entity> list = world.getEntitiesInAABBexcluding(getThrower(), bb, EntitySelectorAttackable.getInstance());

            if(0 < list.size()) {
                StylishRankManager.addRankPoint(getThrower(), StylishRankManager.AttackTypes.TauntFinish);
            }

            setDead();
        }
    }


    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    public void move(MoverType moverType, double x, double y, double z) {
        //super.move(moverType, x, y, z);
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
    public int getBrightnessForRender()
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

        int i = super.getBrightnessForRender();
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
    public float getBrightness()
    {
        float f1 = super.getBrightness();
        float f2 = 0.9F;
        f2 = f2 * f2 * f2 * f2;
        return f1 * (1.0F - f2) + f2;
        //return super.getBrightness();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {}

    @Override
    public void setPortal(BlockPos p_181015_1_) {
    }

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

    @Override
    public void setInWeb() {}


    //IThrowableEntity
    @Override
    public Entity getThrower() {
        if(this.thrower == null){
            int id = getThrowerEntityId();
            if(id != 0){
                this.thrower = this.getEntityWorld().getEntityByID(id);
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
    protected void blastAttackEntity(Entity target){
        if(!this.world.isRemote){
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

    @Override
    protected void attackEntity(Entity target){

        if(this.thrower != null)
            this.thrower.getEntityData().setInteger("LastHitSummonedSwords",this.getEntityId());

        if(!this.world.isRemote){
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
}
