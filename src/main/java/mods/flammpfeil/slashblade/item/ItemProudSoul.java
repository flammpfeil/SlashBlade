package mods.flammpfeil.slashblade.item;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();

        if(!world.isRemote && Blocks.oak_fence == block && player.isSneaking()){

            world.setBlockToAir(pos);
            EntityBladeStand e = new EntityBladeStand(world);
            e.setStandType(stack.getItemDamage());
            e.setPositionAndRotation(pos.getX() + 0.5 ,pos.getY() + 0.5 ,pos.getZ() + 0.5,Math.round(player.rotationYaw / 45.0f) * 45.0f + 180.0f,e.rotationPitch);
            world.spawnEntityInWorld(e);

            AchievementList.triggerAchievement(player,"bladeStand");

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

    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {

        if(player.worldObj.isRemote) return false;

        boolean using = false;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(stack.getItemDamage() == 2){
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

                        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.power,blade);

                        if(0 < level){
                            NBTTagCompound bladeTag = ItemSlashBlade.getItemTagCompound(blade);

                            bladeTag.setBoolean("RangeAttackType",!bladeTag.getBoolean("RangeAttackType"));

                            using = true;
                        }
                    }
                }
            }
        }

        if (stack.isItemEnchanted() && entity instanceof EntityBladeStand)
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
                }else if(damage == 2){
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

            if (stack.stackSize <= 0)
            {
                player.renderBrokenItemStack(stack);
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onCreated(ItemStack p_77622_1_, World p_77622_2_, EntityPlayer p_77622_3_) {
        super.onCreated(p_77622_1_, p_77622_2_, p_77622_3_);

        AchievementList.triggerCraftingAchievement(p_77622_1_, p_77622_3_);
    }
}
