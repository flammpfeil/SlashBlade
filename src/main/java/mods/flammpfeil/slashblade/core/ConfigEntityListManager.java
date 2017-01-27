package mods.flammpfeil.slashblade.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigEntityListManager {

    static public Map<String,Boolean> attackableTargets = new HashMap<String,Boolean>();
    static public Map<String,Boolean> destructableTargets = new HashMap<String,Boolean>();


    /**
     * [\] -> [\\]
     * ["] -> [\quot;]
     * 改行 -> [\r;\r;]
     * 全文を""でquotationする
     * 上記のとおり、エスケープされます。直接configを修正するときに覚えておくべき。
     * @param source
     * @return
     */
    static private String escape(String source){
		return String.format("\"%s\"", source.replace("\\","\\\\").replace("\"","\\quot;").replace("\r", "\\r;").replace("\n", "\\n;"));
    }
    static private String unescape(String source){
    	return source.replace("\"", "").replace("\\quot;", "\"").replace("\\r;","\r").replace("\\n;","\n").replace("\\\\", "\\");
    }


    @SubscribeEvent
    public void onWorldTickEvent(TickEvent.WorldTickEvent event)
    {
    	if(event.side == Side.SERVER && event.phase == Phase.START && event.type ==Type.WORLD){
			try{
				SlashBlade.mainConfiguration.load();



				for(EntityEntry entry : net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES){
					Class cls = entry.getEntityClass();

					String name = entry.getName();//(String)EntityList.func_191302_a(key);
					if(name == null || name.length() == 0)
						continue;

					Entity instance = null;

					try{
                        Constructor<Entity> constructor = cls.getConstructor(World.class);
                        if(constructor != null){
                            instance = constructor.newInstance((Object)event.world);
                        }
					}catch(Throwable e){
						instance = null;
					}


					if(EntityLivingBase.class.isAssignableFrom(cls))
					{
						boolean attackable = true;

						if(instance == null){
							attackable = true;

						}else if(IMob.class.isAssignableFrom(cls)){//instance instanceof IMob){
							attackable = true;

						}else if(instance instanceof IAnimals
								||instance instanceof IEntityOwnable
								||instance instanceof IMerchant){
							attackable = false;

						}
						attackableTargets.put(name, attackable);
					}else{

						boolean destructable = false;

						if(instance instanceof IProjectile
								|| instance instanceof EntityTNTPrimed
								|| instance instanceof EntityFireball
								|| instance instanceof IThrowableEntity){
							destructable = true;
						}

						destructableTargets.put(cls.getSimpleName(), destructable);

					}


				}

				{
					Property propAttackableTargets = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "AttackableTargets" ,new String[]{});
					propAttackableTargets.setShowInGui(false);

					for(String curEntry : propAttackableTargets.getStringList()){
						curEntry = unescape(curEntry);
						int spliterIdx = curEntry.lastIndexOf(":");
						String name = curEntry.substring(0, spliterIdx);
						String attackableStr = curEntry.substring(spliterIdx + 1, curEntry.length());

						boolean attackable = attackableStr.toLowerCase().equals("true");

						attackableTargets.put(name, attackable);
					}

					ArrayList<String> profAttackableTargets = new ArrayList<String>();
					for(Object key : attackableTargets.keySet()){
						Boolean name = (Boolean)attackableTargets.get(key);

						String keyStr = (String)key;
						profAttackableTargets.add(escape(String.format("%s:%b", keyStr ,name)));
					}
					String[] data = profAttackableTargets.toArray(new String[]{});

					propAttackableTargets.set(data);
				}


				{
					Property propDestructableTargets = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "DestructableTargets" ,new String[]{});
					propDestructableTargets.setShowInGui(false);

					for(String curEntry : propDestructableTargets.getStringList()){
						curEntry = unescape(curEntry);
						int spliterIdx = curEntry.lastIndexOf(":");
						String name = curEntry.substring(0, spliterIdx);
						String attackableStr = curEntry.substring(spliterIdx + 1, curEntry.length());

						boolean destructable = attackableStr.toLowerCase().equals("true");

						destructableTargets.put(name, destructable);
					}

					ArrayList<String> profDestructableTargets = new ArrayList<String>();
					for(Object key : destructableTargets.keySet()){
						Boolean name = (Boolean)destructableTargets.get(key);

						String keyStr = (String)key;
						profDestructableTargets.add(escape(String.format("%s:%b", keyStr ,name)));
					}
					String[] data2 = profDestructableTargets.toArray(new String[]{});

					propDestructableTargets.set(data2);
				}




			}
			finally
			{
				SlashBlade.mainConfiguration.save();
			}


	        MinecraftForge.EVENT_BUS.unregister(this);
    	}
    }
}
