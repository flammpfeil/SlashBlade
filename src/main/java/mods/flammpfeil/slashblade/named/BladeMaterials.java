package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.crafting.RecipeBladeSoulUpgrade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Furia on 15/02/12.
 */
public class BladeMaterials {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void init(LoadEvent.InitEvent event){
        ItemStack itemProudSoul = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr , 1);
        ItemStack itemIngotBladeSoul = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr , 1);
        ItemStack itemSphereBladeSoul = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr , 1);
        ItemStack itemTinyBladeSoul = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr , 1);

        {
            ItemStack result = itemTinyBladeSoul.copy();
            result.stackSize = 2;

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
                'I', Items.iron_ingot,
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

        SlashBlade.addRecipe(SlashBlade.ProudSoulStr,
                new ShapedOreRecipe(new ItemStack(Items.experience_bottle),
                        " P ",
                        " S ",
                        " I ",
                        'S', new ItemStack(Items.brewing_stand),
                        'P', itemProudSoul,
                        'I', new ItemStack(Items.potionitem, 1, 8192))
                ,true);

        SlashBlade.addRecipe(SlashBlade.TinyBladeSoulStr,
                new ShapedOreRecipe(new ItemStack(Items.potionitem,1,8233),
                        " P ",
                        " S ",
                        " I ",
                        'S',new ItemStack(Items.brewing_stand),
                        'P',itemProudSoul,
                        'I',new ItemStack(Items.potionitem,1,0))
                ,true);
    }
}
