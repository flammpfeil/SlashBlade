package mods.flammpfeil.slashblade;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.registry.IThrowableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class ConfigEntityListManager implements ITickHandler{

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

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    	if (type.contains(TickType.WORLD)){
    		try{
				SlashBlade.mainConfiguration.load();



				for(Object key : EntityList.classToStringMapping.keySet()){
					Class cls = (Class)key;

					String name = (String)EntityList.classToStringMapping.get(key);
					if(name == null || name.length() == 0)
						continue;

					Entity instance = null;

					try{
						instance = EntityList.createEntityByName(name, (World)tickData[0]);
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
								||instance instanceof EntityOwnable
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
							//allways destruction
						}else{
							destructableTargets.put(cls.getSimpleName(), destructable);

						}

					}


				}

				{
					Property propAttackableTargets = SlashBlade.mainConfiguration.get(Configuration.CATEGORY_GENERAL, "AttackableTargets" ,new String[]{});

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


			loaded = true;
    	}
    }

    static boolean loaded = false;
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        if (!loaded)
        {
            return EnumSet.of(TickType.WORLD);
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getLabel()
    {
        return null;
    }
}
