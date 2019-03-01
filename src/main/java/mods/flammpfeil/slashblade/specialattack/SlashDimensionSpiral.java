package mods.flammpfeil.slashblade.specialattack;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.entity.EntityJudgmentCutManager;
import mods.flammpfeil.slashblade.entity.EntitySlashDimension;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 14/05/27.
 */
public class SlashDimensionSpiral extends SpecialAttackBase implements IJustSpecialAttack,ISuperSpecialAttack{
    @Override
    public String toString() {
        return "slashdimension_spiral";
    }

    @Override
    public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.world;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        Entity target = null;

        int entityId = ItemSlashBlade.TargetEntityId.get(tag);

        if(entityId != 0){
            Entity tmp = world.getEntityByID(entityId);
            if(tmp != null){
                if(tmp.getDistance(player) < 30.0f)
                    target = tmp;
            }
        }

        if(target == null){
            target = getEntityToWatch(player);
        }

        if(target == null) {
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            //spawnParticle(world, target);

            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            if(!player.world.isRemote) {
                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (0.5f + level / 5.0f);
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if (dim != null) {
                    Vec3d pos = player.getLookVec();
                    pos = pos.scale(5);
                    pos = pos.add(player.getPositionVector());
                    pos = pos.addVector(0, player.getEyeHeight(), 0);

                    Vec3d offset = player.getPositionVector().addVector(0,player.getEyeHeight(),0);
                    Vec3d offsettedLook = offset.add(player.getLookVec().scale(5));
                    RayTraceResult movingobjectposition = world.rayTraceBlocks(offset, offsettedLook);
                    if (movingobjectposition != null)
                    {
                        IBlockState state = null;
                        BlockPos blockPos = movingobjectposition.getBlockPos();
                        if(blockPos != null)
                            state = world.getBlockState(blockPos);
                        if(state != null && state.getCollisionBoundingBox(world, blockPos) == null)
                            movingobjectposition = null;
                        else {
                            Vec3d tmppos = new Vec3d(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z);
                            if(1 < tmppos.distanceTo(player.getPositionVector())){
                                pos = tmppos;
                            }
                        }
                    }


                    dim.setPosition(pos.x, pos.y, pos.z);
                    dim.setLifeTime(10);
                    dim.setIsSlashDimension(true);
                    world.spawnEntity(dim);
                }


                Vec3d pos = dim.getPositionVector();
                pos = pos.addVector(0 , -0.5f, 0);

                int rank = StylishRankManager.getStylishRank(player);
                int count = 5 + rank;
                float rotUnit = 360.0f / count;

                float baseRot = (player.getRNG().nextFloat() * 180);

                for(int j = 0; j < 1; j++){
                    for (int i = 0; i < count; i++) {
                        EntityDrive entity = new EntityDrive(world, player, magicDamage);
                        entity.setLifeTime(15);

                        // プレイヤーを中心にぐるっと回るように配置。
                        // 向きは 上から見て右回りになる接線方向。
                        // （輪が広がるような動きになる）
                        // プレイヤー視点で左下から右上に切り上げるように見えるように
                        // y と pitch と roll を調整。

                        // ※ SpiralEdge と、同じ

                        final float rot = rotUnit*i;

                        float yaw = rot + baseRot;
                        float pitch = -30*(float)Math.cos(Math.toRadians(rot - 60));

                    /*if (pitch > 0.0f)
                        pitch = 1.0f;
                    */

                        double x = Math.cos(Math.toRadians(yaw));		// -sin(yaw - 90)
                        double y = 0.7*Math.sin(Math.toRadians(rot - 60));
                        double z = Math.sin(Math.toRadians(yaw));		//  cos(yaw - 90)

                        entity.setLocationAndAngles(pos.x - x,
                                pos.y - y,
                                pos.z - z,
                                yaw,
                                pitch);
                        entity.setRoll((float)(90.0f - 30.0f*Math.cos(Math.toRadians(rot + 30))));
                        entity.setDriveVector(0.3f);

                        world.spawnEntity(entity);
                    }

                    baseRot += 120.0f;
                }
            }

        }else{
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            spawnParticle(world, target);

            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            AxisAlignedBB bb = target.getBoundingBox();
            bb = bb.grow(2.0f, 0.25f, 2.0f);

            List<Entity> list = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());

