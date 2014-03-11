package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLivingBase;
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
import java.util.Map;

/**
 * Created by Furia on 14/03/11.
 */
public class ItemSlashBladeWrapper extends ItemSlashBlade {

    public ItemSlashBladeWrapper(ToolMaterial par2EnumToolMaterial){
        super(par2EnumToolMaterial, 4.0f);
    }

    private ResourceLocation texture = new ResourceLocation("flammpfeil.slashblade","model/scabbard.png");
    @Override
    public ResourceLocation getModelTexture(){
        return texture;
    }

    static final String WrapItemStr = "WrapItem";
    static final String BaseAttackModifiersStr = "baseAttackModifiers";

    public void setWrapItem(ItemStack base,ItemStack wrapTarget){
        NBTTagCompound tag = getItemTagCompound(base);

        NBTTagCompound wrapTag = new NBTTagCompound();
        wrapTarget.writeToNBT(wrapTag);

        tag.setTag(WrapItemStr,wrapTag);
    }

    public void removeWrapItem(ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);
        if(tag.hasKey(WrapItemStr))     tag.removeTag(WrapItemStr);
        if(tag.hasKey(TextureNameStr))  tag.removeTag(TextureNameStr);
        if(tag.hasKey("ench"))          tag.removeTag("ench");
        if(tag.hasKey("display"))       tag.removeTag("display");
        if(tag.hasKey(isBrokenStr))     tag.removeTag(isBrokenStr);
        if(tag.hasKey(BaseAttackModifiersStr)) tag.removeTag(BaseAttackModifiersStr);
        stack.setItemDamage(0);
    }

    @Override
    public void updateAttackAmplifier(EnumSet<SwordType> swordType,NBTTagCompound tag,EntityPlayer el,ItemStack sitem){
        float tagAttackAmplifier = tag.getFloat(attackAmplifierStr);

        float attackAmplifier = 0;

        if(swordType.contains(SwordType.Broken) || !hasWrapedItem(sitem)){
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
    public ComboSequence getNextComboSeq(ItemStack itemStack, ComboSequence current, boolean isRightClick, EntityPlayer player) {

        if(hasWrapedItem(itemStack)){
            return super.getNextComboSeq(itemStack,current,isRightClick,player);
        }else{
            return ComboSequence.None;
        }
    }

    public boolean hasWrapedItem(ItemStack par1ItemStack){
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        return tag.hasKey(WrapItemStr);
    }

    public ItemStack getWrapedItem(ItemStack par1ItemStack){
        ItemStack wrapItem = null;
        if(hasWrapedItem(par1ItemStack)){
            try{
                NBTTagCompound tag = getItemTagCompound(par1ItemStack);
                wrapItem = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(WrapItemStr));
                setWrapItem(par1ItemStack,wrapItem);
            }catch(Throwable e){
                removeWrapItem(par1ItemStack);
                wrapItem = null;
            }
        }
        return wrapItem;
    }


    @Override
    public EnumSet<SwordType> getSwordType(ItemStack itemStack) {
        if(hasWrapedItem(itemStack)){
            return super.getSwordType(itemStack);
        }else{
            EnumSet<SwordType> set =  EnumSet.noneOf(SwordType.class);
            set.add(SwordType.Perfect);
            return set;
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        if(hasWrapedItem(stack)){
            return getWrapedItem(stack).getMaxDamage();
        }else{
            return this.getMaxDamage();
        }
    }

    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
        ComboSequence comboSec = getComboSequence(getItemTagCompound(par1ItemStack));
        switch (comboSec) {
            case Saya1:
            case Saya2:
                break;

            default:
                if(hasWrapedItem(par1ItemStack)){
                    try{
                        ItemStack wrapedItem = getWrapedItem(par1ItemStack);
                        wrapedItem.getItem().hitEntity(wrapedItem,par2EntityLivingBase, par3EntityLivingBase);
                    }catch(Throwable e){
                        removeWrapItem(par1ItemStack);
                    }
                }
                break;
        }

        return super.hitEntity(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase);
    }

    @Override
    protected void damageItem(int damage, ItemStack par1ItemStack, EntityLivingBase par3EntityLivingBase){
        if(hasWrapedItem(par1ItemStack)){
            super.damageItem(damage,par1ItemStack,par3EntityLivingBase);
        }
    }
}
