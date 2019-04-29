package mods.flammpfeil.slashblade.capability.MobEffect;

public class MobEffect implements IMobEffectHandler {

    long stunTimeout = -1;
    long freezeTimeout = -1;

    @Override
    public void setStunTimeOut(long timeout) {
        stunTimeout = timeout;
    }

    @Override
    public long getStunTimeOut() {
        return stunTimeout;
    }

    @Override
    public void setFreezeTimeOut(long timeout) {
        freezeTimeout = timeout;
    }

    @Override
    public long getFreezeTimeOut() {
        return freezeTimeout;
    }
}
