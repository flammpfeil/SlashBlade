package mods.flammpfeil.slashblade.util;

import net.minecraftforge.common.MinecraftForge;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade.ComboSequence;
import mods.flammpfeil.slashblade.network.MessageRangeAttack.RangeAttackState;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Furia on 14/12/25.
 */
public class SlashBladeHooks {
	public static boolean onUpdateHooks(ItemStack blade,World world,Entity entity,int indexOfMainSlot,boolean isCurrent){
        return MinecraftForge.EVENT_BUS.post(new SlashBladeEvent.OnUpdateEvent(blade, world, entity, indexOfMainSlot, isCurrent));
    }
	public static boolean onEntityBladeStandUpdateHooks(EntityBladeStand entityBladeStand){
        return MinecraftForge.EVENT_BUS.post(new SlashBladeEvent.OnEntityBladeStandUpdateEvent(entityBladeStand));
    }
	public static boolean onImpactEffectHooks(ItemStack stack, EntityLivingBase target, EntityLivingBase user, ItemSlashBlade.ComboSequence sequence){
        return MinecraftForge.EVENT_BUS.post(new SlashBladeEvent.ImpactEffectEvent(stack, target, user, sequence));
    }
    public static boolean onBladeStandAttack(EntityBladeStand entityBladeStand, DamageSource damageSource, float damage) {
        return MinecraftForge.EVENT_BUS.post(new SlashBladeEvent.BladeStandAttack(entityBladeStand,damageSource,damage));
    }
	public static boolean onDoAttack(EntityPlayer playerIn,ItemStack bladeStack,ComboSequence comboSeqIn){
        return MinecraftForge.EVENT_BUS.post(new UseSlashBladeEvent.doAttackEvent(playerIn, bladeStack, comboSeqIn));
    }
    public static boolean onDoSpacialAttack(boolean isjust,EntityPlayer playerIn,ItemStack bladeStack,SpecialAttackBase specialAttckIn) {
        return MinecraftForge.EVENT_BUS.post(new UseSlashBladeEvent.doSpacialAttackEvent(isjust, playerIn, bladeStack, specialAttckIn));
    }
    public static boolean onPlayerDoRangeAttack(EntityPlayer playerIn,ItemStack bladeStack,RangeAttackState modeIn) {
        return MinecraftForge.EVENT_BUS.post(new UseSlashBladeEvent.doRangeAttackEvent(playerIn, bladeStack, modeIn));
    }
}
