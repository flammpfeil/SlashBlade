package mods.flammpfeil.slashblade.named.event;

import net.minecraftforge.event.Event;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

/**
 * Created by Furia on 14/07/07.
 */
public abstract class LoadEvent extends Event{

    public static class InitEvent extends LoadEvent{
        public FMLPostInitializationEvent event;

        public InitEvent(FMLPostInitializationEvent event){
            super();
            this.event = event;
        }
    }

    public static class PostInitEvent extends LoadEvent{
        public FMLPostInitializationEvent event;

        public PostInitEvent(FMLPostInitializationEvent event){
            super();
            this.event = event;
        }
    }
}
