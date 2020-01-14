package mods.flammpfeil.slashblade.named;

import mods.flammpfeil.slashblade.event.DropEventHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
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
public class Agito {

    @SubscribeEvent()
    public void init(LoadEvent.InitEvent event){
        ItemStack itemProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr , 1);
        ItemStack itemSphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr , 1);

        {
            //------------- false
            String nameAgito = "flammpfeil.slashblade.named.agito";
            String nameAgitoRust = nameAgito + ".rust";
            String nameAgitoReqired = nameAgito + ".reqired";
            {
                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                ItemSlashBladeNamed.CurrentItemName.set(tag, nameAgito);
                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 60);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getAttackDamage());
                ItemSlashBlade.TextureName.set(tag, "named/agito_false");
                ItemSlashBlade.ModelName.set(tag, "named/agito");
                ItemSlashBlade.SpecialAttackType.set(tag, 2);
                ItemSlashBlade.StandbyRenderType.set(tag, 2);

                SlashBlade.registerCustomItemStack(nameAgito, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameAgito);
            }


            {
                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                ItemSlashBladeNamed.CurrentItemName.set(tag, nameAgitoRust);

                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 60);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.STONE.getAttackDamage());
                ItemSlashBlade.TextureName.set(tag, "named/agito_rust");
                ItemSlashBlade.ModelName.set(tag, "named/agito");
                ItemSlashBlade.SpecialAttackType.set(tag, 2);
                ItemSlashBlade.StandbyRenderType.set(tag, 2);

                ItemSlashBlade.IsSealed.set(tag, true);

                ItemSlashBladeNamed.TrueItemName.set(tag, nameAgito);

                NamedBladeManager.registerBladeSoul(tag , customblade.getDisplayName());
                SlashBlade.registerCustomItemStack(nameAgitoRust, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameAgitoRust);


                {
                    ItemStack reqiredBlade = customblade.copy();
                    NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                    ItemSlashBlade.KillCount.set(reqTag,100);
                    ItemSlashBlade.RepairCount.set(reqTag,1);

                    reqiredBlade.setStackDisplayName("agito rust");

                    SlashBlade.registerCustomItemStack(nameAgitoReqired, reqiredBlade);
                    ItemSlashBladeNamed.NamedBlades.add(nameAgitoReqired);

                    ItemStack destBlade = SlashBlade.findItemStack(SlashBlade.modid,ItemSlashBladeNamed.TrueItemName.get(tag),1);
                    SlashBlade.addRecipe(nameAgito,
                            new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,"agito"),
                                    destBlade,
                            reqiredBlade,
                            " X ",
                            "XBX",
                            " X ",
                            'X',itemProudSoul,
                            'B',reqiredBlade));
                }
            }
        }

        //------------- true
        {
            String nameOrotiagito = "flammpfeil.slashblade.named.orotiagito";
            String nameOrotiagitoSeald = nameOrotiagito + ".seald";
            String nameOrotiagitoReqired = nameOrotiagito + ".reqired";
            String nameOrotiagitoRust = nameOrotiagito + ".rust";
            String nameOrotiagitoSealdReqired = nameOrotiagitoSeald + ".reqired";


            {
                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                ItemSlashBladeNamed.CurrentItemName.set(tag, nameOrotiagito);

                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 60);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.DIAMOND.getAttackDamage());
                ItemSlashBlade.TextureName.set(tag, "named/orotiagito");
                ItemSlashBlade.ModelName.set(tag, "named/agito");
                ItemSlashBlade.SpecialAttackType.set(tag, 2);
                ItemSlashBlade.StandbyRenderType.set(tag, 2);

                SlashBlade.registerCustomItemStack(nameOrotiagito, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameOrotiagito);

                String brokableTest = nameOrotiagito + ".damaged";
                ItemStack brokable = customblade.copy();
                brokable.setItemDamage(brokable.getMaxDamage());
                SlashBlade.registerCustomItemStack(brokableTest, brokable);
                ItemSlashBladeNamed.NamedBlades.add(brokableTest);
            }

            {
                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                ItemSlashBladeNamed.CurrentItemName.set(tag, nameOrotiagitoSeald);

                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 60);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getAttackDamage());
                ItemSlashBlade.TextureName.set(tag,"named/agito_true");
                ItemSlashBlade.ModelName.set(tag,"named/agito");
                ItemSlashBlade.SpecialAttackType.set(tag, 2);
                ItemSlashBlade.StandbyRenderType.set(tag, 2);

                ItemSlashBladeNamed.TrueItemName.set(tag, nameOrotiagito);

                SlashBlade.registerCustomItemStack(nameOrotiagitoSeald, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameOrotiagitoSeald);


                {
                    ItemStack reqiredBlade = customblade.copy();
                    NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                    ItemSlashBlade.KillCount.set(reqTag, 1000);
                    ItemSlashBlade.ProudSoul.set(reqTag,1000);
                    ItemSlashBlade.RepairCount.set(reqTag, 10);

                    reqiredBlade.setStackDisplayName("orotiagito seald");

                    SlashBlade.registerCustomItemStack(nameOrotiagitoReqired, reqiredBlade);
                    ItemSlashBladeNamed.NamedBlades.add(nameOrotiagitoReqired);

                    ItemStack destBlade = SlashBlade.findItemStack(SlashBlade.modid,ItemSlashBladeNamed.TrueItemName.get(tag),1);
                    SlashBlade.addRecipe(nameOrotiagito,
                            new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,"agito_ex"),
                                    destBlade,
                            reqiredBlade,
                            "PXP",
                            "XBX",
                            "PXP",
                            'X',itemSphereBladeSoul,
                            'P',itemProudSoul,
                            'B',reqiredBlade));
                }
            }


            {
                ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                customblade.setTagCompound(tag);

                ItemSlashBladeNamed.CurrentItemName.set(tag, nameOrotiagitoRust);

                ItemSlashBladeNamed.CustomMaxDamage.set(tag, 60);
                ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.STONE.getAttackDamage());
                ItemSlashBlade.TextureName.set(tag, "named/agito_rust_true");
                ItemSlashBlade.ModelName.set(tag, "named/agito");
                ItemSlashBlade.SpecialAttackType.set(tag, 2);
                ItemSlashBlade.StandbyRenderType.set(tag, 2);

                ItemSlashBlade.IsSealed.set(tag, true);

                ItemSlashBladeNamed.TrueItemName.set(tag, nameOrotiagitoSeald);

                NamedBladeManager.registerBladeSoul(tag , customblade.getDisplayName());
                SlashBlade.registerCustomItemStack(nameOrotiagitoRust, customblade);
                ItemSlashBladeNamed.NamedBlades.add(nameOrotiagitoRust);

                {
                    ItemStack reqiredBlade = customblade.copy();
                    NBTTagCompound reqTag = ItemSlashBlade.getItemTagCompound(reqiredBlade);
                    ItemSlashBlade.KillCount.set(reqTag, 100);
                    ItemSlashBlade.RepairCount.set(reqTag, 1);

                    reqiredBlade.setStackDisplayName("agito rust");

                    SlashBlade.registerCustomItemStack(nameOrotiagitoSealdReqired, reqiredBlade);
                    ItemSlashBladeNamed.NamedBlades.add(nameOrotiagitoSealdReqired);

                    ItemStack destBlade = SlashBlade.findItemStack(SlashBlade.modid,ItemSlashBladeNamed.TrueItemName.get(tag),1);
                    SlashBlade.addRecipe(nameOrotiagitoSeald,
                            new RecipeAwakeBlade(new ResourceLocation(SlashBlade.modid,"agito2"),
                                    destBlade,
                            reqiredBlade,
                            " X ",
                            "XBX",
                            " X ",
                            'X',itemProudSoul,
                            'B',reqiredBlade));
                }
            }
        }
    }

    @SubscribeEvent
    public void postinit(LoadEvent.PostInitEvent event){
        DropEventHandler.registerEntityDrop(new ResourceLocation("twilightforest","hydra"), 0.3f, SlashBlade.findItemStack(SlashBlade.modid, "flammpfeil.slashblade.named.orotiagito.rust", 1));
        DropEventHandler.registerEntityDrop(new ResourceLocation("twilightforest","naga"),0.3f,SlashBlade.findItemStack(SlashBlade.modid,"flammpfeil.slashblade.named.agito.rust",1));

    }

}
