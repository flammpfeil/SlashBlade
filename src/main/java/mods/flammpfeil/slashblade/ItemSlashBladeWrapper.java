package mods.flammpfeil.slashblade;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraftforge.oredict.OreDictionary;
import org.omg.CORBA.Current;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 14/03/11.
 */
public class ItemSlashBladeWrapper extends ItemSlashBladeNamed {

    public ItemSlashBladeWrapper(ToolMaterial par2EnumToolMaterial){
        super(par2EnumToolMaterial, 4.0f);
    }

    private ResourceLocationRaw texture = new ResourceLocationRaw("flammpfeil.slashblade","model/scabbard.png");
    @Override
    public ResourceLocationRaw getModelTexture(){
        return texture;
    }

    static final String WrapItemStr = "WrapItem";

    public void setWrapItem(ItemStack base,ItemStack wrapTarget){
        NBTTagCompound tag = getItemTagCompound(base);

        NBTTagCompound wrapTag = new NBTTagCompound();
        wrapTarget.writeToNBT(wrapTag);

        tag.setTag(WrapItemStr,wrapTag);
    }

    public void removeWrapItem(ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);
        if(tag.hasKey(WrapItemStr))     tag.removeTag(WrapItemStr);
        TextureName.remove(tag);
        ModelName.remove(tag);
        if(tag.hasKey("display"))       tag.removeTag("display");
        IsBroken.remove(tag);
        BaseAttackModifier.remove(tag);
        CurrentItemName.remove(tag);
        TrueItemName.remove(tag);
        stack.setItemDamage(0);
    }

    @Override
    public ComboSequence getNextComboSeq(ItemStack itemStack, ComboSequence current, boolean isRightClick, EntityPlayer player) {

        if(hasWrapedItem(itemStack)){
            return super.getNextComboSeq(itemStack,current,isRightClick,player);
        }else{
            return ComboSequence.None;
        }
    }

    public static boolean hasWrapedItem(ItemStack par1ItemStack){
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        return tag.hasKey(WrapItemStr);
    }

    public ItemStack getWrapedItem(ItemStack par1ItemStack){
        ItemStack wrapItem = ItemStack.EMPTY;
        if(hasWrapedItem(par1ItemStack)){
            try{
                NBTTagCompound tag = getItemTagCompound(par1ItemStack);
                wrapItem = new ItemStack(tag.getCompoundTag(WrapItemStr));
                if(wrapItem.isEmpty()) {
                    removeWrapItem(par1ItemStack);
                    wrapItem = ItemStack.EMPTY;
                }

            }catch(Throwable e){
                removeWrapItem(par1ItemStack);
                wrapItem = ItemStack.EMPTY;
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
            set.add(SwordType.Sealed);
            set.remove(SwordType.FiercerEdge);
            return set;
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        if(hasWrapedItem(stack)){
            ItemStack wrapItem = getWrapedItem(stack);
            if(!wrapItem.isEmpty())
                return wrapItem.getMaxDamage();
        }
        return this.getMaxDamage();
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
                        int hurtRT = par2EntityLivingBase.hurtResistantTime;

                        if(par3EntityLivingBase instanceof EntityPlayer){
                            par2EntityLivingBase.hurtResistantTime = par2EntityLivingBase.maxHurtResistantTime;
                            wrapedItem.getItem().onLeftClickEntity(wrapedItem,(EntityPlayer)par3EntityLivingBase,par2EntityLivingBase);
                        }

                        par2EntityLivingBase.hurtResistantTime = 0;
                        wrapedItem.getItem().hitEntity(wrapedItem,par2EntityLivingBase, par3EntityLivingBase);

                        par2EntityLivingBase.hurtResistantTime = hurtRT;
                    }catch(Throwable e){
                        removeWrapItem(par1ItemStack);
                    }
                }
                break;
        }

        return super.hitEntity(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        NBTTagCompound tag = getItemTagCompound(stack);
        if(hasWrapedItem(stack))
            super.setDamage(stack,damage);
        else
            super.setDamage(stack,0);
        if(!TrueItemName.exists(tag))
            IsBroken.set(tag,false);
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        boolean result = super.getIsRepairable(par1ItemStack,par2ItemStack);

        if(!result){
            ItemStack wrapedItem = getWrapedItem(par1ItemStack);
            if(!wrapedItem.isEmpty())
                result = wrapedItem.getItem().getIsRepairable(wrapedItem,par2ItemStack);
        }

        return result;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (!this.isInCreativeTab(tab)) return;
        subItems.add(new ItemStack(this, 1, 0));
    }
}
