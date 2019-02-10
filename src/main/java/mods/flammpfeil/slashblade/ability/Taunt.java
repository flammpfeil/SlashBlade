package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Furia on 2016/10/07.
 */
public class Taunt {
    static final String TauntLevel = "SB.TauntLevel";
    static public int maxTauntLevel = 5;
    static public int expBase = 5;

    public static void fire(ItemStack stack , EntityLivingBase player){
        if(player.world.isRemote)
           return;
        if(!(player.world instanceof WorldServer))
            return;

        AxisAlignedBB bb = player.getEntityBoundingBox();
        bb = bb.grow(10, 5, 10);
        List<Entity> list = player.world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());

        int soundCounter = 0;
        for(Entity entity : list){
            if(!(entity instanceof EntityLivingBase))
                continue;

            if(!((EntityLivingBase) entity).canEntityBeSeen(player))
                return;

            StylishRankManager.addRankPoint(player, StylishRankManager.AttackTypes.Taunt);

            EntityLivingBase livingEntity = (EntityLivingBase) entity;
            DamageSource ds = DamageSource.causeMobDamage(player);

            livingEntity.setRevengeTarget(player);
            if(livingEntity instanceof EntityLiving){
                ((EntityLiving)livingEntity).setAttackTarget(player);
            }
            livingEntity.getCombatTracker().trackDamage(ds, livingEntity.getHealth(), 100);

            livingEntity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,600,1));
            livingEntity.addPotionEffect(new PotionEffect(MobEffects.SPEED,600,1));
            PotionEffect effect = livingEntity.removeActivePotionEffect(MobEffects.RESISTANCE);
            int level = -1;
            if(effect != null)
                level = Math.max(level ,effect.getAmplifier() - 1);
            livingEntity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,600, level));

            ((WorldServer)livingEntity.world).spawnParticle(EnumParticleTypes.VILLAGER_ANGRY,
                    livingEntity.posX,
                    livingEntity.posY,
                    livingEntity.posZ,
                    5,
                    livingEntity.width * 2.0F, livingEntity.height, livingEntity.width * 2.0F,
                    0.02d, new int[0]);

            int tLv = livingEntity.getEntityData().getInteger(TauntLevel);
            tLv = Math.min(tLv + 1, maxTauntLevel);
            livingEntity.getEntityData().setInteger(TauntLevel, tLv);

            if(tLv <= 2)
                StunManager.setStun(livingEntity, 10);

            if(soundCounter++ < 3)
                player.world.playSound(null, livingEntity.posX, livingEntity.posY, livingEntity.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5F, 1.0F);
        }
    }

    @SubscribeEvent
    public void onLivingExperienceDrop(LivingExperienceDropEvent event){
        if(event.getEntityLiving() == null)
            return;

        int dropExp = event.getDroppedExperience();

        int tLv = event.getEntityLiving().getEntityData().getInteger(TauntLevel);
        if( 0 < tLv)
            event.setDroppedExperience(dropExp + tLv * expBase);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityPlayer> event){

        if(!event.getEntity().onGround)
            return;

        ItemStack stack = event.getEntity().getHeldItemMainhand();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        //クイックターン押下状態で射撃攻撃で発動
        byte command = event.getEntity().getEntityData().getByte("SB.MCS");
        if(0 == (command & MessageMoveCommandState.CAMERA))
            return;

        if(!(event.getRenderer().getMainModel() instanceof ModelBiped))
            return;

        ModelBiped model = (ModelBiped)event.getRenderer().getMainModel();


        model.rightArmPose = ModelBiped.ArmPose.BLOCK;
        model.leftArmPose = ModelBiped.ArmPose.EMPTY;

    }
}
