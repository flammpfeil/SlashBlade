package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by Furia on 15/02/12.
 */
public class SimpleBlade {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void init(LoadEvent.InitEvent event){
        ItemStack itemIngotBladeSoul = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.IngotBladeSoulStr,1);
        ItemStack itemSphereBladeSoul = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1);

        {
            ItemStack woodSword = new ItemStack(Items.wooden_sword,1,1);
            NBTTagCompound displayTag = new NBTTagCompound();
            woodSword.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("required 1 damage"));
            loreList.appendTag(new NBTTagString("bad : break block 2damage"));
            loreList.appendTag(new NBTTagString("good : attack mob 1damage﻿"));
            displayTag.setTag("Lore", loreList);

            SlashBlade.addRecipe("slashbladeWood",
                    new ShapedOreRecipe(new ItemStack(SlashBlade.bladeWood),
                            "  #",
                            " # ",
                            "X  ",
                            '#', "logWood",
                            'X', woodSword));//new ItemStack(Items.wooden_sword, 1, 1)));
        }



        SlashBlade.addRecipe("slashbladeBambooLight",
                new RecipeUpgradeBlade(new ItemStack(SlashBlade.bladeBambooLight),
                "  #",
                " # ",
                "X  ",
                '#',"bamboo",
                'X', new ItemStack(SlashBlade.bladeWood,1, OreDictionary.WILDCARD_VALUE)));


        SlashBlade.addRecipe("slashbladeSilverBambooLight",
                new RecipeUpgradeBlade(new ItemStack(SlashBlade.bladeSilverBambooLight),
                " TI",
                "SXK",
                "PS ",
                'T', Items.egg,
                'I', Items.iron_ingot,
                'S', Items.string,
                'X', new ItemStack(SlashBlade.bladeBambooLight,1,OreDictionary.WILDCARD_VALUE),
                'K', "dyeBlack",
                'P', Items.paper //S
        ));
        SlashBlade.addRecipe("slashbladeSilverBambooLight",
                new RecipeUpgradeBlade(new ItemStack(SlashBlade.bladeSilverBambooLight),
                " TI",
                "SXK",
                "PS ",
                'T', Items.egg,
                'I', "ingotSilver",
                'S', Items.string,
                'X', new ItemStack(SlashBlade.bladeBambooLight,1,OreDictionary.WILDCARD_VALUE),
                'K', "dyeBlack",
                'P', Items.paper
        ));

        {
            ItemStack blade = new ItemStack(SlashBlade.bladeSilverBambooLight);
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);

            blade.setItemDamage(blade.getMaxDamage()-1);
            ItemSlashBlade.KillCount.set(tag, 100);

            String key = "testsilver";
            GameRegistry.registerCustomItemStack(key, blade);
            ItemSlashBladeNamed.NamedBlades.add(key);

        }


        SlashBlade.addRecipe("slashbladeWhite",
                new RecipeUpgradeBlade(new ItemStack(SlashBlade.bladeWhiteSheath, 1, SlashBlade.bladeWhiteSheath.getMaxDamage() / 3),
                "  #",
                " # ",
                "XG ",
                '#', Items.iron_ingot,
                'G', Items.gold_ingot,
                'X', new ItemStack(SlashBlade.bladeWood,1,OreDictionary.WILDCARD_VALUE)));
        SlashBlade.addRecipe("slashbladeWhite",
                new RecipeUpgradeBlade(new ItemStack(SlashBlade.bladeWhiteSheath, 1, SlashBlade.bladeWhiteSheath.getMaxDamage() / 4),
                "  #",
                " # ",
                "XG ",
                '#', "ingotSteel",
                'G', Items.gold_ingot,
                'X', new ItemStack(SlashBlade.bladeWood,1,OreDictionary.WILDCARD_VALUE)));

        ItemStack white = new ItemStack(SlashBlade.bladeWhiteSheath, 1);
        white.addEnchantment(Enchantment.unbreaking,4);
        ItemStack requiredBlade = new ItemStack(SlashBlade.bladeWood,1,OreDictionary.WILDCARD_VALUE);
        SlashBlade.addRecipe("slashbladeWhite",
                new RecipeAwakeBlade(white,requiredBlade,
                "  #",
                " # ",
                "XG ",
                '#', itemIngotBladeSoul,
                'G', Items.gold_ingot,
                'X', requiredBlade));


        ItemStack brokenBladeWhite = new ItemStack(SlashBlade.bladeWhiteSheath,1,0);
        brokenBladeWhite.setItemDamage(brokenBladeWhite.getMaxDamage());
        brokenBladeWhite.setStackDisplayName("BrokenBladeWhite");
        ItemSlashBlade.IsBroken.set(brokenBladeWhite.getTagCompound(), true);
        GameRegistry.registerCustomItemStack(SlashBlade.BrokenBladeWhiteStr, brokenBladeWhite);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.BrokenBladeWhiteStr);


        SlashBlade.addRecipe("slashblade",
                new RecipeUpgradeBlade(new ItemStack(SlashBlade.weapon),
                " BI",
                "L#C",
                "SG ",
                'L', Blocks.lapis_block,
                'C', Blocks.coal_block,
                'I', itemSphereBladeSoul,
                'B', Items.blaze_rod,
                'G', Items.gold_ingot,
                'S', Items.string,
                '#', brokenBladeWhite
        ));
    }
}
