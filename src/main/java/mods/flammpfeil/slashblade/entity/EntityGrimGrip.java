package mods.flammpfeil.slashblade.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Furia on 2016/05/30.
 */
public class EntityGrimGrip extends EntityEx {
    public EntityGrimGrip(World worldIn) {
        super(worldIn);

        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    private static final int LIFETIME = 7;

    @Override
    protected void entityInit() {
        //lifetime
        this.getDataWatcher().addObject(LIFETIME, 20);

    }

    public int getLifeTime(){
        return this.getDataWatcher().getWatchableObjectInt(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataWatcher().updateObject(LIFETIME,lifetime);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }

    @Override
    protected void setBeenAttacked() {
        super.setBeenAttacked();

        this.kill();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(!worldObj.isRemote && getLifeTime() < this.ticksExisted){

            this.kill();
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }
}
