package mods.flammpfeil.slashblade.named;

import net.minecraft.init.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
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

            customblade.addEnchantment(Enchantments.SHARPNESS, 4);
            customblade.addEnchantment(Enchantments.UNBREAKING, 3);
            customblade.addEnchantment(Enchantments.FIRE_ASPECT, 2);

            String name = YuzukiTukumo;
            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.IsDefaultBewitched.set(tag, true);
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, 40);
            ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.DIAMOND.getAttackDamage());
            ItemSlashBlade.TextureName.set(tag, "named/a_tukumo");
            ItemSlashBlade.ModelName.set(tag, "named/agito");
            ItemSlashBlade.SpecialAttackType.set(tag, 3);
            ItemSlashBlade.StandbyRenderType.set(tag, 1);

            SlashBlade.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.NamedBlades.add(name);

            {
                ItemStack custombladeReqired = new ItemStack(SlashBlade.weapon,1,0);
                custombladeReqired.setItemDamage(OreDictionary.WILDCARD_VALUE);

                NBTTagCompound tagReqired = new NBTTagCompound();
                custombladeReqired.setTagCompound(tagReqired);

                custombladeReqired.addEnchantment(Enchantments.FIRE_ASPECT, 1);

                ItemSlashBlade.KillCount.set(tagReqired, 1000);

                String nameReqired = "flammpfeil.slashblade.thousandkill";
                SlashBlade.registerCustomItemStack(nameReqired, custombladeReqired);
                ItemSlashBladeNamed.NamedBlades.add(nameReqired);

                SlashBlade.addRecipe(YuzukiTukumo,
                        new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,"tukumo"),
                                customblade,
                                custombladeReqired,
                                "ESD",
                                "RBL",
                                "ISG",
                                'E', new ItemStack(Blocks.EMERALD_BLOCK),
                                'D', new ItemStack(Blocks.DIAMOND_BLOCK),
                                'R', new ItemStack(Blocks.REDSTONE_BLOCK),
                                'L', new ItemStack(Blocks.LAPIS_BLOCK),
                                'I', new ItemStack(Blocks.IRON_BLOCK),
                                'G', new ItemStack(Blocks.GOLD_BLOCK),
                                'S', SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1),
                                'B', custombladeReqired));

            }
        }

    }
}
