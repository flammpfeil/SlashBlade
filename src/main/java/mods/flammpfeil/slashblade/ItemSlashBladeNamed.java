package mods.flammpfeil.slashblade;

import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

/**
 * Created by Furia on 14/05/07.
 */
public class ItemSlashBladeNamed extends ItemSlashBlade {
    public ItemSlashBladeNamed(ToolMaterial par2EnumToolMaterial, float baseAttackModifiers) {
        super(par2EnumToolMaterial, baseAttackModifiers);
    }

    static public TagPropertyAccessor.TagPropertyBoolean IsDefaultBewitched = new TagPropertyAccessor.TagPropertyBoolean("isDefaultBewitched");
    static public TagPropertyAccessor.TagPropertyString TrueItemName = new TagPropertyAccessor.TagPropertyString("TrueItemName");
    static public TagPropertyAccessor.TagPropertyString CurrentItemName = new TagPropertyAccessor.TagPropertyString("CurrentItemName");
    static public TagPropertyAccessor.TagPropertyInteger CustomMaxDamage = new TagPropertyAccessor.TagPropertyInteger("CustomMaxDamage");
    static public final String RepairOreDicMaterialStr = "RepairOreDicMaterial";
    static public final String RepairMaterialNameStr = "RepairMaterialName";

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        String result = super.getUnlocalizedName(par1ItemStack);
        if(par1ItemStack.hasTagCompound()){
            NBTTagCompound tag = par1ItemStack.getTagCompound();
            if(CurrentItemName.exists(tag)){
                result = "item." + CurrentItemName.get(tag);
            }
        }
        return result;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        return CustomMaxDamage.get(tag,super.getMaxDamage(stack));
    }

    public static List<String> NamedBlades = Lists.newArrayList();

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (!this.isInCreativeTab(tab)) return;
        //super.getSubItems(itemIn, tab, subItems);

        ItemStack targetBlade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeNamed",1);
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(targetBlade);
        ItemSlashBlade.ProudSoul.set(tag,1000);
        subItems.add(targetBlade);

        for(String bladename : NamedBlades){
            ItemStack blade = SlashBlade.getCustomBlade(bladename);
            if(blade.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                blade.setItemDamage(0);
            if(!blade.isEmpty()) subItems.add(blade);
        }
    }


    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        Boolean result = super.getIsRepairable(par1ItemStack,par2ItemStack);

        NBTTagCompound tag = getItemTagCompound(par1ItemStack);

        if(!result && tag.hasKey(RepairOreDicMaterialStr))
        {
            String oreName = tag.getString(RepairOreDicMaterialStr);
            List<ItemStack> list = OreDictionary.getOres(oreName);
            for(ItemStack curItem : list){
                if(curItem.getItemDamage() == OreDictionary.WILDCARD_VALUE){
                    result = curItem.getItem() == par2ItemStack.getItem();
                }else{
                    result = curItem.isItemEqual(par2ItemStack);
                }
                if(result)
                    break;
            }
        }

        if(!result && tag.hasKey(RepairMaterialNameStr)){
            String matName = tag.getString(RepairMaterialNameStr);
            Item material = (Item)Item.REGISTRY.getObject(new ResourceLocationRaw(matName));
            if(material != null)
                result = par2ItemStack.getItem() == material;
        }

        return result;

        //return this.toolMaterial.getToolCraftingMaterial() == par2ItemStack.itemID ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }
}
