package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 15/05/21.
 */
public class EnemyStep {

    static final String DoJumping = "SB.DoJumping";
    static final String WallKick = "SB.WallKick";

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;

        ItemStack stack = target.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        EnumSet<ItemSlashBlade.SwordType> swordType = ((ItemSlashBlade)stack.getItem()).getSwordType(stack);
        if(!swordType.contains(ItemSlashBlade.SwordType.Bewitched)) return;

        if(!canCycleJump(target)) return;

        //EnemyStep
        Entity enemy = getStepEntity(target);
        if(enemy != null){
            resetJump(target);

            if(target instanceof EntityPlayer)
                ((EntityPlayer) target).onEnchantmentCritical(enemy);

            return;
        }

        //wall kick jump
        boolean wallKickJumped = target.getEntityData().hasKey(WallKick);
        if(wallKickJumped){
            if(target.onGround){
                target.getEntityData().removeTag(WallKick);
            }
            return;
        }else if(!target.onGround){ //now jumping
            if(hasCollidWallBlocks(target, target.getPosition(1.0f))){
                resetJump(target);

                target.getEntityData().setInteger(WallKick, 1);
                if(target instanceof EntityPlayer)
                    ((EntityPlayer) target).onCriticalHit(target);
            }

            return;
        }
    }


    public boolean canCycleJump(EntityLivingBase target){
        boolean isJumping = ReflectionHelper.getPrivateValue(EntityLivingBase.class,target,"isJumping","field_70703_bu");

        boolean hasKey = target.getEntityData().hasKey(DoJumping);

        if(!isJumping){
            if(hasKey)
                target.getEntityData().removeTag(DoJumping);

            return false;
        }

        if(hasKey) return false;

        return true;
    }
    private void resetJump(EntityLivingBase target){
        target.onGround = true;
        ReflectionHelper.setPrivateValue(EntityLivingBase.class,target,0,"jumpTicks","field_70773_bE");
        target.getEntityData().setInteger(DoJumping,1);
    }

    public boolean hasCollidWallBlocks(Entity target, Vec3 pos)
    {
        AxisAlignedBB bb = this.getPositionAABB(target, pos.xCoord, pos.yCoord, pos.zCoord);
        bb = bb.expand(0.5,0,0.5);
        List blockCollidList = target.worldObj.getCollidingBoundingBoxes(target, bb);

        return !blockCollidList.isEmpty();
    }
    public AxisAlignedBB getPositionAABB(Entity target, double p_70107_1_, double p_70107_3_, double p_70107_5_)
    {
        float f = target.width / 2.0F;
        float f1 = target.height;
        return AxisAlignedBB.getBoundingBox(p_70107_1_ - (double) f, p_70107_3_ - (double) target.yOffset + (double) target.ySize, p_70107_5_ - (double) f, p_70107_1_ + (double) f, p_70107_3_ - (double) target.yOffset + (double) target.ySize + (double) f1, p_70107_5_ + (double) f);
    }

    private Entity getStepEntity(EntityLivingBase target){
        AxisAlignedBB bb = target.boundingBox.copy();
        bb = bb.expand(2.0, 1.5, 2.0);
        bb = bb.offset(0,0.5,0);
        List<Entity> list = target.worldObj.getEntitiesWithinAABBExcludingEntity(target, bb, ItemSlashBlade.AttackableSelector);
        if(0 < list.size()){
            Entity enemy = null;
            float distance = 10.0f;
            for(Entity curEntity : list){
                float curDist = curEntity.getDistanceToEntity(target);
                if(curDist < distance)
                {
                    enemy = curEntity;
                    distance = curDist;
                }
            }

            return enemy;
        }
        return null;
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event){
        EntityLivingBase target = event.entityLiving;
        if(target == null) return;

        ItemStack stack = target.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        target.fallDistance = 0;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        ItemSlashBlade.ComboSequence seq = ItemSlashBlade.getComboSequence(tag);
        if(seq == ItemSlashBlade.ComboSequence.Iai)
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.None);

        //EnemyStepEffect
        if(target.isAirBorne){
            Entity enemy = getStepEntity(target);

            if(enemy != null){
                if(3.0 < target.fallDistance){
                    enemy.motionY -= Math.abs(target.motionY);
                    enemy.fallDistance += target.fallDistance;
                    enemy.attackEntityFrom(new EntityDamageSource("fall",target).setDamageBypassesArmor(), (float)(target.fallDistance - 3.0));
                }
            }
        }

    }
}
