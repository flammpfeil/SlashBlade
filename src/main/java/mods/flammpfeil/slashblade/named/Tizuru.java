package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Furia on 14/07/07.
 */
public class Tizuru {
    String name = "flammpfeil.slashblade.named.muramasa";
    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){
        ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
        NBTTagCompound tag = new NBTTagCompound();
        customblade.setTagCompound(tag);

        ItemSlashBladeNamed.CurrentItemName.set(tag, name);
        ItemSlashBladeNamed.CustomMaxDamage.set(tag, 50);
        ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getDamageVsEntity());
        ItemSlashBlade.TextureName.set(tag, "named/muramasa/muramasa");
        ItemSlashBlade.ModelName.set(tag, "named/muramasa/muramasa");
        ItemSlashBlade.SpecialAttackType.set(tag, 1);
        ItemSlashBlade.StandbyRenderType.set(tag, 2);
        ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

        GameRegistry.registerCustomItemStack(name, customblade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + name);
    }

    @SubscribeEvent
    public void postinit(LoadEvent.PostInitEvent event){

        ItemStack proudsoul = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1);

        {
            ItemStack blade = SlashBlade.getCustomBlade(SlashBlade.modid,name);

            ItemStack reqiredBlade = GameRegistry.findItemStack(SlashBlade.modid,"slashblade",1);
            {

                NBTTagCompound tag = new NBTTagCompound();
                reqiredBlade.setTagCompound(tag);
                ItemSlashBlade.ProudSoul.set(tag, 10000);
                ItemSlashBlade.RepairCount.set(tag,20);

                reqiredBlade.setStackDisplayName("syoukan muramasa");
            }
            String reqiredStr = name + ".reqired";
            GameRegistry.registerCustomItemStack(reqiredStr,reqiredBlade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

            reqiredBlade = reqiredBlade.copy();
            reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);

            IRecipe recipe = new RecipeAwakeBlade(blade,reqiredBlade,
                    "PPP",
                    "PXP",
                    "PPP",
                    'X', reqiredBlade,
                    'P', proudsoul);

            SlashBlade.addRecipe(name, recipe);
        }
    }
}
