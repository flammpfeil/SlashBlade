package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 15/05/21.
 */
public class EnemyStep {

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

        boolean isJumping = ReflectionHelper.getPrivateValue(EntityLivingBase.class,target,"isJumping","field_70703_bu");

        if(!isJumping) return;

        AxisAlignedBB bb = target.boundingBox.copy();
        bb = bb.expand(2.0, 1.5, 2.0);
        List<Entity> list = target.worldObj.getEntitiesWithinAABBExcludingEntity(target, bb, ItemSlashBlade.AttackableSelector);
        if(0 < list.size() && target.isAirBorne){
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

            if(enemy != null){
                target.onGround = true;
                ReflectionHelper.setPrivateValue(EntityLivingBase.class,target,0,"jumpTicks","field_70773_bE");
                //target.setJumping(false);
            }
        }
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
    }
}
