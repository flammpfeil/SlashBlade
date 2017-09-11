package mods.flammpfeil.slashblade.named;

import net.minecraft.init.Enchantments;
import net.minecraft.util.NonNullList;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by Furia on 14/11/11.
 */
public class BambooMod {
    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){
    }

    @SubscribeEvent
    public void postinit(LoadEvent.PostInitEvent event){

        ItemStack innerBlade = SlashBlade.findItemStack("minecraft", "wooden_sword", 1);


        ItemStack reqiredBlade = SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
        {
            SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

            reqiredBlade.addEnchantment(Enchantments.LOOTING,1);
            NBTTagCompound tag = reqiredBlade.getTagCompound();
            ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap.BambooMod.katana");
            ItemSlashBladeNamed.BaseAttackModifier.set(tag, 4.0f);
            ItemSlashBlade.TextureName.set(tag,"BambooKatana");

            NamedBladeManager.registerBladeSoul(tag , reqiredBlade.getDisplayName());

            NBTTagCompound displayTag = new NBTTagCompound();
            reqiredBlade.setTagInfo("display",displayTag);
            NBTTagList loreList = new NBTTagList();
            loreList.appendTag(new NBTTagString("is demo item. is wooden sword"));
            loreList.appendTag(new NBTTagString("true performance : please crafting"));
            displayTag.setTag("Lore", loreList);

            reqiredBlade.setStackDisplayName(reqiredBlade.getDisplayName());
        }
        String reqiredStr = "wrap.BambooMod.katana.sample";
        SlashBlade.registerCustomItemStack(reqiredStr,reqiredBlade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

        ItemStack katana = SlashBlade.findItemStack("BambooMod","katana",1);

        if(Loader.isModLoaded("BambooMod") && !katana.isEmpty()){
            RecipeBambooMod recipe = new BambooMod.RecipeBambooMod();
            SlashBlade.addRecipe("wrap.BambooMod.katana.sample", recipe);

            RecipeSorter.register("flammpfeil.slashblade:bamboomod", RecipeBambooMod.class, RecipeSorter.Category.SHAPED, "after:forge:shaped");
        }
    }
    /**
     * Created by Furia on 14/11/11.
     */
    public static class RecipeBambooMod extends ShapedOreRecipe {
        ItemStack proudSoul;
        ItemStack katana;
        float attackModif;

        public RecipeBambooMod() {
            super(new ResourceLocation(SlashBlade.modid,"bamboo"),
                    SlashBlade.findItemStack(SlashBlade.modid,"wrap.BambooMod.katana.sample",1),
                    "  P",
                    " S ",
                    "B  ",
                    'P', SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1),
                    'S', SlashBlade.findItemStack(SlashBlade.modid,"slashbladeWrapper",1),
                    'B', SlashBlade.findItemStack("BambooMod","katana",1));

            this.proudSoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.ProudSoulStr,1);
            this.katana = SlashBlade.findItemStack("BambooMod","katana",1);
            this.attackModif = 4.0f;
        }
    //RegisterWrapable("BambooMod:katana", "BambooKatana", 4.0f);

        @Override
        public boolean matches(InventoryCrafting cInv, World par2World)
        {
            {
                ItemStack ps = cInv.getStackInRowAndColumn(2, 0);
                if(!(!ps.isEmpty() && ps.isItemEqual(proudSoul)))
                    return false;

                ItemStack sc = cInv.getStackInRowAndColumn(1, 1);
                if(!(!sc.isEmpty() && sc.getItem() == SlashBlade.wrapBlade && !SlashBlade.wrapBlade.hasWrapedItem(sc)))
                    return false;


                ItemStack target = cInv.getStackInRowAndColumn(0, 2);
                if(!(!target.isEmpty() && target.getItem().equals(katana.getItem())))
                    return false;

                return true;
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


            ResourceLocation targetName = Item.REGISTRY.getNameForObject(target.getItem());


            SlashBlade.wrapBlade.removeWrapItem(scabbard);

            SlashBlade.wrapBlade.setWrapItem(scabbard,target);

            NBTTagCompound tag = scabbard.getTagCompound();
            ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap.BambooMod.katana");
            ItemSlashBladeNamed.TextureName.set(tag,"BambooKatana");
            ItemSlashBladeNamed.BaseAttackModifier.set(tag,attackModif);

            if(target.hasDisplayName()){
                scabbard.setStackDisplayName(String.format(I18n.translateToLocal("item.flammpfeil.slashblade.wrapformat").trim(), target.getDisplayName()));
            }else if(target.isItemEnchanted()){
                scabbard.setStackDisplayName(scabbard.getDisplayName());
            }else{
                scabbard.setStackDisplayName(String.format(I18n.translateToLocal("item.flammpfeil.slashblade.wrapformat.low").trim(),target.getDisplayName()));
            }

            if(target.isItemEnchanted()){
                tag.setTag("ench",target.getTagCompound().getTag("ench"));
            }

            return scabbard;
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            NonNullList<ItemStack> stacks = super.getRemainingItems(inv);


            for(ItemStack stack : stacks){
                if(stack.getItem().equals(katana.getItem())) {
                    stack = ItemStack.EMPTY;
                    break;
                }
            }

            return stacks;
        }
    }
}
