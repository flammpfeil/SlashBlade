package mods.flammpfeil.slashblade.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Furia on 2016/05/31.
 */
public class AnvilEventHandler {
    @SubscribeEvent
    public void onAnvil(AnvilUpdateEvent event){
        if(!(event.left.getItem() instanceof ItemSlashBlade))
            return;
        if(event.right == null)
            return;
        if(!(event.right.getItem() instanceof ItemSWaeponMaterial))
            return;

        event.materialCost = 1;

        ItemStack out = event.left.copy();

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(out);

        int cost = event.cost;

        Map<Integer, Integer> map = EnchantmentHelper.getEnchantments(event.left);
        for (int enchIdx: map.keySet())
        {
            Enchantment enchantment1 =
                    (0 <= enchIdx && enchIdx < Enchantment.enchantmentsList.length)
                    ? Enchantment.enchantmentsList[enchIdx] : null;
            if (enchantment1 == null) continue;

            int level = map.get(enchantment1.effectId);

            int baseCost = 0;

            switch (enchantment1.getWeight())
            {
                case 1:
                    baseCost = 8;
                    break;
                case 2:
                    baseCost = 4;
                case 3:
                case 4:
                case 6:
                case 7:
                case 8:
                case 9:
                default:
                    break;
                case 5:
                    baseCost = 2;
                    break;
                case 10:
                    baseCost = 1;
            }

            cost += baseCost * level;
        }

        float repairFactor;
        switch(event.right.getItemDamage()){
            case 0:
                cost = Math.max(2, cost);
                repairFactor = 0.4f;
                ItemSlashBlade.ProudSoul.add(tag, 200);
                break;
            case 1:
                cost = Math.max(3, cost);
                repairFactor = 0.6f;
                ItemSlashBlade.ProudSoul.add(tag, 400);
                break;
            case 2:
                cost = Math.max(4, cost);
                repairFactor = 0.7f;
                ItemSlashBlade.ProudSoul.add(tag, 400);
                break;
            case 3:
                cost = Math.max(1, cost);
                repairFactor = 0.2f;
                ItemSlashBlade.ProudSoul.add(tag, 100);
                break;
            default: {
                cost = Math.max(5, cost);

                NBTTagCompound matTag = ItemSlashBlade.getItemTagCompound(event.right);

                if (ItemSlashBladeNamed.CurrentItemName.exists(matTag)){
                    ItemStack targetBlade = GameRegistry.findItemStack(SlashBlade.modid,"slashbladeNamed",1);
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
                    repairFactor = 1.0f;
                    ItemSlashBlade.ProudSoul.add(tag, -1000);
                }else{
                    repairFactor = 1.0f;
                    ItemSlashBlade.ProudSoul.add(tag, 500);
                }

                break;
            }
        }
        event.cost = cost;

        ItemSlashBlade.RepairCount.add(tag, 1);

        int repair = Math.min(out.getItemDamage(),(int)(out.getMaxDamage() * repairFactor));

        out.setItemDamage(out.getItemDamage() - repair);

        if(StringUtils.isBlank(event.name)){
            if(ItemSlashBladeNamed.IsDefaultBewitched.get(tag)){
                out.func_135074_t();
                out.setStackDisplayName(out.getDisplayName());
            }else if(out.hasDisplayName()) {
                out.func_135074_t();
            }
        }else {
            out.setStackDisplayName(event.name);
        }

        event.output = (out);
    }

    public void copyTag(TagPropertyAccessor acc, NBTTagCompound dest , NBTTagCompound src){
        if(acc.exists(src))
            acc.set(dest, acc.get(src));
    }
}
