package mods.flammpfeil.slashblade.event;

import com.google.common.collect.Queues;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Queue;

/**
 * Created by Furia on 2016/02/08.
 */
public class ScheduleEntitySpawner {

    private static final class SingletonHolder {
        private static final ScheduleEntitySpawner instance = new ScheduleEntitySpawner();
    }

    public static ScheduleEntitySpawner getInstance() {
        return SingletonHolder.instance;
    }

    private ScheduleEntitySpawner(){}

    private Queue<Entity> targetQueue = Queues.newConcurrentLinkedQueue();

    public boolean offer(Entity entity) {
        return targetQueue.offer(entity);
    }

    @SubscribeEvent
    public void onWorldTickEvent(TickEvent.WorldTickEvent event){
        if(event.phase != TickEvent.Phase.START) return;
        if(event.side != Side.SERVER) return;

        for(Entity entity = targetQueue.poll(); entity != null; entity = targetQueue.poll()){
            if(entity.world == null)
                continue;

            entity.world.spawnEntity(entity);
        }
    }
}
