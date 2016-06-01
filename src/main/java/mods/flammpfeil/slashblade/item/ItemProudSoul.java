package mods.flammpfeil.slashblade.item;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntityGrimGrip;
import mods.flammpfeil.slashblade.entity.EntityGrimGripKey;
import mods.flammpfeil.slashblade.named.NamedBladeManager;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ItemProudSoul extends Item {

	public ItemProudSoul() {
        setHasSubtypes(true);
	}

    @Override
	public boolean hasEffect(ItemStack par1ItemStack) {

        if(0x10000 <= par1ItemStack.getItemDamage()){
            return 0x20000 < par1ItemStack.getItemDamage();
        }

		if(	par1ItemStack.getItem() == SlashBlade.proudSoul){
			return true;
		}
		return super.hasEffect(par1ItemStack);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		String s = super.getUnlocalizedName(par1ItemStack);
		switch(par1ItemStack.getItemDamage()){
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
		}
		return s;
	}

    @Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		par3List.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
        par3List.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
        par3List.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1));
        par3List.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.CrystalBladeSoulStr, 1));

        ItemStack sphere = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
        for(int saType : ItemSlashBlade.specialAttacks.keySet()){
            ItemStack stack = sphere.copy();
            NBTTagCompound sphereTag = ItemSlashBlade.getItemTagCompound(stack);
            ItemSlashBlade.SpecialAttackType.set(sphereTag, saType);
            par3List.add(stack);
        }

        ItemStack tiny = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
        for(Enchantment ench : EnchantHelper.rare){
            ItemStack stack = tiny.copy();
            stack.addEnchantment(ench, 1);
            par3List.add(stack);
        }

        for(ItemStack stack : NamedBladeManager.namedbladeSouls){
            par3List.add(stack);
        }
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();

        if(!world.isRemote && Blocks.OAK_FENCE == block && player.isSneaking()){

            world.setBlockToAir(pos);
            EntityBladeStand e = new EntityBladeStand(world);
            e.setStandType(stack.getItemDamage());
            e.setPositionAndRotation(pos.getX() + 0.5 ,pos.getY() + 0.5 ,pos.getZ() + 0.5,Math.round(player.rotationYaw / 45.0f) * 45.0f + 180.0f,e.rotationPitch);
            world.spawnEntityInWorld(e);

            AchievementList.triggerAchievement(player,"bladeStand");

            return EnumActionResult.SUCCESS;
        }else if(stack.getItemDamage() == 4 //crystal
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
                world.spawnEntityInWorld(e);
            }

            stack.stackSize--;

            return EnumActionResult.SUCCESS;
        }else if(stack.getItemDamage() == 4 //crystal
                && !world.isRemote
                && player.isSneaking()){

            stack.setTagInfo("GPX", new NBTTagInt(pos.getX() + side.getFrontOffsetX()));
            stack.setTagInfo("GPY", new NBTTagInt(pos.getY() + side.getFrontOffsetY()));
            stack.setTagInfo("GPZ", new NBTTagInt(pos.getZ() + side.getFrontOffsetZ()));

            return EnumActionResult.SUCCESS;
        }else{
            return super.onItemUse(stack, player, world, pos, hand, side, hitX, hitY, hitZ);
        }

    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer p_77624_2_, List par3List, boolean p_77624_4_) {
        super.addInformation(par1ItemStack, p_77624_2_, par3List, p_77624_4_);

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(par1ItemStack);

        if(ItemSlashBlade.SpecialAttackType.exists(tag)){
            String key = "flammpfeil.slashblade.specialattack." + SlashBlade.weapon.getSpecialAttack(par1ItemStack).toString();

            par3List.add(String.format("SA:%s",  I18n.translateToLocal(key)));
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

        if(stack.getItemDamage() == 3 && !stack.isItemEnchanted() && entity instanceof EntityBladeStand) {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.isBurning()) {
                stand.extinguish();
            }else {
                stand.setFire(10);
                player.worldObj.playSound(null, stand.posX, stand.posY, stand.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }

        if(player.worldObj.isRemote) return false;

        boolean using = false;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(stack.getItemDamage() == 0 && entity instanceof EntityBladeStand) {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade() && stand.isBurning()){

                ItemStack blade = stand.getBlade();

                NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);

                if(ItemSlashBlade.ProudSoul.tryAdd(bladeTag, -400, false)){
                    player.onEnchantmentCritical(stand);

                    using = true;

                    boolean isLottery = false;
                    if(stack.isItemEnchanted()){
                        isLottery = true;
                    }

                    ItemStack bladeSoulCrystal = null;

                    if(isLottery) {
                        if(stand.getType(stand) == EntityBladeStand.StandType.Dual){
                            int lotNum = stand.getEntityData().getInteger("LastLotNumber");
                            lotNum++;
                            lotNum %= NamedBladeManager.namedbladeSouls.size();
                            stand.getEntityData().setInteger("LastLotNumber",lotNum);

                            bladeSoulCrystal = NamedBladeManager.getNamedSoulSequential(lotNum);
                        }else{
                            bladeSoulCrystal = NamedBladeManager.getNamedSoul(Item.itemRand);
                        }
                    }else
                        bladeSoulCrystal = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);


                    bladeSoulCrystal.setTagInfo("BIRTH", new NBTTagLong(stand.worldObj.getTotalWorldTime()));

                    EntityItem entityitem = new EntityItem(stand.worldObj, stand.posX, stand.posY + 2.0, stand.posZ, bladeSoulCrystal);
                    entityitem.setDefaultPickupDelay();
                    entityitem.setGlowing(true);
                    stand.worldObj.spawnEntityInWorld(entityitem);
                }

            }
        }

        if(!using && stack.getItemDamage() == 2){
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

                            using = true;
                        }
                    }
                }
            }
        }

        if (!using && stack.isItemEnchanted() && entity instanceof EntityBladeStand && !entity.isBurning())
        {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade()){
                using = true;

                int damage = stack.getItemDamage();
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
            stack.stackSize--;

            player.renderBrokenItemStack(stack);

            if (stack.stackSize <= 0)
            {
                player.setHeldItem(EnumHand.MAIN_HAND, (ItemStack)null);
            }


            return true;
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onCreated(ItemStack p_77622_1_, World p_77622_2_, EntityPlayer p_77622_3_) {
        super.onCreated(p_77622_1_, p_77622_2_, p_77622_3_);

        AchievementList.triggerCraftingAchievement(p_77622_1_, p_77622_3_);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {

        ItemStack stack = entityItem.getEntityItem();
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        if(stack.getItemDamage() == 4){
            long current = entityItem.getEntityWorld().getTotalWorldTime();

            if(entityItem.getEntityData().hasKey("FloatingTimeout")){
                long timeout = entityItem.getEntityData().getLong("FloatingTimeout");

                if(5 < entityItem.ticksExisted && tag.hasKey("BIRTH"))
                    tag.removeTag("BIRTH");

                if(current < timeout){

                    if(entityItem.worldObj.isRemote) {

                        int j = Item.itemRand.nextInt(2) * 2 - 1;
                        int k = Item.itemRand.nextInt(2) * 2 - 1;
                        double d0 = entityItem.posX + 0.25D * (double)j;
                        double d1 = (double)((float)entityItem.posY + Item.itemRand.nextFloat());
                        double d2 = entityItem.posZ + 0.5D + 0.25D * (double)k;
                        double d3 = (double)(Item.itemRand.nextFloat() * (float)j);
                        double d4 = ((double)Item.itemRand.nextFloat() - 0.5D) * 0.125D;
                        double d5 = (double)(Item.itemRand.nextFloat() * (float)k);
                        entityItem.worldObj.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5, new int[0]);
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

                    entityItem.worldObj.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5F, 1.0F);
                    entityItem.worldObj.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.25F, 1.25F);

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
