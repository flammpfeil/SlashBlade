package mods.flammpfeil.slashblade.item;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntityGrimGripKey;
import mods.flammpfeil.slashblade.named.NamedBladeManager;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ItemProudSoul extends Item {

	public ItemProudSoul() {
        setHasSubtypes(true);
	}
	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		return (itemStack.getItem() == this && itemStack.getMetadata() == 0) ? 10000 : 0;
	}
	public enum EnumSoulType implements IStringSerializable{
        NONE    (-1, "none"),
	    SOUL    (0, "soul"),
        TINY    (3, "tiny"),
        INGOT   (1, "ingot"),
        SPHERE  (2, "sphere"),
        CRYSTAL (4, "crystal"),
        TRAPEZOHEDRON   (5, "trapezohedron"),
        STEEL_INGOT     (0x1000 | 1, "steel_ingot"),
        SILVER_INGOT    (0x1000 | 2, "silver_ingot")
	    ;
	    private final int meta;
	    private final String name;

        private static final Map<Integer, EnumSoulType> metamap = new Supplier<Map<Integer, EnumSoulType>>(){
            @Override
            public Map<Integer, EnumSoulType> get() {
                Map<Integer, EnumSoulType> result = Maps.newHashMap();
                for(EnumSoulType type : values()){
                    result.put(type.getMetadata(), type);
                }
                return result;
            }
        }.get();

        private EnumSoulType(int metaIn, String nameIn)
        {
            this.meta = metaIn;
            this.name = nameIn;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public static EnumSoulType byMetadata(int meta)
        {
            if(metamap.containsKey(meta))
                return metamap.get(meta);
			return NONE;
        }
    }

	IBlockState baseState = (new BlockStateContainer(Blocks.STONE, new IProperty[]{SOUL_TYPE})).getBaseState().withProperty(SOUL_TYPE, EnumSoulType.NONE);
    public static final PropertyEnum<EnumSoulType> SOUL_TYPE = PropertyEnum.<EnumSoulType>create("type", EnumSoulType.class);
	public IBlockState getStateFromMeta(int meta){
	    return baseState.withProperty(SOUL_TYPE , EnumSoulType.byMetadata(meta));
    }

    static public final int AchievementIconIdHead = 0x1000;
    static public final int AchievementEffectedIconIdHead = 0x1500;

    @Override
	public boolean hasEffect(ItemStack par1ItemStack) {

        if(AchievementIconIdHead <= par1ItemStack.getMetadata()){
            return AchievementEffectedIconIdHead <= par1ItemStack.getMetadata();
        }

		if(	par1ItemStack.getItem() == SlashBlade.proudSoul){
			return true;
		}
		return super.hasEffect(par1ItemStack);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		String s = super.getUnlocalizedName(par1ItemStack);

		int meta = par1ItemStack.getMetadata();
		EnumSoulType type = EnumSoulType.byMetadata(meta);
		if(type != EnumSoulType.NONE)
            s += "." + type.getName();

        /*
		switch(par1ItemStack.getMetadata()){
		case 1:
			s += ".ingot";
			break;
        case 2:
            s += ".sphere";
            break;
        case 3:
            s += ".tiny";
            break;
        case 4:
            s += ".crystal";
            break;
        case 5:
            s += ".trapezohedron";
            break;
		}
		*/
		return s;
	}

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (!this.isInCreativeTab(tab)) return;
        super.getSubItems(tab, subItems);
		subItems.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		subItems.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
        subItems.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
        subItems.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1));
        subItems.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.CrystalBladeSoulStr, 1));
        subItems.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

        ItemStack sphere = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
        for(int saType : ItemSlashBlade.specialAttacks.keySet()){
            ItemStack stack = sphere.copy();
            NBTTagCompound sphereTag = ItemSlashBlade.getItemTagCompound(stack);
            ItemSlashBlade.SpecialAttackType.set(sphereTag, saType);
            subItems.add(stack);
        }

        ItemStack tiny = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
        for(Enchantment ench : EnchantHelper.rare){
            ItemStack stack = tiny.copy();
            stack.addEnchantment(ench, 1);
            subItems.add(stack);
        }

        for(ItemStack stack : NamedBladeManager.namedbladeSouls.values()){
            subItems.add(stack);
        }
	}

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        Block block = world.getBlockState(pos).getBlock();

        if(!world.isRemote && block instanceof BlockFence && player.isSneaking()){
            world.setBlockToAir(pos);
            EntityBladeStand e = new EntityBladeStand(world);
            e.setStandType(stack.getMetadata());
            e.setPositionAndRotation(pos.getX() + 0.5 ,pos.getY() + 0.5 ,pos.getZ() + 0.5,Math.round(player.rotationYaw / 45.0f) * 45.0f + 180.0f,e.rotationPitch);
            world.spawnEntity(e);
            return EnumActionResult.SUCCESS;
        }else if(stack.getMetadata() == 4 //crystal
                && stack.hasTagCompound() && stack.getTagCompound().hasKey("GPX")
                && Blocks.QUARTZ_BLOCK == block
                && player.isSneaking()) {

            NBTTagCompound tag = stack.getTagCompound();
            int x = tag.getInteger("GPX");
            int y = tag.getInteger("GPY");
            int z = tag.getInteger("GPZ");

            BlockPos gripPos = new BlockPos(x, y, z);

            if (30 > gripPos.distanceSq(pos))
                return EnumActionResult.FAIL;

            //world.setBlockToAir(pos);
            if (!world.isRemote) {
                EntityGrimGripKey e = new EntityGrimGripKey(world);
                e.setPositionAndRotation(
                        pos.getX() + 0.5 + side.getFrontOffsetX(),
                        pos.getY() + 0.5 + side.getFrontOffsetY(),
                        pos.getZ() + 0.5 + side.getFrontOffsetZ(), e.rotationYaw, e.rotationPitch);
                //e.setLifeTime(1000);


                e.setGrimGripPos(gripPos);
                //e.ticksExisted = 0;
                //e.setGlowing(true);
                world.spawnEntity(e);
            }

            stack.shrink(1);

            return EnumActionResult.SUCCESS;
        }else if(stack.getMetadata() == 4 //crystal
                && !world.isRemote
                && player.isSneaking()){

            stack.setTagInfo("GPX", new NBTTagInt(pos.getX() + side.getFrontOffsetX()));
            stack.setTagInfo("GPY", new NBTTagInt(pos.getY() + side.getFrontOffsetY()));
            stack.setTagInfo("GPZ", new NBTTagInt(pos.getZ() + side.getFrontOffsetZ()));

            return EnumActionResult.SUCCESS;
        }else{
            return super.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
        }

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World world, List<String> par3List, ITooltipFlag flagIn) {
        super.addInformation(par1ItemStack, world, par3List, flagIn);

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(par1ItemStack);

        if(ItemSlashBlade.SpecialAttackType.exists(tag)){
            String key = "flammpfeil.slashblade.specialattack." + SlashBlade.weapon.getSpecialAttack(par1ItemStack).toString();

            par3List.add(String.format("SA:%s",  I18n.format(key)));
        }

        NBTTagCompound etag = ItemSlashBlade.getSpecialEffect(par1ItemStack);
        if(0 < etag.getSize()){
            Set<String> tagKeys = etag.getKeySet();

            for(String key : tagKeys){
                int reqiredLevel = etag.getInteger(key);

                par3List.add(
                        I18n.format("slashblade.seffect.name." + key)
                                + reqiredLevel);
            }
        }

        if(tag.hasKey("GPX")){
            par3List.add(String.format("GrimGrip pos x:%d y:%d z:%d",
                    tag.getInteger("GPX"),
                    tag.getInteger("GPY"),
                    tag.getInteger("GPZ")));
        }

    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if(player.world.isRemote) return false;
        boolean using = false;
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        if(stack.getMetadata() == 3 && !stack.isItemEnchanted() && entity instanceof EntityBladeStand) {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade()){
                if(stand.isBurning()) {
                    stand.extinguish();
                    player.world.playSound(null, stand.posX, stand.posY, stand.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.PLAYERS, 0.5F, 0.5F);
                }else {
                    using = true;
                    stand.setFire(200);

                    NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(stand.getBlade());
                    ItemSlashBlade.ProudSoul.add(bladeTag,50);
                    player.world.playSound(null, stand.posX, stand.posY, stand.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5F, 1.0F);
                }
            }
        }

        if(stack.getMetadata() == 5 && !stack.isItemEnchanted() && entity instanceof EntityBladeStand) {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade()){
                NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(stand.getBlade());

                if(!bladeTag.hasUniqueId("Owner")) {
                    using = true;

                    bladeTag.setUniqueId("Owner", player.getUniqueID());
                    player.world.playSound(null, stand.posX, stand.posY, stand.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F);using = true;

                    stand.setFire(200);
                    ItemSlashBlade.ProudSoul.add(bladeTag,500);
                    player.world.playSound(null, stand.posX, stand.posY, stand.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.5F, 0.5F);

                }else if(player.getServer() != null){
                    boolean hasPerm = bladeTag.getUniqueId("Owner").equals(player.getUniqueID());

                    if(!hasPerm) {
                        UserListOpsEntry userlistopsentry = (UserListOpsEntry) player.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
                        if (userlistopsentry != null ? userlistopsentry.getPermissionLevel() >= 2 : player.getServer().isSinglePlayer()) {
                            hasPerm = true;
                        }
                    }

                    if(hasPerm){
                        bladeTag.removeTag("OwnerMost");
                        bladeTag.removeTag("OwnerLeast");
                        stand.extinguish();
                        player.world.playSound(null, stand.posX, stand.posY, stand.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.PLAYERS, 0.5F, 0.5F);
                    }
                }
            }
        }


        if((stack.getMetadata() == 0 || stack.getMetadata() == 4) && entity instanceof EntityBladeStand) {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade() && stand.isBurning()){

                ItemStack blade = stand.getBlade();

                NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);

                if(ItemSlashBlade.ProudSoul.tryAdd(bladeTag, -50, false)){
                    player.onEnchantmentCritical(stand);

                    ItemStack bladeSoulCrystal = ItemStack.EMPTY;

                    using = true;

                    boolean isLottery = false;
                    if(stack.isItemEnchanted() && stack.getMetadata() == 0){
                        isLottery = true;
                    }

                    if(isLottery) {
                        if(EntityBladeStand.getType(stand) == EntityBladeStand.StandType.Dual){
                            int lotNum = stand.getEntityData().getInteger("LastLotNumber");
                            lotNum++;
                            lotNum %= NamedBladeManager.namedbladeSouls.size();
                            stand.getEntityData().setInteger("LastLotNumber",lotNum);

                            bladeSoulCrystal = NamedBladeManager.getNamedSoulSequential(lotNum);
                        }else{
                            bladeSoulCrystal = NamedBladeManager.getNamedSoul(Item.itemRand);
                        }

                        if(bladeSoulCrystal.isEmpty())
                            bladeSoulCrystal = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);
                        /* todo: advancement
                        else
                            AchievementList.triggerAchievement(player, "namedbladeSoul");
                        */
                    }else if(stack.getMetadata() == 0) {
                        bladeSoulCrystal = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.CrystalBladeSoulStr, 1);
                        /* todo: advancement
                        AchievementList.triggerAchievement(player, "soulCrystal");
                        */
                    }else {
                        bladeSoulCrystal = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1);
                        /* todo: advancement
                        AchievementList.triggerAchievement(player, "soulTrapezohedron");
                        */
                    }
                    stand.extinguish();

                    bladeSoulCrystal.setTagInfo("BIRTH", new NBTTagLong(stand.world.getTotalWorldTime()));

                    EntityItem entityitem = new EntityItem(stand.world, stand.posX, stand.posY + 2.0, stand.posZ, bladeSoulCrystal);
                    entityitem.setDefaultPickupDelay();
                    entityitem.setGlowing(true);
                    stand.world.spawnEntity(entityitem);
                }

            }
        }

        if(!using && stack.getMetadata() == 2){
            if(ItemSlashBlade.SpecialAttackType.exists(tag))
            {
                int saType = ItemSlashBlade.SpecialAttackType.get(tag);

                if (entity instanceof EntityBladeStand)
                {
                    EntityBladeStand stand = (EntityBladeStand)entity;

                    if(stand.hasBlade()){
                        using = true;

                        ItemStack blade = stand.getBlade();

                        NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);

                        ItemSlashBlade.SpecialAttackType.set(bladeTag,saType);

                        player.onEnchantmentCritical(stand);
                    }
                }
            }else{
                if (entity instanceof EntityBladeStand)
                {
                    EntityBladeStand stand = (EntityBladeStand)entity;

                    if(stand.hasBlade()){

                        ItemStack blade = stand.getBlade();

                        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER,blade);

                        if(0 < level){
                            NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);

                            bladeTag.setBoolean("RangeAttackType",!bladeTag.getBoolean("RangeAttackType"));
                            /* todo: advancement
                            AchievementList.triggerAchievement(player,"phantomBlade");
                            */

                            using = true;
                        }
                    }
                }
            }
        }

        if(!using && stack.getMetadata() == 4 && entity instanceof EntityBladeStand && !entity.isBurning()){
            EntityBladeStand stand = (EntityBladeStand)entity;
            NBTTagCompound seTag = ItemSlashBlade.getSpecialEffect(stack);
            if(stand.hasBlade()){
                ItemStack blade = stand.getBlade();

                if(0 < seTag.getSize()) //wirte
                {
                    for(String key : seTag.getKeySet()){
                        int level = seTag.getInteger(key);
                        SpecialEffects.addEffect(blade, key, level);
                        player.onEnchantmentCritical(stand);
                        using = true;
                    }
                }else{ //remove
                    int playerLevel = 0;
                    if(player != null){
                        playerLevel = player.experienceLevel;
                    }
                    NBTTagCompound effects = ItemSlashBlade.getSpecialEffect(blade);

                    for(String key : effects.getKeySet()){
                        int level = effects.getInteger(key);
                        if(playerLevel < level) continue; //削除者のLevelが満たない場合解除できない

                        boolean canRemoval = true;
                        boolean canCopy = true;

                        ISpecialEffect effect = SpecialEffects.getEffect(key);
                        if(effect != null && effect instanceof IRemovable){
                            canCopy = ((IRemovable) effect).canCopy(blade);
                            canRemoval = ((IRemovable) effect).canRemoval(blade);
                        }

                        if(canRemoval)
                            effects.removeTag(key);

                        if(canCopy){
                            ItemStack bladeSoulCrystal = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);
                            SpecialEffects.addEffect(bladeSoulCrystal, key , level);

                            bladeSoulCrystal.setTagInfo("BIRTH", new NBTTagLong(stand.world.getTotalWorldTime()));

                            EntityItem entityitem = new EntityItem(stand.world, stand.posX, stand.posY + 2.0, stand.posZ, bladeSoulCrystal);
                            entityitem.setDefaultPickupDelay();
                            entityitem.setGlowing(true);
                            stand.world.spawnEntity(entityitem);
                        }

                        using = canRemoval || canCopy;
                        if(using)
                            break;
                    }
                }
            }
        }

        if (!using && stack.isItemEnchanted() && entity instanceof EntityBladeStand && !entity.isBurning())
        {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade()){
                using = true;

                int damage = stack.getMetadata();
                float rate = 0.0f;
                if(damage == 0){
                    rate = 0.5f;
                }else if(damage == 1){
                    rate = 0.75f;
                }else if(damage == 2 || damage == 4){
                    rate = 1.0f;
                }else{
                    rate = 0.25f;
                }

                if(player.getRNG().nextFloat() < rate){
                    ItemStack blade = stand.getBlade();
                    Map<Enchantment,Integer> bladeEnchMap = EnchantmentHelper.getEnchantments(blade);

                    Map<Enchantment,Integer> enchMap = EnchantmentHelper.getEnchantments(stack);
                    for(Map.Entry<Enchantment,Integer> entry : enchMap.entrySet()){
                        Enchantment ench = entry.getKey();

                        int level = 1;
                        if(bladeEnchMap.containsKey(entry.getKey())){
                            int currentLevel = bladeEnchMap.get(entry.getKey());
                            int maxLevel = ench.getMaxLevel();

                            if(ench.equals(Enchantments.UNBREAKING)){
                                maxLevel = 5;
                            }

                            if(currentLevel < maxLevel)
                                level = currentLevel+1;
                            else{
                                level = maxLevel;

                                if(0.7f < rate){
                                    ItemStack bladeSphere = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1);

                                    NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);
                                    int saType = ItemSlashBlade.SpecialAttackType.get(bladeTag);

                                    NBTTagCompound sphereTag = ItemSlashBlade.getItemTagCompound(bladeSphere);
                                    ItemSlashBlade.SpecialAttackType.set(sphereTag,saType);

                                    stand.entityDropItem(bladeSphere, 0.0F);
                                }
                            }
                        }

                        bladeEnchMap.put(entry.getKey(),level);

                        EnchantmentHelper.setEnchantments(bladeEnchMap,blade);
                    }

                    player.onCriticalHit(stand);
                }
            }
        }

        if(using){
            stack.shrink(1);

            player.renderBrokenItemStack(stack);

            if (stack.getCount() <= 0)
            {
                player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }


            return true;
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onCreated(ItemStack p_77622_1_, World p_77622_2_, EntityPlayer p_77622_3_) {
        super.onCreated(p_77622_1_, p_77622_2_, p_77622_3_);

        /* todo: advancement
        AchievementList.triggerCraftingAchievement(p_77622_1_, p_77622_3_);
        */
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {

        ItemStack stack = entityItem.getItem();
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        if(stack.getMetadata() == 4 || stack.getMetadata() == 5){
            long current = entityItem.getEntityWorld().getTotalWorldTime();

            if(entityItem.getEntityData().hasKey("FloatingTimeout")){
                long timeout = entityItem.getEntityData().getLong("FloatingTimeout");

                if(5 < entityItem.ticksExisted && tag.hasKey("BIRTH"))
                    tag.removeTag("BIRTH");

                if(current < timeout){

                    if(entityItem.world.isRemote) {

                        int j = Item.itemRand.nextInt(2) * 2 - 1;
                        int k = Item.itemRand.nextInt(2) * 2 - 1;
                        double d0 = entityItem.posX + 0.25D * (double)j;
                        double d1 = (double)((float)entityItem.posY + Item.itemRand.nextFloat());
                        double d2 = entityItem.posZ + 0.5D + 0.25D * (double)k;
                        double d3 = (double)(Item.itemRand.nextFloat() * (float)j);
                        double d4 = ((double)Item.itemRand.nextFloat() - 0.5D) * 0.125D;
                        double d5 = (double)(Item.itemRand.nextFloat() * (float)k);
                        entityItem.world.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5, new int[0]);
                    }

                    if(entityItem.getEntityData().hasKey("LPX")) {
                        double x = entityItem.getEntityData().getDouble("LPX");
                        double y = entityItem.getEntityData().getDouble("LPY");
                        double z = entityItem.getEntityData().getDouble("LPZ");
                        entityItem.setPosition(x, y, z);
                    }

                    entityItem.motionX = 0;
                    entityItem.motionY = 0.03999999910593033D;
                    entityItem.motionZ = 0;

                    return false;
                }
            }

            if(tag.hasKey("BIRTH")){
                long birth = tag.getLong("BIRTH");

                if(1000 > Math.abs(current - birth)){

                    entityItem.world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5F, 1.0F);
                    entityItem.world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.25F, 1.25F);

                    entityItem.getEntityData().setLong("FloatingTimeout", current + 1000);

                    entityItem.getEntityData().setDouble("LPX",entityItem.posX);
                    entityItem.getEntityData().setDouble("LPY",entityItem.posY);
                    entityItem.getEntityData().setDouble("LPZ",entityItem.posZ);

                    return true;
                }
            }

        }

        return super.onEntityItemUpdate(entityItem);
    }
}
