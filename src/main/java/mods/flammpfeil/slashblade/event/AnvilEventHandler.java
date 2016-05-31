package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.Doutanuki;
import net.minecraft.item.Item;
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
            default: {


                NBTTagCompound matTag = ItemSlashBlade.getItemTagCompound(event.getRight());

                if (ItemSlashBladeNamed.CurrentItemName.exists(matTag)){
                    ItemStack targetBlade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeNamed",1);
                    if(out.getUnlocalizedName().equals(targetBlade.getUnlocalizedName())){

                        if(1000 > ItemSlashBlade.ProudSoul.get(tag)){
                            return;
                        }

                        ItemSlashBladeNamed.CurrentItemName.set(tag, ItemSlashBladeNamed.CurrentItemName.get(matTag));

                        if(ItemSlashBlade.BaseAttackModifier.exists(matTag))
                            ItemSlashBlade.setBaseAttackModifier(tag, ItemSlashBlade.BaseAttackModifier.get(matTag));

                        TagPropertyAccessor[] accessors = {
                                ItemSlashBladeNamed.CustomMaxDamage,
                                ItemSlashBlade.TextureName,
                                ItemSlashBlade.ModelName,
                                ItemSlashBlade.SpecialAttackType,
                                ItemSlashBlade.StandbyRenderType,
                                ItemSlashBladeNamed.IsDefaultBewitched,
                                ItemSlashBladeNamed.TrueItemName,
                                ItemSlashBlade.SummonedSwordColor,
                                ItemSlashBlade.IsDestructable,
                                ItemSlashBlade.IsBroken
                        };

                        for(TagPropertyAccessor acc : accessors)
                            copyTag(acc, tag, matTag);
                    }
                    if (event.getCost() <= 0) {
                        event.setCost(5);
                    }
                    repairFactor = 1.0f;
                    ItemSlashBlade.ProudSoul.add(tag, -1000);
                }else{

                    if (event.getCost() <= 0) {
                        event.setCost(5);
                    }
                    repairFactor = 1.0f;
                    ItemSlashBlade.ProudSoul.add(tag, 500);
                }

                break;
            }
        }

        ItemSlashBlade.RepairCount.add(tag, 1);

        int repair = Math.min(out.getItemDamage(),(int)(out.getMaxDamage() * repairFactor));

        out.setItemDamage(out.getItemDamage() - repair);

        if(StringUtils.isBlank(event.getName())){
            if(ItemSlashBladeNamed.IsDefaultBewitched.get(tag)){
                out.clearCustomName();
                out.setStackDisplayName(out.getDisplayName());
            }else if(out.hasDisplayName()) {
                out.clearCustomName();
            }
        }else{
            out.setStackDisplayName(event.getName());
        }

        event.setOutput(out);
    }

    public void copyTag(TagPropertyAccessor acc, NBTTagCompound dest , NBTTagCompound src){
        if(acc.exists(src))
            acc.set(dest, acc.get(src));
    }
}
