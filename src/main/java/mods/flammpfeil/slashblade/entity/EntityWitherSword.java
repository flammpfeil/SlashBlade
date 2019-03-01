package mods.flammpfeil.slashblade.entity;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;

/**
 * Created by Furia on 15/06/21.
 */
public class EntityWitherSword extends EntitySummonedSwordBase {
    public EntityWitherSword(World par1World) {
        super(par1World);
    }

    public EntityWitherSword(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    public EntityWitherSword(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll) {
        super(par1World, entityLiving, AttackLevel, roll);
    }

    private static final DataParameter<Boolean> Burst = EntityDataManager.<Boolean>createKey(EntityWitherSword.class, DataSerializers.BOOLEAN);
    @Override
    protected void registerData() {
        super.registerData();


        //burst
        this.getDataManager().register(Burst, false);
    }

    public boolean getBurst(){
        return this.getDataManager().get(Burst);
    }
    public void setBurst(boolean value){
        this.getDataManager().set(Burst,value);
    }

    @Override
    protected void attackEntity(Entity target) {
        if(getBurst())
            this.world.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, false);

        if(!this.world.isRemote){
            float magicDamage = Math.max(1.0f, AttackLevel);
            target.hurtResistantTime = 0;
            DamageSource ds = new EntityDamageSource("directMagic",this.getThrower()).setDamageBypassesArmor().setMagicDamage();
            target.attackEntityFrom(ds, magicDamage);

            if(!blade.isEmpty() && target instanceof EntityLivingBase && thrower != null && thrower instanceof EntityLivingBase){
                StylishRankManager.setNextAttackType(this.thrower, StylishRankManager.AttackTypes.PhantomSword);
                ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)target,(EntityLivingBase)thrower);

                if (!target.isEntityAlive())
                    ((EntityLivingBase)thrower).heal(1.0F);

                ReflectionAccessHelper.setVelocity(target, 0, 0, 0);
                target.addVelocity(0.0, 0.1D, 0.0);

                ((EntityLivingBase) target).hurtTime = 1;

                if(!getBurst())
                    ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.WITHER, 20 * 5, 1));

                ((ItemSlashBlade)blade.getItem()).setDaunting(((EntityLivingBase) target));
            }
        }

        this.setDead();
    }
}
