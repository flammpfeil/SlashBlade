package mods.flammpfeil.slashblade.ability;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
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
        EntityLivingBase target = event.getEntityLiving();
        if(target == null) return;

        if(!target.worldObj.isRemote) return;

        ItemStack stack = target.getHeldItem(EnumHand.MAIN_HAND);
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        EnumSet<ItemSlashBlade.SwordType> swordType = ((ItemSlashBlade)stack.getItem()).getSwordType(stack);
        if(!swordType.contains(ItemSlashBlade.SwordType.Bewitched)) return;


        //wall kick jump
        boolean wallKickJumped = target.getEntityData().hasKey(WallKick);

        if(!canCycleJump(target)){

            if(wallKickJumped){
                if(target.onGround){
                    target.getEntityData().removeTag(WallKick);
                }
            }

            return;
        }

        //EnemyStep
        Entity enemy = getStepEntity(target);
        if(enemy != null){
            resetJump(target);

            if(wallKickJumped){
                target.getEntityData().removeTag(WallKick);
            }

            if(target instanceof EntityPlayer)
                ((EntityPlayer) target).onEnchantmentCritical(enemy);

            return;
        }

        if(!wallKickJumped && !target.onGround){ //now jumping
            if(hasCollidWallBlocks(target, target.getPositionVector())){
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

        int jumpState = target.getEntityData().getInteger(DoJumping);

        if(isJumping){
            switch (jumpState){
                case 0: //firstJump
                    target.getEntityData().setInteger(DoJumping,1);
                    if(target.fallDistance == 0){
                        return false;
                    }else{ //no jump falling
                        return true;
                    }
                case 1: //now first jumping
                    return false;

                case 2: //special jump
                    target.getEntityData().setInteger(DoJumping,1);
                    return true;

                default:
                    return false;
            }
        }else{

            switch (jumpState){
                case 1: //set can special jump
                    target.getEntityData().setInteger(DoJumping,2);
                    return false;

                default:
                    if(target.onGround)
                        target.getEntityData().removeTag(DoJumping);

                    return false;
            }
        }
    }
    private void resetJump(EntityLivingBase target){
        target.onGround = true;
        ReflectionHelper.setPrivateValue(EntityLivingBase.class, target, 0, "jumpTicks", "field_70773_bE");
    }

    public boolean hasCollidWallBlocks(Entity target, Vec3d pos)
    {
        AxisAlignedBB bb = this.getPositionAABB(target, pos);
        bb = bb.expand(1.0, 0.0, 1.0);
        List<AxisAlignedBB> blockCollidList = target.worldObj.getCubes(target, bb);

        return !blockCollidList.isEmpty();
    }

    public AxisAlignedBB getPositionAABB(Entity target, Vec3d pos) {
        return getPositionAABB(target, pos.xCoord, pos.yCoord, pos.zCoord);
    }
    public AxisAlignedBB getPositionAABB(Entity target, double x, double y, double z)
    {
        float f = target.width / 2.0F;
        float f1 = target.height;
        return new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f);
    }

    private Entity getStepEntity(EntityLivingBase target){
        AxisAlignedBB bb = target.getEntityBoundingBox();
        bb = bb.expand(2.0, 1.5, 2.0);
        bb = bb.offset(0,0.5,0);
        List<Entity> list = target.worldObj.getEntitiesInAABBexcluding(target, bb, EntitySelectorAttackable.getInstance());
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
        EntityLivingBase target = event.getEntityLiving();
        if(target == null) return;

        ItemStack stack = target.getHeldItem(EnumHand.MAIN_HAND);
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        target.fallDistance = 0;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        ItemSlashBlade.ComboSequence seq = ItemSlashBlade.getComboSequence(tag);
        if(seq != ItemSlashBlade.ComboSequence.Kiriage)
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
