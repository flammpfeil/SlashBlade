package mods.flammpfeil.slashblade.entity.selector;

import com.google.common.base.Predicate;
import mods.flammpfeil.slashblade.core.ConfigEntityListManager;
import mods.flammpfeil.slashblade.entity.EntityGrimGrip;
import net.minecraft.entity.*;

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

        if(input instanceof MultiPartEntityPart){
            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart)input).parent;

            if (ientitymultipart instanceof EntityLivingBase)
            {
                input = (EntityLivingBase)ientitymultipart;
            }
        }

        String entityStr = EntityList.getEntityString(input);

        if (((entityStr != null && ConfigEntityListManager.attackableTargets.containsKey(entityStr) && ConfigEntityListManager.attackableTargets.get(entityStr))
                || input instanceof EntityGrimGrip
        ))
            result = input.isEntityAlive();

        return result;
    }
}