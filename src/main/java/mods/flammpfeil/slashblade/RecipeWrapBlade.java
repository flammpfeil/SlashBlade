package mods.flammpfeil.slashblade;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.NamedBladeManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Enchantments;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
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
        super(SlashBlade.modid+":wrap",3, 3, NonNullList.<Ingredient>from(Ingredient.EMPTY,
                Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.fromStacks(SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.ProudSoulStr,1)),
                Ingredient.EMPTY, Ingredient.fromStacks(new ItemStack(SlashBlade.wrapBlade, 1, 0)), Ingredient.EMPTY ,
                Ingredient.fromStacks(new ItemStack(Items.WOODEN_SWORD)), Ingredient.EMPTY, Ingredient.EMPTY)
                , new ItemStack(SlashBlade.wrapBlade, 1, 0));

        proudSoul = new ItemStack(SlashBlade.proudSoul,1,0);

        //RegisterWrapable("BambooMod:katana", "BambooKatana", 4.0f);

        RegisterWrapable("reforged:wooden_katana", "BalkonWood", 2.0f); 
        RegisterWrapable("reforged:stone_katana", "BalkonStone", 4.0f); 
        RegisterWrapable("reforged:iron_katana", "BalkonIron", 6.0f); 
        RegisterWrapable("reforged:diamond_katana", "BalkonDiamond", 8.0f); 
        RegisterWrapable("reforged:golden_katana",  "BalkonGold", 2.0f); 

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

        ItemStack innerBlade = SlashBlade.findItemStack("minecraft", "wooden_sword", 1);

        ItemStack reqiredBlade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
        {
            SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

            reqiredBlade.addEnchantment(Enchantments.LOOTING,1);
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
        SlashBlade.registerCustomItemStack(reqiredStr,reqiredBlade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

        return reqiredBlade;
    }


    @Override
    public boolean matches(InventoryCrafting cInv, World par2World)
    {
        {
            ItemStack ps = cInv.getStackInRowAndColumn(2, 0);
            boolean hasProudSuol = (!ps.isEmpty() && ps.isItemEqual(proudSoul));
            ItemStack sc = cInv.getStackInRowAndColumn(1, 1);
            boolean hasScabbard = (!sc.isEmpty() && sc.getItem() == SlashBlade.wrapBlade && !ItemSlashBladeWrapper.hasWrapedItem(sc));

            boolean hasTarget = false;

            ItemStack target = cInv.getStackInRowAndColumn(0, 2);
            if(!target.isEmpty()){
                ResourceLocation targetName = target.getItem().getRegistryName();
                hasTarget = wrapableTextureNames.containsKey(targetName.toString());
            }

            return hasProudSuol && hasScabbard && hasTarget;
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting cInv)
    {
        ItemStack scabbard = cInv.getStackInRowAndColumn(1, 1);
        if(scabbard.isEmpty()) return ItemStack.EMPTY;
        scabbard = scabbard.copy();

        ItemStack target = cInv.getStackInRowAndColumn(0, 2);
        if(target.isEmpty()) return ItemStack.EMPTY;
        target = target.copy();


        ResourceLocation targetName = target.getItem().getRegistryName();


        SlashBlade.wrapBlade.removeWrapItem(scabbard);

        SlashBlade.wrapBlade.setWrapItem(scabbard,target);

        NBTTagCompound tag = scabbard.getTagCompound();
        ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap." + targetName.toString().replace(':','.'));
        ItemSlashBladeNamed.TextureName.set(tag,wrapableTextureNames.get(targetName.toString()));
        ItemSlashBladeNamed.BaseAttackModifier.set(tag,wrapableBaseAttackModifiers.get(targetName.toString()));

        if(target.hasDisplayName()){
            scabbard.setStackDisplayName(I18n.format("item.flammpfeil.slashblade.wrapformat", target.getDisplayName()));
        }else if(target.isItemEnchanted()){
            scabbard.setStackDisplayName(scabbard.getDisplayName());
        }else{
            scabbard.setStackDisplayName(I18n.format("item.flammpfeil.slashblade.wrapformat.low",target.getDisplayName()));
        }

        if(target.isItemEnchanted()){
            tag.setTag("ench",target.getTagCompound().getTag("ench"));
        }

        return scabbard;
    }
}