            if(!EntitySelectorAttackable.getInstance().apply(target))
                list.add(target);

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            for(Entity curEntity : list){
                if(stack.isEmpty()) break;
                StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.SlashDim);
                blade.attackTargetEntity(stack, curEntity, player, true);
            }

            if(!target.world.isRemote){
                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                float magicDamage = 0.5f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if(dim != null){
                    dim.setPosition(target.posX,target.posY + target.height / 2.0,target.posZ);
                    dim.setLifeTime(10);
                    dim.setGlowing(true);
                    world.spawnEntity(dim);
                }



                Vec3d pos = target.getPositionVector();
                pos = pos.addVector(0 , -0.5f, 0);

                int rank = StylishRankManager.getStylishRank(player);
                int count = 5 + rank;
                float rotUnit = 360.0f / count;

                float baseRot = (player.getRNG().nextFloat() * 180);

                for(int j = 0; j < 1; j++){
                    for (int i = 0; i < count; i++) {
                        EntityDrive entity = new EntityDrive(world, player, magicDamage);
                        entity.setLifeTime(15);

                        // プレイヤーを中心にぐるっと回るように配置。
                        // 向きは 上から見て右回りになる接線方向。
                        // （輪が広がるような動きになる）
                        // プレイヤー視点で左下から右上に切り上げるように見えるように
                        // y と pitch と roll を調整。

                        // ※ SpiralEdge と、同じ

                        final float rot = rotUnit*i;

                        float yaw = rot + baseRot;
                        float pitch = -30*(float)Math.cos(Math.toRadians(rot - 60));

                /*if (pitch > 0.0f)
                    pitch = 1.0f;
                */

                        double x = Math.cos(Math.toRadians(yaw));		// -sin(yaw - 90)
                        double y = 0.7*Math.sin(Math.toRadians(rot - 60));
                        double z = Math.sin(Math.toRadians(yaw));		//  cos(yaw - 90)

                        entity.setLocationAndAngles(pos.x - x,
                                pos.y - y,
                                pos.z - z,
                                yaw,
                                pitch);
                        entity.setRoll((float)(90.0f - 30.0f*Math.cos(Math.toRadians(rot + 30))));
                        entity.setDriveVector(0.3f);

                        world.spawnEntity(entity);
                    }

                    baseRot += 120.0f;
                }
            }



        }
    }

    private Entity getEntityToWatch(EntityPlayer player){
        World world = player.world;
        Entity target = null;
        for(int dist = 2; dist < 20; dist+=2){
            AxisAlignedBB bb = player.getBoundingBox();
            Vec3d vec = player.getLookVec();
            vec = vec.normalize();
            bb = bb.grow(2.0f, 0.25f, 2.0f);
            bb = bb.offset(vec.x*(float)dist,vec.y*(float)dist,vec.z*(float)dist);

            List<Entity> list = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
            float distance = 30.0f;
            for(Entity curEntity : list){
                float curDist = curEntity.getDistance(player);
                if(curDist < distance)
                {
                    target = curEntity;
                    distance = curDist;
                }
            }
            if(target != null)
                break;
        }
        return target;
    }

    private void spawnParticle(World world, Entity target){
        //target.spawnExplosionParticle();
        world.spawnParticle(Particles.EXPLOSION_LARGE,
                target.posX,
                target.posY + target.height,
                target.posZ,
                3.0, 3.0, 3.0);
        world.spawnParticle(Particles.EXPLOSION_LARGE,
                target.posX + 1.0,
                target.posY + target.height + 1.0,
                target.posZ,
                3.0, 3.0, 3.0);
        world.spawnParticle(Particles.EXPLOSION_LARGE,
                target.posX,
                target.posY + target.height + 0.5,
                target.posZ + 1.0,
                3.0, 3.0, 3.0);
    }

    @Override
    public void doJustSpacialAttack(ItemStack stack, EntityPlayer player) {
        World world = player.world;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        Entity target = null;

        int entityId = ItemSlashBlade.TargetEntityId.get(tag);

        if(entityId != 0){
            Entity tmp = world.getEntityByID(entityId);
            if(tmp != null){
                if(tmp.getDistance(player) < 30.0f)
                    target = tmp;
            }
        }

        if(target == null){
            target = getEntityToWatch(player);
        }

        if(target == null) {
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            //spawnParticle(world, target);

            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            if(!player.world.isRemote) {
                Vec3d pos = player.getLookVec();
                pos = pos.scale(5);
                pos = pos.add(player.getPositionVector());
                pos = pos.addVector(0, player.getEyeHeight(), 0);

                ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

                Vec3d offset = player.getPositionVector().addVector(0,player.getEyeHeight(),0);
                Vec3d offsettedLook = offset.add(player.getLookVec().scale(5));
                RayTraceResult movingobjectposition = world.rayTraceBlocks(offset, offsettedLook);
                if (movingobjectposition != null)
                {
                    IBlockState state = null;
                    BlockPos blockPos = movingobjectposition.getBlockPos();
                    if(blockPos != null)
                        state = world.getBlockState(blockPos);
                    if(state != null && state.getCollisionBoundingBox(world, blockPos) == null)
                        movingobjectposition = null;
                    else {
                        Vec3d tmppos = new Vec3d(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z);
                        if(1 < tmppos.distanceTo(player.getPositionVector())){
                            pos = tmppos;
                        }
                    }
                }

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (0.5f + level / 5.0f);
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if (dim != null) {
                    dim.setPosition(pos.x, pos.y, pos.z);
                    dim.setLifeTime(10);
                    dim.setIsSlashDimension(true);
                    world.spawnEntity(dim);
                }


                pos = pos.addVector(0 , -0.5f, 0);

                int rank = StylishRankManager.getStylishRank(player);
                int count = 5 + rank;
                float rotUnit = 360.0f / count;

                float baseRot = (player.getRNG().nextFloat() * 180);

                for(int j = 0; j < 2; j++){
                    for (int i = 0; i < count; i++) {
                        EntityDrive entity = new EntityDrive(world, player, magicDamage);
                        entity.setLifeTime(15);

                        // プレイヤーを中心にぐるっと回るように配置。
                        // 向きは 上から見て右回りになる接線方向。
                        // （輪が広がるような動きになる）
                        // プレイヤー視点で左下から右上に切り上げるように見えるように
                        // y と pitch と roll を調整。

                        // ※ SpiralEdge と、同じ

                        final float rot = rotUnit*i;

                        float yaw = rot + baseRot;
                        float pitch = -30*(float)Math.cos(Math.toRadians(rot - 60));

                    /*if (pitch > 0.0f)
                        pitch = 1.0f;
                    */

                        double x = Math.cos(Math.toRadians(yaw));		// -sin(yaw - 90)
                        double y = 0.7*Math.sin(Math.toRadians(rot - 60));
                        double z = Math.sin(Math.toRadians(yaw));		//  cos(yaw - 90)

                        entity.setLocationAndAngles(pos.x - x,
                                pos.y - y,
                                pos.z - z,
                                yaw,
                                pitch);
                        entity.setRoll((float)(90.0f - 30.0f*Math.cos(Math.toRadians(rot + 30))));
                        entity.setDriveVector(0.3f);
                        entity.setIsSlashDimension(true);

                        world.spawnEntity(entity);
                    }

                    baseRot += 120.0f;
                }
            }

        }else{
            ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.SlashDim);

            //spawnParticle(world, target);

            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 1.0F);

            final int cost = -20;
            if(!ItemSlashBlade.ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 10, player);
            }

            AxisAlignedBB bb = target.getBoundingBox();
            bb = bb.grow(2.0f, 0.25f, 2.0f);

            List<Entity> list = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());

            if(!EntitySelectorAttackable.getInstance().apply(target))
                list.add(target);

            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            for(Entity curEntity : list){
                if(stack.isEmpty()) break;
                StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.SlashDim);
                blade.attackTargetEntity(stack, curEntity, player, true);
                player.onEnchantmentCritical(curEntity);
            }

            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            float magicDamage = 1.0f + ItemSlashBlade.AttackAmplifier.get(tag) * (level / 5.0f);

            if(!target.world.isRemote){
                EntitySlashDimension dim = new EntitySlashDimension(world, player, magicDamage);
                if(dim != null){
                    dim.setPosition(target.posX,target.posY + target.height / 2.0,target.posZ);
                    dim.setLifeTime(10);
                    dim.setIsSlashDimension(true);
                    world.spawnEntity(dim);
                }
            }


            Vec3d pos = target.getPositionVector();
            pos = pos.addVector(0 , -0.5f, 0);

            int rank = StylishRankManager.getStylishRank(player);
            int count = 5 + rank;
            float rotUnit = 360.0f / count;

            float baseRot = (player.getRNG().nextFloat() * 180);

            for(int j = 0; j < 2; j++){
                for (int i = 0; i < count; i++) {
                    EntityDrive entity = new EntityDrive(world, player, magicDamage);
                    entity.setLifeTime(15);

                    // プレイヤーを中心にぐるっと回るように配置。
                    // 向きは 上から見て右回りになる接線方向。
                    // （輪が広がるような動きになる）
                    // プレイヤー視点で左下から右上に切り上げるように見えるように
                    // y と pitch と roll を調整。

                    // ※ SpiralEdge と、同じ

                    final float rot = rotUnit*i;

                    float yaw = rot + baseRot;
                    float pitch = -30*(float)Math.cos(Math.toRadians(rot - 60));

                    /*if (pitch > 0.0f)
                        pitch = 1.0f;
                    */

                    double x = Math.cos(Math.toRadians(yaw));		// -sin(yaw - 90)
                    double y = 0.7*Math.sin(Math.toRadians(rot - 60));
                    double z = Math.sin(Math.toRadians(yaw));		//  cos(yaw - 90)

                    entity.setLocationAndAngles(pos.x - x,
                            pos.y - y,
                            pos.z - z,
                            yaw,
                            pitch);
                    entity.setRoll((float)(90.0f - 30.0f*Math.cos(Math.toRadians(rot + 30))));
                    entity.setDriveVector(0.3f);
                    entity.setIsSlashDimension(true);

                    world.spawnEntity(entity);
                }

                baseRot += 120.0f;
            }
        }
    }

    @Override
    public void doSuperSpecialAttack(ItemStack stack, EntityPlayer player) {

        EntityJudgmentCutManager entityDA = new EntityJudgmentCutManager(player.world, player);
        if (entityDA != null) {
            ScheduleEntitySpawner.getInstance().offer(entityDA);
            //player.world.spawnEntity(entityDA);
        }
        UntouchableTime.setUntouchableTime(player, 30, true);
    }
}
