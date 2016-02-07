package mods.flammpfeil.slashblade.entity.selector;

import com.google.common.base.Predicate;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public final class EntitySelectorDestructable implements Predicate<Entity> {

    private EntitySelectorDestructable(){}

    private static final class SingletonHolder {
        private static final Predicate<Entity> instance = new EntitySelectorDestructable();
    }

    public static Predicate<Entity> getInstance(){
        return SingletonHolder.instance;
    }

    @Override
    public boolean apply(Entity input) {
        boolean result = false;

        if (input instanceof IProjectile
                || input instanceof EntityTNTPrimed
                || input instanceof EntityFireball
                || input instanceof IThrowableEntity) {
            result = input.isEntityAlive();
        } else {
            String className = input.getClass().getSimpleName();
            if (SlashBlade.manager.destructableTargets.containsKey(className) && SlashBlade.manager.destructableTargets.get(className))
                result = input.isEntityAlive();
        }

        return result;
    }
}