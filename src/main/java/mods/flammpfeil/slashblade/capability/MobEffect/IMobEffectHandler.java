package mods.flammpfeil.slashblade.capability.MobEffect;

public interface IMobEffectHandler {
    void setStunTimeOut(long timeout);
    default void clearStunTimeOut(){
        setStunTimeOut(-1);
    }

    long getStunTimeOut();

    default boolean isStun(long now, long limit) {
        return isStun(now,limit,false);
    }
    default boolean isStun(long now, long limit, boolean isVirtual){

        long timeout = getStunTimeOut();

        //not stun
        if(timeout <= 0) return false;


        //timeout
        timeout = timeout - now;
        if(timeout <= 0 || limit < timeout){
            if(!isVirtual)
                clearStunTimeOut();
            return false;
        }

        //it is in Effect
        return true;
    }


    void setFreezeTimeOut(long timeout);
    default void clearFreezeTimeOut(){
        setFreezeTimeOut(-1);
    }

    long getFreezeTimeOut();

    default boolean isFreeze(long now, long limit) {
        return isFreeze(now,limit,false);
    }
    default boolean isFreeze(long now, long limit, boolean isVirtual){

        long timeout = getFreezeTimeOut();

        //not Freeze
        if(timeout <= 0) return false;


        //timeout
        timeout = timeout - now;
        if(timeout <= 0 || limit < timeout){
            if(!isVirtual)
                clearFreezeTimeOut();
            return false;
        }

        //it is in Effect
        return true;
    }
}
