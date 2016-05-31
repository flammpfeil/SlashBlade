package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Furia on 2016/05/31.
 */
public class AnvilEventHandler {
    @SubscribeEvent
    public void onAnvil(AnvilUpdateEvent event){
        if(!(event.getLeft().getItem() instanceof ItemSlashBlade))
            return;
        if(event.getRight() == null)
            return;
        if(!(event.getRight().getItem() instanceof ItemProudSoul))
            return;

        event.setMaterialCost(1);

        ItemStack out = event.getLeft().copy();

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(out);

        float repairFactor;
        switch(event.getRight().getItemDamage()){
            case 0:
                if(event.getCost() <= 0){
                    event.setCost(2);
                }
                repairFactor = 0.4f;
                ItemSlashBlade.ProudSoul.add(tag, 200);
                break;
            case 1:
                if(event.getCost() <= 0){
                    event.setCost(3);
                }
                repairFactor = 0.6f;
                ItemSlashBlade.ProudSoul.add(tag, 400);
                break;
            case 2:
                if(event.getCost() <= 0){
                    event.setCost(4);
                }
                repairFactor = 0.7f;
                ItemSlashBlade.ProudSoul.add(tag, 400);
                break;
            case 3:
                repairFactor = 0.2f;
                ItemSlashBlade.ProudSoul.add(tag, 100);
                break;
            default:
                if(event.getCost() <= 0){
                    event.setCost(5);
                }
                repairFactor = 1.0f;
                ItemSlashBlade.ProudSoul.add(tag, 500);
                break;
        }

        ItemSlashBlade.RepairCount.add(tag, 1);

        int repair = Math.min(out.getItemDamage(),(int)(out.getMaxDamage() * repairFactor));

        out.setItemDamage(out.getItemDamage() - repair);

        if(StringUtils.isBlank(event.getName())){
            out.clearCustomName();
        }else {
            out.setStackDisplayName(event.getName());
        }

        event.setOutput(out);
    }
}
