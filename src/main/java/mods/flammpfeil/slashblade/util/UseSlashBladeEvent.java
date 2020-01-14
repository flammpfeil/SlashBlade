package mods.flammpfeil.slashblade.util;

import mods.flammpfeil.slashblade.item.ItemSlashBlade.ComboSequence;
import mods.flammpfeil.slashblade.network.MessageRangeAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class UseSlashBladeEvent extends Event {
	private final ItemStack blade;
	private final EntityPlayer player;
	public UseSlashBladeEvent(EntityPlayer playerIn,ItemStack bladeStack) {
		blade=bladeStack;
    	player=playerIn;
	}
	public ItemStack getBlade() {
		return blade;
	}
	public EntityPlayer getPlayer() {
		return player;
	}
	@Cancelable
    public static class doAttackEvent extends UseSlashBladeEvent{
    	private final ComboSequence comboSeq;
        public doAttackEvent(EntityPlayer playerIn,ItemStack bladeStack,ComboSequence comboSeqIn){
        	super(playerIn, bladeStack);
        	comboSeq=comboSeqIn;
        }

		public ComboSequence getComboSeq() {
			return comboSeq;
		}
    }
	@Cancelable
    public static class doSpacialAttackEvent extends UseSlashBladeEvent{
    	private final SpecialAttackBase specialAttck;
    	private final boolean isJust;
        public doSpacialAttackEvent(boolean isjust,EntityPlayer playerIn,ItemStack bladeStack,SpecialAttackBase specialAttckIn){
        	super(playerIn, bladeStack);
        	specialAttck=specialAttckIn;
        	isJust=isjust;
        }
		public SpecialAttackBase getSpecialAttck() {
			return specialAttck;
		}
		public boolean isJustSpecialAttack() {
			return isJust;
		}
    }
	@Cancelable
    public static class doRangeAttackEvent extends UseSlashBladeEvent{
    	private final MessageRangeAttack.RangeAttackState mode;
        public doRangeAttackEvent(EntityPlayer playerIn,ItemStack bladeStack,MessageRangeAttack.RangeAttackState modeIn){
        	super(playerIn, bladeStack);
        	mode=modeIn;
        }
		public MessageRangeAttack.RangeAttackState getMode() {
			return mode;
		}
    }
}
