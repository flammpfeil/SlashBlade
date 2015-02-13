package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Furia on 15/02/12.
 */
public class Yamato {
    @SubscribeEvent()
    public void init(LoadEvent.InitEvent event){
        ItemStack itemSphereBladeSoul = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1);

        {
            String nameTrue = "flammpfeil.slashblade.named.yamato";
            {

                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                customblade.addEnchantment(Enchantment.thorns, 1);
                customblade.addEnchantment(Enchantment.featherFalling, 4);
                customblade.addEnchantment(Enchantment.power, 5);
                customblade.addEnchantment(Enchantment.punch, 2);

                ItemSlashBladeNamed.CurrentItemName.set(tag, nameTrue);
                ItemSlashBladeNamed.IsDefaultBewitched.set(tag, true);
                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 40);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.EMERALD.getDamageVsEntity());
                ItemSlashBlade.TextureName.set(tag,"named/yamato");
                ItemSlashBlade.ModelName.set(tag,"named/yamato");
                ItemSlashBlade.SpecialAttackType.set(tag, 0);
                ItemSlashBlade.StandbyRenderType.set(tag, 1);

                ItemSlashBlade.KillCount.set(tag,1000);
                ItemSlashBlade.ProudSoul.set(tag, 1000);
                GameRegistry.registerCustomItemStack(nameTrue, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameTrue);
            }

            {

                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                String nameBrokend = nameTrue + ".broken";
                ItemSlashBladeNamed.CurrentItemName.set(tag, nameBrokend);

                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 40);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.EMERALD.getDamageVsEntity());
                ItemSlashBlade.TextureName.set(tag, "named/yamato");
                ItemSlashBlade.ModelName.set(tag, "named/yamato");
                ItemSlashBlade.SpecialAttackType.set(tag, 0);
                ItemSlashBlade.StandbyRenderType.set(tag, 1);

                ItemSlashBlade.IsBroken.set(tag, true);
                ItemSlashBlade.IsNoScabbard.set(tag, true);
                ItemSlashBlade.IsSealed.set(tag, true);
                ItemSlashBladeNamed.TrueItemName.set(tag, nameTrue);
                GameRegistry.registerCustomItemStack(nameBrokend, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameBrokend);

                {
                    ItemStack reqiredBlade = customblade.copy();
                    reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);
                    NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                    ItemSlashBlade.ProudSoul.set(reqTag, 1000);

                    reqiredBlade.setStackDisplayName("thousandProudSouls");

                    String nameReqired = nameTrue + ".reqired";
                    GameRegistry.registerCustomItemStack(nameReqired, reqiredBlade);
                    ItemSlashBladeNamed.NamedBlades.add(nameReqired);

                    ItemStack yamato = GameRegistry.findItemStack(SlashBlade.modid,nameTrue,1);
                    SlashBlade.addRecipe(nameTrue,
                            new RecipeAwakeBlade(yamato,
                                    reqiredBlade,
                                    "XXX",
                                    "XBX",
                                    "XXX",
                                    'X', itemSphereBladeSoul,
                                    'B', reqiredBlade));
                }
            }
        }
    }
    @SubscribeEvent
    public void postinit(LoadEvent.PostInitEvent event){
        DropEventHandler.registerEntityDrop("HardcoreEnderExpansion.Dragon", 1.0f, GameRegistry.findItemStack(SlashBlade.modid, "flammpfeil.slashblade.named.yamato.broken", 1));
        DropEventHandler.registerEntityDrop("EnderDragon", 1.0f, GameRegistry.findItemStack(SlashBlade.modid, "flammpfeil.slashblade.named.yamato.broken", 1));
    }
}
