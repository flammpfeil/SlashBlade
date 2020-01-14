package mods.flammpfeil.slashblade;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class ItemSlashBladeDetune extends ItemSlashBlade {

	public ItemSlashBladeDetune(ToolMaterial par2EnumToolMaterial,float baseAttackModifiers){
		super(par2EnumToolMaterial, baseAttackModifiers);
		texture = new ResourceLocationRaw("flammpfeil.slashblade","model/wood.png");
	}


	public ResourceLocationRaw texture;
	@Override
	public ResourceLocationRaw getModelTexture(){
		return texture;
	}
	public ItemSlashBladeDetune setModelTexture(ResourceLocationRaw loc){
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
    public void addInformationSwordClass(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {}
}
