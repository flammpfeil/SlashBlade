package mods.flammpfeil.slashblade;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Furia on 14/05/07.
 */
public class ItemSlashBladeNamed extends ItemSlashBlade {
    public ItemSlashBladeNamed(ToolMaterial par2EnumToolMaterial, float baseAttackModifiers) {
        super(par2EnumToolMaterial, baseAttackModifiers);
    }


    static public final String TrueItemNameStr = "TrueItemName";
    static public final String CurrentItemNameStr = "CurrentItemName";
    static public final String CustomMaxDamageStr = "CustomMaxDamage";
    static public final String RepairOreDicMaterialStr = "RepairOreDicMaterial";
    static public final String RepairMaterialNameStr = "RepairMaterialName";

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        String result = super.getUnlocalizedName(par1ItemStack);
        if(par1ItemStack.hasTagCompound()){
            NBTTagCompound tag = par1ItemStack.getTagCompound();
            if(tag.hasKey(CurrentItemNameStr)){
                result = "item." + tag.getString(CurrentItemNameStr);
            }
        }
        return result;
    }


    @Override
    public int getMaxDamage(ItemStack stack) {
        NBTTagCompound tag = this.getItemTagCompound(stack);
        if(tag.hasKey(CustomMaxDamageStr))
            return tag.getInteger(CustomMaxDamageStr);
        else
            return super.getMaxDamage(stack);
    }

    public static List<String> BladeNames =Lists.newArrayList();

    public static ItemStack getCustomBlade(String key){
        ItemStack blade = GameRegistry.findItemStack(SlashBlade.modid, key, 1);
        if(blade != null && key.endsWith(".youtou")){
            blade.setStackDisplayName(blade.getDisplayName());
        }
        return blade;
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs,
                            List par3List) {

        if(this == SlashBlade.bladeNamed){
            for(String bladename : BladeNames){
                ItemStack blade = getCustomBlade(bladename);
                if(blade != null) par3List.add(blade);
            }
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
                result = curItem.isItemEqual(par2ItemStack);
                if(result)
                    break;
            }
        }

        if(!result && tag.hasKey(RepairMaterialNameStr))
        {
            String matName = tag.getString(RepairMaterialNameStr);
            Item material = (Item)Item.itemRegistry.getObject(matName);
            if(material != null)
                result = par2ItemStack.getItem() == material;
        }

        return result;

        //return this.toolMaterial.getToolCraftingMaterial() == par2ItemStack.itemID ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }
}
