package mods.flammpfeil.slashblade;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Created by Furia on 14/03/11.
 */
public class RecipeWrapBlade extends ShapedRecipes {
    ItemStack proudSoul;
    private static Map<String,String> wrapableTextureNames = Maps.newHashMap();
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
