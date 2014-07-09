package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.DropEventHandler;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * Created by Furia on 14/07/07.
 */
public class PSSange {
    String name = "flammpfeil.slashblade.named.sange";
    @ForgeSubscribe
    public void init(LoadEvent.InitEvent event){
        ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
        NBTTagCompound tag = new NBTTagCompound();
        customblade.setTagCompound(tag);

        ItemSlashBladeNamed.CurrentItemName.set(tag, name);
        ItemSlashBladeNamed.CustomMaxDamage.set(tag, 70);
        ItemSlashBlade.setBaseAttackModifier(tag, 4 + EnumToolMaterial.IRON.getDamageVsEntity());
        ItemSlashBlade.TextureName.set(tag, "named/sange/sange");
        ItemSlashBlade.ModelName.set(tag, "named/sange/sange");
        ItemSlashBlade.SpecialAttackType.set(tag, 4); //4:シュンカ一段
        ItemSlashBlade.StandbyRenderType.set(tag, 2);
        ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

        GameRegistry.registerCustomItemStack(name, customblade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + name);
    }

    @ForgeSubscribe
    public void postinit(LoadEvent.PostInitEvent event){
        DropEventHandler.registerEntityDrop("WitherBoss", 0.7f, GameRegistry.findItemStack(SlashBlade.modid, name, 1));
    }
}
