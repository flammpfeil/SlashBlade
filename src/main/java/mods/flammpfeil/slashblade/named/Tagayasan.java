package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.RecipeAwakeBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Furia on 15/02/12.
 */
public class Tagayasan {

    public static final String Tagayasan = "flammpfeil.slashblade.named.tagayasan";

    @SubscribeEvent()
    public void init(LoadEvent.InitEvent event){
        ItemStack itemSphereBladeSoul = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr , 1);

        ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
        NBTTagCompound tag = new NBTTagCompound();
        customblade.setTagCompound(tag);

        customblade.addEnchantment(Enchantment.unbreaking,3);
        customblade.addEnchantment(Enchantment.smite,3);
        String name = Tagayasan;
        ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);
        ItemSlashBladeNamed.CurrentItemName.set(tag, name);
        ItemSlashBladeNamed.CustomMaxDamage.set(tag, 70);
        ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getDamageVsEntity());
        ItemSlashBlade.TextureName.set(tag,"named/tagayasan");
        ItemSlashBlade.SpecialAttackType.set(tag, 1);
        ItemSlashBlade.StandbyRenderType.set(tag, 1);

        tag.setString(ItemSlashBladeNamed.RepairMaterialNameStr,"iron_ingot");

        GameRegistry.registerCustomItemStack(name, customblade);
        ItemSlashBladeNamed.NamedBlades.add(name);
        {
            ItemStack reqiredBlade = new ItemStack(SlashBlade.bladeWood);
            reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);
            NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
            ItemSlashBlade.KillCount.set(reqTag,1000);

            reqiredBlade.setStackDisplayName("thousandkill woodblade");

            name = "flammpfeil.slashblade.tagayasan.reqired";
            GameRegistry.registerCustomItemStack(name, reqiredBlade);
            ItemSlashBladeNamed.NamedBlades.add(name);

            SlashBlade.addRecipe(Tagayasan,
                    new RecipeAwakeBlade(customblade,
                    reqiredBlade,
                    "XEX",
                    "PBP",
                    "XEX",
                    'X',itemSphereBladeSoul,
                    'B',reqiredBlade,
                    'P',new ItemStack(Items.ender_pearl),
                    'E',new ItemStack(Items.ender_eye)));
        }
    }
}
