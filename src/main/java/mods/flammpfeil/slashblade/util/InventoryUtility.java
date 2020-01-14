package mods.flammpfeil.slashblade.util;

import com.google.common.collect.Lists;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Furia on 15/05/18.
 */
public class InventoryUtility {
    public static boolean consumeInventoryItem(IInventory inventory, ItemStack stack, boolean isVirtual)
    {
        List<Integer> indexList = findItemStackSlots(inventory, stack);

        if (indexList.size() == 0){
            return false;

        }
		int count = 0;
		for(int index : indexList){
		    ItemStack tmp = inventory.getStackInSlot(index);
		    count += tmp.getCount();
		}

		if(count < stack.getCount()) return false;

		if(!isVirtual){
		    int usage = stack.getCount();

		    for(int index : indexList){
		        ItemStack tmp = inventory.decrStackSize(index, usage);
		        usage -= tmp.getCount();

		        if(usage <= 0) break;
		    }
		}

		return true;
    }


    public static List<Integer> findItemStackSlots(IInventory inventory, ItemStack stack)
    {
        List<Integer> result = Lists.newArrayList();

        for (int idx = inventory.getSizeInventory(); 0 < --idx;)
        {
            ItemStack current = inventory.getStackInSlot(idx);
            if(current.isEmpty()) continue;
            if(stack.isItemEqual(current) && ItemStack.areItemStackTagsEqual(stack,current)){
                result.add(idx);
            }
        }

        return result;
    }
}
