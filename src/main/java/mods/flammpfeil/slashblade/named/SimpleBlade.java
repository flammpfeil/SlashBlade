package mods.flammpfeil.slashblade.named;

import mods.flammpfeil.slashblade.RecipeAwakeBlade;
import mods.flammpfeil.slashblade.capability.BladeCapabilityProvider;
import mods.flammpfeil.slashblade.item.crafting.BladeIngredient;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.init.Enchantments;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.RecipeUpgradeBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
        ItemStack itemIngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.IngotBladeSoulStr,1);
        ItemStack itemSphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1);

        {
            ItemStack woodSword = new ItemStack(Items.WOODEN_SWORD,1, OreDictionary.WILDCARD_VALUE/*,1*/);
            NBTTagCompound displayTag = new NBTTagCompound();
            /*
            woodSword.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("required 1 damage"));
            loreList.appendTag(new NBTTagString("bad : break block 2damage"));
            loreList.appendTag(new NBTTagString("good : attack mob 1damage﻿"));
            displayTag.setTag("Lore", loreList);
            */

            SlashBlade.addRecipe("slashbladeWood",
                    new ShapedOreRecipe(new ResourceLocation(SlashBlade.modid,"recipexes"), new ItemStack(SlashBlade.bladeWood) ,
                            "  #",
                            " # ",
                            "X  ",
                            '#', "logWood",
                            'X', Ingredient.fromStacks(woodSword)));//new ItemStack(Items.wooden_sword, 1, 1)));
        }


        if(OreDictionary.getOres("bamboo").isEmpty()){

            SlashBlade.addRecipe("slashbladeBambooLight",
                    new RecipeUpgradeBlade(new ResourceLocation(SlashBlade.modid,"bamboolight"),
                            new ItemStack(SlashBlade.bladeBambooLight),
                            "  #",
                            " # ",
                            "X  ",
                            '#', new ItemStack(Items.REEDS),
                            'X', new BladeIngredient(new ItemStack(SlashBlade.bladeWood,1, OreDictionary.WILDCARD_VALUE))));
        }else{

            SlashBlade.addRecipe("slashbladeBambooLight",
                    new RecipeUpgradeBlade(new ResourceLocation(SlashBlade.modid,"bamboolight"),
                            new ItemStack(SlashBlade.bladeBambooLight),
                            "  #",
                            " # ",
                            "X  ",
                            '#',"bamboo",
                            'X', new BladeIngredient(new ItemStack(SlashBlade.bladeWood,1, OreDictionary.WILDCARD_VALUE))));
        }


        SlashBlade.addRecipe("slashbladeSilverBambooLight",
                new RecipeUpgradeBlade(new ResourceLocation(SlashBlade.modid,"silverlight"),
                        new ItemStack(SlashBlade.bladeSilverBambooLight),
                " TI",
                "SXK",
                "PS ",
                'T', new ItemStack(Items.EGG),
                'I', new ItemStack(Items.IRON_INGOT),
                'S', new ItemStack(Items.STRING),
                'X', new BladeIngredient(new ItemStack(SlashBlade.bladeBambooLight,1,OreDictionary.WILDCARD_VALUE)),
                'K', "dyeBlack",
                'P', new ItemStack(Items.PAPER) //S
        ));
        SlashBlade.addRecipe("slashbladeSilverBambooLight",
                new RecipeUpgradeBlade(new ResourceLocation(SlashBlade.modid,"silverlight2"),
                        new ItemStack(SlashBlade.bladeSilverBambooLight),
                " TI",
                "SXK",
                "PS ",
                'T', Items.EGG,
                'I', "ingotSilver",
                'S', Items.STRING,
                'X', new BladeIngredient(new ItemStack(SlashBlade.bladeBambooLight,1,OreDictionary.WILDCARD_VALUE)),
                'K', "dyeBlack",
                'P', Items.PAPER
        ));

        {
            ItemStack blade = new ItemStack(SlashBlade.bladeSilverBambooLight);
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);

            blade.setItemDamage(blade.getMaxDamage()-1);
            ItemSlashBlade.KillCount.set(tag, 100);

            String key = "testsilver";
            SlashBlade.registerCustomItemStack(key, blade);
            ItemSlashBladeNamed.NamedBlades.add(key);

        }


        SlashBlade.addRecipe("slashbladeWhite",
                new RecipeUpgradeBlade(new ResourceLocation(SlashBlade.modid,"white"),
                        new ItemStack(SlashBlade.bladeWhiteSheath, 1, SlashBlade.bladeWhiteSheath.getMaxDamage() / 3),
                "  #",
                " # ",
                "XG ",
                '#', new ItemStack(Items.IRON_INGOT),
                'G', new ItemStack(Items.GOLD_INGOT),
                'X', new BladeIngredient(new ItemStack(SlashBlade.bladeWood,1,OreDictionary.WILDCARD_VALUE))));
        SlashBlade.addRecipe("slashbladeWhite",
                new RecipeUpgradeBlade(new ResourceLocation(SlashBlade.modid,"white2"),
                        new ItemStack(SlashBlade.bladeWhiteSheath, 1, SlashBlade.bladeWhiteSheath.getMaxDamage() / 4),
                "  #",
                " # ",
                "XG ",
                '#', "ingotSteel",
                'G', new ItemStack(Items.GOLD_INGOT),
                'X', new BladeIngredient(new ItemStack(SlashBlade.bladeWood,1,OreDictionary.WILDCARD_VALUE))));

        ItemStack white = new ItemStack(SlashBlade.bladeWhiteSheath, 1);
        white.addEnchantment(Enchantments.UNBREAKING,4);
        ItemStack requiredBlade =new ItemStack(SlashBlade.bladeWood,1,OreDictionary.WILDCARD_VALUE);
        SlashBlade.addRecipe("slashbladeWhite",
                new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,"white3"),
                        white, requiredBlade,
                "  #",
                " # ",
                "XG ",
                '#', itemIngotBladeSoul,
                'G', new ItemStack(Items.GOLD_INGOT),
                'X', new BladeIngredient(requiredBlade)));


        ItemStack brokenBladeWhite = new ItemStack(SlashBlade.bladeWhiteSheath,1,0);
        brokenBladeWhite.setItemDamage(brokenBladeWhite.getMaxDamage());
        brokenBladeWhite.setStackDisplayName("BrokenBladeWhite");
        ItemSlashBlade.IsBroken.set(brokenBladeWhite.getTagCompound(), true);
        SlashBlade.registerCustomItemStack(SlashBlade.BrokenBladeWhiteStr, brokenBladeWhite);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.BrokenBladeWhiteStr);


        SlashBlade.addRecipe("slashblade",
                new RecipeUpgradeBlade(new ResourceLocation(SlashBlade.modid,"slashblade"),
                        new ItemStack(SlashBlade.weapon),
                " BI",
                "L#C",
                "SG ",
                'L', new ItemStack(Blocks.LAPIS_BLOCK),
                'C', new ItemStack(Blocks.COAL_BLOCK),
                'I', itemSphereBladeSoul,
                'B', new ItemStack(Items.BLAZE_ROD),
                'G', new ItemStack(Items.GOLD_INGOT),
                'S', new ItemStack(Items.STRING),
                '#', new BladeIngredient(brokenBladeWhite)
        ));

        {
            ItemStack customblade = new ItemStack(SlashBlade.weapon);
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(customblade);

            SpecialEffects.addEffect(customblade,SpecialEffects.BlastEdge);

            customblade.setStackDisplayName("TestBlastEdge");

            String key = "TestBlastEdge";
            SlashBlade.registerCustomItemStack(key, customblade);
            ItemSlashBladeNamed.NamedBlades.add(key);

        }

        {
            ItemStack customblade = new ItemStack(SlashBlade.weapon);
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(customblade);

            SpecialEffects.addEffect(customblade,SpecialEffects.HFCustom);

            customblade.setStackDisplayName("TestHFCustom");

            String key = "TestHFCustom";
            SlashBlade.registerCustomItemStack(key, customblade);
            ItemSlashBladeNamed.NamedBlades.add(key);

        }
        {
            ItemStack customblade = new ItemStack(SlashBlade.weapon);
            NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(customblade);

            SpecialEffects.addEffect(customblade,SpecialEffects.HFCustom);

            customblade.setStackDisplayName("TestHFCustom");

            IEnergyStorage storage = customblade.getCapability(BladeCapabilityProvider.ENERGY, null);
            storage.receiveEnergy(storage.getMaxEnergyStored(),false);

            String key = "TestHFCustomFull";
            SlashBlade.registerCustomItemStack(key, customblade);
            ItemSlashBladeNamed.NamedBlades.add(key);

        }
    }
}
