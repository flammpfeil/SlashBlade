package mods.flammpfeil.slashblade.named;

import mods.flammpfeil.slashblade.util.DummyPotionRecipe;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingOreRecipe;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.crafting.RecipeBladeSoulUpgrade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Furia on 15/02/12.
 */
public class BladeMaterials {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void init(LoadEvent.InitEvent event){
        ItemStack itemProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr , 1);
        ItemStack itemIngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr , 1);
        ItemStack itemSphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr , 1);
        ItemStack itemTinyBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr , 1);

        {
            ItemStack result = itemTinyBladeSoul.copy();
            result.setCount(2);

            SlashBlade.addRecipe(SlashBlade.TinyBladeSoulStr,
                    new RecipeBladeSoulUpgrade(result,"X", 'X', itemProudSoul));
        }

        SlashBlade.addRecipe(SlashBlade.ProudSoulStr,
                new RecipeBladeSoulUpgrade(itemProudSoul,
                "XX",
                'X', itemTinyBladeSoul));

        SlashBlade.addRecipe(SlashBlade.IngotBladeSoulStr,
                new RecipeBladeSoulUpgrade(itemIngotBladeSoul,
                " P ",
                "PIP",
                " P ",
                'I', Items.IRON_INGOT,
                'P', itemProudSoul));

        SlashBlade.addRecipe(SlashBlade.IngotBladeSoulStr,
                new RecipeBladeSoulUpgrade(itemIngotBladeSoul,
                " P ",
                "PIP",
                " P ",
                'I', "ingotSteel",
                'P', itemTinyBladeSoul));

        SlashBlade.addSmelting(SlashBlade.SphereBladeSoulStr,
                itemIngotBladeSoul, itemSphereBladeSoul, 2.0F);

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), itemSphereBladeSoul, new ItemStack(Items.EXPERIENCE_BOTTLE)){

            @Override
            public boolean isInput(ItemStack stack) {
                return PotionUtils.getPotionFromItem(getInput()).equals( PotionUtils.getPotionFromItem(stack));
            }
        });

        SlashBlade.addRecipe(SlashBlade.SphereBladeSoulStr,
                new DummyPotionRecipe(new ItemStack(Items.EXPERIENCE_BOTTLE),
                        itemSphereBladeSoul,
                        PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER)));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), itemProudSoul, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.STRONG_STRENGTH)){
            @Override
            public boolean isInput(ItemStack stack) {
                return PotionUtils.getPotionFromItem(getInput()).equals( PotionUtils.getPotionFromItem(stack));
            }
        });

        SlashBlade.addRecipe(SlashBlade.ProudSoulStr,
                new DummyPotionRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.STRONG_STRENGTH),
                       itemProudSoul,
                       PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER)));
    }
}
