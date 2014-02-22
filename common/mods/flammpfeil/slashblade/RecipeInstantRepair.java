package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeInstantRepair extends ShapedRecipes implements ICraftingHandler
{

    public RecipeInstantRepair()
    {
        super(2, 2, new ItemStack[] {
        		null, new ItemStack(Block.cobblestone),
        		new ItemStack(SlashBlade.weapon, 1, 0), null}
        , new ItemStack(SlashBlade.weapon, 1, 0));
    }

    @Override
    public boolean matches(InventoryCrafting cInv, World par2World)
    {
        {
        	boolean hasBlade = false;
        	boolean hasGrindstone = false;

        	ItemStack stone = cInv.getStackInRowAndColumn(1, 0);
        	hasGrindstone = (stone != null && stone.itemID == Block.cobblestone.blockID);

        	if(hasGrindstone){

	            ItemStack target = cInv.getStackInRowAndColumn(0, 1);
	            if(target != null &&
	            		(target.getItem().itemID == SlashBlade.weapon.itemID
	            		|| target.getItem().itemID == SlashBlade.bladeWood.itemID
	            		|| target.getItem().itemID == SlashBlade.bladeBambooLight.itemID
	            		|| target.getItem().itemID == SlashBlade.bladeSilverBambooLight.itemID
	            		|| target.getItem().itemID == SlashBlade.bladeWhiteSheath.itemID
	            		) ){

	            	if(0 < target.getItemDamage()){
	            		if(target.hasTagCompound()){
	            			NBTTagCompound tag = target.getTagCompound();
	            			int proudSoul = tag.getInteger(ItemSlashBlade.proudSoulStr);

	            			if(0 < proudSoul){
	            				hasBlade = true;
	            			}
	            		}
	            	}
	            }
        	}

            return hasBlade && hasGrindstone;
        }
    }

    public static final String RepairCountStr = "RepairCount";
    public static int RepairProudSoulCount = 20;

    @Override
    public ItemStack getCraftingResult(InventoryCrafting cInv)
    {
    	ItemStack stone = cInv.getStackInRowAndColumn(1, 0);

        ItemStack target = cInv.getStackInRowAndColumn(0, 1);

        ItemStack itemstack = target.copy();

        if(target != null &&
        		(target.getItem().itemID == SlashBlade.weapon.itemID
        		|| target.getItem().itemID == SlashBlade.bladeWood.itemID
        		|| target.getItem().itemID == SlashBlade.bladeBambooLight.itemID
        		|| target.getItem().itemID == SlashBlade.bladeSilverBambooLight.itemID
        		|| target.getItem().itemID == SlashBlade.bladeWhiteSheath.itemID
        		) ){

        	if(0 < itemstack.getItemDamage()){
        		if(itemstack.hasTagCompound()){
        			NBTTagCompound tag = itemstack.getTagCompound();
        			int proudSoul = tag.getInteger(ItemSlashBlade.proudSoulStr);
        			int repairPoints = proudSoul / 10;

        			if(0 < proudSoul){
        				int damage = itemstack.getItemDamage();
        				int repair = Math.min(stone.stackSize, Math.min(repairPoints,damage));

        				proudSoul -= repair * RepairProudSoulCount;

        				itemstack.setItemDamage(itemstack.getItemDamage()-repair);

        				tag.setInteger(ItemSlashBlade.proudSoulStr, proudSoul);
        				tag.setInteger(RepairCountStr, repair);
        			}
        		}
        	}
        }

        return itemstack;
    }

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item,
			IInventory craftMatrix) {
		NBTTagCompound tag = item.getTagCompound();
		int repair = tag.getInteger(RepairCountStr);
		tag.removeTag(RepairCountStr);

		ItemStack stone = craftMatrix.getStackInSlot(1);
		stone.stackSize -= repair;
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
	}
}

