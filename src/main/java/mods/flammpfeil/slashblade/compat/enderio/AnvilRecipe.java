package mods.flammpfeil.slashblade.compat.enderio;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Created by Furia on 2017/01/11.
 */
public class AnvilRecipe{

    public void register(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    static public class AnvilRecipeEntry{
        ResourceLocation item;
        int meta;
        int targetLevel;
        int newLevel;

        public AnvilRecipeEntry(ResourceLocation item, int meta, int targetLevel, int newLevel){
            this.item = item;
            this.meta = meta;
            this.targetLevel = targetLevel;
            this.newLevel = newLevel;
        }

        public boolean matches(AnvilUpdateEvent event) {

            if(!(event.getLeft().getItem() instanceof ItemSlashBlade)) return false;
            if(event.getRight() == null) return false;

            Item rItem = event.getRight().getItem();
            if(!rItem.getRegistryName().equals(this.item)) return false;

            if(this.meta != event.getRight().getMetadata()) return false;

            NBTTagCompound tag = ItemSlashBlade.getSpecialEffect(event.getLeft());
            int level = tag.getInteger(SpecialEffects.HFCustom.getEffectKey());
            if(this.targetLevel != level) return false;

            return true;
        }

        @Nullable
        public ItemStack getCraftingResult(AnvilUpdateEvent event) {
            ItemStack result = event.getLeft().copy();
            SpecialEffects.addEffect(result, SpecialEffects.HFCustom.getEffectKey(), this.newLevel);
            return result;
        }

        public int getMaterialCost(AnvilUpdateEvent event) {
            return 1;
        }

        public int getLevelCost(AnvilUpdateEvent event){
            return 5;
        }
    }

    Set<AnvilRecipeEntry> recipeEntries = Sets.newHashSet(
            new AnvilRecipeEntry(new ResourceLocation("enderio","itemMaterial"), 6, 0, 1),
            new AnvilRecipeEntry(new ResourceLocation("enderio","itemBasicCapacitor"), 0, 1, 2),
            new AnvilRecipeEntry(new ResourceLocation("enderio","itemBasicCapacitor"), 1, 2, 3),
            new AnvilRecipeEntry(new ResourceLocation("enderio","itemBasicCapacitor"), 2, 3, 4)
    );

    @SubscribeEvent
    public void onAnvil(AnvilUpdateEvent event){
        AnvilRecipeEntry recipeEntry = null;
        for(AnvilRecipeEntry entry : recipeEntries){
            if(entry.matches(event)) {
                recipeEntry = entry;
                break;
            }
        }

        if(recipeEntry == null) return;

        event.setMaterialCost(recipeEntry.getMaterialCost(event));
        event.setCost(recipeEntry.getLevelCost(event));
        event.setOutput(recipeEntry.getCraftingResult(event));
    }
}
