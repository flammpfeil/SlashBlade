package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.DropEventHandler;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 14/07/07.
 */
public class Fox {
    String name = "flammpfeil.slashblade.named.fox";
    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){

        {
            String name = this.name + ".white";

            ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, 70);
            ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getDamageVsEntity());
            ItemSlashBlade.TextureName.set(tag, "named/sange/white");
            ItemSlashBlade.ModelName.set(tag, "named/sange/sange");
            ItemSlashBlade.SpecialAttackType.set(tag, 4); //4:シュンカ一段
            ItemSlashBlade.StandbyRenderType.set(tag, 2);
            ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + name);
        }

        {
            String name = this.name + ".black";
            ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
            NBTTagCompound tag = new NBTTagCompound();
            customblade.setTagCompound(tag);

            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, 70);
            ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getDamageVsEntity());
            ItemSlashBlade.TextureName.set(tag, "named/sange/black");
            ItemSlashBlade.ModelName.set(tag, "named/sange/sange");
            ItemSlashBlade.SpecialAttackType.set(tag, 4); //4:シュンカ一段
            ItemSlashBlade.StandbyRenderType.set(tag, 2);
            ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

            GameRegistry.registerCustomItemStack(name, customblade);
            ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + name);
        }
    }
}
