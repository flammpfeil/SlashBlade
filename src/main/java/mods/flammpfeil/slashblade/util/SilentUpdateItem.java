package mods.flammpfeil.slashblade.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * Created by Furia on 2015/10/04.
 */
public class SilentUpdateItem {
    public static void silentUpdateItem(EntityPlayer player, EnumHand hand){
        Slot slot;
        if(hand == EnumHand.MAIN_HAND)
            slot = player.inventoryContainer.getSlotFromInventory(player.inventory,player.inventory.currentItem);
        else
            slot = player.inventoryContainer.getSlotFromInventory(player.inventory,player.inventory.getSizeInventory()); //40 offhandslot

        if(slot == null) return;

        ItemStack stack = slot.getStack();
        if(stack.func_190926_b()) return;

        stack.getTagCompound().setInteger("markdirty", player.ticksExisted);

        int idx = player.inventoryContainer.inventorySlots.indexOf(slot);
        if(idx < 0) return;

        player.inventoryContainer.inventoryItemStacks.set(idx, stack/*.copy()*/);
    }

    public static void onUpdate(ItemStack stack, Entity owner, boolean isCurrent) {
        if(!(owner instanceof EntityPlayer)) return;

        if(!owner.worldObj.isRemote
                && (!isCurrent
                || (((EntityPlayer) owner).getActiveItemStack().func_190926_b() && !((EntityPlayer) owner).isSwingInProgress))
                && stack.hasTagCompound() && stack.getTagCompound().hasKey("markdirty")) {

            for(Object objSlot : ((EntityPlayer) owner).inventoryContainer.inventorySlots){
                Slot slot = (Slot)objSlot;
                if(ItemStack.areItemStacksEqual(slot.getStack(),stack)){
                    int idx = ((EntityPlayer) owner).inventoryContainer.inventorySlots.indexOf(slot);
                    if(idx < 0) return;
                    ((EntityPlayer) owner).inventoryContainer.inventoryItemStacks.set(idx, stack.copy());
                }
            }

            stack.getTagCompound().removeTag("markdirty");


        }
    }
    public static void forceUpdate(ItemStack stack, Entity owner) {
        if(!(owner instanceof EntityPlayer)) return;

        if(!owner.worldObj.isRemote
                && stack.hasTagCompound() && stack.getTagCompound().hasKey("markdirty")) {

            for(Object objSlot : ((EntityPlayer) owner).inventoryContainer.inventorySlots){
                Slot slot = (Slot)objSlot;
                if(ItemStack.areItemStacksEqual(slot.getStack(),stack)){
                    int idx = ((EntityPlayer) owner).inventoryContainer.inventorySlots.indexOf(slot);
                    if(idx < 0) return;
                    ((EntityPlayer) owner).inventoryContainer.inventoryItemStacks.set(idx, stack.copy());
                }
            }

            stack.getTagCompound().removeTag("markdirty");
        }
    }
}
