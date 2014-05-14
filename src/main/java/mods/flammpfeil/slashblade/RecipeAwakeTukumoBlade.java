package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.EnumSet;
import java.util.Map;

public class RecipeAwakeTukumoBlade extends RecipeAwakeBlade {

	public RecipeAwakeTukumoBlade() {
		super(ItemSlashBladeNamed.getCustomBlade("slashblade.named.yuzukitukumo.youtou"),
                "ESD",
                "RBL",
                "ISG",
                'E', new ItemStack(Blocks.emerald_block),
                'D', new ItemStack(Blocks.diamond_block),
                'R', new ItemStack(Blocks.redstone_block),
                'L', new ItemStack(Blocks.lapis_block),
                'I', new ItemStack(Blocks.iron_block),
                'G', new ItemStack(Blocks.gold_block),
                'S', GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1),
                'B', GameRegistry.findItemStack(SlashBlade.modid, "slashblade.thousandkill.youtou", 1));
	}

    @Override
    public boolean matches(InventoryCrafting inv, World world) {

        boolean result = super.matches(inv, world);

        if(result){
            for(int idx = 0; idx < inv.getSizeInventory(); idx++){
                ItemStack curIs = inv.getStackInSlot(idx);
                if(curIs != null
                        && curIs.getItem() instanceof ItemSlashBlade
                        && curIs.hasTagCompound()){

                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, curIs);
                    if(0 < level){
                        EnumSet<ItemSlashBlade.SwordType> types = ((ItemSlashBlade)curIs.getItem()).getSwordType(curIs);
                        result = types.contains(ItemSlashBlade.SwordType.FiercerEdge);
                    }

                    break;
                }
            }
        }

        return result;
    }

}
