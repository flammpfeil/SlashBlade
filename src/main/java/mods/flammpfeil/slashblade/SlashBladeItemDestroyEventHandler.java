package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

/**
 * Created by Furia on 14/05/27.
 */
public class SlashBladeItemDestroyEventHandler {
    @SubscribeEvent
    public void PlayerDestroyItemEvent(PlayerDestroyItemEvent event){
        ItemStack stack = event.original;
        EntityPlayer player = event.entityPlayer;
        if(stack != null && stack.getItem() instanceof ItemSlashBlade){
            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

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

                if(player.inventory.mainInventory[player.inventory.currentItem] == null)
                    player.inventory.mainInventory[player.inventory.currentItem] = broken;
                else if (!player.inventory.addItemStackToInventory(broken))
                    player.dropPlayerItemWithRandomChoice(broken, false);

            }

            blade.dropItemDestructed(player, stack);

        }
    }
}
