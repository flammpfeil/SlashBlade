package mods.flammpfeil.slashblade.named;

import net.minecraft.init.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Furia on 15/06/22.
 */
public class Koseki {
    String name = "flammpfeil.slashblade.named.koseki";
    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){
        ItemStack customblade = new ItemStack(SlashBlade.bladeNamed,1,0);
        NBTTagCompound tag = new NBTTagCompound();
        customblade.setTagCompound(tag);

        ItemSlashBladeNamed.CurrentItemName.set(tag, name);
        ItemSlashBladeNamed.CustomMaxDamage.set(tag, 70);
        ItemSlashBlade.setBaseAttackModifier(tag, 4 + Item.ToolMaterial.IRON.getAttackDamage());
        ItemSlashBlade.TextureName.set(tag, "named/dios/koseki");
        ItemSlashBlade.ModelName.set(tag, "named/dios/dios");
        ItemSlashBlade.SpecialAttackType.set(tag, 6);
        ItemSlashBlade.StandbyRenderType.set(tag, 3);
        ItemSlashBladeNamed.IsDefaultBewitched.set(tag,true);
        ItemSlashBlade.SummonedSwordColor.set(tag,-0x1c1c1c);

        SpecialEffects.addEffect(customblade,SpecialEffects.WitherEdge);

        customblade.addEnchantment(Enchantments.THORNS,1);
        customblade.addEnchantment(Enchantments.POWER,2);

        SlashBlade.registerCustomItemStack(name, customblade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + name);
    }

    @SubscribeEvent
    public void postinit(LoadEvent.PostInitEvent event){
       MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBladeStandAttack(SlashBladeEvent.BladeStandAttack event){
        if(!event.entityBladeStand.hasBlade()) return;
        if(EntityBladeStand.getType(event.entityBladeStand) != EntityBladeStand.StandType.Single) return;
        if(!(event.damageSource.getTrueSource() instanceof EntityWither)) return;
        if(!event.damageSource.isExplosion()) return;
        if(!event.damageSource.getDamageType().equals("explosion.player")) return;

        ItemStack targetBlade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeNamed",1);

        if(!event.blade.getUnlocalizedName().equals(targetBlade.getUnlocalizedName())) return;

        ItemStack resultBlade = SlashBlade.getCustomBlade(name);

        event.entityBladeStand.setBlade(resultBlade);
    }

    /* todo:advancement
    @SubscribeEvent
    public void onSlashBladeAchievementCreate(SlashBladeAchievementCreateEvent event){
        {
            ItemStack blade = SlashBlade.getCustomBlade(name);

            Achievement startParent = net.minecraft.stats.AchievementList.SPAWN_WITHER;
            if(!blade.isEmpty()){
                ItemStack noname = SlashBlade.findItemStack(SlashBlade.modid, "slashbladeNamed", 1);

                NBTTagCompound displayTag = new NBTTagCompound();
                noname.setTagInfo("display",displayTag);
                NBTTagList loreList = new NBTTagList();
                loreList.appendTag(new NBTTagString("Required"));
                loreList.appendTag(new NBTTagString("ingot bladestand"));
                loreList.appendTag(new NBTTagString("wither summoning explosion"));
                displayTag.setTag("Lore", loreList);

                IRecipe dummyRecipe = new ShapedOreRecipe(blade,
                        "   ",
                        " X ",
                        "   ",
                        'X',noname);

                SlashBlade.addRecipe("koseki",dummyRecipe,true);

                Achievement achievement = AchievementList.registerCraftingAchievement("koseki", blade, startParent);
                AchievementList.setContent(achievement, "koseki");
            }
        }
    }
    */
}
