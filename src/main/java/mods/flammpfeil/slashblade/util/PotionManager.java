package mods.flammpfeil.slashblade.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSWaeponMaterial;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PotionBrewEvent;

/**
 * Created by Furia on 2015/11/30.
 */
public class PotionManager {

    public PotionManager(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void BrewHandler(PotionBrewEvent.Pre event){

        int factorIdx = event.getLength() - 1;

        ItemStack stack = event.getItem(factorIdx);
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSWaeponMaterial)) return;
        event.setCanceled(true);

        int damage = stack.getItemDamage();

        switch (damage){
            default :
                return;
            case 0: //soul
            {
                for(int i = 0; i < factorIdx; i++){
                    ItemStack potion = event.getItem(i);
                    if(potion != null && potion.getItemDamage() == 8192) { // Mundane
                        event.setItem(i, new ItemStack(Items.experience_bottle));

                        if (--stack.stackSize <= 0)
                            event.setItem(factorIdx , null);

                        return;
                    }
                }
                break;
            }
            case 3: //tiny
                for(int i = 0; i < factorIdx; i++){
                    ItemStack potion = event.getItem(i);
                    if(potion != null && potion.getItemDamage() == 0){
                        potion.setItemDamage(8233);

                        if (--stack.stackSize <= 0)
                            event.setItem(factorIdx , null);

                        return;
                    }
                }
                break;
        }

    }
}
