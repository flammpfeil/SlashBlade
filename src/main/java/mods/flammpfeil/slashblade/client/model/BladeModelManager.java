package mods.flammpfeil.slashblade.client.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    static final ResourceLocation resourceDefaultModel = new ResourceLocation("flammpfeil.slashblade","model/blade.obj");

    LoadingCache<ResourceLocation, WavefrontObject> cache;

    private BladeModelManager() {
        defaultModel = new WavefrontObject(resourceDefaultModel);

        cache = CacheBuilder.newBuilder().build(
                CacheLoader.asyncReloading(new CacheLoader<ResourceLocation, WavefrontObject>() {
                    @Override
                    public WavefrontObject load(ResourceLocation key) throws Exception {
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

    public WavefrontObject getModel(ResourceLocation loc) {
        try {
            return cache.get(loc);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return defaultModel;
        }
    }
}
