package mods.flammpfeil.slashblade.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Furia on 2016/05/30.
 */
public class EntityGrimGrip extends Entity {
    public EntityGrimGrip(World worldIn) {
        super(worldIn);

        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    private static final DataParameter<Integer> LIFETIME = EntityDataManager.<Integer>createKey(EntityGrimGrip.class, DataSerializers.VARINT);

    @Override
    protected void registerData() {
        //lifetime
        this.getDataManager().register(LIFETIME, 20);

    }

    public int getLifeTime(){
        return this.getDataManager().get(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataManager().set(LIFETIME,lifetime);
    }

    @Override
    protected void readAdditional(NBTTagCompound compound) {

    }

    @Override
    protected void writeAdditional(NBTTagCompound compound) {

    }

    @Override
    protected void markVelocityChanged() {
        super.markVelocityChanged();

        this.onKillCommand();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(!world.isRemote && getLifeTime() < this.ticksExisted){

            this.createRunningParticles();
            this.onKillCommand();
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
