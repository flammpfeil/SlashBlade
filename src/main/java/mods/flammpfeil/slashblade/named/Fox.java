package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
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

            ItemStack customblade = GameRegistry.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            SlashBlade.wrapBlade.removeWrapItem(customblade);

            customblade.addEnchantment(Enchantment.knockback,2);
            customblade.addEnchantment(Enchantment.baneOfArthropods,2);
            customblade.addEnchantment(Enchantment.unbreaking,3);
            customblade.addEnchantment(Enchantment.looting,3);
            customblade.addEnchantment(Enchantment.fireAspect,2);

            NBTTagCompound tag = customblade.getTagCompound();

            ItemStack innerBlade = GameRegistry.findItemStack("minecraft", "wooden_sword", 1);

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
            GameRegistry.registerCustomItemStack(name, customblade);

            customblade = customblade.copy();
            NBTTagCompound displayTag = new NBTTagCompound();
            customblade.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("is demo item. is wooden sword"));
            loreList.appendTag(new NBTTagString("true performance : please crafting"));
            displayTag.setTag("Lore", loreList);
            String creativeStr = name+".creative";
            GameRegistry.registerCustomItemStack(creativeStr, customblade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + creativeStr);
        }

        {
            String name = nameBlack;

            ItemStack customblade = GameRegistry.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            SlashBlade.wrapBlade.removeWrapItem(customblade);

            customblade.addEnchantment(Enchantment.smite,4);
            customblade.addEnchantment(Enchantment.knockback,2);
            customblade.addEnchantment(Enchantment.fireAspect, 2);

            NBTTagCompound tag = customblade.getTagCompound();

            ItemStack innerBlade = GameRegistry.findItemStack("minecraft", "wooden_sword", 1);

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
            GameRegistry.registerCustomItemStack(name, customblade);

            customblade = customblade.copy();
            NBTTagCompound displayTag = new NBTTagCompound();
            customblade.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("is demo item. is wooden sword"));
            loreList.appendTag(new NBTTagString("true performance : please crafting"));
            displayTag.setTag("Lore", loreList);
            String creativeStr = name+".creative";
            GameRegistry.registerCustomItemStack(creativeStr, customblade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + creativeStr);
        }
    }

    @SubscribeEvent
    public void postInit(LoadEvent.PostInitEvent event){

        ItemStack innerBlade = GameRegistry.findItemStack("minecraft", "wooden_sword", 1);

        ItemStack kitunebi = GameRegistry.findItemStack("BambooMod","kitunebi",1);
        if(kitunebi == null)
            return;

        ItemStack inari = GameRegistry.findItemStack("TofuCraft","foodSet",1);
        if(inari != null)
            inari.setItemDamage(14);
        else
            inari = new ItemStack(Items.wheat,1);

        ItemStack proudsoul = GameRegistry.findItemStack(SlashBlade.modid,"proudsoul",1);

        {
            ItemStack blade = SlashBlade.getCustomBlade(SlashBlade.modid,nameWhite);

            ItemStack reqiredBlade = GameRegistry.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            {
                SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

                reqiredBlade.addEnchantment(Enchantment.looting,1);
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
            GameRegistry.registerCustomItemStack(reqiredStr,reqiredBlade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

            reqiredBlade = reqiredBlade.copy();
            reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);

            IRecipe recipe = new RecipeAwakeBladeFox(blade,reqiredBlade,
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

            ItemStack reqiredBlade = GameRegistry.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
            {
                SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

                reqiredBlade.addEnchantment(Enchantment.smite,1);
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
            GameRegistry.registerCustomItemStack(reqiredStr,reqiredBlade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

            reqiredBlade = reqiredBlade.copy();
            reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);

            IRecipe recipe = new RecipeAwakeBladeFox(blade,reqiredBlade,
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
