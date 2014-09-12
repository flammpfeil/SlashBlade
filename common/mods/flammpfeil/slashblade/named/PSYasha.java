package mods.flammpfeil.slashblade.named;

import net.minecraftforge.event.ForgeSubscribe;
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

/**
 * Created by Furia on 14/07/07.
 */
public class PSYasha {
    String name = "flammpfeil.slashblade.named.yasha";
    @ForgeSubscribe
    public void init(LoadEvent.InitEvent event){
        ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
        NBTTagCompound tag = new NBTTagCompound();
        customblade.setTagCompound(tag);

        ItemSlashBladeNamed.CurrentItemName.set(tag, name);
        ItemSlashBladeNamed.CustomMaxDamage.set(tag, 70);
        ItemSlashBlade.setBaseAttackModifier(tag, 4 + EnumToolMaterial.IRON.getDamageVsEntity());
        ItemSlashBlade.TextureName.set(tag, "named/yasha/yasha");
        ItemSlashBlade.ModelName.set(tag, "named/yasha/yasha");
        ItemSlashBlade.SpecialAttackType.set(tag, 4);
        ItemSlashBlade.StandbyRenderType.set(tag, 2);
        ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

        GameRegistry.registerCustomItemStack(name, customblade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + name);
    }

    @ForgeSubscribe
    public void postinit(LoadEvent.PostInitEvent event){
        ItemStack stack = GameRegistry.findItemStack(SlashBlade.modid, name, 1);
        DropEventHandler.registerEntityDrop("TwilightForest.Minotaur"  , 0.05f, stack);
        DropEventHandler.registerEntityDrop("TwilightForest.Minoshroom", 0.2f , stack);
    }
}
