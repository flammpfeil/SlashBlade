package mods.flammpfeil.slashblade;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeInstantRepair extends ShapedOreRecipe
{

    public RecipeInstantRepair()
    {
        super(new ResourceLocation(SlashBlade.modid,"recipexxsx"), new ItemStack(SlashBlade.weapon, 1, 0),
                " X",
                "B ",
                'X',"cobblestone",
                'B',new ItemStack(SlashBlade.weapon, 1, 0));
    }

    public static boolean containsMatch(boolean strict, List<ItemStack> inputs, ItemStack... targets)
    {
        for (ItemStack input : inputs)
        {
            for (ItemStack target : targets)
            {
                if (OreDictionary.itemMatches(target, input, strict))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean matches(InventoryCrafting cInv, World par2World)
    {
        {
        	boolean hasBlade = false;
        	boolean hasGrindstone = false;

        	ItemStack stone = cInv.getStackInRowAndColumn(1, 0);

            if(stone.isEmpty())
                return false;


            List<ItemStack> ores = OreDictionary.getOres("cobblestone");
            hasGrindstone = containsMatch(false,ores,stone);
            //stone);

        	if(hasGrindstone){

	            ItemStack target = cInv.getStackInRowAndColumn(0, 1);
	            if(!target.isEmpty() && target.getItem() instanceof ItemSlashBlade){

	            	if(0 < target.getItemDamage()){
	            		if(target.hasTagCompound()){
	            			NBTTagCompound tag = target.getTagCompound();
	            			int proudSoul = ItemSlashBlade.ProudSoul.get(tag);

	            			if(RepairProudSoulCount < proudSoul){
	            				hasBlade = true;
	            			}
	            		}
	            	}
	            }
        	}
        	boolean hasOthers = false;
        	if(hasBlade && hasGrindstone){
           	 for (int i = 0; i <= cInv.getWidth() - this.getRecipeWidth(); ++i)
             {
                 for (int j = 0; j <= cInv.getHeight() - this.getRecipeHeight(); ++j)
                 {
                	 if((i==1&&j==0)||(i==0&&j==1))continue;
                	 ItemStack slot = cInv.getStackInRowAndColumn(i, j);
                	 if(!slot.isEmpty()) hasOthers=true;
                 }
             }
        	}
            return hasBlade&&hasGrindstone&&!hasOthers;
        }
    }

    public static final String RepairCountStr = "RepairCount";
    public static int RepairProudSoulCount = 2;

    @Override
    public ItemStack getCraftingResult(InventoryCrafting cInv)
    {
    	ItemStack stone = cInv.getStackInRowAndColumn(1, 0);

        ItemStack target = cInv.getStackInRowAndColumn(0, 1);

        ItemStack itemstack = target.copy();

        if(!target.isEmpty() && target.getItem() instanceof ItemSlashBlade){

        	if(0 < itemstack.getItemDamage()){
        		if(itemstack.hasTagCompound()){
        			NBTTagCompound tag = itemstack.getTagCompound();
        			int proudSoul = ItemSlashBlade.ProudSoul.get(tag);
        			int repairPoints = proudSoul / RepairProudSoulCount;

        			if(0 < proudSoul){
        				int damage = itemstack.getItemDamage();
        				int repair = Math.min(stone.getCount(), Math.min(repairPoints,damage));

        				proudSoul -= repair * RepairProudSoulCount;

        				itemstack.setItemDamage(itemstack.getItemDamage()-repair);

                        ItemSlashBlade.ProudSoul.set(tag, proudSoul);

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

		if(!item.isEmpty()){
	        if(item.getItem() instanceof ItemSlashBlade){

	        	if(item.hasTagCompound()){

	        		NBTTagCompound tag = item.getTagCompound();
	        		if(tag.hasKey(RepairCountStr)){
	            		int repair = tag.getInteger(RepairCountStr);
	            		tag.removeTag(RepairCountStr);

	            		try{
		            		ItemStack stone = craftMatrix.getStackInSlot(1);
		            		if(!stone.isEmpty()){

                                List<ItemStack> ores = OreDictionary.getOres("cobblestone");
                                boolean hasGrindstone = containsMatch(false,ores,new ItemStack(Blocks.COBBLESTONE));

                                if(hasGrindstone){
                                    if(stone.getCount() < repair){
                                        int overDamage = repair - stone.getCount();
                                        item.setItemDamage(item.getItemDamage()+overDamage);
                                        stone.setCount(0);
                                    }else{
                                        stone.shrink(repair);
                                    }
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

