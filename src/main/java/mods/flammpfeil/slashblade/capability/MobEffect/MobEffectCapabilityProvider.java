package mods.flammpfeil.slashblade.capability.MobEffect;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobEffectCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTBase> {

    @CapabilityInject(IMobEffectHandler.class)
    public static Capability<IMobEffectHandler> MOB_EFFECT = null;

    protected IMobEffectHandler storage = null;

    public MobEffectCapabilityProvider(){
        storage = MOB_EFFECT.getDefaultInstance();
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == MOB_EFFECT) return true;
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == MOB_EFFECT) return (T)this.storage;
        return null;
    }

    @Override
    public NBTBase serializeNBT() {
        return MOB_EFFECT.writeNBT(storage, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        MOB_EFFECT.readNBT(storage, null, nbt);
    }
}
