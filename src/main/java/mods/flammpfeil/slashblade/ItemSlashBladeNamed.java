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


    static final String BaseAttackModifiersStr = "baseAttackModifiers";
    static final String TrueItemNameStr = "TrueItemName";
    static final String CurrentItemNameStr = "CurrentItemName";
    static final String CustomMaxDamageStr = "CustomMaxDamage";

    @Override
    public void updateAttackAmplifier(EnumSet<SwordType> swordType,NBTTagCompound tag,EntityPlayer el,ItemStack sitem){
        float tagAttackAmplifier = tag.getFloat(attackAmplifierStr);

        float attackAmplifier = 0;

        if(swordType.contains(SwordType.Broken)){
            attackAmplifier = -4;
        }else if(swordType.contains(SwordType.FiercerEdge)){
            float tmp = el.experienceLevel;
            tmp = 1.0f + (float)( tmp < 15.0f ? tmp * 0.5f : tmp < 30.0f ? 3.0f +tmp*0.45f : 7.0f+0.4f * tmp);
            attackAmplifier = tmp;
        }

        if(tagAttackAmplifier != attackAmplifier)
        {
            tag.setFloat(attackAmplifierStr, attackAmplifier);

            NBTTagList attrTag = null;

            attrTag = new NBTTagList();
            tag.setTag("AttributeModifiers",attrTag);

            float baseModif = this.baseAttackModifiers;
            if(tag.hasKey(BaseAttackModifiersStr)){
                baseModif = tag.getFloat(BaseAttackModifiersStr);
            }
            attrTag.appendTag(
                    getAttrTag(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),new AttributeModifier(field_111210_e, "Weapon modifier", (double)(attackAmplifier + baseModif), 0))
            );

            el.getAttributeMap().removeAttributeModifiers(sitem.getAttributeModifiers());
            el.getAttributeMap().applyAttributeModifiers(sitem.getAttributeModifiers());
        }
    }

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

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs,
                            List par3List) {

        if(this == SlashBlade.bladeNamed){
            for(String bladename : BladeNames){
                ItemStack blade = GameRegistry.findItemStack(SlashBlade.modid, bladename, 1);
                par3List.add(blade);
            }
        }
    }
}
