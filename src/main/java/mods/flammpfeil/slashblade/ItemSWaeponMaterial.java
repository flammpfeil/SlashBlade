package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntityGrimGripKey;
import mods.flammpfeil.slashblade.named.NamedBladeManager;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.BlockPos;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class ItemSWaeponMaterial extends Item {

    public ItemSWaeponMaterial() {
        setHasSubtypes(true);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {

        if(	par1ItemStack.getItem() == SlashBlade.proudSoul){
            return true;
        }
        return super.hasEffect(par1ItemStack, pass);
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
        case 5:
            s += ".trapezohedron";
            break;
		}
		return s;
	}

    IIcon tiny;

    @Override
    public IIcon getIconFromDamage(int par1) {
        switch(par1){
            case 1:
                return Items.iron_ingot.getIconFromDamage(0);
            case 2:
                return Items.snowball.getIconFromDamage(0);
            case 3:
                return tiny;
            case 4:
                return Items.nether_star.getIconFromDamage(0);
            default:
                return super.getIconFromDamage(par1);
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_) {
        if(p_82790_1_.getItemDamage() == 4)
            return 0xCCC0FF;
        return super.getColorFromItemStack(p_82790_1_, p_82790_2_);
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        tiny = par1IconRegister.registerIcon("flammpfeil.slashblade:tinyps");
    }

    @Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.CrystalBladeSoulStr, 1));
        par3List.add(GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

        ItemStack sphere = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
        for(int saType : ItemSlashBlade.specialAttacks.keySet()){
            ItemStack stack = sphere.copy();
            NBTTagCompound sphereTag = ItemSlashBlade.getItemTagCompound(stack);
            ItemSlashBlade.SpecialAttackType.set(sphereTag, saType);
            par3List.add(stack);
        }

        ItemStack tiny = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int dir, float fx, float fy, float yz) {
        Block block = world.getBlock(x, y, z);

        if(!world.isRemote && Blocks.fence == block && player.isSneaking()){

            world.setBlockToAir(x,y,z);
            EntityBladeStand e = new EntityBladeStand(world);
            e.setStandType(stack.getItemDamage());
            e.setPositionAndRotation(x + 0.5 ,y + 0.5 ,z + 0.5,Math.round(player.rotationYaw / 45.0f) * 45.0f + 180.0f,e.rotationPitch);
            world.spawnEntityInWorld(e);

            AchievementList.triggerAchievement(player,"bladeStand");

            return true;
        }else if(stack.getItemDamage() == 4 //crystal
                && stack.hasTagCompound() && stack.getTagCompound().hasKey("GPX")
                && Blocks.quartz_block == block
                && player.isSneaking()) {
            //world.setBlockToAir(pos);
            //e.setLifeTime(1000);

            NBTTagCompound tag = stack.getTagCompound();
            int bx = tag.getInteger("GPX");
            int by = tag.getInteger("GPY");
            int bz = tag.getInteger("GPZ");

            BlockPos gripPos = new BlockPos(bx,by,bz);
            BlockPos pos = new BlockPos(x,y,z);
            if(30 > gripPos.distanceSq(pos))
                return false;

            //world.setBlockToAir(pos);
            if (!world.isRemote) {
                EntityGrimGripKey e = new EntityGrimGripKey(world);
                ForgeDirection side = ForgeDirection.getOrientation(dir);
                e.setPositionAndRotation(
                        x + 0.5 + side.offsetX,
                        y + 0.5 + side.offsetY,
                        z + 0.5 + side.offsetZ, e.rotationYaw, e.rotationPitch);

                //e.setLifeTime(1000);


                e.setGrimGripPos(gripPos);
                //e.ticksExisted = 0;
                //e.setGlowing(true);
                world.spawnEntityInWorld(e);
            }

            stack.stackSize--;

            return true;
        }else if(stack.getItemDamage() == 4 //crystal
                && !world.isRemote
                && player.isSneaking()){

            ForgeDirection side = ForgeDirection.getOrientation(dir);
            stack.setTagInfo("GPX", new NBTTagInt(x + side.offsetX));
            stack.setTagInfo("GPY", new NBTTagInt(y + side.offsetY));
            stack.setTagInfo("GPZ", new NBTTagInt(z + side.offsetZ));

            return true;
        }else{
            return false;
        }

    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer p_77624_2_, List par3List, boolean p_77624_4_) {
        super.addInformation(par1ItemStack, p_77624_2_, par3List, p_77624_4_);

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(par1ItemStack);

        if(ItemSlashBlade.SpecialAttackType.exists(tag)){
            String key = "flammpfeil.slashblade.specialattack." + SlashBlade.weapon.getSpecialAttack(par1ItemStack).toString();

            par3List.add(String.format("SA:%s",  StatCollector.translateToLocal(key)));
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

        if(player.worldObj.isRemote) return false;

        boolean using = false;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(stack.getItemDamage() == 3 && !stack.isItemEnchanted() && entity instanceof EntityBladeStand) {
            EntityBladeStand stand = (EntityBladeStand)entity;

            if(stand.hasBlade()){
                if(stand.isBurning()) {
                    stand.extinguish();
                    entity.playSound("mob.enderdragon.wings", 0.5F, 0.5F);
                }else {
                    using = true;
                    stand.setFire(20);

                    NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(stand.getBlade());
                    ItemSlashBlade.ProudSoul.add(bladeTag,50);
                    //entity.playSound("fire.fire",  0.5F, 1.0F);
                    entity.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, (int)entity.posX, (int)entity.posY, (int)entity.posZ, 0);
                }
            }
        }

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
                        bladeSoulCrystal = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.CrystalBladeSoulStr,1);


                    bladeSoulCrystal.setTagInfo("BIRTH", new NBTTagLong(stand.worldObj.getTotalWorldTime()));

                    EntityItem entityitem = new EntityItem(stand.worldObj, stand.posX, stand.posY + 2.0, stand.posZ, bladeSoulCrystal);
                    entityitem.delayBeforeCanPickup = 10;
                    //entityitem.setGlowing(true);
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

                        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId,blade);

                        if(0 < level){
                            NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);

                            bladeTag.setBoolean("RangeAttackType",!bladeTag.getBoolean("RangeAttackType"));

                            player.onCriticalHit(stand);

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
                    Map<Integer,Integer> bladeEnchMap = EnchantmentHelper.getEnchantments(blade);

                    Map<Integer,Integer> enchMap = EnchantmentHelper.getEnchantments(stack);
                    for(Map.Entry<Integer,Integer> entry : enchMap.entrySet()){
                        Enchantment ench = Enchantment.enchantmentsList[entry.getKey()];

                        int level = 1;
                        if(bladeEnchMap.containsKey(entry.getKey())){
                            int currentLevel = bladeEnchMap.get(entry.getKey());
                            int maxLevel = ench.getMaxLevel();

                            if(ench.effectId == Enchantment.unbreaking.effectId){
                                maxLevel = 5;
                            }

                            if(currentLevel < maxLevel)
                                level = currentLevel+1;
                            else{
                                level = maxLevel;

                                if(0.7f < rate){
                                    ItemStack bladeSphere = GameRegistry.findItemStack(SlashBlade.modid,SlashBlade.SphereBladeSoulStr,1);

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

            if (stack.stackSize <= 0)
            {
                player.destroyCurrentEquippedItem();
            }
            player.renderBrokenItemStack(stack);

            if (stack.stackSize <= 0)
            {
                player.setCurrentItemOrArmor(0, (ItemStack)null);
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
    public boolean isPotionIngredient(ItemStack p_150892_1_) {
        return p_150892_1_.getItemDamage() == 0 || p_150892_1_.getItemDamage() == 3;
    }

    @Override
    public String getPotionEffect(ItemStack p_150896_1_) {
        switch (p_150896_1_.getItemDamage()){
            case 0:  //soul
                return "+14&13-13";
            default: //tiny
                return "+4";
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {

        ItemStack stack = entityItem.getEntityItem();
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        if(stack.getItemDamage() == 4){
            long current = entityItem.worldObj.getTotalWorldTime();

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
                        entityItem.worldObj.spawnParticle("portal", d0, d1, d2, d3, d4, d5);
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

                    entityItem.playSound("random.levelup",  0.25F, 1.25F);

                    //entityItem.worldObj.playSoundEffect(entityItem.posX, entityItem.posY, entityItem.posZ, "fire.ignite",  0.5F, 1.0F);
                    //entityItem.worldObj.playSound(entityItem.posX, entityItem.posY, entityItem.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.25F, 1.25F);
                    //entityItem.playSound("fire.fire",  0.5F, 1.0F);
                    entityItem.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, (int)entityItem.posX, (int)entityItem.posY, (int)entityItem.posZ, 0);
                    entityItem.playSound("random.levelup",  0.25F, 1.25F);

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
