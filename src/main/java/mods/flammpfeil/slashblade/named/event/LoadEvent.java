package mods.flammpfeil.slashblade.named.event;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Created by Furia on 14/07/07.
 */
public abstract class LoadEvent extends Event{

    public static class InitEvent extends LoadEvent{
        public FMLInitializationEvent event;

        public InitEvent(FMLInitializationEvent event){
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
