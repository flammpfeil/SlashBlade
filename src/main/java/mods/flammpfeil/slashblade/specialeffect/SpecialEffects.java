package mods.flammpfeil.slashblade.specialeffect;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.Set;

/**
 * Created by Furia on 15/06/20.
 */
public class SpecialEffects {

    private static Map<String,ISpecialEffect> effects = Maps.newHashMap();
    public static ISpecialEffect WitherEdge = register(new WitherEdge());
    public static ISpecialEffect CrystalHealing = register(new CrystalHealing());
    public static ISpecialEffect Limitter = register(new Limitter());
    public static ISpecialEffect BlastEdge = register(new BlastEdge());
    public static ISpecialEffect HFCustom = register(new HFCustom());

    public static ISpecialEffect register(ISpecialEffect effect){
        effects.put(effect.getEffectKey(), effect);
        return effect;
    }

    public static ISpecialEffect getEffect(String key){
        return effects.get(key);
    }

    public static void init(){
        for(ISpecialEffect effect : effects.values()){
            effect.register();
        }
    }

    public static ItemStack addEffect(ItemStack blade, ISpecialEffect effect){
        return addEffect(blade,effect.getEffectKey(),effect.getDefaultRequiredLevel());
    }

    public static ItemStack addEffect(ItemStack blade, String key, int requiredLevel){
        NBTTagCompound etag = ItemSlashBlade.getSpecialEffect(blade);
        etag.setInt(key,requiredLevel);
        return blade;
    }

    public static enum State{
        None,
        NonEffective,
        Effective
    }

    public static boolean isPlayer(Entity target){

        if(target == null) return false;
        if(target.world == null) return false;
        if(target.world.isRemote) return false;
        if(!(target instanceof EntityPlayer)) return false;
        if(!target.isEntityAlive()) return false;
        return true;
    }

    public static boolean isBlade(ItemStack stack){
        if(stack.isEmpty()) return false;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return false;
        return true;
    }

    public static State isEffective(EntityPlayer player, ItemStack blade, ISpecialEffect effect){
        return isEffective(player, blade, effect.getEffectKey());
    }
    public static State isEffective(EntityPlayer player, ItemStack blade, String EffectKey){

        NBTTagCompound tag = ItemSlashBlade.getSpecialEffect(blade);

        int requiredLevel = tag.getInt(EffectKey);
        if(0 == requiredLevel) return State.None;
        if(requiredLevel<= player.experienceLevel)
            return State.Effective;
        else
            return State.NonEffective;
    }
}
