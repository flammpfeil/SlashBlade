package mods.flammpfeil.slashblade.specialeffect;

import com.google.common.collect.Sets;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

/**
 * Created by Furia on 15/06/20.
 */
public class SpecialEffects {

    private static Set<ISpecialEffect> effects = Sets.newHashSet();
    public static ISpecialEffect WitherEdge = register(new WitherEdge());
    public static ISpecialEffect CrystalHealing = register(new CrystalHealing());

    public static ISpecialEffect register(ISpecialEffect effect){
        effects.add(effect);
        return effect;
    }

    public static void init(){
        for(ISpecialEffect effect : effects){
            effect.register();
        }
    }

    public static ItemStack addEffect(ItemStack blade, ISpecialEffect effect){
        return addEffect(blade,effect.getEffectKey(),effect.getDefaultRequiredLevel());
    }

    public static ItemStack addEffect(ItemStack blade, String key, int requiredLevel){
        NBTTagCompound etag = ItemSlashBlade.getSpecialEffect(blade);
        etag.setInteger(key,requiredLevel);
        return blade;
    }

    public static enum State{
        None,
        NonEffective,
        Effective
    }

    public static boolean isPlayer(Entity target){

        if(target == null) return false;
        if(target.worldObj == null) return false;
        if(target.worldObj.isRemote) return false;
        if(!(target instanceof EntityPlayer)) return false;
        return true;
    }

    public static boolean isBlade(ItemStack stack){
        if(stack == null) return false;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return false;
        return true;
    }

    public static State isEffective(EntityPlayer player, ItemStack blade, ISpecialEffect effect){
        return isEffective(player, blade, effect.getEffectKey());
    }
    public static State isEffective(EntityPlayer player, ItemStack blade, String EffectKey){

        NBTTagCompound tag = ItemSlashBlade.getSpecialEffect(blade);

        int requiredLevel = tag.getInteger(EffectKey);
        if(0 == requiredLevel) return State.None;
        if(requiredLevel<= player.experienceLevel)
            return State.Effective;
        else
            return State.NonEffective;
    }
}
