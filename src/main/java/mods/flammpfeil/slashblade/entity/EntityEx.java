package mods.flammpfeil.slashblade.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Furia on 2016/06/09.
 */
public abstract class EntityEx extends Entity {
    public EntityEx(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    protected boolean glowing;

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        this.setGlowing(p_70037_1_.getBoolean("Glowing"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        if (this.glowing)
        {
            p_70014_1_.setBoolean("Glowing", this.glowing);
        }
    }

    public boolean isGlowing()
    {
        return this.glowing || this.worldObj.isRemote && this.getFlag(6);
    }

    public void setGlowing(boolean glowingIn)
    {
        this.glowing = glowingIn;

        if (!this.worldObj.isRemote)
        {
            this.setFlag(6, this.glowing);
        }
    }
}
