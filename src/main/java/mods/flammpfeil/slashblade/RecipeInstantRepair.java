package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeInstantRepair extends ShapedRecipes
{

    public RecipeInstantRepair()
    {
        super(2, 2, new ItemStack[] {
        		null, new ItemStack(Blocks.cobblestone),
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
        	hasGrindstone = (stone != null && stone.getItem() == Item.getItemFromBlock(Blocks.cobblestone));

        	if(hasGrindstone){

	            ItemStack target = cInv.getStackInRowAndColumn(0, 1);
	            if(target != null && SlashBlade.blades.contains(target.getItem())){

	            	if(0 < target.getItemDamage()){
	            		if(target.hasTagCompound()){
	            			NBTTagCompound tag = target.getTagCompound();
	            			int proudSoul = tag.getInteger(ItemSlashBlade.proudSoulStr);

	            			if(RepairProudSoulCount < proudSoul){
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
    public static int RepairProudSoulCount = 10;

    @Override
    public ItemStack getCraftingResult(InventoryCrafting cInv)
    {
    	ItemStack stone = cInv.getStackInRowAndColumn(1, 0);

        ItemStack target = cInv.getStackInRowAndColumn(0, 1);

        ItemStack itemstack = target.copy();

        if(target != null && SlashBlade.blades.contains(target.getItem())){

        	if(0 < itemstack.getItemDamage()){
        		if(itemstack.hasTagCompound()){
        			NBTTagCompound tag = itemstack.getTagCompound();
        			int proudSoul = tag.getInteger(ItemSlashBlade.proudSoulStr);
        			int repairPoints = proudSoul / RepairProudSoulCount;

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

	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event){
        EntityPlayer player = event.player;
        ItemStack item = event.crafting;
		IInventory craftMatrix = event.craftMatrix;

		if(item != null){
	        if(SlashBlade.blades.contains(item.getItem())){

	        	if(item.hasTagCompound()){

	        		NBTTagCompound tag = item.getTagCompound();
	        		if(tag.hasKey(RepairCountStr)){
	            		int repair = tag.getInteger(RepairCountStr);
	            		tag.removeTag(RepairCountStr);

	            		try{
		            		ItemStack stone = craftMatrix.getStackInSlot(1);
		            		if(stone != null && stone.getItem() == Item.getItemFromBlock(Blocks.cobblestone)){
		                		if(stone.stackSize < repair){
		                			int overDamage = repair - stone.stackSize;
		                			item.setItemDamage(item.getItemDamage()+overDamage);
		                			stone.stackSize = 0;
		                		}else{
		                			stone.stackSize -= repair;
		                		}
		            		}
	            		}catch(Throwable e){

	            		}
	        		}
	        	}
	        }
		}

	}

}

