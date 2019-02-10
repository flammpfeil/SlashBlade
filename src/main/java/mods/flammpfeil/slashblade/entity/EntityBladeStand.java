package mods.flammpfeil.slashblade.entity;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

/**
 * Created by Furia on 14/08/15.
 */
public class EntityBladeStand extends Entity {
    public EntityBladeStand(World p_i1582_1_) {
        super(p_i1582_1_);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    public EntityBladeStand(World p_i1582_1_, double x, double y, double z, ItemStack blade) {
        this(p_i1582_1_);
        this.setStandType(-1);
        this.setPositionAndRotation(x,y,z, 180.0f * (this.rand.nextFloat() * 2.0f - 1.0f),this.rotationPitch);
        this.setBlade(blade);
    }

    private static final DataParameter<ItemStack> WatchIndexBlade
            = EntityDataManager.<ItemStack>createKey(EntityBladeStand.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> WatchIndexFlipState
            = EntityDataManager.<Integer>createKey(EntityBladeStand.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WatchIndexStandType
            = EntityDataManager.<Integer>createKey(EntityBladeStand.class, DataSerializers.VARINT);
    
    @Override
    protected void entityInit() {
        //this.getDataManager().register(WatchIndexBlade, SlashBlade.getCustomBlade(SlashBlade.modid,"flammpfeil.slashblade.named.muramasa"));
        this.getDataManager().register(WatchIndexBlade, ItemStack.EMPTY); //ItemStack
        this.getDataManager().register(WatchIndexFlipState, 0);
        this.getDataManager().register(WatchIndexStandType, 0);
    }


    public int getFlip() {
        return this.getDataManager().get(WatchIndexFlipState);
    }
    public void setFlip(int value) {
        if(hasBlade() && getBlade().getItem() instanceof ItemSlashBladeWrapper && !ItemSlashBladeWrapper.hasWrapedItem(getBlade())){
            if(2 <= value)
                value = 0;
        }
        this.getDataManager().set(WatchIndexFlipState,value);
    }
    public void doFlip(){
        setFlip(Math.abs((getFlip() + 1) % 4));
    }

    public enum StandType{
        Dual,
        Single,
        Upright,
        Naked,
        Wall;

        public static StandType getType(int id){
            if(typeMap.containsKey(id))
                return typeMap.get(id);
            else
                return Naked;
        }
        static Map<Integer,StandType> typeMap = Maps.newHashMap();
        static{
            typeMap.put(-1,Naked);  //dropItem
            typeMap.put(0,Dual);    //soul
            typeMap.put(1,Single);  //ingot
            typeMap.put(2,Wall);    //sphere
            typeMap.put(3,Upright); //tiny
        }
    }

    public int getStandType(){
        return this.getDataManager().get(WatchIndexStandType);
    }
    public void setStandType(int value){
        this.getDataManager().set(WatchIndexStandType,value);
    }
    public static StandType getType(EntityBladeStand e){
        return StandType.getType(e.getStandType());
    }

    public ItemStack getBlade(){
        return this.getDataManager().get(WatchIndexBlade);
    }
    public void setBlade(ItemStack blade){
        if(!blade.isEmpty() && blade.getItem() instanceof ItemSlashBladeWrapper && !ItemSlashBladeWrapper.hasWrapedItem(blade)){
            if(2 <= getFlip())
                setFlip(0);
        }

        this.getDataManager().set(WatchIndexBlade, !blade.isEmpty() ? blade : ItemStack.EMPTY);
        this.getDataManager().setDirty(WatchIndexBlade);
    }
    public boolean hasBlade(){
        return !getBlade().isEmpty();
    }


    static final String SaveKeyBlade = "Blade";
    static final String SaveKeyStandType = "StandType";
    static final String SaveKeyFlip = "Flip";
    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {

        if(p_70037_1_.hasKey(SaveKeyStandType)){
            int type = p_70037_1_.getInteger(SaveKeyStandType);
            this.setStandType(type);
        }

        if(p_70037_1_.hasKey(SaveKeyBlade)){
            NBTTagCompound tag = p_70037_1_.getCompoundTag(SaveKeyBlade);
            ItemStack blade = new ItemStack(tag);

            this.setBlade(blade);
        }

        if(p_70037_1_.hasKey(SaveKeyFlip)){
            int flip = p_70037_1_.getInteger(SaveKeyFlip);
            this.setFlip(flip);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

        ItemStack blade = getBlade();
        if(!blade.isEmpty()){
            NBTTagCompound tag = new NBTTagCompound();
            blade.writeToNBT(tag);

            p_70014_1_.setTag(SaveKeyBlade,tag);
        }

        {
            int type = this.getStandType();
            p_70014_1_.setInteger(SaveKeyStandType,type);
        }

        {
            int flip = this.getFlip();
            p_70014_1_.setInteger(SaveKeyFlip,flip);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if(SlashBladeHooks.onBladeStandAttack(this, p_70097_1_, p_70097_2_)){
            return false;
        }
        return super.attackEntityFrom(p_70097_1_, p_70097_2_);
    }

    /*
    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        super.setPositionAndRotation2(x, y, z, yaw, pitch, posRotationIncrements, p_180426_10_);
    }
    */

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    @Override
    public void onUpdate() {

        /*
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        */

        super.onUpdate();
        ReflectionAccessHelper.setVelocity(this,0, this.motionY,0);

        if(SlashBladeHooks.onEntityBladeStandUpdateHooks(this)){
            return;
        }

        if(getType(this) == StandType.Wall) {
            this.motionY = 0.0;
        }else if(hasBlade()){
            if (this.posY > 0.0D)
            {
                this.motionY = -0.1D;
            }
            else if(this.posY < -0.5){
                this.motionY = 1.0f;
            }
            else
            {
                this.motionY = 0.0;
            }
        }else{
            this.motionY = -0.1D;
        }

        noClip = false;
        BlockPos pos = new BlockPos(this.posX,this.posY,this.posZ);
        if(!this.world.isAirBlock(pos)
                && this.world.getBlockState(pos).getBlockHardness(this.world, pos) < 0){

            ReflectionAccessHelper.setVelocity(this,0,0,0);
            this.setPositionAndUpdate(this.posX,this.posY+1.5,this.posZ);
        }

        this.move(MoverType.SELF,  this.motionX, this.motionY, this.motionZ);

        if(!hasBlade() && posY < -10){
            this.setDead();
        }

        if(getType(this) == StandType.Naked && !this.hasBlade() && 200 < this.ticksExisted){
            this.setDead();
        }
    }

    public boolean setStandBlade(Entity e){

        if(e instanceof EntityPlayer && !e.world.isRemote){

            EntityPlayer p = (EntityPlayer)e;

            ItemStack stack = p.getHeldItem(EnumHand.MAIN_HAND);
            if(stack.isEmpty() && this.hasBlade()){

                ItemStack blade = this.getBlade();

                NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);
                if(bladeTag.hasUniqueId("Owner")){
                    boolean hasPerm = bladeTag.getUniqueId("Owner").equals(p.getUniqueID());

                    if(!hasPerm) {
                        if(p.getServer() != null) {
                            UserListOpsEntry userlistopsentry = (UserListOpsEntry) p.getServer().getPlayerList().getOppedPlayers().getEntry(p.getGameProfile());
                            if (userlistopsentry != null ? userlistopsentry.getPermissionLevel() >= 2 : p.getServer().isSinglePlayer()) {
                               hasPerm = true;
                            }
                        }
                    }

                    if(!hasPerm) {
                        p.sendMessage(new TextComponentTranslation("flammpfeil.swaepon.info.notowner.msg"));
                        return false;
                    }
                }

                if(SpecialEffects.isEffective(p,blade, SpecialEffects.Limitter) == SpecialEffects.State.NonEffective
                        && !p.isCreative()){
                    int level = ItemSlashBlade.getSpecialEffect(blade).getInteger(SpecialEffects.Limitter.getEffectKey());
                    p.sendMessage(new TextComponentTranslation("flammpfeil.swaepon.info.needlevel.msg",Integer.toString(level)));
                    return false;
                }

                //todo:advancement
                //AchievementList.triggerCraftingAchievement(this.getBlade(), p);

                p.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, this.getBlade());
                this.setBlade(ItemStack.EMPTY);

                if(getType(this) == StandType.Naked)
                    this.setDead();

                return true;

            }else if(!stack.isEmpty()
                    && stack.getItem() instanceof ItemSlashBlade
                    && !this.hasBlade()){

                this.setBlade(stack);

                p.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);

                return true;
            }
        }

        return false;
    }


    @Override
    public boolean hitByEntity(Entity p_85031_1_) {

        if(setStandBlade(p_85031_1_))
            return true;

        if(!this.hasBlade()){
            if(p_85031_1_.isSneaking()){

                if(!p_85031_1_.world.isRemote)
                    this.setDead();

                return true;
            }
        }

        return super.hitByEntity(p_85031_1_);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {

        if(hand == EnumHand.MAIN_HAND){

            if(player.isSneaking()){
                doFlip();
                return true;
            }

            if(setStandBlade(player))
                return true;
        }

        return super.processInitialInteract(player, hand);
    }

    @Override
    protected boolean canTriggerWalking() {
        return true;
    }
    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    public Random getRand(){
        return this.rand;
    }


    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public void onKillCommand() {
        if(hasBlade() && this.posY < 0 ){
            this.posY = 0;
        }else
            super.onKillCommand();
    }
}
