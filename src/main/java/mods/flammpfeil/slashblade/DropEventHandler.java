package mods.flammpfeil.slashblade;

import com.google.common.collect.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Furia on 14/05/16.
 */
public class DropEventHandler {
    public static final String header = "flammpfeil.slashblade.entitydrops.";

    public static Set<String> registerdEntityNames = Sets.newHashSet();

    public static Map<String,Multimap<Float,ItemStack>> dropData = Maps.newHashMap();

    public static void registerEntityDrop(String entityName,float rate, ItemStack item){
        Multimap<Float,ItemStack> drops = null;
        if(!dropData.containsKey(entityName)){
            drops = HashMultimap.create();
            dropData.put(entityName,drops);
        }else{
            drops = dropData.get(entityName);
        }
        drops.put(rate, item);
    }

    @SubscribeEvent
    public void LivingDrops(LivingDropsEvent event){
        String key = EntityList.getEntityString(event.entityLiving);

        if(dropData.containsKey(key)){
            Random rand = event.entityLiving.getRNG();

            Multimap<Float,ItemStack> drops = dropData.get(key);

            for(Map.Entry<Float,ItemStack> drop : drops.entries()){

                boolean isDrop = false;

                float rate = Math.abs(drop.getKey());
                boolean requiredBlade = drop.getKey() < 0;

                isDrop = rate * (1.0f + 0.5f * event.lootingLevel) > rand.nextFloat();

                boolean forceDrop = rate > 1.1f;

                if(requiredBlade){
                    EntityLivingBase target =event.entityLiving.getAITarget();
                    if(target == null) return;

                    ItemStack attackItem = target.getHeldItem();
                    if(attackItem == null) return;
                    if(!(attackItem.getItem() instanceof ItemSlashBlade)) return;

                }

                if((event.recentlyHit || forceDrop) && isDrop && drop.getValue() != null){
                    ItemStack dropitem = drop.getValue().copy();

                    dropitem.stackSize =
                            Math.max(
                                dropitem.getMaxStackSize(),
                                Math.max(1,
                                        (int)Math.round((float)dropitem.stackSize * rand.nextFloat())));

                    if(dropitem.getItem() instanceof ItemSlashBlade){

                        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(dropitem);
                        if(!tag.getBoolean("IsNoStandDrop")){
                            EntityBladeStand e = new EntityBladeStand(event.entityLiving.worldObj
                                    ,event.entityLiving.posX
                                    ,event.entityLiving.posY
                                    ,event.entityLiving.posZ
                                    ,dropitem);
                            e.setGlowing(true);
                            event.entityLiving.worldObj.spawnEntityInWorld(e);

                            return;
                        }
                    }

                    if(dropitem.stackSize != 0)
                        event.entityLiving.entityDropItem(dropitem,1);
                }
            }
        }
    }
}
