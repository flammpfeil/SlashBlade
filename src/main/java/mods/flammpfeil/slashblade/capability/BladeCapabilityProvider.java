package mods.flammpfeil.slashblade.capability;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Furia on 2017/01/10.
 */
public class BladeCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTBase> {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY = null;

    protected final ItemStack container;
    static final String tagParent = "Parent";

    protected IEnergyStorage storage = null;
    static final String tagEnergyStorage = "EnergyStorage";
    static final String tagCapacity = "Capacity";
    static final String tagEnergy = "Energy";

    static final int defaultCapacity = 1000000;

    public BladeCapabilityProvider(ItemStack container, NBTTagCompound capNBT){
        this.container = container;

        if(capNBT != null && capNBT.hasKey(tagParent)){
            NBTTagCompound tag = capNBT.getCompoundTag(tagParent);
            
            if(tag.hasKey(tagEnergyStorage))
                this.storage = new EnergyStorage(defaultCapacity);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        updateStorage();
        if(capability == ENERGY && storage != null) return true;
        return false;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        updateStorage();
        if(capability == ENERGY) return (T)this.storage;
        return null;
    }
    
    @Override
    public NBTBase serializeNBT() {
        NBTTagCompound parentTag = new NBTTagCompound();

        updateStorage();

        if(this.storage != null){
            NBTTagCompound tag = new NBTTagCompound();
            parentTag.setTag(tagEnergyStorage, tag);

            tag.setInteger(tagCapacity, this.storage.getMaxEnergyStored());
            tag.setTag(tagEnergy, ENERGY.writeNBT(this.storage, null));
        }
        return parentTag;
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        NBTTagCompound parentTag = (NBTTagCompound)nbt;

        if(parentTag.hasKey(tagEnergyStorage)){
            NBTTagCompound tag = parentTag.getCompoundTag(tagEnergyStorage);

            int capacity = tag.getInteger(tagCapacity);
            this.storage = new EnergyStorage(capacity);
            ENERGY.readNBT(this.storage, null, tag.getTag("Energy"));
        }
        
    }

    private void updateStorage(){
        NBTTagCompound tag = ItemSlashBlade.getSpecialEffect(this.container);
        int level = tag.getInteger(SpecialEffects.HFCustom.getEffectKey());

        if(0 < level){
            int newCapacity = getDefaultCapacity(container);
            if(storage == null){
                this.storage = new EnergyStorage(newCapacity);
            }else{
                int currentCapacity = storage.getMaxEnergyStored();
                if(currentCapacity != newCapacity){
                    NBTBase stored = ENERGY.writeNBT(storage, null);
                    this.storage = new EnergyStorage(newCapacity);
                    ENERGY.readNBT(storage, null, stored);
                }
            }
        }


    }

    private static Map<Integer, Integer> defaultCapacityMap = new HashMap<Integer, Integer>(){{
        this.put(1, 100000);
        this.put(2, 150000);
        this.put(3, 250000);
        this.put(4, 1000000);
    }};
    private static int getDefaultCapacity(ItemStack stack){
        int effectLevel = ItemSlashBlade.getSpecialEffect(stack).getInteger(SpecialEffects.HFCustom.getEffectKey());

        if(defaultCapacityMap.containsKey(effectLevel))
            return defaultCapacityMap.get(effectLevel);
        else if(effectLevel <= 0)
            return 0;
        else
            return defaultCapacity;
    }
}
