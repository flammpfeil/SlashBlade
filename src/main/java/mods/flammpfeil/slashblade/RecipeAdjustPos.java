package mods.flammpfeil.slashblade;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeAdjustPos extends ShapedRecipes
{

    static private ItemStack dirIS(String name){
        return new ItemStack(Items.stick, 0, 0).setStackDisplayName(name);
    }

    public RecipeAdjustPos()
    {
        super(3, 3, new ItemStack[] {
        		null, dirIS("Up"), dirIS("Front"),
                dirIS("Left"), new ItemStack(SlashBlade.weapon, 1, 0), dirIS("Right"),
                dirIS("Back"), dirIS("Down"), null}
        , new ItemStack(SlashBlade.weapon, 1, 0));
    }

    public boolean isFactor(ItemStack itemStack){
        return itemStack != null && itemStack.getItem() == Items.stick;
    }

    @Override
    public boolean matches(InventoryCrafting cInv, World par2World)
    {
        {
            ItemStack itemstack = null;

            int x = 0;
            int y = 0;
            int z = 0;

            if(cInv.getStackInRowAndColumn(0, 0) != null)
                return false;
            if(cInv.getStackInRowAndColumn(2, 2) != null)
                return false;

            ItemStack tmp;
            if(isFactor(tmp = cInv.getStackInRowAndColumn(0, 1)))
                y += tmp.stackSize;
            if(isFactor(tmp = cInv.getStackInRowAndColumn(0, 2)))
                z += tmp.stackSize;
            if(isFactor(tmp = cInv.getStackInRowAndColumn(1, 0)))
                x += tmp.stackSize;

            if(isFactor(tmp = cInv.getStackInRowAndColumn(1, 2)))
                x -= tmp.stackSize;
            if(isFactor(tmp = cInv.getStackInRowAndColumn(2, 0)))
                z -= tmp.stackSize;
            if(isFactor(tmp = cInv.getStackInRowAndColumn(2, 1)))
                y -= tmp.stackSize;


            ItemStack target = cInv.getStackInRowAndColumn(1, 1);
            if(target != null && target.getItem() instanceof ItemSlashBlade)
                itemstack = target;


            return itemstack != null && (x != 0 || y != 0 || z != 0);
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting cInv)
    {
        ItemStack itemstack = null;

        int x = 0;
        int y = 0;
        int z = 0;

        ItemStack tmp;
        if(isFactor(tmp = cInv.getStackInRowAndColumn(1, 0)))
            y += tmp.stackSize;
        if(isFactor(tmp = cInv.getStackInRowAndColumn(2, 0)))
            z += tmp.stackSize;
        if(isFactor(tmp = cInv.getStackInRowAndColumn(0, 1)))
            x += tmp.stackSize;

        if(isFactor(tmp = cInv.getStackInRowAndColumn(2, 1)))
            x -= tmp.stackSize;
        if(isFactor(tmp = cInv.getStackInRowAndColumn(0, 2)))
            z -= tmp.stackSize;
        if(isFactor(tmp = cInv.getStackInRowAndColumn(1, 2)))
            y -= tmp.stackSize;


        ItemStack target = cInv.getStackInRowAndColumn(1, 1);
        if(target != null && target.getItem() instanceof ItemSlashBlade)
            itemstack = target;

        itemstack = itemstack.copy();

        NBTTagCompound tag;

        if(!itemstack.hasTagCompound()){
            tag = new NBTTagCompound();
            itemstack.setTagCompound(tag);
        }else{
            tag = itemstack.getTagCompound();
        }

        float ax = tag.getFloat(ItemSlashBlade.adjustXStr);
        float ay = tag.getFloat(ItemSlashBlade.adjustYStr);
        float az = tag.getFloat(ItemSlashBlade.adjustZStr);

        ax = Math.round(ax * 10 + x) / 10.0f;
        ay = Math.round(ay * 10 + y) / 10.0f;
        az = Math.round(az * 10 + z) / 10.0f;

        tag.setFloat(ItemSlashBlade.adjustXStr, ax);
        tag.setFloat(ItemSlashBlade.adjustYStr, ay);
        tag.setFloat(ItemSlashBlade.adjustZStr, az);

        return itemstack;
    }
}

