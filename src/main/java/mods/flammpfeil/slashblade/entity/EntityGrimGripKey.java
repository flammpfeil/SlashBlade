package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import mods.flammpfeil.slashblade.util.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Furia on 2016/05/30.
 */
public class EntityGrimGripKey extends EntityEx {

    EntityGrimGrip grip = null;
    int interval = 0;
    public int active = 0;

    public EntityGrimGripKey(World worldIn) {
        super(worldIn);

        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    private static final int LIFETIME = 7;
    private static final int GRIM_GRIP_POS = 8;

    private static final int INVISIBLE = 9;

    @Override
    protected void entityInit() {
        //lifetime
        this.getDataWatcher().addObject(LIFETIME, 60);
        //pos
        this.getDataWatcher().addObject(GRIM_GRIP_POS, "");

        //view
        this.getDataWatcher().addObject(INVISIBLE, (byte)0);

    }

    public int getLifeTime(){
        return this.getDataWatcher().getWatchableObjectInt(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataWatcher().updateObject(LIFETIME,lifetime);
    }

    public void setGrimGripPos(@Nullable BlockPos grimGripPos)
    {
        this.getDataWatcher().updateObject(GRIM_GRIP_POS, grimGripPos == null ? "" : grimGripPos.toString());
    }
    public BlockPos getGrimGripPos()
    {
        String str = this.getDataWatcher().getWatchableObjectString(GRIM_GRIP_POS);
        BlockPos result = null;
        if(str != null && str.length() != 0)
        {
            try{
                str = str.substring(1,str.length()-1);
                String[] strs = str.split(",");
                result = new BlockPos(
                        Integer.parseInt(strs[0]),
                        Integer.parseInt(strs[1]),
                        Integer.parseInt(strs[2]));
            }catch (Exception e){
            }
        }

        if(result == null)
            result = new BlockPos(
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.posY),
                    MathHelper.floor_double(this.posZ));

        return result;
    }


    public boolean isHide(){
        return this.getDataWatcher().getWatchableObjectByte(INVISIBLE) != 0;
    }
    public void setHide(boolean hide){
        this.getDataWatcher().updateObject(INVISIBLE,hide ? 1 : 0);
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
                && ((EntityLivingBase) entityIn).getHeldItem() == null
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
            ItemStack stack = ((EntityLivingBase)entityIn).getHeldItem();
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
