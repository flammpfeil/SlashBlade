package mods.flammpfeil.slashblade;

import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;

public class ItemSlashBladeDetune extends ItemSlashBlade {

	public ItemSlashBladeDetune(int par1, EnumToolMaterial par2EnumToolMaterial,float baseAttackModifiers){
		super(par1, par2EnumToolMaterial, baseAttackModifiers);
		texture = new ResourceLocation("flammpfeil.slashblade","model/wood.png");
	}


	public ResourceLocation texture;
	@Override
	public ResourceLocation getModelTexture(){
		return texture;
	}
	public ItemSlashBladeDetune setModelTexture(ResourceLocation loc){
		this.texture = loc;
		return this;
	}

	public boolean isDestructable = true;
	public ItemSlashBladeDetune setDestructable(boolean enabled){
		this.isDestructable = enabled;
		return this;
	}
	public boolean getDestructable(){
		return this.isDestructable;
	}


	@Override
	public void damageItem(int damage, ItemStack par1ItemStack, EntityLivingBase par3EntityLivingBase){

		NBTTagCompound tag = getItemTagCompound(par1ItemStack);

		if(par1ItemStack.getItemDamage() == 0){
			tag.setBoolean(isBrokenStr, false);
		}

		if(par1ItemStack.attemptDamageItem(damage, par3EntityLivingBase.getRNG())){
			if(!this.getDestructable()){
				par1ItemStack.setItemDamage(par1ItemStack.getMaxDamage());
			}else{
            	par1ItemStack.stackSize = 0;
			}

			if(!tag.getBoolean(isBrokenStr)){

				tag.setBoolean(isBrokenStr, true);
				par3EntityLivingBase.renderBrokenItemStack(par1ItemStack);

				if(!par3EntityLivingBase.worldObj.isRemote)
					par3EntityLivingBase.entityDropItem(new ItemStack(SlashBlade.proudSoul,1), 0.0F);
			}
		}
	}

	@Override
	public EnumSet<SwordType> getSwordType(ItemStack itemStack) {
		EnumSet<SwordType> set = super.getSwordType(itemStack);
		set.remove(SwordType.Enchanted);
		set.remove(SwordType.Bewitched);
		set.remove(SwordType.SoulEeater);
		return set;
	}

	@Override
    public void addInformationSwordClass(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {}
}
