package mods.flammpfeil.slashblade.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import mods.flammpfeil.slashblade.EntityDirectAttackDummy;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.entity.EntityJudgmentCutManager;
import mods.flammpfeil.slashblade.entity.EntityPhantomSwordBase;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

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
                    int entityId = entityPlayer.getEntityData().getInteger("LastHitSummonedSwords");

                    if(entityId == 0) return null;

                    Entity lastHitSS = entityPlayer.worldObj.getEntityByID(entityId);

                    if(lastHitSS == null) return null;

                    if(!(lastHitSS instanceof EntityPhantomSwordBase)) return null;

                    Entity target = ((EntityPhantomSwordBase) lastHitSS).getRidingEntity();

                    if(target == null)
                        target = lastHitSS;

                    if(entityPlayer.playerNetServerHandler == null) return null;

                    Vec3 look = entityPlayer.getLookVec();

                    look.yCoord = 0;
                    look.normalize();
                    entityPlayer.onEnchantmentCritical(entityPlayer);

                    UntouchableTime.setUntouchableTime(entityPlayer,20,true);

                    boolean teleported = false;
                    for(look.yCoord = 0.5f; 0.0f < look.yCoord; look.yCoord -= 0.1f){
                        Vec3 pos = Vec3.createVectorHelper(-look.xCoord + target.posX, look.yCoord + target.posY, -look.zCoord + target.posZ);

                        if(getCanSpawnHere(entityPlayer,pos, target, lastHitSS)){
                            entityPlayer.playerNetServerHandler.setPlayerLocation(pos.xCoord,pos.yCoord,pos.zCoord,entityPlayer.rotationYaw,entityPlayer.rotationPitch);
                            entityPlayer.onEnchantmentCritical(entityPlayer);
                            teleported = true;
                            break;
                        }
                    }
                    if(!teleported){
                        for(look.yCoord = 0.6f; look.yCoord < 1.5f; look.yCoord += 0.1f){
                            Vec3 pos = Vec3.createVectorHelper(-look.xCoord + target.posX, look.yCoord + target.posY, -look.zCoord + target.posZ);

                            if(getCanSpawnHere(entityPlayer,pos, target, lastHitSS)){
                                entityPlayer.playerNetServerHandler.setPlayerLocation(pos.xCoord,pos.yCoord,pos.zCoord,entityPlayer.rotationYaw,entityPlayer.rotationPitch);
                                entityPlayer.onEnchantmentCritical(entityPlayer);
                                teleported = true;
                                break;
                            }
                        }
                    }

                    if(teleported){
                        lastHitSS.setDead();
                        entityPlayer.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, "mob.endermen.portal", 1.0F, 1.0F);
                    }
                }
                break;
        }
        return null;
    }

    public boolean getCanSpawnHere(Entity target,Vec3 pos,Entity... ignore)
    {
        AxisAlignedBB bb = this.setPosition(target,pos.xCoord,pos.yCoord,pos.zCoord);

        List blockCollidList = target.worldObj.getCollidingBoundingBoxes(target, bb);

        return /*target.worldObj.checkNoEntityCollision(bb) && */blockCollidList.isEmpty();// && !target.worldObj.isAnyLiquid(bb);
    }

    public AxisAlignedBB setPosition(Entity target, double p_70107_1_, double p_70107_3_, double p_70107_5_)
    {
        float f = target.width / 2.0F;
        float f1 = target.height;
        return AxisAlignedBB.getBoundingBox(p_70107_1_ - (double)f, p_70107_3_ - (double)target.yOffset + (double)target.ySize, p_70107_5_ - (double)f, p_70107_1_ + (double)f, p_70107_3_ - (double)target.yOffset + (double)target.ySize + (double)f1, p_70107_5_ + (double)f);
    }
}
