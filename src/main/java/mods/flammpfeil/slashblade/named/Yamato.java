package mods.flammpfeil.slashblade.named;

import mods.flammpfeil.slashblade.event.DropEventHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.init.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 15/02/12.
 */
public class Yamato {
    @SubscribeEvent()
    public void init(LoadEvent.InitEvent event){
        ItemStack itemSphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1);

        {
            String nameTrue = "flammpfeil.slashblade.named.yamato";
            {

                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                customblade.addEnchantment(Enchantments.THORNS, 1);
                customblade.addEnchantment(Enchantments.FEATHER_FALLING, 4);
                customblade.addEnchantment(Enchantments.POWER, 5);
                customblade.addEnchantment(Enchantments.PUNCH, 2);

                ItemSlashBladeNamed.CurrentItemName.set(tag, nameTrue);
                ItemSlashBladeNamed.IsDefaultBewitched.set(tag, true);
                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 40);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.DIAMOND.getAttackDamage());
                ItemSlashBlade.TextureName.set(tag,"named/yamato");
                ItemSlashBlade.ModelName.set(tag,"named/yamato");
                ItemSlashBlade.SpecialAttackType.set(tag, 0);
                ItemSlashBlade.StandbyRenderType.set(tag, 1);

                ItemSlashBlade.KillCount.set(tag,1000);
                ItemSlashBlade.ProudSoul.set(tag, 1000);
                SlashBlade.registerCustomItemStack(nameTrue, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameTrue);
            }

            {

                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                String nameBrokend = nameTrue + ".broken";
                ItemSlashBladeNamed.CurrentItemName.set(tag, nameBrokend);

                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 40);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.DIAMOND.getAttackDamage());
                ItemSlashBlade.TextureName.set(tag, "named/yamato");
                ItemSlashBlade.ModelName.set(tag, "named/yamato");
                ItemSlashBlade.SpecialAttackType.set(tag, 0);
                ItemSlashBlade.StandbyRenderType.set(tag, 1);

                SpecialEffects.addEffect(customblade,SpecialEffects.Limitter);

                ItemSlashBlade.IsBroken.set(tag, true);
                ItemSlashBlade.IsNoScabbard.set(tag, true);
                ItemSlashBlade.IsSealed.set(tag, true);
                ItemSlashBladeNamed.TrueItemName.set(tag, nameTrue);
                SlashBlade.registerCustomItemStack(nameBrokend, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameBrokend);

                {
                    ItemStack reqiredBlade = customblade.copy();
                    NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                    ItemSlashBlade.ProudSoul.set(reqTag, 1000);

                    reqiredBlade.setStackDisplayName("thousandProudSouls");

                    String nameReqired = nameTrue + ".reqired";
                    SlashBlade.registerCustomItemStack(nameReqired, reqiredBlade);
                    ItemSlashBladeNamed.NamedBlades.add(nameReqired);

                    ItemStack yamato = SlashBlade.findItemStack(SlashBlade.modid,nameTrue,1);
                    SlashBlade.addRecipe(nameTrue,
                            new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,"yamato"),
                                    yamato,
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

        //todo: drop point move enderdragon egg point
        DropEventHandler.registerEntityDrop(new ResourceLocation("hardcoreenderexpansion","dragon"), 1.0f, SlashBlade.findItemStack(SlashBlade.modid, "flammpfeil.slashblade.named.yamato.broken", 1));
        DropEventHandler.registerEntityDrop(new ResourceLocation("ender_dragon"), 1.0f, SlashBlade.findItemStack(SlashBlade.modid, "flammpfeil.slashblade.named.yamato.broken", 1));
    }
}
