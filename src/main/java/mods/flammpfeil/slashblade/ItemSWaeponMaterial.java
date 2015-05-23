package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

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
		default:
			return super.getIconFromDamage(par1);
		}
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

        ItemStack tiny = GameRegistry.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
        for(Enchantment ench : EnchantHelper.rare){
            ItemStack stack = tiny.copy();
            stack.addEnchantment(ench,1);
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

    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {

        if(player.worldObj.isRemote) return false;

        boolean using = false;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        if(stack.getItemDamage() == 2 && ItemSlashBlade.SpecialAttackType.exists(tag)){
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
                    Map<Integer,Integer> bladeEnchMap = EnchantmentHelper.getEnchantments(blade);

                    Map<Integer,Integer> enchMap = EnchantmentHelper.getEnchantments(stack);
                    for(Map.Entry<Integer,Integer> entry : enchMap.entrySet()){
                        Enchantment ench = Enchantment.enchantmentsList[entry.getKey()];

                        int level = 1;
                        if(bladeEnchMap.containsKey(entry.getKey())){
                            int currentLevel = bladeEnchMap.get(entry.getKey());
                            int maxLevel = ench.getMaxLevel();
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
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onCreated(ItemStack p_77622_1_, World p_77622_2_, EntityPlayer p_77622_3_) {
        super.onCreated(p_77622_1_, p_77622_2_, p_77622_3_);

        AchievementList.triggerCraftingAchievement(p_77622_1_, p_77622_3_);
    }
}
