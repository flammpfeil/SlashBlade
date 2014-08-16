package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemSWaeponMaterial extends Item {

	public ItemSWaeponMaterial() {
        setHasSubtypes(true);
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {

		if(	par1ItemStack.getItem() == SlashBlade.proudSoul){
			return true;
		}
		return super.hasEffect(par1ItemStack, pass);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		String s = super.getUnlocalizedName(par1ItemStack);
		switch(par1ItemStack.getItemDamage()){
		case 1:
			s += ".ingot";
			break;
        case 2:
            s += ".sphere";
            break;
        case 3:
            s += ".tiny";
            break;
		}
		return s;
	}

    IIcon tiny;

	@Override
	public IIcon getIconFromDamage(int par1) {
		switch(par1){
		case 1:
			return Items.iron_ingot.getIconFromDamage(0);
		case 2:
			return Items.snowball.getIconFromDamage(0);
        case 3:
            return tiny;
		default:
			return super.getIconFromDamage(par1);
		}
	}

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        tiny = par1IconRegister.registerIcon("flammpfeil.slashblade:tinyps");
    }

    @Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1));
	}

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int dir, float fx, float fy, float yz) {
        Block block = world.getBlock(x, y, z);

        if(!world.isRemote && Blocks.fence == block && player.isSneaking()){

            world.setBlockToAir(x,y,z);
            EntityBladeStand e = new EntityBladeStand(world);
            e.setStandType(stack.getItemDamage());
            e.setPositionAndRotation(x + 0.5 ,y + 0.5 ,z + 0.5,Math.round(player.rotationYaw / 45.0f) * 45.0f + 180.0f,e.rotationPitch);
            world.spawnEntityInWorld(e);

            return true;
        }else{
            return false;
        }

    }
}
