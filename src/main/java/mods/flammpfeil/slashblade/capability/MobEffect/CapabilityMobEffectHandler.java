package mods.flammpfeil.slashblade.capability.MobEffect;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityMobEffectHandler {

    @CapabilityInject(IMobEffectHandler.class)
    public static Capability<IMobEffectHandler> MOB_EFFECT = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IMobEffectHandler.class, new DefaultMobEffectHandlerStorage<>(), () -> new MobEffect());

    }

    private static class DefaultMobEffectHandlerStorage<T extends IMobEffectHandler> implements Capability.IStorage<T> {
        @Override
        public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side)
        {
            NBTTagCompound nbt = new NBTTagCompound();

            nbt.setLong("StunTimeout", instance.getStunTimeOut());
            nbt.setLong("FreezeTimeout", instance.getFreezeTimeOut());

            return nbt;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagCompound tags = (NBTTagCompound) nbt;

            instance.setStunTimeOut(tags.getInteger("StunTimeout"));
            instance.setFreezeTimeOut(tags.getInteger("FreezeTimeout"));
        }
    }
}
