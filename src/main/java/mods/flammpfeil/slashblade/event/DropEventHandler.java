package mods.flammpfeil.slashblade.event;

import com.google.common.collect.*;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

    public static Map<ResourceLocation,Multimap<Float,ItemStack>> dropData = Maps.newHashMap();

    /**
     *
     * @param entityKey
     * @param rate
     * under 0 : need blade
     * abs(rate) over 1 forcedrop
     *
     * @param item
     */
    public static void registerEntityDrop(ResourceLocation entityKey,float rate, ItemStack item){
        Multimap<Float,ItemStack> drops = null;
        if(!dropData.containsKey(entityKey)){
            drops = HashMultimap.create();
            dropData.put(entityKey,drops);
        }else{
            drops = dropData.get(entityKey);
        }
        drops.put(rate, item);
    }

    @SubscribeEvent
    public void LivingDrops(LivingDropsEvent event){
        ResourceLocation key = EntityList.getKey(event.getEntityLiving());

        if(dropData.containsKey(key)){
            Random rand = event.getEntityLiving().getRNG();

            Multimap<Float,ItemStack> drops = dropData.get(key);

            for(Map.Entry<Float,ItemStack> drop : drops.entries()){

                boolean isDrop = false;

                float rate = Math.abs(drop.getKey());
                boolean requiredBlade = drop.getKey() < 0;

                isDrop = rate * (1.0f + 0.5f * event.getLootingLevel()) > rand.nextFloat();

                boolean forceDrop = rate > 1.1f;

                if(requiredBlade){
                    EntityLivingBase target =event.getEntityLiving().getRevengeTarget();
                    if(target == null) return;

                    ItemStack attackItem = target.getHeldItem(EnumHand.MAIN_HAND);
                    if(attackItem.isEmpty()) return;
                    if(!(attackItem.getItem() instanceof ItemSlashBlade)) return;

                }

                if((event.isRecentlyHit() || forceDrop) && isDrop && drop.getValue() != null){
                    ItemStack dropitem = drop.getValue().copy();

                    dropitem.setCount(
                            Math.max(
                                dropitem.getMaxStackSize(),
                                Math.max(1,
                                        (int)Math.round((float)dropitem.getCount() * rand.nextFloat()))));

                    if(dropitem.getItem() instanceof ItemSlashBlade){

                        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(dropitem);
                        if(!tag.getBoolean("IsNoStandDrop")){
                            EntityBladeStand e = new EntityBladeStand(event.getEntityLiving().world
                                    ,event.getEntityLiving().posX
                                    ,event.getEntityLiving().posY
                                    ,event.getEntityLiving().posZ
                                    ,dropitem);
                            e.setGlowing(true);
                            event.getEntityLiving().world.spawnEntity(e);

                            return;
                        }
                    }

                    if(dropitem.getCount() != 0)
                        event.getEntityLiving().entityDropItem(dropitem,1);
                }
            }
        }
    }
}
