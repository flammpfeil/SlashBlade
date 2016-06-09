package mods.flammpfeil.slashblade;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.named.NamedBladeManager;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Created by Furia on 14/03/11.
 */
public class RecipeWrapBlade extends ShapedRecipes {
    ItemStack proudSoul;
    public static Map<String,String> wrapableTextureNames = Maps.newHashMap();
    private static Map<String,Float> wrapableBaseAttackModifiers = Maps.newHashMap();
    public RecipeWrapBlade()
    {
        super(3, 3, new ItemStack[] {
                null, null, GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.ProudSoulStr,1),
                null, new ItemStack(SlashBlade.wrapBlade, 1, 0), null ,
                new ItemStack(Items.wooden_sword), null, null }
                , new ItemStack(SlashBlade.wrapBlade, 1, 0));

        proudSoul = new ItemStack(SlashBlade.proudSoul,1,0);

        //RegisterWrapable("BambooMod:katana", "BambooKatana", 4.0f);

        RegisterWrapable("weaponmod:katana.wood",  "BalkonWood", 2.0f);
        RegisterWrapable("weaponmod:katana.stone", "BalkonStone", 4.0f);
        RegisterWrapable("weaponmod:katana.iron",  "BalkonIron", 6.0f);
        RegisterWrapable("weaponmod:katana.diamond", "BalkonDiamond", 8.0f);
        RegisterWrapable("weaponmod:katana.gold",  "BalkonGold", 2.0f);

        //RegisterWrapable("Minecraft:wooden_sword", "BambooKatana", 4.0f);
    }

    static public void RegisterWrapable(String name,String texture,float attackModifier){
        wrapableTextureNames.put(name, texture);
        wrapableBaseAttackModifiers.put(name, attackModifier);

        ItemStack blade = getWrapSampleBlade(name, texture);
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);
        ItemSlashBladeNamed.BaseAttackModifier.set(tag,attackModifier);
        tag.removeTag("display");

        NamedBladeManager.registerBladeSoul(tag , blade.getDisplayName());
    }

    static public ItemStack getWrapSampleBlade(String name,String texture){

        ItemStack innerBlade = GameRegistry.findItemStack("minecraft", "wooden_sword", 1);

        ItemStack reqiredBlade = GameRegistry.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
        {
            SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

            reqiredBlade.addEnchantment(Enchantment.looting,1);
            NBTTagCompound tag = reqiredBlade.getTagCompound();
            ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap." + name.replace(':', '.'));
            ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);
            ItemSlashBlade.TextureName.set(tag,texture);

            NBTTagCompound displayTag = new NBTTagCompound();
            reqiredBlade.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("is demo item. is wooden sword"));
            loreList.appendTag(new NBTTagString("true performance : please crafting"));
            displayTag.setTag("Lore", loreList);

            reqiredBlade.setStackDisplayName(reqiredBlade.getDisplayName());
        }
        String reqiredStr = "wrap." + name.replace(':', '.') + ".sample";
        GameRegistry.registerCustomItemStack(reqiredStr,reqiredBlade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

        return reqiredBlade;
    }


    @Override
    public boolean matches(InventoryCrafting cInv, World par2World)
    {
        {
            ItemStack ps = cInv.getStackInRowAndColumn(2, 0);
            boolean hasProudSuol = (ps != null && ps.isItemEqual(proudSoul));
            ItemStack sc = cInv.getStackInRowAndColumn(1, 1);
            boolean hasScabbard = (sc != null && sc.getItem() == SlashBlade.wrapBlade && !SlashBlade.wrapBlade.hasWrapedItem(sc));

            boolean hasTarget = false;

            ItemStack target = cInv.getStackInRowAndColumn(0, 2);
            if(target != null){
                String targetName = Item.itemRegistry.getNameForObject(target.getItem());

                hasTarget = wrapableTextureNames.containsKey(targetName);
            }

            return hasProudSuol && hasScabbard && hasTarget;
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting cInv)
    {
        ItemStack scabbard = cInv.getStackInRowAndColumn(1, 1);
        if(scabbard == null) return null;
        scabbard = scabbard.copy();

        ItemStack target = cInv.getStackInRowAndColumn(0, 2);
        if(target == null) return null;
        target = target.copy();


        String targetName = Item.itemRegistry.getNameForObject(target.getItem());


        SlashBlade.wrapBlade.removeWrapItem(scabbard);

        SlashBlade.wrapBlade.setWrapItem(scabbard,target);

        NBTTagCompound tag = scabbard.getTagCompound();
        ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap." + targetName.replace(':','.'));
        ItemSlashBladeNamed.TextureName.set(tag,wrapableTextureNames.get(targetName));
        ItemSlashBladeNamed.BaseAttackModifier.set(tag,wrapableBaseAttackModifiers.get(targetName));

        if(target.hasDisplayName()){
            scabbard.setStackDisplayName(String.format(StatCollector.translateToLocal("item.flammpfeil.slashblade.wrapformat").trim(), target.getDisplayName()));
        }else if(target.isItemEnchanted()){
            scabbard.setStackDisplayName(scabbard.getDisplayName());
        }else{
            scabbard.setStackDisplayName(String.format(StatCollector.translateToLocal("item.flammpfeil.slashblade.wrapformat.low").trim(),target.getDisplayName()));
        }

        if(target.isItemEnchanted()){
            tag.setTag("ench",target.getTagCompound().getTag("ench"));
        }

        return scabbard;
    }
}
