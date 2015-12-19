package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

/**
 * Created by Furia on 14/05/27.
 */
public class SlashBladeItemDestroyEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void PlayerDestroyItemEvent(PlayerDestroyItemEvent event){
        if(event.entityLiving.worldObj.isRemote) return;

        ItemStack stack = event.original;
        EntityPlayer player = event.entityPlayer;
        if(stack != null && stack.getItem() instanceof ItemSlashBlade){
            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            {//checkNowBroken
                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
                if(ItemSlashBlade.IsBroken.get(tag)){
                    return;
                }else{
                    ItemSlashBlade.IsBroken.set(tag,true);
                }
            }

            if(!blade.isDestructable(stack)){
                ItemStack broken = stack.copy();
                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(broken);
                broken.stackSize = 1;
                broken.setItemDamage(broken.getMaxDamage());

                ItemSlashBlade.IsBroken.set(tag,true);

                if(blade instanceof ItemSlashBladeWrapper){
                    if(!ItemSlashBladeWrapper.TrueItemName.exists(tag)){
                        ((ItemSlashBladeWrapper)blade).removeWrapItem(broken);
                    }
                }

                if(blade == SlashBlade.bladeWhiteSheath){
                    AchievementList.triggerAchievement(event.entityPlayer, "brokenWhiteSheath");
                }
/*
                if(player.inventory.mainInventory[player.inventory.currentItem] == null)
                    player.inventory.mainInventory[player.inventory.currentItem] = broken;
                else if (!player.inventory.addItemStackToInventory(broken))
                    player.dropPlayerItemWithRandomChoice(broken, false);
*/
                EntityItem entityitem = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, broken);
                if(!player.worldObj.isRemote)
                    player.worldObj.spawnEntityInWorld(entityitem);
                /**/
                stack = broken;
            }
            blade.dropItemDestructed(player, stack);

        }
    }
}
