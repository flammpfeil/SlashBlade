package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
public class ItemSWaeponMaterial extends Item {

	public ItemSWaeponMaterial(int par1) {
		super(par1);
        setHasSubtypes(true);
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack,int pass) {
		if(	par1ItemStack.itemID == SlashBlade.proudSoul.itemID){
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

    Icon tiny;

	@Override
	public Icon getIconFromDamage(int par1) {
		switch(par1){
		case 1:
			return Item.ingotIron.getIconFromDamage(0);
		case 2:
			return Item.snowball.getIconFromDamage(0);
        case 3:
            return tiny;
		default:
			return super.getIconFromDamage(par1);
		}
	}

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        tiny = par1IconRegister.registerIcon("flammpfeil.slashblade:tinyps");
    }

    @Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1));

        ItemStack tiny = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
        for(Enchantment ench : EnchantHelper.rare){
            ItemStack stack = tiny.copy();
            stack.addEnchantment(ench,1);
            par3List.add(stack);
        }
	}

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int dir, float fx, float fy, float yz) {
        int blockId = world.getBlockId(x, y, z);

        if(!world.isRemote && Block.fence.blockID == blockId && player.isSneaking()){

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

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (stack.isItemEnchanted() && entity instanceof EntityBladeStand)
        {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade()){
                --stack.stackSize;

                int damage = stack.getItemDamage();
                float rate = 0.0f;
                if(damage == 0){
                    rate = 0.5f;
                }else if(damage == 1){
                    rate = 0.75f;
                }else if(damage == 2){
                    rate = 1.0f;
                }else{
                    rate = 0.25f;
                }

                if(player.getRNG().nextFloat() < rate){
                    ItemStack blade = stand.getBlade();
                    Map<Integer,Integer> bladeEnchMap = EnchantmentHelper.getEnchantments(blade);

                    Map<Integer,Integer> enchMap = EnchantmentHelper.getEnchantments(stack);
                    for(Map.Entry<Integer,Integer> entry : enchMap.entrySet()){
                        Enchantment ench = Enchantment.enchantmentsList[entry.getKey()];

                        int level = 1;
                        if(bladeEnchMap.containsKey(entry.getKey())){
                            level = Math.min(ench.getMaxLevel(),bladeEnchMap.get(entry.getKey())+1);
                        }

                        bladeEnchMap.put(entry.getKey(),level);

                        EnchantmentHelper.setEnchantments(bladeEnchMap,blade);
                    }

                    for (int i1 = 0; i1 < 5; ++i1)
                    {
                        double d0 = itemRand.nextGaussian() * 0.02D;
                        double d1 = itemRand.nextGaussian() * 0.02D;
                        double d2 = itemRand.nextGaussian() * 0.02D;
                        entity.worldObj.spawnParticle("happyVillager", (double) ((float) entity.posX + itemRand.nextFloat()), (double) entity.posY + (double) itemRand.nextFloat() * 1.0f, (double) ((float) entity.posZ + itemRand.nextFloat()), d0, d1, d2);
                    }

                }
                if (stack.stackSize <= 0)
                {
                    player.destroyCurrentEquippedItem();
                }
                return true;
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    };
}
