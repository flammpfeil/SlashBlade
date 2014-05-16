package mods.flammpfeil.slashblade;

import com.google.common.collect.*;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
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

    public static Map<String,Map<Float,ItemStack>> dropData = Maps.newHashMap();

    public static void registerEntityDrop(String entityName,float rate, ItemStack item){
        Map<Float,ItemStack> drops = null;
        if(!dropData.containsKey(entityName)){
            drops = Maps.newHashMap();
            dropData.put(entityName,drops);
        }else{
            drops = dropData.get(entityName);
        }
        drops.put(rate, item);
    }

    public void LivingDrops(LivingDropsEvent event){
        String key = EntityList.getEntityString(event.entityLiving);

        if(dropData.containsKey(key)){
            Random rand = event.entityLiving.getRNG();

            Map<Float,ItemStack> drops = dropData.get(key);

            for(Map.Entry<Float,ItemStack> drop : drops.entrySet()){
                if(drop.getKey() > rand.nextFloat()){
                    ItemStack dropitem = drop.getValue().copy();

                    dropitem.stackSize = Math.max(1,(int)Math.round((float)dropitem.stackSize * rand.nextFloat()));

                    if(dropitem.stackSize != 0)
                        event.entityLiving.entityDropItem(dropitem,1);
                }
            }
        }
    }
}
