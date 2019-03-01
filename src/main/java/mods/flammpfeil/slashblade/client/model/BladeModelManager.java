package mods.flammpfeil.slashblade.client.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by Furia on 2016/02/06.
 */
public class BladeModelManager {

    private static final class SingletonHolder {
        private static final BladeModelManager instance = new BladeModelManager();
    }

    public static BladeModelManager getInstance() {
        return SingletonHolder.instance;
    }

    WavefrontObject defaultModel;
    static final ResourceLocationRaw resourceDefaultModel = new ResourceLocationRaw("flammpfeil.slashblade","model/blade.obj");

    public static final ResourceLocationRaw resourceDurabilityModel = new ResourceLocationRaw("flammpfeil.slashblade","model/util/durability.obj");
    public static final ResourceLocationRaw resourceDurabilityTexture = new ResourceLocationRaw("flammpfeil.slashblade","model/util/durability.png");

    LoadingCache<ResourceLocationRaw, WavefrontObject> cache;

    private BladeModelManager() {
        defaultModel = new WavefrontObject(resourceDefaultModel);

        cache = CacheBuilder.newBuilder().build(
                CacheLoader.asyncReloading(new CacheLoader<ResourceLocationRaw, WavefrontObject>() {
                    @Override
                    public WavefrontObject load(ResourceLocationRaw key) throws Exception {
                        try{
                            return new WavefrontObject(key);
                        }catch(Exception e){
                            return defaultModel;
                        }
                    }
                }, Executors.newCachedThreadPool())
        );
    }

    @SubscribeEvent
    public void reload(TextureStitchEvent.Pre event){
        cache.invalidateAll();

        defaultModel = new WavefrontObject(resourceDefaultModel);
    }

    public WavefrontObject getModel(ResourceLocationRaw loc) {
        try {
            return cache.get(loc);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return defaultModel;
        }
    }
}
