package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 2015/11/19.
 */
public class UpthrustBlast {

    static final String UpthrustBlastKey = "UpthrustBlast";

    static public void setUpthrustBlastSword(ItemStack blade, EntityLivingBase user, EntityLivingBase target){

        if(!(blade.getItem() instanceof ItemSlashBlade)) return;

        ItemSlashBlade slashBlade = (ItemSlashBlade)blade.getItem();
        EnumSet<ItemSlashBlade.SwordType> types = slashBlade.getSwordType(blade);

        if(!types.contains(ItemSlashBlade.SwordType.Bewitched)) return;
        if(types.contains(ItemSlashBlade.SwordType.Broken)) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, blade);
        if(level <= 0) return;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(blade);

        EntitySummonedSwordBase entitySS = new EntitySummonedSwordBase(user.world, user, 1,0.0f);
        if (entitySS != null) {
            entitySS.getEntityData().setBoolean(UpthrustBlastKey,true);

            entitySS.setLifeTime(30);

            entitySS.setLocationAndAngles(
                    target.posX,
                    target.posY + target.getEyeHeight() / 2.0,
                    target.posZ,
                    target.rotationYaw + 30.0f,
                    target.rotationPitch + 30.0f);

            entitySS.setDriveVector(1.75f,true);

            if(ItemSlashBlade.SummonedSwordColor.exists(tag))
                entitySS.setColor(ItemSlashBlade.SummonedSwordColor.get(tag));

            entitySS.mountEntity(target);

            target.world.spawnEntity(entitySS);

        }
    }

    static public void doBlast(ItemStack blade, EntityLivingBase user){

        if(!user.onGround) return;

        AxisAlignedBB bb = user.getEntityBoundingBox();
        bb = bb.grow(20, 5, 20);
        List<EntitySummonedSwordBase> list = user.world.getEntitiesWithinAABB(EntitySummonedSwordBase.class,bb);

        for(EntitySummonedSwordBase ss : list){
            if(ss == null) continue;
            if(ss.isDead) continue;
            if(ss.ridingEntity2 == null) continue;
            if(!ss.getEntityData().getBoolean(UpthrustBlastKey)) continue;

            Entity target = ss.ridingEntity2;

            ss.setDead();

            if(!user.world.isRemote){
                target.hurtResistantTime = 0;
                DamageSource ds = new EntityDamageSource("directMagic",user).setDamageBypassesArmor().setMagicDamage().setDamageIsAbsolute();
                target.attackEntityFrom(ds, list.size());

                if(target instanceof EntityLivingBase && !blade.isEmpty()){
                    StylishRankManager.setNextAttackType(user ,StylishRankManager.AttackTypes.PhantomSword);
                    blade.getItem().hitEntity(blade,(EntityLivingBase)target,user);

                    ReflectionAccessHelper.setVelocity(target,0,0,0);
                    target.addVelocity(0.0, 1.0d, 0.0);

                    ((EntityLivingBase) target).hurtTime = 1;

                    ((ItemSlashBlade)blade.getItem()).setDaunting(((EntityLivingBase) target));
                }
            }

            if(user instanceof EntityPlayer)
                ((EntityPlayer) user).onEnchantmentCritical(ss);
        }
    }
}
