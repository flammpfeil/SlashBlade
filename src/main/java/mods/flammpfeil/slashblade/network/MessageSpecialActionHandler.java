package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.AirTrick;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Furia on 15/05/15.
 */
public class MessageSpecialActionHandler implements IMessageHandler<MessageSpecialAction,IMessage> {

    @Override
    public IMessage onMessage(MessageSpecialAction message, MessageContext ctx) {
        if(ctx.getServerHandler() == null) return null;

        EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;

        if(entityPlayer == null) return null;

        ItemStack stack = entityPlayer.getHeldItem();
        if(stack == null) return null;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return null;

        switch(message.mode){
            case 4:{
                //rising star

                NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

                ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.RisingStar);

                ItemSlashBlade blade = (ItemSlashBlade) stack.getItem();
                blade.doSwingItem(stack, entityPlayer);

                if (!entityPlayer.worldObj.isRemote) {
                    AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
                    bb = bb.expand(4, 0, 4);

                    bb = bb.addCoord(entityPlayer.motionX, entityPlayer.motionY, entityPlayer.motionZ);

                    List<Entity> list = entityPlayer.worldObj.getEntitiesInAABBexcluding(entityPlayer, bb, EntitySelectorAttackable.getInstance());

                    StylishRankManager.setNextAttackType(entityPlayer, StylishRankManager.AttackTypes.RapidSlash);

                    for (Entity curEntity : list) {
                        curEntity.hurtResistantTime = 0;
                        if (entityPlayer instanceof EntityPlayer) {
                            ItemSlashBlade itemBlade = (ItemSlashBlade) stack.getItem();
                            itemBlade.attackTargetEntity(stack, curEntity, (EntityPlayer) entityPlayer, true);
                            //((ItemSlashBlade) stack.getItem()).hitEntity(stack, (EntityLivingBase) curEntity, (EntityLivingBase) entityPlayer);
                        } else {
                            DamageSource ds = new EntityDamageSource("mob", entityPlayer);
                            curEntity.attackEntityFrom(ds, 10);
                            if (stack != null && curEntity instanceof EntityLivingBase)
                                ((ItemSlashBlade) stack.getItem()).hitEntity(stack, (EntityLivingBase) curEntity, (EntityLivingBase) entityPlayer);
                        }
                    }

                }

                break;
            }
            case 3:
                {
                    ItemSlashBlade itemBlade = (ItemSlashBlade)stack.getItem();
                    SpecialAttackBase base = itemBlade.getSpecialAttack(stack);
                    if(base instanceof ISuperSpecialAttack){
                        ((ISuperSpecialAttack) base).doSuperSpecialAttack(stack, entityPlayer);
                    }else if(ItemSlashBlade.defaultSA instanceof ISuperSpecialAttack){
                        ((ISuperSpecialAttack) ItemSlashBlade.defaultSA).doSuperSpecialAttack(stack, entityPlayer);
                    }

                    break;
                }
            case 2:
                {
                    UntouchableTime.setUntouchableTime(entityPlayer,3,true);
                    break;
                }
            default:
                {
                    AirTrick.SummonOrDo(entityPlayer);
                }
                break;
        }
        return null;
    }
}
