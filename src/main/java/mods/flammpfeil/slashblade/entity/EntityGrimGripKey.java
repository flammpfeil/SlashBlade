package mods.flammpfeil.slashblade.entity;

import com.google.common.base.Optional;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import net.minecraft.command.CommandResultStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Furia on 2016/05/30.
 */
public class EntityGrimGripKey extends Entity {

    EntityGrimGrip grip = null;
    int interval = 0;
    public int active = 0;

    public EntityGrimGripKey(World worldIn) {
        super(worldIn);

        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    private static final DataParameter<Integer> LIFETIME = EntityDataManager.<Integer>createKey(EntityGrimGripKey.class, DataSerializers.VARINT);
    protected static final DataParameter<Optional<BlockPos>> GRIM_GRIP_POS = EntityDataManager.<Optional<BlockPos>>createKey(EntityGrimGripKey.class, DataSerializers.OPTIONAL_BLOCK_POS);

    private static final DataParameter<Boolean> INVISIBLE = EntityDataManager.<Boolean>createKey(EntityGrimGripKey.class, DataSerializers.BOOLEAN);

    @Override
    protected void entityInit() {
        //lifetime
        this.getDataManager().register(LIFETIME, 60);
        //pos
        this.getDataManager().register(GRIM_GRIP_POS, Optional.<BlockPos>absent());

        //view
        this.getDataManager().register(INVISIBLE, false);

    }

    public int getLifeTime(){
        return this.getDataManager().get(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataManager().set(LIFETIME,lifetime);
    }

    public void setGrimGripPos(@Nullable BlockPos grimGripPos)
    {
        this.getDataManager().set(GRIM_GRIP_POS, Optional.fromNullable(grimGripPos));
    }
    public BlockPos getGrimGripPos()
    {
        BlockPos result = (BlockPos)((Optional)this.getDataManager().get(GRIM_GRIP_POS)).orNull();
        if(result == null)
            result = this.getPosition();
        return result;
    }


    public boolean isHide(){
        return this.getDataManager().get(INVISIBLE);
    }
    public void setHide(boolean hide){
        this.getDataManager().set(INVISIBLE,hide);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

        if (compound.hasKey("GPX"))
        {
            int i = compound.getInteger("GPX");
            int j = compound.getInteger("GPY");
            int k = compound.getInteger("GPZ");
            setGrimGripPos(new BlockPos(i, j, k));
        }
        else
        {
            setGrimGripPos(null);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        BlockPos blockpos = this.getGrimGripPos();

        if (blockpos != null)
        {
            compound.setInteger("GPX", blockpos.getX());
            compound.setInteger("GPY", blockpos.getY());
            compound.setInteger("GPZ", blockpos.getZ());
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(0 < this.interval)
            this.interval--;

        if(0 < this.active)
            this.active--;

        if (this.grip != null){
            if(!this.grip.isEntityAlive())
                removeGrip();
        }

        /*
        List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox());

        if (!list.isEmpty())
        {
            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity = (Entity)list.get(i);
                summonGrimGrip(entity);
            }
        }
        */
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {

        if(entityIn instanceof EntityLivingBase
                && ((EntityLivingBase) entityIn).getHeldItemMainhand().func_190926_b()
                && entityIn.isSneaking()
                && !entityIn.worldObj.isRemote){

            this.setDead();

            return true;
        }

        return super.hitByEntity(entityIn);
    }


    /*
    @Override
    public void applyEntityCollision(Entity entityIn) {
        //super.applyEntityCollision(entityIn);

        summonGrimGrip(entityIn);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return entityIn instanceof EntityLivingBase ? this.getEntityBoundingBox() : super.getCollisionBox(entityIn);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
    */


    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        super.onCollideWithPlayer(entityIn);

        summonGrimGrip(entityIn);
    }


    private void summonGrimGrip(Entity entityIn) {
        this.active = 60;

        if (this.grip != null){
            if(this.grip.isEntityAlive())
                this.grip.ticksExisted = 0;
            else
                removeGrip();
        }

        if(!this.worldObj.isRemote && this.grip == null && this.interval <= 0){
            EntityGrimGrip newGrip = new EntityGrimGrip(this.worldObj);
            BlockPos pos = getGrimGripPos();
            newGrip.setPositionAndRotation(pos.getX() + 0.5 ,pos.getY() + 0.5 ,pos.getZ() + 0.5, newGrip.rotationYaw, newGrip.rotationPitch);
            newGrip.setLifeTime(getLifeTime()); // 5sec
            newGrip.ticksExisted = 0;
            newGrip.setGlowing(true);

            if(this.worldObj.spawnEntityInWorld(newGrip)) {
                this.grip = newGrip;
            }
        }

        if(grip != null && entityIn instanceof EntityLivingBase && 0 < (entityIn.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK)){
            ItemStack stack = ((EntityLivingBase)entityIn).getHeldItemMainhand();
            if(stack != null && stack.getItem() instanceof ItemSlashBlade){
                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

                if(ItemSlashBlade.TargetEntityId.get(tag) != grip.getEntityId())
                    ItemSlashBlade.TargetEntityId.set(tag, grip.getEntityId());
            }
        }
    }

    private void removeGrip(){
        this.grip = null;
        this.interval = 10;
    }

    @Override
    protected boolean canTriggerWalking() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        //殴れない
        return active <= 0;
    }
}
