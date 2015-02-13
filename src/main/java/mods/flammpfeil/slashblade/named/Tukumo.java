package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.RecipeAwakeBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Furia on 15/02/12.
 */
public class Tukumo {

    static public final String YuzukiTukumo = "flammpfeil.slashblade.named.yuzukitukumo";

    @SubscribeEvent()
    public void init(LoadEvent.InitEvent event){

        {
            ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            customblade.addEnchantment(Enchantment.sharpness, 4);
            customblade.addEnchantment(Enchantment.unbreaking, 3);
            customblade.addEnchantment(Enchantment.fireAspect, 2);

            String name = YuzukiTukumo;
            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.IsDefaultBewitched.set(tag, true);
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, 40);
            ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.EMERALD.getDamageVsEntity());
            ItemSlashBlade.TextureName.set(tag, "named/a_tukumo");
            ItemSlashBlade.ModelName.set(tag, "named/agito");
            ItemSlashBlade.SpecialAttackType.set(tag, 3);
            ItemSlashBlade.StandbyRenderType.set(tag, 1);

            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.NamedBlades.add(name);

            {
                ItemStack custombladeReqired = new ItemStack(SlashBlade.weapon,1,0);
                custombladeReqired.setItemDamage(OreDictionary.WILDCARD_VALUE);

                NBTTagCompound tagReqired = new NBTTagCompound();
                custombladeReqired.setTagCompound(tagReqired);

                custombladeReqired.addEnchantment(Enchantment.fireAspect, 1);

                ItemSlashBlade.KillCount.set(tagReqired, 1000);

                String nameReqired = "flammpfeil.slashblade.thousandkill";
                GameRegistry.registerCustomItemStack(nameReqired, custombladeReqired);
                ItemSlashBladeNamed.NamedBlades.add(nameReqired);

                SlashBlade.addRecipe(YuzukiTukumo,
                        new RecipeAwakeBlade(customblade,
                                custombladeReqired,
                                "ESD",
                                "RBL",
                                "ISG",
                                'E', new ItemStack(Blocks.emerald_block),
                                'D', new ItemStack(Blocks.diamond_block),
                                'R', new ItemStack(Blocks.redstone_block),
                                'L', new ItemStack(Blocks.lapis_block),
                                'I', new ItemStack(Blocks.iron_block),
                                'G', new ItemStack(Blocks.gold_block),
                                'S', GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1),
                                'B', custombladeReqired));

            }
        }

    }
}
