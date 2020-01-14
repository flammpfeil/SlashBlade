package mods.flammpfeil.slashblade.specialeffect;

/**
 * Created by Furia on 15/06/20.
 */
public interface ISpecialEffect {
    void register();

    int getDefaultRequiredLevel();
    String getEffectKey();
}
