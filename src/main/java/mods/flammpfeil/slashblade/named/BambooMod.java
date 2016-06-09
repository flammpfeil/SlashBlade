package mods.flammpfeil.slashblade.named;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.ItemSlashBlade;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by Furia on 14/11/11.
 */
public class BambooMod {
    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){

        ItemStack innerBlade = GameRegistry.findItemStack("minecraft", "wooden_sword", 1);


        ItemStack reqiredBlade = GameRegistry.findItemStack(SlashBlade.modid,"slashbladeWrapper",1);
        {
            SlashBlade.wrapBlade.setWrapItem(reqiredBlade,innerBlade);

            reqiredBlade.addEnchantment(Enchantment.looting,1);
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
        GameRegistry.registerCustomItemStack(reqiredStr,reqiredBlade);
        ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);
    }

    @SubscribeEvent
    public void postinit(LoadEvent.PostInitEvent event){

        if(Loader.isModLoaded("BambooMod")){
            RecipeBambooMod recipe = new BambooMod.RecipeBambooMod();
            SlashBlade.addRecipe("wrap.BambooMod.katana.sample", recipe);

            FMLCommonHandler.instance().bus().register(recipe);

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
            super(GameRegistry.findItemStack(SlashBlade.modid,"wrap.BambooMod.katana.sample",1),
                    "  P",
                    " S ",
                    "B  ",
                    'P', GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1),
                    'S', GameRegistry.findItemStack(SlashBlade.modid,"slashbladeWrapper",1),
                    'B', GameRegistry.findItemStack("BambooMod","katana",1));

            this.proudSoul = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.ProudSoulStr,1);
            this.katana = GameRegistry.findItemStack("BambooMod","katana",1);
            this.attackModif = 4.0f;
        }
    //RegisterWrapable("BambooMod:katana", "BambooKatana", 4.0f);

        @Override
        public boolean matches(InventoryCrafting cInv, World par2World)
        {
            {
                ItemStack ps = cInv.getStackInRowAndColumn(2, 0);
                if(!(ps != null && ps.isItemEqual(proudSoul)))
                    return false;

                ItemStack sc = cInv.getStackInRowAndColumn(1, 1);
                if(!(sc != null && sc.getItem() == SlashBlade.wrapBlade && !SlashBlade.wrapBlade.hasWrapedItem(sc)))
                    return false;


                ItemStack target = cInv.getStackInRowAndColumn(0, 2);
                if(!(target != null && target.getItem().equals(katana.getItem())))
                    return false;

                return true;
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
            ItemSlashBladeNamed.CurrentItemName.set(tag,"wrap.BambooMod.katana");
            ItemSlashBladeNamed.TextureName.set(tag,"BambooKatana");
            ItemSlashBladeNamed.BaseAttackModifier.set(tag,attackModif);

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

        @SubscribeEvent
        public void itemCraftedEvent(PlayerEvent.ItemCraftedEvent event){

            try{
                if(!(event.craftMatrix instanceof InventoryCrafting))
                    return;

                if(!this.matches((InventoryCrafting)event.craftMatrix,null))
                    return;

                ItemStack target = event.craftMatrix.getStackInSlot(0+2*3);
                if(target == null)
                    return;

                if(!target.getItem().equals(katana.getItem()))
                    return;

                event.craftMatrix.setInventorySlotContents(0 + 2*3,null);

            }catch(Exception e){
            }
        }
    }
}
