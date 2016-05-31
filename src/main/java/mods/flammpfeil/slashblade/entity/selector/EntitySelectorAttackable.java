package mods.flammpfeil.slashblade.entity.selector;

import com.google.common.base.Predicate;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityGrimGrip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.EntityDragonPart;

public final class EntitySelectorAttackable implements Predicate<Entity>{

    private EntitySelectorAttackable(){}

    private static final class SingletonHolder {
        private static final Predicate<Entity> instance = new EntitySelectorAttackable();
    }

    public static Predicate<Entity> getInstance(){
        return SingletonHolder.instance;
    }

    @Override
    public boolean apply(Entity input) {
        boolean result = false;

        String entityStr = EntityList.getEntityString(input);

        if (((entityStr != null && SlashBlade.manager.attackableTargets.containsKey(entityStr) && SlashBlade.manager.attackableTargets.get(entityStr))
                || input instanceof EntityDragonPart
                || input instanceof EntityGrimGrip
        ))
            result = input.isEntityAlive();

        return result;
    }
}