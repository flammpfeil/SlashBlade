package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.RecipeAwakeBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Furia on 14/11/08.
 */
public class Doutanuki {
    public static final String name = "flammpfeil.slashblade.named.sabigatana";

    public static float spawnRate = 0.05f;
    public static float isBrokenRate = 0.7f;
    public static float noSheathRate = 0.9f;
    public static float dropRate = 0.2f;

    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){
        ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
        NBTTagCompound tag = new NBTTagCompound();
        customblade.setTagCompound(tag);

        ItemSlashBladeNamed.CurrentItemName.set(tag, name);
        ItemSlashBladeNamed.CustomMaxDamage.set(tag, 50);
        ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getDamageVsEntity());
        ItemSlashBlade.TextureName.set(tag, "named/muramasa/sabigatana");
        ItemSlashBlade.ModelName.set(tag, "named/muramasa/muramasa");
        ItemSlashBlade.SpecialAttackType.set(tag, 1);
        ItemSlashBlade.StandbyRenderType.set(tag, 2);
        ItemSlashBlade.IsSealed.set(tag,true);
        //ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);

        GameRegistry.registerCustomItemStack(name, customblade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + name);
    }

    @SubscribeEvent
    public void postinit(LoadEvent.PostInitEvent event){

        MinecraftForge.EVENT_BUS.register(this);

        try{
            SlashBlade.mainConfiguration.load();
            {
                Property prop;
                prop = SlashBlade.mainConfiguration.get("RustBlade","SpawnRate",(double)spawnRate);
                spawnRate = (float)prop.getDouble();
                spawnRate = Math.min(1.0f,Math.max(0.0f,spawnRate));
                prop.set(spawnRate);
            }
            {
                Property prop;
                prop = SlashBlade.mainConfiguration.get("RustBlade","IsBrokenRate",(double)isBrokenRate);
                isBrokenRate = (float)prop.getDouble();
                isBrokenRate = Math.min(1.0f,Math.max(0.0f,isBrokenRate));
                prop.set(isBrokenRate);
            }
            {
                Property prop;
                prop = SlashBlade.mainConfiguration.get("RustBlade","NoSheathRate",(double)noSheathRate,"0.0<rate<1");
                noSheathRate = (float)prop.getDouble();
                noSheathRate = Math.min(1.0f,Math.max(0.0f,noSheathRate));
                prop.set(noSheathRate);
            }
            {
                Property prop;
                prop = SlashBlade.mainConfiguration.get("RustBlade","DropRate",(double)dropRate,"0:nodrop , 0<droprate<1 , 2:forceDrop");
                dropRate = (float)prop.getDouble();
                dropRate = Math.max(0.0f,dropRate);
                prop.set(dropRate);
            }
        }
        finally
        {
            SlashBlade.mainConfiguration.save();
        }
    }

    @SubscribeEvent
    public void specialSpawn(LivingSpawnEvent.SpecialSpawn event){
        if(event.entityLiving instanceof EntityZombie){
            if (spawnRate > event.entityLiving.getRNG().nextFloat())
            {

                ItemStack blade = SlashBlade.getCustomBlade(SlashBlade.modid , name);

                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);


                if (noSheathRate > event.entityLiving.getRNG().nextFloat())
                    ItemSlashBlade.IsNoScabbard.set(tag,true);

                if(isBrokenRate > event.entityLiving.getRNG().nextFloat())
                    ItemSlashBlade.IsBroken.set(tag,true);



                ItemSlashBlade.KillCount.set(tag,event.entityLiving.getRNG().nextInt(200));

                if (0.1 > event.entityLiving.getRNG().nextFloat())
                    ItemSlashBlade.KillCount.add(tag,1000);


                event.entityLiving.setCurrentItemOrArmor(0, blade);
                ((EntityZombie)event.entityLiving).setEquipmentDropChance(0,dropRate);
            }
        }

    }
}
