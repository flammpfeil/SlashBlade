package mods.flammpfeil.slashblade;

import com.google.common.collect.Multimap;

import cpw.mods.fml.common.registry.GameRegistry;
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

import java.util.EnumSet;
import java.util.List;

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

	private boolean isDestructable = true;
	public ItemSlashBladeDetune setDestructable(boolean enabled){
		this.isDestructable = enabled;
		return this;
	}

    @Override
    public boolean isDestructable(ItemStack stack) {
        return super.isDestructable(stack) || isDestructable;
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
