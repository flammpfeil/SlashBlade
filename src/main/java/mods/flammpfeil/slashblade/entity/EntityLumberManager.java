package mods.flammpfeil.slashblade.entity;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Furia on 2016/05/30.
 */
public class EntityLumberManager extends Entity {
    public EntityLumberManager(World worldIn) {
        super(worldIn);

        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }
    public EntityLumberManager(World worldIn, Block target) {
        this(worldIn);

        targetBlock = target;
    }

    private static final DataParameter<Integer> LIFETIME = EntityDataManager.<Integer>createKey(EntityLumberManager.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityLumberManager.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    @Override
    protected void entityInit() {
        //lifetime
        this.getDataManager().register(LIFETIME, 20);

        this.getDataManager().register(OWNER, Optional.<UUID>absent());
    }

    public int getLifeTime(){
        return this.getDataManager().get(LIFETIME);
    }
    public void setLifeTime(int lifetime){
        this.getDataManager().set(LIFETIME,lifetime);
    }

    public Optional<EntityPlayer> getOwner(){
        Optional<UUID> owner = this.getDataManager().get(OWNER);

        return owner.isPresent()
                ? Optional.fromNullable(world.getPlayerEntityByUUID(owner.get()))
                : Optional.<EntityPlayer>absent();
    }

    public void setOwner(EntityPlayer owner){
        this.getDataManager().set(OWNER
                , owner != null
                        ? Optional.of(owner.getUniqueID())
                        : Optional.<UUID>absent());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.ticksExisted = compound.getInteger("ticksExisted");
        this.setLifeTime(compound.getInteger("lifetime"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("ticksExisted",this.ticksExisted);
        compound.setInteger("lifetime",this.getLifeTime());
    }

    @Override
    protected void setBeenAttacked() {
        super.setBeenAttacked();

        this.kill();
    }

    Deque<BlockPos> harvestTargets = Queues.newLinkedBlockingDeque(50);
    BlockPos root = null;
    Block targetBlock = null;

    static final List<BlockPos> checkOffsets = Lists.newArrayList(BlockPos.getAllInBox(new BlockPos(1,0,1),new BlockPos(-1,0,-1)));
    static final List<BlockPos> checkOffsetsUpper = Lists.newArrayList(BlockPos.getAllInBox(new BlockPos(1,1,1),new BlockPos(-1,1,-1)));


    private boolean validTarget(BlockPos target) {
        IBlockState state = world.getBlockState(target);
        if(state.getBlock().isAir(state, world, target))
            return false;

        Block block = state.getBlock();
        if(block.isWood(world, target)){
            if(targetBlock != block)
                return false;

            BlockPos under = target.add(EnumFacing.DOWN.getDirectionVec());

            IBlockState underState = world.getBlockState(under);
            boolean canHarvest = false;

            canHarvest |= underState.getBlock().isAir(underState, world, under);
            canHarvest |= underState.getBlock().isLeaves(underState, world, under);
            canHarvest |= underState.getBlock().isWood(world, under);
            if(!canHarvest)
                return false;
        }else if(block.isLeaves(state, world, target)){
            /*
            if(!state.getPropertyNames().contains(BlockLeaves.DECAYABLE)
                    || !((Boolean)state.getValue(BlockLeaves.DECAYABLE)).booleanValue())
                return false;
            */

            BlockHarvestDropsEventHandler.captureDrops(true);

            block.beginLeavesDecay(state, world, target);
            block.updateTick(world, target, state, this.rand);

            List<ItemStack> drops = BlockHarvestDropsEventHandler.captureDrops(false);
            for (ItemStack item : drops) {
                Block.spawnAsEntity(world, root, item);
            }
            if(!state.getBlock().isAir(state, world, target))
                return false;
        }else{
            return false;
        }

        return true;
    }

    public static class BlockHarvestDropsEventHandler{

        static public boolean fastLeavesDecay = false;

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onBlockHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
            IBlockState state = event.getState();
            Block block = state.getBlock();
            World world = event.getWorld();
            BlockPos root = event.getPos();

            if(fastLeavesDecay && block.isLeaves(state, world, root)){
                for(EnumFacing facing : EnumFacing.VALUES){
                    BlockPos pos = root.offset(facing);
                    world.scheduleBlockUpdate(pos, block, block.tickRate(world) + world.rand.nextInt(10) , 1);
                }
            }

            if(captureDrops.get()){
                List<ItemStack> drops = event.getDrops();
                float chance = event.getDropChance();
                for(ItemStack stack : drops)
                    if(world.rand.nextFloat() < chance)
                        capturedDrops.get().add(stack);
                drops.clear();
            }
        }

        protected static ThreadLocal<Boolean> captureDrops = new ThreadLocal<Boolean>()
        {
            @Override protected Boolean initialValue() { return false; }
        };
        protected static ThreadLocal<List<ItemStack>> capturedDrops = new ThreadLocal<List<ItemStack>>()
        {
            @Override protected List<ItemStack> initialValue() { return new java.util.ArrayList<ItemStack>(); }
        };
        public static List<ItemStack> captureDrops(boolean start)
        {
            if (start)
            {
                captureDrops.set(true);
                capturedDrops.get().clear();
                return null;
            }
            else
            {
                captureDrops.set(false);
                return capturedDrops.get();
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(world.isRemote)
            return;

        if(!world.isRemote
                && (getLifeTime() < this.ticksExisted || !getOwner().isPresent())){
            this.kill();
            return;
        }

        if(targetBlock == null) {
            this.kill();
            return;
        }

        if(root == null)
            root = getPosition();

        Optional<EntityPlayer> player = getOwner();
        if (player.isPresent() && player.get() instanceof EntityPlayerMP) {

            EntityPlayerMP playerMp = ((EntityPlayerMP) player.get());

            if(playerMp.getHeldItemMainhand().isEmpty()
                    || !(playerMp.getHeldItemMainhand().getItem() instanceof ItemSlashBlade)){
                this.kill();
                return;
            }

            boolean harvested = false;
            int stackCounter = 0;
            do {
                BlockPos pos = harvestTargets.pollFirst();
                if (pos == null)
                    pos = root;
                harvestTargets.offerLast(pos);
                /**
                 * 取得位置を終端に追加
                 * 周囲を探索 周囲1マス縦２マス
                 * 　ブロックが有り下にブロックがないor葉or木ものがあれば登録
                 * 　    登録できなかった場合途中終了
                 * すべてチェックできた場合、終端から取得位置ブロックを削除
                 * 取得位置のブロックを破壊
                 * 破壊できなかった場合、探索をもう一度
                 */

                PlayerInteractionManager im = playerMp.interactionManager;
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if (block.isWood(world, pos)) {
                    BlockHarvestDropsEventHandler.captureDrops(true);

                    harvested |= im.tryHarvestBlock(pos);

                    List<ItemStack> drops = BlockHarvestDropsEventHandler.captureDrops(false);
                    for (ItemStack item : drops) {
                        Block.spawnAsEntity(world, root, item);
                    }
                }else if(block.isLeaves(state, world, pos)){

                    BlockHarvestDropsEventHandler.captureDrops(true);

                    block.beginLeavesDecay(state, world, pos);
                    block.updateTick(world, pos, state, this.rand);

                    List<ItemStack> drops = BlockHarvestDropsEventHandler.captureDrops(false);
                    harvested |= 0 < drops.size();
                    for (ItemStack item : drops) {
                        Block.spawnAsEntity(world, root, item);
                    }
                }

                boolean isFull = false;
                Iterator<BlockPos> itrOffset = checkOffsets.iterator();
                do {
                    BlockPos currentPos = pos.add(itrOffset.next());

                    if (validTarget(currentPos) && !harvestTargets.contains(currentPos))
                        isFull |= !harvestTargets.offerFirst(currentPos);

                } while (!isFull && itrOffset.hasNext());

                itrOffset = checkOffsetsUpper.iterator();
                do {
                    BlockPos currentPos = pos.add(itrOffset.next());

                    if (validTarget(currentPos) && !harvestTargets.contains(currentPos))
                        isFull |= !harvestTargets.offerFirst(currentPos);

                } while (!isFull && itrOffset.hasNext());

                if (!isFull) {
                    harvestTargets.pollLast();
                }
            } while (!harvested && !harvestTargets.isEmpty() && stackCounter++ < 30);

            if(30 <= stackCounter)
                this.kill();

        }

        if(harvestTargets.isEmpty())
            this.kill();
    }

    @Override
    protected boolean canTriggerWalking() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }
}
