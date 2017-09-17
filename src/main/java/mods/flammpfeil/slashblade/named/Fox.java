package mods.flammpfeil.slashblade.named;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.init.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

/**
 * Created by Furia on 14/07/07.
 */
public class Fox {
    static public final String nameWhite = "flammpfeil.slashblade.named.fox.white";
    static public final String nameBlack = "flammpfeil.slashblade.named.fox.black";

    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){

        {
            String name = nameWhite;

            ItemStack customblade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            SlashBlade.wrapBlade.removeWrapItem(customblade);

            customblade.addEnchantment(Enchantments.KNOCKBACK,2);
            customblade.addEnchantment(Enchantments.BANE_OF_ARTHROPODS,2);
            customblade.addEnchantment(Enchantments.UNBREAKING,3);
            customblade.addEnchantment(Enchantments.LOOTING,3);
            customblade.addEnchantment(Enchantments.FIRE_ASPECT,2);

            NBTTagCompound tag = customblade.getTagCompound();

            ItemStack innerBlade = SlashBlade.findItemStack("minecraft", "wooden_sword", 1);

            SlashBlade.wrapBlade.setWrapItem(customblade,innerBlade);

            ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);

            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.TrueItemName.set(tag, name);

            ItemSlashBlade.TextureName.set(tag, "named/sange/white");
            ItemSlashBlade.ModelName.set(tag, "named/sange/sange");

            ItemSlashBlade.SpecialAttackType.set(tag, 0); //0:次元斬
            ItemSlashBlade.StandbyRenderType.set(tag, 1);

            ItemSlashBladeNamed.IsDefaultBewitched.set(tag, true);

            NamedBladeManager.registerBladeSoul(tag , customblade.getDisplayName());
            SlashBlade.registerCustomItemStack(name, customblade);

            customblade = customblade.copy();
            NBTTagCompound displayTag = new NBTTagCompound();
            customblade.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("is demo item. is wooden sword"));
            loreList.appendTag(new NBTTagString("true performance : please crafting"));
            displayTag.setTag("Lore", loreList);
            String creativeStr = name+".creative";
            SlashBlade.registerCustomItemStack(creativeStr, customblade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + creativeStr);
        }

        {
            String name = nameBlack;

            ItemStack customblade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            SlashBlade.wrapBlade.removeWrapItem(customblade);

            customblade.addEnchantment(Enchantments.SMITE,4);
            customblade.addEnchantment(Enchantments.KNOCKBACK,2);
            customblade.addEnchantment(Enchantments.FIRE_ASPECT, 2);

            NBTTagCompound tag = customblade.getTagCompound();

            ItemStack innerBlade = SlashBlade.findItemStack("minecraft", "wooden_sword", 1);

            SlashBlade.wrapBlade.setWrapItem(customblade, innerBlade);

            ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);

            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.TrueItemName.set(tag, name);

            ItemSlashBlade.TextureName.set(tag, "named/sange/black");
            ItemSlashBlade.ModelName.set(tag, "named/sange/sange");

            ItemSlashBlade.SpecialAttackType.set(tag, 4); //4:シュンカ一段
            ItemSlashBlade.StandbyRenderType.set(tag, 1);

            ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

            NamedBladeManager.registerBladeSoul(tag , customblade.getDisplayName());
            SlashBlade.registerCustomItemStack(name, customblade);

            customblade = customblade.copy();
            NBTTagCompound displayTag = new NBTTagCompound();
            customblade.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("is demo item. is wooden sword"));
            loreList.appendTag(new NBTTagString("true performance : please crafting"));
            displayTag.setTag("Lore", loreList);
            String creativeStr = name+".creative";
            SlashBlade.registerCustomItemStack(creativeStr, customblade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + creativeStr);
        }
    }

    @SubscribeEvent
    public void postInit(LoadEvent.PostInitEvent event){

        ItemStack innerBlade = SlashBlade.findItemStack("minecraft", "wooden_sword", 1);

        ItemStack kitunebi = SlashBlade.findItemStack("BambooMod","kitunebi",1);
        if(kitunebi.isEmpty())
            return;

        ItemStack inari = SlashBlade.findItemStack("TofuCraft","foodSet",1);
        if(!inari.isEmpty())
            inari.setItemDamage(14);
        else
            inari = new ItemStack(Items.WHEAT,1);

        ItemStack proudsoul = SlashBlade.findItemStack(SlashBlade.modid,"proudsoul",1);

        {
            ItemStack blade = SlashBlade.getCustomBlade(SlashBlade.modid,nameWhite);

            ItemStack reqiredBlade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            {
                SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

                reqiredBlade.addEnchantment(Enchantments.LOOTING,1);
                NBTTagCompound tag = reqiredBlade.getTagCompound();
                ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap.BambooMod.katana");
                ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);
                ItemSlashBlade.TextureName.set(tag,"BambooKatana");
                ItemSlashBlade.KillCount.set(tag,199);
                ItemSlashBlade.ProudSoul.set(tag,1000);
                ItemSlashBlade.RepairCount.set(tag,1);

                reqiredBlade.setStackDisplayName("BambooMod katana");
            }
            String reqiredStr = nameWhite + ".reqired";
            SlashBlade.registerCustomItemStack(reqiredStr,reqiredBlade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

            reqiredBlade = reqiredBlade.copy();
            reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);

            IRecipe recipe = new RecipeAwakeBladeFox(new ResourceLocation(SlashBlade.modid,"fox_white"),
                    blade,reqiredBlade,
                    "FPF",
                    "FXF",
                    "FIF",
                    'X', reqiredBlade,
                    'F', kitunebi,
                    'I', inari,
                    'P', proudsoul);

            SlashBlade.addRecipe(nameWhite, recipe);
        }
        {
            ItemStack blade = SlashBlade.getCustomBlade(SlashBlade.modid,nameBlack);

            ItemStack reqiredBlade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            {
                SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

                reqiredBlade.addEnchantment(Enchantments.SMITE,1);
                NBTTagCompound tag = reqiredBlade.getTagCompound();
                ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap.BambooMod.katana");
                ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);
                ItemSlashBlade.TextureName.set(tag,"BambooKatana");
                ItemSlashBlade.KillCount.set(tag,199);
                ItemSlashBlade.ProudSoul.set(tag,1000);
                ItemSlashBlade.RepairCount.set(tag,1);

                reqiredBlade.setStackDisplayName("BambooMod katana");
            }
            String reqiredStr = nameBlack + ".reqired";
            SlashBlade.registerCustomItemStack(reqiredStr,reqiredBlade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

            reqiredBlade = reqiredBlade.copy();
            reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);

            IRecipe recipe = new RecipeAwakeBladeFox(new ResourceLocation(SlashBlade.modid,"fox_black"),
                    blade,reqiredBlade,
                    "FPF",
                    "FXF",
                    "FIF",
                    'X', reqiredBlade,
                    'F', kitunebi,
                    'I', inari,
                    'P', proudsoul);

            SlashBlade.addRecipe(nameBlack, recipe);
        }

        RecipeSorter.register("flammpfeil.slashblade:fox", RecipeAwakeBladeFox.class, SHAPED, "after:forge:shaped");
    }
}
