package mods.flammpfeil.slashblade.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.core.CoreProxy;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import mods.flammpfeil.slashblade.network.MessageMoveCommandState;
import mods.flammpfeil.slashblade.network.MessageRangeAttack;
import mods.flammpfeil.slashblade.network.MessageSpecialAction;
import mods.flammpfeil.slashblade.network.NetworkManager;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.ability.*;
import mods.flammpfeil.slashblade.ability.StylishRankManager.*;
import mods.flammpfeil.slashblade.specialattack.*;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import mods.flammpfeil.slashblade.util.InventoryUtility;
import mods.flammpfeil.slashblade.util.SilentUpdateItem;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ItemSlashBlade extends ItemSword {


    private static ResourceLocation texture = new ResourceLocation("flammpfeil.slashblade","model/blade.png");
	public ResourceLocation getModelTexture(){
		return texture;
	}
    static public Map<String,ResourceLocation> textureMap = new HashMap<String, ResourceLocation>();

    static public TagPropertyAccessor.TagPropertyString TextureName = new TagPropertyAccessor.TagPropertyString("TextureName");
    static public ResourceLocation getModelTexture(ItemStack par1ItemStack){
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        if(TextureName.exists(tag)){
            String textureName = TextureName.get(tag);
            ResourceLocation loc;
            if(!textureMap.containsKey(textureName))
            {
                loc = new ResourceLocation("flammpfeil.slashblade","model/" + textureName + ".png");
                textureMap.put(textureName,loc);
            }else{
                loc = textureMap.get(textureName);
            }
            return loc;
        }
        return ((ItemSlashBlade)par1ItemStack.getItem()).getModelTexture();
    }


    private ResourceLocation model =  new ResourceLocation("flammpfeil.slashblade","model/blade.obj");
    public ResourceLocation getModel(){ return model; }
    static public Map<String,ResourceLocation> modelMap = new HashMap<String, ResourceLocation>();

    static public TagPropertyAccessor.TagPropertyString ModelName = new TagPropertyAccessor.TagPropertyString("ModelName");
    static public ResourceLocation getModelLocation(ItemStack par1ItemStack){
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        if(ModelName.exists(tag)){
            String modelName = ModelName.get(tag);
            ResourceLocation loc;
            if(!modelMap.containsKey(modelName))
            {
                loc = new ResourceLocation("flammpfeil.slashblade","model/" + modelName + ".obj");
                modelMap.put(modelName,loc);
            }else{
                loc = modelMap.get(modelName);
            }
            return loc;
        }
        return ((ItemSlashBlade)par1ItemStack.getItem()).getModel();
    }



    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.NONE;
    }

    public static final String adjustXStr = "adjustX";
    public static final String adjustYStr = "adjustY";
    public static final String adjustZStr = "adjustZ";

	public static final String comboSeqStr = "comboSeq";
	public static final String lastPosHashStr = "lastPosHash";
    public static final float RefineBase = 10.0f;

    static public TagPropertyAccessor.TagPropertyLong LastActionTime = new TagPropertyAccessor.TagPropertyLong("lastActionTime");

    static public TagPropertyAccessor.TagPropertyInteger SpecialAttackType = new TagPropertyAccessor.TagPropertyInteger("SpecialAttackType");
    static public TagPropertyAccessor.TagPropertyInteger StandbyRenderType = new TagPropertyAccessor.TagPropertyInteger("StandbyRenderType");
    static public TagPropertyAccessor.TagPropertyInteger TargetEntityId = new TagPropertyAccessor.TagPropertyInteger("TargetEntity");

    static public TagPropertyAccessor.TagPropertyBoolean IsBroken = new TagPropertyAccessor.TagPropertyBoolean("isBroken");
    static public TagPropertyAccessor.TagPropertyBoolean OnClick = new TagPropertyAccessor.TagPropertyBoolean("onClick");
    static public TagPropertyAccessor.TagPropertyBoolean OnJumpAttacked = new TagPropertyAccessor.TagPropertyBoolean("onJumpAttacked");
    static public TagPropertyAccessor.TagPropertyBoolean IsNoScabbard = new TagPropertyAccessor.TagPropertyBoolean("isNoScabbard");
    static public TagPropertyAccessor.TagPropertyBoolean IsSealed = new TagPropertyAccessor.TagPropertyBoolean("isSealed");
    static public TagPropertyAccessor.TagPropertyBoolean IsCharged = new TagPropertyAccessor.TagPropertyBoolean("isCharged");
    static public TagPropertyAccessor.TagPropertyBoolean IsDestructable = new TagPropertyAccessor.TagPropertyBoolean("isDestructable");

    static public TagPropertyAccessor.TagPropertyFloat AttackAmplifier = new TagPropertyAccessor.TagPropertyFloat("AttackAmplifier");
    static public TagPropertyAccessor.TagPropertyFloat BaseAttackModifier = new TagPropertyAccessor.TagPropertyFloat("baseAttackModifier");

    static public TagPropertyAccessor.TagPropertyInteger PrevExp = new TagPropertyAccessor.TagPropertyInteger("prevExp");

    static public TagPropertyAccessor.TagPropertyIntegerWithRange ProudSoul = new TagPropertyAccessor.TagPropertyIntegerWithRange("ProudSoul",0,999999999);
    static public TagPropertyAccessor.TagPropertyIntegerWithRange KillCount = new TagPropertyAccessor.TagPropertyIntegerWithRange("killCount",0,999999999);
    static public TagPropertyAccessor.TagPropertyIntegerWithRange RepairCount = new TagPropertyAccessor.TagPropertyIntegerWithRange("RepairCounter",0,999999999);

    static public TagPropertyAccessor.TagPropertyInteger SummonedSwordColor = new TagPropertyAccessor.TagPropertyInteger("SummonedSwordColor");

	public static int AnvilRepairBonus = 100;

	public static void setComboSequence(NBTTagCompound tag,ComboSequence comboSeq){
		tag.setInteger(comboSeqStr, comboSeq.ordinal());
        if(comboSeq == ComboSequence.None){
            IsCharged.set(tag, false);
        }
	}

	public static ComboSequence getComboSequence(NBTTagCompound tag){
		return ComboSequence.get(tag.getInteger(comboSeqStr));
	}


	private static ArrayList<ComboSequence> Seqs = new ArrayList<ItemSlashBlade.ComboSequence>();
    public enum ComboSequence
	{
    	None(true,0.0f,0.0f,false,0),
    	Saya1(true,200.0f,5.0f,false,20),
    	Saya2(true,-200.0f,5.0f,false,20),
    	Battou(false,240.0f,0.0f,false,12),
    	Noutou(false,-210.0f,10.0f,false,5),
    	Kiriage(false,260.0f,70.0f,false,20),
    	Kiriorosi(false,-260.0f,90.0f,false,12),
    	SlashDim(false,-220.0f,10.0f,true,8),
        Iai(false,240.0f,0.0f,false,20),
        HiraTuki(false,180.0f,180.0f,false,20),
        SlashEdge(false, 240.0f,20.0f,false,12),
        ReturnEdge(false, 250.0f,-160.0f,false,12),
        SIai(false,240.0f,0.0f,false,12),
        SSlashEdge(false, 240.0f,20.0f,false,25),
        SReturnEdge(false, 250.0f,-160.0f,false,25),
        SSlashBlade(false, 200.0f,-315.0f,false,25),

        AerialRave(false, 240.0f, 20.0f,false,25), //startflag
        ASlashEdge(false, 240.0f, 20.0f,false,25),
        AKiriorosi(false, 200.0f, -360.0f+120f,false,25),

        AKiriorosiB(false, 200.0f,-360.0f+80f,false,25), //changeflag
        AKiriage(false, 180f+60f, -360.0f+180f+110f,false,12),
        AKiriorosiFinish(false, 200.0f,-360.0f+90f,false,25),

        RapidSlash(false, 360.0f + 240.0f,-360.0f - 20.0f,false,12),
        RapidSlashEnd(false, 240.0f,20.0f,false,12),
        RisingStar(false, 250.0f,-160.0f,false,12), //rize


        HelmBraker(false, 200.0f,-360.0f+90f,false,25),

        Calibur(false, 360.0f + 240.0f,-360.0f - 20.0f,false,25),

        Force1(false, 300.0f, -360.0f+130f,false,25, None),
        Force2(false, 250.0f, -360.0f + 330.0f,false,25, None),
        Force3(true,200.0f,5.0f,false,20),
        Force4(true,-200.0f,5.0f,false,20),
        Force5(false,240.0f,0.0f,false,12),
        Force6(false, 200.0f,-360.0f+90f,false,25, Force5),

        ;

	    /**
	     * ordinal : コンボ進行ID
	     */

	    /**
	     * 抜刀フラグ trueなら鞘打ち
	     */
	    public boolean useScabbard;

	    /**
	     * 振り幅 マイナスは振り切った状態から逆に振る
	     */
	    public float swingAmplitude;

	    /**
	     * 振る方向 360度
	     */
	    public float swingDirection;

	    /**
	     * チャージエフェクト
	     */
	    public boolean isCharged;

	    public int comboResetTicks;

        public ComboSequence mainHandCombo;

	    /**
	     *
	     * @param useScabbard true:鞘も動く
	     * @param swingAmplitude 振り幅 マイナスは振り切った状態から逆に振る
	     * @param swingDirection 振る角度
	     * @param isCharged チャージエフェクト有無
	     */
        private ComboSequence(boolean useScabbard, float swingAmplitude, float swingDirection, boolean isCharged,int comboResetTicks)
        {
            this(useScabbard, swingAmplitude, swingDirection, isCharged, comboResetTicks, null);
        }

        private ComboSequence(boolean useScabbard, float swingAmplitude, float swingDirection, boolean isCharged,int comboResetTicks, ComboSequence mainHandCombo)
        {
            Seqs.add(this.ordinal(), this);

            this.useScabbard = useScabbard;
            this.swingAmplitude = swingAmplitude;
            this.swingDirection = swingDirection;
            this.isCharged = isCharged;
            this.comboResetTicks = comboResetTicks;

            this.mainHandCombo = mainHandCombo;
        }

	    public static ComboSequence get(int ordinal){
	    	return Seqs.get(ordinal);
	    }
	}

	static public int RequiredChargeTick = 15;
	static public int ComboInterval = 4;

    public void dropItemDestructed(Entity entity, ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);

        if(!entity.worldObj.isRemote){
            int proudSouls = ProudSoul.get(tag);
            int count = 0;
            if(proudSouls > 1000){
                count = (proudSouls - 800) / 100;
                count = Math.min(8,Math.max(0,count));
                proudSouls = proudSouls - count * 100;
            }else{
                count = proudSouls / 100;
                proudSouls = proudSouls - count * 100;
            }
            count++;

            ProudSoul.set(tag, proudSouls);
            entity.entityDropItem(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, count), 0.0F);
            if(entity instanceof EntityPlayer)
                AchievementList.triggerAchievement((EntityPlayer)entity,"proudSoul");

            if(stack.isItemEnchanted() && entity instanceof EntityLivingBase){

                ItemStack tinySoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.TinyBladeSoulStr,1);
                int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
                int lootingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack);

                Random rand = ((EntityLivingBase)entity).getRNG();

                boolean isRare = false;
                if(0 < unbreakingLevel && 0 < lootingLevel){
                    isRare = true;
                }else {
                    for (int loop = 0; loop < unbreakingLevel; loop++) {
                        isRare = rand.nextFloat() < 0.3;
                        if (isRare) break;
                    }
                }

                if(isRare)
                    tinySoul.addEnchantment(EnchantHelper.getEnchantmentRare(rand),1);
                else
                    tinySoul.addEnchantment(EnchantHelper.getEnchantmentNormal(rand),1);

                entity.entityDropItem(tinySoul, 0.0F);

                if(entity instanceof EntityPlayer)
                    AchievementList.triggerAchievement((EntityPlayer)entity,"enchantmentSoul");

                int enchCount = stack.getEnchantmentTagList().tagCount();
                if(5 < enchCount){
                    if(0 < unbreakingLevel){
                        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

                        if(unbreakingLevel == 1)
                            enchantments.remove(Enchantments.UNBREAKING);
                        else
                            enchantments.put(Enchantments.UNBREAKING,unbreakingLevel-1);

                        ItemStack rareTinySoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.TinyBladeSoulStr,1);
                        rareTinySoul.addEnchantment(EnchantHelper.getEnchantmentRare(rand),1);
                        entity.entityDropItem(rareTinySoul, 0.0F);

                        EnchantmentHelper.setEnchantments(enchantments,stack);
                    }else{

                        int targetTag = rand.nextInt(enchCount);

                        NBTTagCompound enchTag = stack.getEnchantmentTagList().getCompoundTagAt(targetTag);
                        enchTag = (NBTTagCompound)enchTag.copy();

                        stack.getEnchantmentTagList().removeTag(targetTag);

                        ItemStack proudSoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.ProudSoulStr,1);

                        if (!proudSoul.hasTagCompound())
                            proudSoul.setTagCompound(new NBTTagCompound());
                        if (!proudSoul.getTagCompound().hasKey("ench", 9))
                            proudSoul.getTagCompound().setTag("ench", new NBTTagList());

                        NBTTagList nbttaglist = proudSoul.getTagCompound().getTagList("ench", 10);
                        nbttaglist.appendTag(enchTag);

                        entity.entityDropItem(proudSoul, 0.0F);
                    }
                }
            }

        }
    }

	public EntityLivingBase setDaunting(EntityLivingBase entity){
		if(!entity.worldObj.isRemote){
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,10,30,true,false));
		}

        StunManager.setStun(entity, 20);

		return entity;
	}

    public static void updateKillCount(ItemStack stack, EntityLivingBase target,EntityLivingBase player){
        NBTTagCompound tag = getItemTagCompound(stack);
        if(!target.isEntityAlive() && target.deathTime == 0){
            int count = KillCount.add(tag, 1);

            incrementProudSoul(stack, target, player);

            SoulEater.entityKilled(stack, target, player);
            DefeatTheBoss.entityKilled(stack, target, player);

            if(player instanceof EntityPlayer){
                switch (count){
                    case 100:
                        AchievementList.triggerAchievement((EntityPlayer)player,"hundredKill");
                        break;
                    case 1000:
                        AchievementList.triggerAchievement((EntityPlayer)player,"thousandKill");
                        break;
                    default:
                }
            }
        }
    }

    public void setArmorDrop(ItemStack stack, EntityLivingBase entity){

        if(!(entity instanceof EntityLiving))
            return;

        if(!stack.isItemEnchanted())
            return;

        int lv = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

        EntityEquipmentSlot[] slots = new EntityEquipmentSlot[]{};

        switch(lv){

            case 0:
                return;
            case 1:
                slots = new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND,EntityEquipmentSlot.OFFHAND};
                break;
            default:
                slots = EntityEquipmentSlot.values();
                break;
        }

        for(EntityEquipmentSlot slot : slots){
            try{
                ((EntityLiving) entity).setDropChance(slot, 0.99f);
            }catch(Exception e){
            }
        }
    }

    public void setImpactEffect(ItemStack stack, EntityLivingBase target,EntityLivingBase user, ComboSequence comboSec){

        if(SlashBladeHooks.onImpactEffectHooks(stack,target,user,comboSec))
            return;

        switch (comboSec) {
            case RisingStar:
                target.onGround = false;
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                target.addVelocity(0.0, 0.6D, 0.0);

                setDaunting(target);
                break;

            case Kiriage:
                target.onGround = false;
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                target.addVelocity(0.0, 0.6D, 0.0);

                setDaunting(target);
                break;

            case Kiriorosi:
            {
                if(0 < target.motionY)
                    target.motionY = 0;

                target.fallDistance += 4;

                    float knockbackFactor = 0.5f;
                    target.addVelocity((double)(-MathHelper.sin(user.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackFactor * 0.5F), -0.2D, (double)(MathHelper.cos(user.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackFactor * 0.5F));


                target.hurtResistantTime = 0;

                break;
            }
            case HiraTuki:
                setDaunting(target);
            case ReturnEdge:
            case Battou:
            {
                float knockbackFactor = 0f;
                if(target instanceof EntityLivingBase)
                    knockbackFactor = EnchantmentHelper.getKnockbackModifier(user);

                if(!(0 < knockbackFactor))
                    knockbackFactor = 1.5f;

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                target.addVelocity(
                        (double) (-MathHelper.sin(user.rotationYaw * (float) Math.PI / 180.0F) * (float) knockbackFactor * 0.5F),
                        0.2D,
                        (double) (MathHelper.cos(user.rotationYaw * (float) Math.PI / 180.0F) * (float) knockbackFactor * 0.5F));

                if(user.onGround)
                    UpthrustBlast.setUpthrustBlastSword(stack,user,target);

                break;
            }
            case Force1:
            case Force2:
            case Force5:
            case Force6:
            case Calibur:
            case RapidSlash:
            case SlashEdge:
            case SIai:
            case SSlashEdge:
            case SReturnEdge:
            case SSlashBlade:
            case Iai:
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

                if(!target.onGround){

                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack);
                    if(0 < level){
                        target.addVelocity(0.0, 0.3D, 0.0);
                    }else{
                        target.addVelocity(0.0, 0.2D, 0.0);
                    }
                }

                setDaunting(target);

                break;


            //AerialRave
            case ASlashEdge:
            case AKiriorosi:
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

            {

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack);
                if(0 < level){
                    target.addVelocity(0.0, 0.3D, 0.0);
                }else{
                    target.addVelocity(0.0, 0.2D, 0.0);
                }
            }

            setDaunting(target);

            break;
            case AKiriage:
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

                target.addVelocity(0.0, 0.7D, 0.0);

                setDaunting(target);

                break;
            case AKiriorosiFinish:
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

                target.fallDistance += 4;

                target.addVelocity(0.0, -0.8D, 0.0);

                target.hurtResistantTime = 0;


                StunManager.removeStun(target);

                break;

                //==================================

            case HelmBraker:
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

                target.fallDistance += 5;

                target.addVelocity(0.0, -1.0D, 0.0);

                target.hurtResistantTime = 0;


                StunManager.removeStun(target);

                break;

            case Force3:
            case Force4:
            case Saya1:
            case Saya2:

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

                setDaunting(target);
                setArmorDrop(stack,target);
                break;

            case SlashDim:


                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;
                /*
                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                if(0 < level){
                    target.motionX = 0;
                    target.motionY = 0;
                    target.motionZ = 0;
                    target.addVelocity(
                            (double) (MathHelper.sin(user.rotationYaw * (float) Math.PI / 180.0F) * (float) level * 0.5F),
                            0.2D,
                            (double) (-MathHelper.cos(user.rotationYaw * (float) Math.PI / 180.0F) * (float) level * 0.5F));
                }*/

                setDaunting(target);
                break;

            default:
                break;
        }
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
	@Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
		NBTTagCompound tag = getItemTagCompound(par1ItemStack);

        updateKillCount(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase);

    	ComboSequence comboSec = getComboSequence(tag);

        setImpactEffect(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase, comboSec);

        if((!comboSec.useScabbard && comboSec.mainHandCombo == null) || IsNoScabbard.get(tag)) {
            ItemSlashBlade.damageItem(par1ItemStack, 1, par3EntityLivingBase);

            if(par1ItemStack.stackSize <= 0) {
                ItemSlashBlade blade = (ItemSlashBlade)par1ItemStack.getItem();

                if(!this.isDestructable(par1ItemStack)){
                    par1ItemStack.stackSize = 1;
                    IsBroken.set(tag,true);

                    if(blade instanceof ItemSlashBladeWrapper){
                        if(!ItemSlashBladeWrapper.TrueItemName.exists(tag)){
                            ((ItemSlashBladeWrapper)blade).removeWrapItem(par1ItemStack);
                        }
                    }

                    if(blade == SlashBlade.bladeWhiteSheath && par3EntityLivingBase instanceof EntityPlayer){
                        AchievementList.triggerAchievement((EntityPlayer) par3EntityLivingBase, "brokenWhiteSheath");
                    }

                    blade.dropItemDestructed(par3EntityLivingBase, par1ItemStack);
                }
            }
        }

        StylishRankManager.doAttack(par3EntityLivingBase);

		return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState blockIn, BlockPos pos, EntityLivingBase entityLiving) {
        if (blockIn.getBlockHardness(worldIn, pos) != 0.0)
            ItemSlashBlade.damageItem(stack, 1, entityLiving);

        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)defaultBaseAttackModifier, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0));
        }

        return multimap;
    }

	public float defaultBaseAttackModifier = 4.0f;

	public ItemSlashBlade(Item.ToolMaterial par2EnumToolMaterial,float defaultBaseAttackModifier) {
		super(par2EnumToolMaterial);
        this.setMaxDamage(50);
        this.defaultBaseAttackModifier = defaultBaseAttackModifier;
	}

    public static NBTTagCompound getItemTagCompound(ItemStack stack){
		NBTTagCompound tag;
		if(stack.hasTagCompound()){
			tag = stack.getTagCompound();
		}else{
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}

		return tag;
	}

    Map<ComboSequence, ComboSequence> AerialRave = createAerialRaveMap();
    static Map<ComboSequence, ComboSequence> createAerialRaveMap(){
        Map<ComboSequence, ComboSequence> result = Maps.newHashMap();

        //result.put(ComboSequence.None, ComboSequence.Iai);
        result.put(ComboSequence.Iai, ComboSequence.Battou);

        result.put(ComboSequence.AerialRave, ComboSequence.ASlashEdge);
        result.put(ComboSequence.ASlashEdge, ComboSequence.AKiriorosi);
        result.put(ComboSequence.AKiriorosi, ComboSequence.Battou);

        result.put(ComboSequence.AKiriorosiB,ComboSequence.AKiriage);
        result.put(ComboSequence.AKiriage,ComboSequence.AKiriorosiFinish);

        return result;
    }

    Map<ComboSequence, ComboSequence> ForceEdgeCombo = createForceEdgeComboMap();
    static Map<ComboSequence, ComboSequence> createForceEdgeComboMap(){
        Map<ComboSequence, ComboSequence> result = Maps.newHashMap();

        //result.put(ComboSequence.None, ComboSequence.Iai);
        result.put(ComboSequence.Force1, ComboSequence.Force2);
        result.put(ComboSequence.Force2, ComboSequence.Force3);
        result.put(ComboSequence.Force3, ComboSequence.Force4);
        result.put(ComboSequence.Force4, ComboSequence.Force5);
        result.put(ComboSequence.Force5,ComboSequence.Force6);

        return result;
    }

	public ComboSequence getNextComboSeq(ItemStack itemStack, ComboSequence current, boolean isRightClick, EntityPlayer player) {
        ComboSequence result = ComboSequence.None;

        EnumSet<SwordType> types = getSwordType(itemStack);
        if (types.contains(SwordType.NoScabbard)) {
            result = ComboSequence.None;
        } else if (!player.onGround) {

            int rank = StylishRankManager.getStylishRank(player);

            int helmBrakerState = MessageMoveCommandState.FORWARD + MessageMoveCommandState.SNEAK;

            long currentTime = player.getEntityWorld().getTotalWorldTime();
            int caliburState = MessageMoveCommandState.FORWARD + MessageMoveCommandState.SNEAK;
            long backKeyLastActiveTime = player.getEntityData().getLong("SB.MCS.B");
            final int TypeAheadBuffer = 7;

            if((currentTime - backKeyLastActiveTime) <= (TypeAheadBuffer)
                    && caliburState == (player.getEntityData().getByte("SB.MCS") & caliburState)
                    && current != ComboSequence.Calibur
                    && !OnJumpAttacked.get(getItemTagCompound(itemStack))) {
                result = ComboSequence.Calibur;
                OnJumpAttacked.set(getItemTagCompound(itemStack),true);

            }else if(helmBrakerState == (player.getEntityData().getByte("SB.MCS") & helmBrakerState)
                    && current != ComboSequence.HelmBraker ){
                result = ComboSequence.HelmBraker;

            }else switch (current) {
                case AKiriorosi:
                {
                    long last = LastActionTime.get(getItemTagCompound(itemStack));
                    long now = player.worldObj.getTotalWorldTime();

                    if (7 < (now - last))
                        current = ComboSequence.AKiriorosiB;

                    result = AerialRave.get(current);

                    break;
                }
                default:
                    result = AerialRave.get(current);
            }

            if(result == null){
                if (isRightClick)
                    result = AerialRave.get(ComboSequence.AerialRave);
                else
                    result = ComboSequence.Iai;
            }

            /*
            switch (current) {
                case Iai:
                    result = ComboSequence.Battou;
                    break;

                default:
                    result = ComboSequence.Iai;
                    break;
            }*/

        } else if (isRightClick) {

            int upperSlashState = MessageMoveCommandState.BACK + MessageMoveCommandState.SNEAK;

            int rapidSlashState = MessageMoveCommandState.FORWARD + MessageMoveCommandState.SNEAK;
            if(rapidSlashState == (player.getEntityData().getByte("SB.MCS") & rapidSlashState)
                    && current != ComboSequence.RapidSlash && current != ComboSequence.RapidSlashEnd){
                result = ComboSequence.RapidSlash;

            }else if(upperSlashState == (player.getEntityData().getByte("SB.MCS") & upperSlashState)
                    && current != ComboSequence.Kiriage){
                result = ComboSequence.Kiriage;

            }else switch (current) {
                case RapidSlash:
                    result = ComboSequence.RapidSlashEnd;
                    break;

                case Saya1:
                    result = ComboSequence.Saya2;
                    break;

                case Saya2:
                    int rank = StylishRankManager.getStylishRank(player);
                    long last = LastActionTime.get(getItemTagCompound(itemStack));
                    long now = player.worldObj.getTotalWorldTime();
                    if (rank < 5 || (ComboSequence.Saya2.comboResetTicks * 0.4) < (now - last)) {
                        result = ComboSequence.Battou;
                    } else {
                        result = ComboSequence.SIai;
                    }
                    break;

                case Kiriage:
                    if(!( 0 < (player.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK)))
                        result = ComboSequence.Kiriorosi;
                    else
                        result = ComboSequence.Saya1;
                    break;

                case SIai:
                    result = ComboSequence.SSlashEdge;
                    break;

                case SSlashEdge:
                    result = ComboSequence.SReturnEdge;
                    break;

                case SReturnEdge:
                    result = ComboSequence.SSlashBlade;
                    break;

                default: {
                    ItemStack offHand = player.getHeldItemOffhand();
                    boolean hasOffhandSword = offHand != null && offHand.getItem() instanceof ItemSlashBlade;
                    if(ForceEdgeCombo.containsKey(current) && hasOffhandSword) {
                        result = ForceEdgeCombo.get(current);
                    }else {
                        if (hasOffhandSword) {
                            result = ComboSequence.Force1;
                        } else {
                            result = ComboSequence.Saya1;
                        }
                    }

                    break;
                }
            }
        } else {
            switch (current) {

                case Kiriage:
                    result = ComboSequence.Kiriorosi;
                    break;

                default:
                    result = ComboSequence.Kiriage;
                    break;
            }
        }

        return result;
    }

	public void setPlayerEffect(ItemStack itemStack, ComboSequence current, EntityPlayer player){

		EnumSet<SwordType> swordType = getSwordType(itemStack);

		NBTTagCompound tag = getItemTagCompound(itemStack);

		switch (current) {
            case RapidSlash: {
                double playerDist = 2.5;

                if(!player.onGround)
                    playerDist *= 0.35f;
                player.motionX = -Math.sin(Math.toRadians(player.rotationYaw)) * playerDist;
                player.motionZ =  Math.cos(Math.toRadians(player.rotationYaw)) * playerDist;

                UntouchableTime.setUntouchableTime(player, 6, false);

                if(!player.worldObj.isRemote){
                    EntityRapidSlashManager mgr = new EntityRapidSlashManager(player.worldObj,player,false);
                    if(mgr != null){
                        mgr.setLifeTime(6);

                        player.worldObj.spawnEntityInWorld(mgr);
                    }
                }
                break;
            }
            case HelmBraker: {
                player.fallDistance = 0;

                UntouchableTime.setUntouchableTime(player, 6, false);

                if(!player.worldObj.isRemote) {
                    EntityHelmBrakerManager mgr = new EntityHelmBrakerManager(player.worldObj, player, false);
                    if (mgr != null) {
                        mgr.setLifeTime(20);

                        player.worldObj.spawnEntityInWorld(mgr);
                    }
                }
                break;
            }
            case Calibur: {
                player.fallDistance = 0;

                double playerDist = 2.5;

                /*
                if(!player.onGround)
                    playerDist *= 0.35f;
                */
                player.motionX = -Math.sin(Math.toRadians(player.rotationYaw)) * playerDist;
                player.motionZ =  Math.cos(Math.toRadians(player.rotationYaw)) * playerDist;
                player.motionY =  0;

                UntouchableTime.setUntouchableTime(player, 6, false);

                if(player.worldObj.isRemote) {
                    NetworkManager.INSTANCE.sendToServer(new MessageSpecialAction((byte) 5));
                }
                break;
            }
            case ASlashEdge:
            case AKiriorosi:
                player.fallDistance = 0;

                if(!OnJumpAttacked.get(tag)){
                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, itemStack);
                    if(level == 0){
                        player.motionY = 0;
                        player.addVelocity(0.0, 0.3D,0.0);
                    }
                }

                break;

            case AKiriage: {
                player.fallDistance = 0;
                player.motionY = 0;
                player.addVelocity(0.0, 0.7D, 0.0);

                /*
                if(!player.worldObj.isRemote){
                    int currentTime = (int)player.getEntityWorld().getWorldTime();
                    final int holdLimit = 8;

                    if(player.getEntityData().hasKey("SB.SPHOLDID")){
                        if(currentTime < (player.getEntityData().getInteger("SB.SPHOLDID") + holdLimit)){
                            player.getEntityData().removeTag("SB.SPHOLDID");
                            return;
                        }
                    }


                    //if (!ProudSoul.tryAdd(tag, -10, false)) return;

                    //player.worldObj.playSound((EntityPlayer) null, player.prevPosX, player.prevPosY, player.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.7F, 1.0F);

                    int count = 6;

                    //if (rank < 3)
                    //    level = Math.min(1, level);


                    float magicDamage = 1;

                    float arc = 360.0f / count;

                    player.getEntityData().setInteger("SB.SPHOLDID", currentTime);

                    for (int i = 0; i < count; i++) {

                        float offset = i * arc;

                        EntitySpiralSwords summonedSword = new EntitySpiralSwords(player.worldObj, player, magicDamage, 0, offset);
                        if (summonedSword != null) {
                            summonedSword.setHoldId(currentTime);
                            summonedSword.setInterval(holdLimit+5);
                            summonedSword.setLifeTime(holdLimit);
                            summonedSword.setRotTicks(12);

                            summonedSword.setRotPitch(110.0f);
                            summonedSword.setRotYaw(80.0f);

                            if (SummonedSwordColor.exists(tag))
                                summonedSword.setColor(SummonedSwordColor.get(tag));

                            ScheduleEntitySpawner.getInstance().offer(summonedSword);
                            //w.spawnEntityInWorld(entityDrive);

                        }
                    }
                }
                */

                break;
            }
            case AKiriorosiFinish:
                player.fallDistance = 0;

                player.motionY = 0;
                player.addVelocity(0.0, 0.1D,0.0);

                break;

        case Iai:
            if (!player.onGround){
                player.fallDistance = 0;

                if(!OnJumpAttacked.get(tag)){
                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, itemStack);
                    if(level == 0){
                        player.motionY = 0;
                        player.addVelocity(0.0, 0.3D,0.0);
                    }
                }
            }
			break;

		case Battou:

			if (!player.onGround){
                player.fallDistance = 0;

				if(!OnJumpAttacked.get(tag)){
                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, itemStack);
                    if(level == 0){
                        player.motionY = 0;
                        player.addVelocity(0.0, 0.2D,0.0);
                    }

                    OnJumpAttacked.set(tag,true);
				}
			}

			if(swordType.containsAll(SwordType.BewitchedPerfect)){
				Random rand =  player.getRNG();
				for(int spread = 0 ; spread < 12 ;spread ++){
					float xSp = rand.nextFloat() * 2 - 1.0f;
					float zSp = rand.nextFloat() * 2 - 1.0f;
					xSp += 0.2 * Math.signum(xSp);
					zSp += 0.2 * Math.signum(zSp);
					player.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE,
							player.posX + 3.0f*xSp,
							player.posY + 0.5f,
							player.posZ + 3.0f*zSp,
		            		1.0, 1.0, 1.0);
				}
			}

			break;
		default:

			break;
		}

        if(!current.useScabbard){
            if(IsCharged.get(tag)){
                IsCharged.set(tag,false);

                int rank = StylishRankManager.getStylishRank(player);

                if(4 <= rank
                    && !IsBroken.get(tag)
                    && swordType.contains(SwordType.Bewitched)
                    && player instanceof EntityPlayer){
                    doAddAttack(itemStack,player,current);
                }
            }

            switch(current) {
                case Force5: {
                    int rank = StylishRankManager.getStylishRank(player);
                    if(rank <= 4)
                        break;
                }
                case Force6:
                case SSlashBlade:
                    doSlashBladeAttack(itemStack, player, current);
                    break;

                default:
                    break;
            }

            {
                double d0 = (double)(-MathHelper.sin(player.rotationYaw * 0.017453292F));
                double d1 = (double)MathHelper.cos(player.rotationYaw * 0.017453292F);

                player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                /*
                if (player.worldObj instanceof WorldServer)
                {
                    ((WorldServer)player.worldObj).spawnParticle(EnumParticleTypes.SWEEP_ATTACK
                            , player.posX + d0, player.posY + (double)player.height * 0.5D, player.posZ + d1
                            , 0
                            , d0, 0.0D, d1
                            , 0.5D, new int[0]);
                }
                */
            }
        }
	}

    @Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {

		NBTTagCompound tag = getItemTagCompound(stack);

		if(!OnClick.get(tag) ){ // onClick中は rightClickなので無視
	        if (entity.canBeAttackedWithItem()){
	            if (!entity.hitByEntity(player) || entity instanceof EntityLivingBase){

	            	//左クリック攻撃は無敵時間を考慮する コンボインターバルが入っている
	            	if(entity instanceof EntityLivingBase
	            			&& ((EntityLivingBase)entity).maxHurtTime != 0 && ((ComboInterval + 2) > ((EntityLivingBase)entity).maxHurtTime - ((EntityLivingBase)entity).hurtTime))
	            	{
	            		//腕振りしない
	            		player.swingProgressInt = 0;
	            		player.swingProgress = 0.0f;
	            		player.isSwingInProgress = false;
	            		return true;
	            	}

		        	ComboSequence comboSec = getComboSequence(tag);

		        	comboSec = getNextComboSeq(stack, comboSec, false, player);
                    setPlayerEffect(stack,comboSec,player);
		        	setComboSequence(tag, comboSec);

                    LastActionTime.set(tag, player.worldObj.getTotalWorldTime());

                    updateStyleAttackType(stack, player);
	            }
	        }
		}
		//無敵時間無視
		entity.hurtResistantTime = 0;


		return false;
	}

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {

        if(hand == EnumHand.OFF_HAND){
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        }

        SlashBlade.abilityJustGuard.setJustGuardState(playerIn);

        /*
        if(!par3EntityPlayer.isUsingItem()){
            NBTTagCompound tag = getItemTagCompound(sitem);
            if(!JustGuard.atJustGuard(par3EntityPlayer)){
                //OnClick.set(tag, true);
                //par3EntityPlayer.motionY = 0.0;
            }
        }
        */

        //sitem.setItemDamage(1320);
        NBTTagCompound tag = getItemTagCompound(itemStackIn);
        long prevAttackTime = LastActionTime.get(tag);
        long currentTime = playerIn.worldObj.getTotalWorldTime();
        ComboSequence comboSeq = getComboSequence(tag);
        //if(prevAttackTime + ComboInterval < currentTime)
        {
            nextAttackSequence(itemStackIn, comboSeq, playerIn);

            SilentUpdateItem.silentUpdateItem(playerIn, hand);
        }

        playerIn.setActiveHand(hand);

        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    public void nextAttackSequence(ItemStack stack, ComboSequence prevComboSeq, EntityPlayer player) {
        ComboSequence comboSeq = getNextComboSeq(stack, prevComboSeq, true, player);

        doAttack(stack, comboSeq, player);
    }

    public void doAttack(ItemStack stack, ComboSequence comboSeq, EntityPlayer player){
        World world = player.getEntityWorld();
        NBTTagCompound tag = getItemTagCompound(stack);
        EnumSet<SwordType> swordType = getSwordType(stack);

        long currentTime = world.getTotalWorldTime();
        LastActionTime.set(tag, currentTime);

        OnClick.set(tag,true);
        setPlayerEffect(stack, comboSeq, player);
        setComboSequence(tag, comboSeq);


        //par3EntityPlayer.swingItem();
        doSwingItem(stack, player);

        updateStyleAttackType(stack, player);

        AxisAlignedBB bb = getBBofCombo(stack, comboSeq, player);

        int rank = StylishRankManager.getStylishRank(player);

        List<Entity> list = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
        for(Entity curEntity : list){

            switch (comboSeq) {
                case Saya1:
                case Saya2:
                case Force3:
                case Force4:
                    float attack = 4.0f;
                    if(rank < 3 || swordType.contains(SwordType.Broken)){
                        attack = 2.0f;
                    }else{
                        attack += Item.ToolMaterial.STONE.getDamageVsEntity(); //stone like
                        if(swordType.contains(SwordType.FiercerEdge) && player instanceof EntityPlayer){
                            attack += AttackAmplifier.get(tag) * 0.5f;
                        }
                    }

                    if (curEntity instanceof EntityLivingBase)
                    {
                        float var4 = 0;
                        var4 = EnchantmentHelper.getModifierForCreature(stack, ((EntityLivingBase)curEntity).getCreatureAttribute());
                        if(var4 > 0)
                            attack += var4;
                    }


                    if (curEntity instanceof EntityLivingBase){
                        attack = Math.min(attack,((EntityLivingBase)curEntity).getHealth()-1);
                    }


                    curEntity.hurtResistantTime = 0;
                    curEntity.attackEntityFrom(DamageSource.causeMobDamage(player), attack);


                    if (curEntity instanceof EntityLivingBase){
                        this.hitEntity(stack, (EntityLivingBase)curEntity, player);
                    }

                    break;

                case None:
                    break;

                default:
                    attackTargetEntity(stack, curEntity, player, true);
                    //player.attackTargetEntityWithCurrentItem(curEntity);
                    player.onCriticalHit(curEntity);
                    break;
            }
        }
        OnClick.set(tag, false);


        if (swordType.containsAll(SwordType.BewitchedPerfect) && comboSeq.equals(ComboSequence.Battou)) {
            ItemSlashBlade.damageItem(stack, 10, player);
            //todo 超短距離Drive周囲にばら撒くことで居合い再現はどーか
        }
    }

    public void doAddAttack(ItemStack stack, EntityPlayer player, ComboSequence setCombo){

        NBTTagCompound tag = getItemTagCompound(stack);
        World world = player.worldObj;
        if(!world.isRemote){

            final int cost = -10;
            if(!ProudSoul.tryAdd(tag, cost, false)){
                ItemSlashBlade.damageItem(stack, 5, player);
            }

            float baseModif = getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            float magicDamage = baseModif;
            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank){
                magicDamage += AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));
            }

            EntityDrive entityDrive = new EntityDrive(world, player, magicDamage, false, 90.0f - setCombo.swingDirection);
            if (entityDrive != null) {
                entityDrive.setInitialSpeed(0.75f);
                entityDrive.setLifeTime(20);
                world.spawnEntityInWorld(entityDrive);
            }

            setComboSequence(tag, setCombo);
            return;
        }
    }

    public void doSlashBladeAttack(ItemStack stack, EntityLivingBase player, ComboSequence setCombo){

        NBTTagCompound tag = getItemTagCompound(stack);
        World world = player.worldObj;

        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                0.8F, 0.01F);

        if(!world.isRemote){

            float baseModif = getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            float magicDamage = baseModif;
            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank){
                magicDamage += AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));
            }
            boolean disableMultiHit = rank <= 5;
            EntityDrive entityDrive = new EntityDrive(world, player, magicDamage, disableMultiHit, 90.0f - Math.abs(setCombo.swingDirection));
            if (entityDrive != null) {
                entityDrive.setInitialSpeed(0.1f);
                entityDrive.setLifeTime(20);

                EnumSet<SwordType> type = getSwordType(stack);
                entityDrive.setIsSlashDimension(type.contains(SwordType.FiercerEdge));

                world.spawnEntityInWorld(entityDrive);
            }

            setComboSequence(tag, setCombo);
            return;
        }
    }


    public void doChargeAttack(ItemStack stack, EntityPlayer par3EntityPlayer,boolean isJust){

        AchievementList.triggerAchievement(par3EntityPlayer, "enchanted");

        SpecialAttackBase sa = getSpecialAttack(stack);
        if(isJust && sa instanceof IJustSpecialAttack){
            ((IJustSpecialAttack)sa).doJustSpacialAttack(stack,par3EntityPlayer);
        }else {
            sa.doSpacialAttack(stack, par3EntityPlayer);
        }

        NBTTagCompound tag = getItemTagCompound(stack);
        IsCharged.set(tag, true);

    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        EnumSet<SwordType> swordType = getSwordType(stack);
        int charge = this.getMaxItemUseDuration(stack) - count;
        NBTTagCompound tag = getItemTagCompound(stack);

        if(player.worldObj.isRemote && player.onGround) {
            if (charge == 3 && getComboSequence(tag) == ComboSequence.Kiriage) {
                Method jump = ReflectionHelper.findMethod(EntityLivingBase.class, player, new String[]{"jump", "func_70664_aZ"});
                try {
                    if (jump != null)
                        jump.invoke(player);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                player.addVelocity(0.0, 0.2D, 0.0);

            } else if (charge == 7 && getComboSequence(tag) == ComboSequence.RapidSlash) {
                if (player.worldObj.isRemote) {
                    Method jump = ReflectionHelper.findMethod(EntityLivingBase.class, player, new String[]{"jump", "func_70664_aZ"});
                    try {
                        if (jump != null)
                            jump.invoke(player);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    player.addVelocity(0.0, 0.2D, 0.0);

                    NetworkManager.INSTANCE.sendToServer(new MessageSpecialAction((byte) 4));
                }

                setComboSequence(tag, ComboSequence.RisingStar);
                doSwingItem(stack, player);
            }
        }

        if(player instanceof EntityPlayer && RequiredChargeTick == charge && swordType.contains(SwordType.Enchanted) && !swordType.contains(SwordType.Broken)){
            ((EntityPlayer) player).onCriticalHit(player);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);

		NBTTagCompound tag = getItemTagCompound(stack);


		int var6 = this.getMaxItemUseDuration(stack) - timeLeft;

		EnumSet<SwordType> swordType = getSwordType(stack);

		if(RequiredChargeTick < var6 && swordType.contains(SwordType.Enchanted) && !swordType.contains(SwordType.Broken)){


            SilentUpdateItem.forceUpdate(stack, entityLiving);

            doSwingItem(stack, entityLiving);

            boolean isJust = false;

            if(var6 < (RequiredChargeTick + 4)) {
                if(entityLiving instanceof EntityPlayer)
                    ((EntityPlayer) entityLiving).onEnchantmentCritical(entityLiving);
                isJust = true;
            }


            if(entityLiving instanceof EntityPlayer)
                doChargeAttack(stack, (EntityPlayer)entityLiving, isJust);

            LastActionTime.set(tag, entityLiving.worldObj.getTotalWorldTime());

		}

        if(getComboSequence(tag) == ComboSequence.Kiriage && entityLiving.worldObj.isRemote){
            if(entityLiving.getEntityData().hasKey("SB.MCS.B")){
                entityLiving.getEntityData().removeTag("SB.MCS.B");
            }
        }

	}

    public NBTTagCompound getAttrTag(String attrName ,AttributeModifier par0AttributeModifier, EntityEquipmentSlot slot)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("AttributeName",attrName);
        nbttagcompound.setString("Name", par0AttributeModifier.getName());
        nbttagcompound.setDouble("Amount", par0AttributeModifier.getAmount());
        nbttagcompound.setInteger("Operation", par0AttributeModifier.getOperation());
        nbttagcompound.setUniqueId("UUID",par0AttributeModifier.getID());
        nbttagcompound.setString("Slot", slot.getName());
        return nbttagcompound;
    }

    public AxisAlignedBB getBBofCombo(ItemStack itemStack, ComboSequence combo, EntityLivingBase user){

    	NBTTagCompound tag = getItemTagCompound(itemStack);
    	EnumSet<SwordType> swordType = getSwordType(itemStack);

    	AxisAlignedBB bb = user.getEntityBoundingBox();

    	Vec3d vec = user.getLookVec();
        vec = new Vec3d(vec.xCoord,0,vec.zCoord);
    	vec = vec.normalize();

    	switch (combo) {
        case Calibur:
        case RapidSlash:
        case RisingStar:
        case SlashEdge:
        case ReturnEdge:
        case SSlashEdge:
        case SReturnEdge:
		case Battou:
			if(swordType.contains(SwordType.Broken)){
				bb = bb.expand(1.0f, 0.0f, 1.0f);
				bb = bb.offset(vec.xCoord*1.0f,0,vec.zCoord*1.0f);

			}else if(swordType.containsAll(SwordType.BewitchedPerfect)){
				bb = bb.expand(5.0f, 0.75f, 5.0f);
			}else{
				bb = bb.expand(2.0f, 0.75f, 2.0f);
				bb = bb.offset(vec.xCoord*2.5f,0,vec.zCoord*2.5f);
			}
			break;

        case SSlashBlade:
            if(swordType.contains(SwordType.Broken)) {
                bb = bb.expand(1.0f, 0.0f, 1.0f);
                bb = bb.offset(vec.xCoord * 1.0f, 0, vec.zCoord * 1.0f);
            }else{
                bb = bb.expand(3.0f, 1.0f, 3.0f);
                bb = bb.offset(vec.xCoord * 2.5f, 0, vec.zCoord * 2.5f);
            }
            break;

        case SIai:
        case Iai:
            if(swordType.contains(SwordType.Broken)){
                bb = bb.expand(1.0f, 0.0f, 1.0f);
                bb = bb.offset(vec.xCoord*1.0f,0,vec.zCoord*1.0f);
            }else{
                bb = bb.expand(2.0f, 1.0f, 2.0f);
                bb = bb.offset(vec.xCoord*2.5f,0,vec.zCoord*2.5f);
            }
			break;

		case Saya1:
		case Saya2:
			bb = bb.expand(1.2f, 0.25f, 1.2f);
			bb = bb.offset(vec.xCoord*2.0f,0,vec.zCoord*2.0f);
			break;

        case HelmBraker:
            if(swordType.contains(SwordType.Broken)){
                bb = bb.expand(1.0f, 0.0f, 1.0f);
                bb = bb.offset(vec.xCoord*1.0f,0,vec.zCoord*1.0f);
            }else{
                bb = bb.expand(2.0f, 2.5f, 2.0f);
                bb = bb.offset(vec.xCoord*2.5f,0,vec.zCoord*2.5f);
            }
            break;
        case Kiriorosi:
		default:
            if(swordType.contains(SwordType.Broken)){
                bb = bb.expand(1.0f, 0.0f, 1.0f);
                bb = bb.offset(vec.xCoord*1.0f,0,vec.zCoord*1.0f);
            }else{
                bb = bb.expand(1.2f, 1.25f, 1.2f);
                bb = bb.offset(vec.xCoord*2.0f,0.5f,vec.zCoord*2.0f);
            }
			break;
		}

    	return bb;
    }

    public enum SwordType{
    	Broken,
    	Perfect,
    	Enchanted,
    	Bewitched,
    	SoulEeater,
    	FiercerEdge,
        NoScabbard,
        Sealed,
    	;

    	public static final EnumSet<SwordType> BewitchedSoulEater = EnumSet.of(SwordType.SoulEeater,SwordType.Bewitched);
    	public static final EnumSet<SwordType> BewitchedPerfect = EnumSet.of(SwordType.Perfect,SwordType.Bewitched);
    }

    public EnumSet<SwordType> getSwordType(ItemStack itemStack){
    	EnumSet<SwordType> result = EnumSet.noneOf(SwordType.class);

		NBTTagCompound tag = getItemTagCompound(itemStack);


        if(IsSealed.get(tag)){
            result.add(SwordType.Sealed);
        }else{
            if(itemStack.isItemEnchanted()){
                result.add(SwordType.Enchanted);

                if(itemStack.hasDisplayName()){
                    result.add(SwordType.Bewitched);
                }
            }
        }

		if(itemStack.getItemDamage() == 0 && !result.contains(SwordType.Sealed))
			result.add(SwordType.Perfect);

		if(IsBroken.get(tag)){
			if(result.contains(SwordType.Perfect)){
                IsBroken.set(tag,false);
			}else{
				result.add(SwordType.Broken);
			}
		}

    	if(1000 <= ProudSoul.get(tag))
    		result.add(SwordType.SoulEeater);

    	if(1000 <= KillCount.get(tag))
    		result.add(SwordType.FiercerEdge);

        if(IsNoScabbard.get(tag)){
            result.add(SwordType.NoScabbard);
        }

    	return result;
    }


    public void updateAttackAmplifier(EnumSet<SwordType> swordType,NBTTagCompound tag,EntityPlayer el,ItemStack sitem){
        float tagAttackAmplifier = this.AttackAmplifier.get(tag);


        float baseModif = getBaseAttackModifiers(tag);
        float attackAmplifier = 0;

        int rank = StylishRankManager.getStylishRank(el);

        if(rank < 3 || swordType.contains(SwordType.Broken) || swordType.contains(SwordType.Sealed)){
            attackAmplifier = 2 - baseModif;
        }else if( rank == 7 || 5 <= rank && swordType.contains(SwordType.FiercerEdge)){
            float level = el.experienceLevel;

            float max = RefineBase + RepairCount.get(tag);

            attackAmplifier = Math.min(level, max);
        }

        if(tagAttackAmplifier != attackAmplifier)
        {
            this.AttackAmplifier.set(tag, attackAmplifier);

            NBTTagList attrTag = null;

            attrTag = new NBTTagList();
            tag.setTag("AttributeModifiers",attrTag);

            attrTag.appendTag(
                    getAttrTag(
                            SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName()
                            , new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)(attackAmplifier + baseModif), 0)
                            , EntityEquipmentSlot.MAINHAND)
            );
            attrTag.appendTag(
                    getAttrTag(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName()
                            , new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0)
                            , EntityEquipmentSlot.MAINHAND)
            );

            el.getAttributeMap().removeAttributeModifiers(sitem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
            el.getAttributeMap().applyAttributeModifiers(sitem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        }
    }

	@Override
	public void onUpdate(ItemStack sitem, World par2World,
			Entity par3Entity, int indexOfMainSlot, boolean isCurrent) {

        SilentUpdateItem.onUpdate(sitem,par3Entity,isCurrent);

        if(SlashBladeHooks.onUpdateHooks(sitem, par2World, par3Entity, indexOfMainSlot, isCurrent)){
            return;
        }

		if(!(par3Entity instanceof EntityPlayer)){
			super.onUpdate(sitem, par2World, par3Entity, indexOfMainSlot, isCurrent);
			return;
		}

        EntityPlayer el = (EntityPlayer)par3Entity;

		NBTTagCompound tag = getItemTagCompound(sitem);

		int curDamage = sitem.getItemDamage();

		EnumSet<SwordType> swordType = getSwordType(sitem);

		updateAttackAmplifier(swordType, tag ,el, sitem);

        if(tag.hasUniqueId("Owner")){
            UUID ownerid = tag.getUniqueId("Owner");
            boolean isOwner = ownerid.equals(el.getUniqueID());

            if(isOwner && IsSealed.get(tag) == true){
                IsSealed.set(tag,false);
            }else if(!isOwner){
                IsSealed.set(tag,true);
            }
        }


		{
			int cost = sitem.getRepairCost();
			if(cost != 0){
				Map map = EnchantmentHelper.getEnchantments(sitem);

				cost = map.size() + 1;
				cost *= AnvilRepairBonus;

				ProudSoul.add(tag, cost);
                RepairCount.add(tag, 1);

				sitem.setRepairCost(0);
			}
		}

        /*
        if(!par2World.isRemote && !isCurrent && PrevExp.exists(tag)){
            PrevExp.remove(tag);
        }
        if(!par2World.isRemote && isCurrent && par2World.getTotalWorldTime() % 20 == 0){
        	int nowExp = el.experienceTotal;

            int increasedExp = 0;

            if(PrevExp.exists(tag)){
                int prevExp = PrevExp.get(tag);
                increasedExp = nowExp - prevExp;
            }
            PrevExp.set(tag,nowExp);

        	if(0 < increasedExp){
            	if(0 < curDamage && swordType.containsAll(SwordType.BewitchedSoulEater) && !swordType.contains(SwordType.NoScabbard)){

                    int repairAmount = Math.max(1 , (int)(increasedExp / 10.0));
                    increasedExp -= repairAmount;
            		sitem.setItemDamage(Math.max(0,curDamage-repairAmount));

                    if(sitem.getItemDamage() == 0)

            	}

                ProudSoul.add(tag, increasedExp);

        	}
        }
        */

		if(!isCurrent && !par2World.isRemote){
			if(swordType.contains(SwordType.Bewitched) && !swordType.contains(SwordType.NoScabbard) && 0 < curDamage && par2World.getTotalWorldTime() % 20 == 0){

				int idx = Arrays.asList(el.inventory.mainInventory).indexOf(sitem);

                boolean doMaterialRepair = false;

                if(0<= idx && idx < 9 && swordType.contains(SwordType.Broken)){
                    ItemStack tinySoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.TinyBladeSoulStr,1);
                    ItemStack tinySoulHasEmptyTag = tinySoul.copy();
                    tinySoulHasEmptyTag.setTagCompound(new NBTTagCompound());

                    doMaterialRepair = InventoryUtility.consumeInventoryItem(el.inventory,tinySoul,false)
                                    || InventoryUtility.consumeInventoryItem(el.inventory,tinySoulHasEmptyTag,false);
                }

				if(0<= idx && idx < 9 && 0 < el.experienceLevel || doMaterialRepair){
					int repair;
					int descExp = 0;
                    int descLv = 0;
                    int addProudSoul = 0;

                    if(doMaterialRepair){
                        repair = Math.max(1,(int)(sitem.getMaxDamage() / 10.0));

                    }else if(swordType.contains(SwordType.Broken)){
						repair = Math.max(1,(int)(sitem.getMaxDamage() / 10.0));

                        addProudSoul = 20;
                        descLv = 1;
					}else{
						repair = 1;
						descExp = 10;
                        addProudSoul = 10;
					}

					if(0 < curDamage){
                        el.addExhaustion(0.025F);
						sitem.setItemDamage(Math.max(0,curDamage-repair));
					}

                    ProudSoul.add(tag, addProudSoul);

                    if(0 < descExp){
                        for(;descExp > 0;descExp--){
                            if(el.experienceLevel <= 0) break;

                            el.addExperience(-1);

                            if(el.experience < 0){
                                if(el.experienceLevel <= 0){
                                    el.experience = 0;
                                }else{
                                    //el.experienceLevel--;
                                    el.addExperienceLevel(-1);
                                    el.experience = 1.0f - (0.9f/el.xpBarCap());
                                }
                            }
                        }
                    }

                    if(0 < descLv){
                        for(;descLv > 0;descLv--){
                            if(0 < el.experienceLevel){
                                //el.experienceLevel--;
                                el.addExperienceLevel(-1);
                                //el.addExperience(-1);
                            }
                        }
                    }
				}
			}
		}

        /*
		if(el.onGround && !el.isAirBorne && OnJumpAttacked.get(tag)){
			setComboSequence(tag, ComboSequence.None);
		}*/

		if(el.onGround && OnJumpAttacked.get(tag))
            OnJumpAttacked.set(tag, false);


		ComboSequence comboSeq = getComboSequence(tag);

		long prevAttackTime = LastActionTime.get(tag);
        long currentTime = el.worldObj.getTotalWorldTime();

        if(currentTime + 1000L < prevAttackTime){
            prevAttackTime = 0L;
            LastActionTime.set(tag, 0L);
        }

		if(isCurrent){

            /*
			if(OnClick.get(tag)){

				//sitem.setItemDamage(1320);
				if(prevAttackTime + ComboInterval < currentTime){
                    LastActionTime.set(tag,currentTime);

					comboSeq = getNextComboSeq(sitem, comboSeq, true, el);
                    setPlayerEffect(sitem,comboSeq,el);
					setComboSequence(tag, comboSeq);

                    doSwingItem(sitem, el);

                    updateStyleAttackType(sitem, el);

					AxisAlignedBB bb = getBBofCombo(sitem, comboSeq, el);

                    int rank = StylishRankManager.getStylishRank(el);

					List<Entity> list = par2World.getEntitiesInAABBexcluding(el, bb, getInstance);
					for(Entity curEntity : list){

						switch (comboSeq) {
						case Saya1:
						case Saya2:
							float attack = 4.0f;
                            if(rank < 3 || swordType.contains(SwordType.Broken)){
                                attack = 2.0f;
                            }else{
                                attack += Item.ToolMaterial.STONE.getDamageVsEntity(); //stone like
                                if(swordType.contains(SwordType.FiercerEdge) && el instanceof EntityPlayer){
                                    attack += AttackAmplifier.get(tag) * 0.5f;
                                }
                            }

							if (curEntity instanceof EntityLivingBase)
			                {
				                float var4 = 0;
			                    var4 = EnchantmentHelper.getEnchantmentModifierLiving(el, (EntityLiving)curEntity);
				                if(var4 > 0)
				                	attack += var4;
			                }


			                if (curEntity instanceof EntityLivingBase){
			                	attack = Math.min(attack,((EntityLivingBase)curEntity).getHealth()-1);
			                }


							curEntity.hurtResistantTime = 0;
							curEntity.attackEntityFrom(DamageSource.causeMobDamage(el), attack);


			                if (curEntity instanceof EntityLivingBase){
			                	this.hitEntity(sitem, (EntityLivingBase)curEntity, el);
			                }

							break;

                        case None:
                            break;

						default:
							((EntityPlayer)el).attackTargetEntityWithCurrentItem(curEntity);
							((EntityPlayer)el).onCriticalHit(curEntity);
							break;
						}
					}
                    OnClick.set(tag, false);


					if(swordType.containsAll(SwordType.BewitchedPerfect) && comboSeq.equals(ComboSequence.Battou)){
						ItemSlashBlade.damageItem(sitem, 10, el);
                        //todo 超短距離Drive周囲にばら撒くことで居合い再現はどーか
					}
				}
			}else*/
            {
				if(comboSeq != ComboSequence.None
                        && ((prevAttackTime + (comboSeq.comboResetTicks - (el.worldObj.isRemote ? 1 : 0))) < (currentTime + 1))
						&& (comboSeq.useScabbard
					       || !el.isSwingInProgress /*swingProgress <= 0.0f*/)
					    //&& (!el.isUsingItem())
						){
					switch (comboSeq) {
					case None:
						break;

					case Noutou:
						//※動かず納刀完了させ、敵に囲まれている場合にボーナス付与。

						if(tag.getInteger(lastPosHashStr) == (int)((el.posX + el.posZ) * 10.0)){

                            SoulEater.fire(sitem, el);

                            Taunt.fire(sitem, el);

							AxisAlignedBB bb = el.getEntityBoundingBox();
							bb = bb.expand(10, 5, 10);
							List<Entity> list = par2World.getEntitiesInAABBexcluding(el, bb, EntitySelectorAttackable.getInstance());

							if(0 < list.size()){

                                StylishRankManager.addRankPoint(el,AttackTypes.Noutou);

                                /*
								if(swordType.containsAll(SwordType.BewitchedSoulEater)
										&& 10 < sitem.getItemDamage()){
									int j1 = (int)Math.min(Math.ceil(list.size() * 0.5),5);
							        dropXpOnBlockBreak(par2World, MathHelper.ceiling_double_int(el.posX), MathHelper.ceiling_double_int(el.posY), MathHelper.ceiling_double_int(el.posZ), j1);
								}
                                */

								el.onCriticalHit(el);

                                /*
								if(!el.worldObj.isRemote){
									el.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(),200,3,true));
									el.addPotionEffect(new PotionEffect(Potion.resistance.getId(),200,3,true));
								}
								*/
							}

						}
                        StylishRankManager.setNextAttackType(el, AttackTypes.None);
                        setComboSequence(tag, ComboSequence.None);
                        break;


					case SlashDim:
                    case Iai:
                    case SIai:
                            StylishRankManager.setNextAttackType(el, AttackTypes.None);
							setComboSequence(tag, ComboSequence.None);
							break;
					default:
						if(comboSeq.useScabbard || (comboSeq.mainHandCombo != null && comboSeq.mainHandCombo.useScabbard)){
                            StylishRankManager.setNextAttackType(el, AttackTypes.None);
							setComboSequence(tag, ComboSequence.None);
						}else{

                            tag.setInteger(lastPosHashStr, (int) ((el.posX + el.posZ) * 10.0));
                            LastActionTime.set(tag, currentTime + 5);
                            setComboSequence(tag, ComboSequence.Noutou);

                            UpthrustBlast.doBlast(sitem, el);

                            doSwingItem(sitem, el);
                        }
						break;
					}
				}

				if(!comboSeq.equals(ComboSequence.None) && el.swingProgressInt != 0 && currentTime < (prevAttackTime + comboSeq.comboResetTicks)){
                    DestructEntity(el, sitem);
				}
			}
		}else{
			if(!comboSeq.equals(ComboSequence.None) && ((prevAttackTime + comboSeq.comboResetTicks) < currentTime)){
                StylishRankManager.setNextAttackType(el, AttackTypes.None);
				setComboSequence(tag, ComboSequence.None);
			}
		}



		if(sitem.equals(el.getHeldItem(EnumHand.MAIN_HAND))){

            if(!el.worldObj.isRemote){
                int eId = TargetEntityId.get(tag);

                if(0 < (el.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.SNEAK)){
                    if(eId == 0){

                        Entity rayEntity = getRayTrace(el,20.0f);

                        if(rayEntity == null)
                            rayEntity = getRayTrace(el,10.0f,5.0f);

                        /*
                        if(rayEntity !=null){
                            if(!EntitySelectorAttackable.getInstance().apply(rayEntity)){

                            }
                        }
                        */

                        if(rayEntity != null){
                            eId = rayEntity.getEntityId();

                        }else{
                            AxisAlignedBB bb = el.getEntityBoundingBox();
                            bb = bb.expand(10, 5, 10);
                            float distance = 20.0f;

                            List<Entity> list = par2World.getEntitiesInAABBexcluding(el, bb, EntitySelectorAttackable.getInstance());
                            for(Entity curEntity : list){
                                if(!el.canEntityBeSeen(curEntity)) continue;

                                float curDist = curEntity.getDistanceToEntity(el);
                                if(curDist < distance)
                                {
                                    eId = curEntity.getEntityId();
                                    distance = curDist;
                                }
                            }
                        }
                        TargetEntityId.set(tag,eId);
                    }else{
                        {
                            Entity target = par2World.getEntityByID(eId);
                            if (target != null)
                                if(!target.isEntityAlive())
                                    TargetEntityId.set(tag,0);
                        }
                        if(3 <= EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, sitem)){
                            Entity target = par2World.getEntityByID(eId);
                            if(target != null && target instanceof EntityWither
                                    && 10 > el.getDistanceToEntity(target)
                                    && ((EntityWither)target).getHealth() / ((EntityWither)target).getMaxHealth() > 0.5)
                            {


                                Vec3d vec = el.getLookVec();

                                double y = -vec.yCoord * 2.0;
                                if(target.posY <= el.posY + 5.0)
                                    y = 0;

                                target.addVelocity(vec.xCoord,y,vec.zCoord);
                            }
                        }
                    }
                    /*
                    Entity target = par2World.getEntityByID(eId);
                    if(target != null)
                        this.faceEntity(el,target, 1000.0f,1000.0f);
*/
                }else if(eId != 0){
                    TargetEntityId.set(tag, 0);
                }
            }else{

                int eId = TargetEntityId.get(tag);
                if(eId != 0){
                    Entity target = par2World.getEntityByID(eId);
                    if(target != null)
                        this.faceEntity(el,target, 10.0f,10.0f);
                }else{
                    int camState = el.getEntityData().getByte("camerareset");
                    if(0 < (el.getEntityData().getByte("SB.MCS") & MessageMoveCommandState.CAMERA)){
                        switch (camState){
                            case 0:
                                el.getEntityData().setByte("camerareset",(byte)1);
                                break;
                            case 1: {

                                float par2 = 180f;
                                float par3 = 180f;
                                {
                                    double d0 = el.motionX;
                                    double d1 = el.motionZ;
                                    double d2;

                                    if((new Vec3d(d0,0,d1)).lengthVector() < 0.05f) {
                                        el.getEntityData().setByte("camerareset",(byte)2);
                                        break;
                                    };

                                    d2 = el.posY + (double) el.getEyeHeight() - (el.posY + (double) el.getEyeHeight());

                                    double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1);
                                    float f2 = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
                                    float f3 = (float) (-(Math.atan2(d2, d3) * 180.0D / Math.PI));


                                    el.rotationPitch = this.updateRotation(el.rotationPitch, f3, par3);
                                    el.rotationPitch = (float) Math.min(Math.max(el.rotationPitch, 0), 60);

                                    el.rotationYaw = this.updateRotation(el.rotationYaw, f2, par2);
                                }
                                el.getEntityData().setByte("camerareset",(byte)2);
                                break;
                            }

                            default:

                        }
                    }else if(camState != 0){
                        el.getEntityData().setByte("camerareset",(byte)0);
                    }
                }
            }


		}
	}

    Map<ComboSequence, String> attackTypeMap = crateAttackTypeMap();
    public Map crateAttackTypeMap(){
        attackTypeMap = Maps.newHashMap();

        attackTypeMap.put(ComboSequence.Kiriage,AttackTypes.Kiriage);
        attackTypeMap.put(ComboSequence.Kiriorosi,AttackTypes.Kiriorosi);

        attackTypeMap.put(ComboSequence.Iai,AttackTypes.Iai);

        attackTypeMap.put(ComboSequence.Saya1,AttackTypes.Saya1);
        attackTypeMap.put(ComboSequence.Saya2,AttackTypes.Saya2);


        attackTypeMap.put(ComboSequence.HiraTuki,AttackTypes.Kiriage);


        attackTypeMap.put(ComboSequence.SlashEdge,AttackTypes.SlashEdge);
        attackTypeMap.put(ComboSequence.ReturnEdge,AttackTypes.ReturnEdge);

        attackTypeMap.put(ComboSequence.SIai,AttackTypes.SIai);
        attackTypeMap.put(ComboSequence.SSlashEdge,AttackTypes.SSlashEdge);
        attackTypeMap.put(ComboSequence.SReturnEdge,AttackTypes.SReturnEdge);
        attackTypeMap.put(ComboSequence.SSlashBlade,AttackTypes.SSlashBlade);


        attackTypeMap.put(ComboSequence.ASlashEdge,AttackTypes.ASlashEdge);
        attackTypeMap.put(ComboSequence.AKiriorosi,AttackTypes.AKiriorosi);


        attackTypeMap.put(ComboSequence.AKiriage,AttackTypes.AKiriage);
        attackTypeMap.put(ComboSequence.AKiriorosiFinish,AttackTypes.AKiriorosiFinish);

        attackTypeMap.put(ComboSequence.HelmBraker,AttackTypes.HelmBraker);

        attackTypeMap.put(ComboSequence.Calibur,AttackTypes.Calibur);

        attackTypeMap.put(ComboSequence.RapidSlash,AttackTypes.RapidSlash);
        attackTypeMap.put(ComboSequence.RisingStar,AttackTypes.RisingStar);

        attackTypeMap.put(ComboSequence.Force1,AttackTypes.Force1);
        attackTypeMap.put(ComboSequence.Force2,AttackTypes.Force2);
        attackTypeMap.put(ComboSequence.Force3,AttackTypes.Force3);
        attackTypeMap.put(ComboSequence.Force4,AttackTypes.Force4);
        attackTypeMap.put(ComboSequence.Force5,AttackTypes.Force5);
        attackTypeMap.put(ComboSequence.Force6,AttackTypes.Force6);

        return attackTypeMap;
    }

    private void updateStyleAttackType(ItemStack stack, EntityLivingBase e) {
        NBTTagCompound tag = getItemTagCompound(stack);

        ComboSequence combo = getComboSequence(tag);

        String key = attackTypeMap.get(combo);

        if(key == null){
            switch (combo) {
                case Battou:
                    EnumSet<SwordType> swordType = getSwordType(stack);
                    if (swordType.containsAll(SwordType.BewitchedPerfect)) {
                        if (e instanceof EntityPlayer)
                            AchievementList.triggerAchievement((EntityPlayer) e, "bewitched");
                        key = AttackTypes.IaiBattou;
                    } else if (e.onGround)
                        key = AttackTypes.Battou;
                    else
                        key = AttackTypes.JumpBattou;
                    break;

                default:
                    key = AttackTypes.SimpleAttack;
            }
        }
        StylishRankManager.setNextAttackType(e, key);
    }


    protected void dropXpOnBlockBreak(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote)
        {
            while (par5 > 0)
            {
                int i1 = EntityXPOrb.getXPSplit(par5);
                par5 -= i1;
                par1World.spawnEntityInWorld(new EntityXPOrb(par1World, (double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, i1));
            }
        }
    }

    public void faceEntity(EntityLivingBase owner, Entity par1Entity, float par2, float par3)
    {
        double d0 = par1Entity.posX - owner.posX;
        double d1 = par1Entity.posZ - owner.posZ;
        double d2;

        if (par1Entity instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)par1Entity;
            d2 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (owner.posY + (double)owner.getEyeHeight());
        }
        else
        {
            d2 = (par1Entity.getEntityBoundingBox().minY + par1Entity.getEntityBoundingBox().maxY) / 2.0D - (owner.posY + (double)owner.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));


        owner.rotationPitch = this.updateRotation(owner.rotationPitch, f3, par3);
        owner.rotationPitch = (float)Math.min(Math.max(owner.rotationPitch,0), 60);

        owner.rotationYaw = this.updateRotation(owner.rotationYaw, f2, par2);
    }

    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapDegrees(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }

    public void addInformationOwner(ItemStack par1ItemStack,
                                         EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        if(tag.hasUniqueId("Owner") && par2EntityPlayer != null){
            UUID ownerid = tag.getUniqueId("Owner");
            if(!ownerid.equals(par2EntityPlayer.getUniqueID())) {
                par3List.add(I18n.translateToLocal("flammpfeil.swaepon.info.notowner.msg"));

                EntityPlayer owner = par2EntityPlayer.worldObj.getPlayerEntityByUUID(ownerid);
                if(owner != null)
                    par3List.add("owner:" + owner.getName());
            }
        }
    }
    
    public void addInformationSwordClass(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		EnumSet<SwordType> swordType = getSwordType(par1ItemStack);
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);

		if(swordType.contains(SwordType.Enchanted)){
			if(swordType.contains(SwordType.Bewitched)){
                if(tag.hasUniqueId("Owner"))
    				par3List.add(String.format("§6%s", I18n.translateToLocal("flammpfeil.swaepon.info.bewitched")));
                else
                    par3List.add(String.format("§5%s", I18n.translateToLocal("flammpfeil.swaepon.info.bewitched")));
			}else{
				par3List.add(String.format("§3%s", I18n.translateToLocal("flammpfeil.swaepon.info.magic")));
			}
		}else{
			par3List.add(String.format("§8%s", I18n.translateToLocal("flammpfeil.swaepon.info.noname")));
		}
    }

    public void addInformationKillCount(ItemStack par1ItemStack,
    		EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	EnumSet<SwordType> swordType = getSwordType(par1ItemStack);
		NBTTagCompound tag = getItemTagCompound(par1ItemStack);

		par3List.add(String.format("%sKillCount : %d", swordType.contains(SwordType.FiercerEdge) ? "§4" : "", KillCount.get(tag)));

    }

    public void addInformationProudSoul(ItemStack par1ItemStack,
                                        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        EnumSet<SwordType> swordType = getSwordType(par1ItemStack);
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);

        par3List.add(String.format("%sProudSoul : %d", swordType.contains(SwordType.SoulEeater) ? "§5" : "", ProudSoul.get(tag)));

    }

    public void addInformationSpecialAttack(ItemStack par1ItemStack,
                                            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        EnumSet<SwordType> swordType = getSwordType(par1ItemStack);

        if(swordType.contains(SwordType.Bewitched)){
            NBTTagCompound tag = getItemTagCompound(par1ItemStack);

            String key = "flammpfeil.slashblade.specialattack." + getSpecialAttack(par1ItemStack).toString();

            par3List.add(String.format("SA:%s",  I18n.translateToLocal(key)));
        }
    }

    public void addInformationRangeAttack(ItemStack par1ItemStack,
                                            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        EnumSet<SwordType> swordType = getSwordType(par1ItemStack);

        if(swordType.contains(SwordType.Bewitched) && 0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER,par1ItemStack)){
            NBTTagCompound tag = getItemTagCompound(par1ItemStack);

            String key = "slashblade.rangeattack." + Boolean.toString(tag.getBoolean("RangeAttackType")).toLowerCase();

            par3List.add(I18n.translateToLocal(key));
        }
    }

    public void addInformationRepairCount(ItemStack par1ItemStack,
                                          EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        int repair = RepairCount.get(tag);
        if(0 < repair){
            par3List.add(String.format("Refine : %d", repair));
        }
    }

    public void addInformationMaxAttack(ItemStack par1ItemStack,
                                        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        float repair = RepairCount.get(tag);
        EnumSet<SwordType> swordType = getSwordType(par1ItemStack);

        par3List.add("");
        par3List.add("§4RankAttackDamage");
        String header;
        String template;

        if(swordType.contains(SwordType.FiercerEdge)){
            header = "§6B-A§r/§4S-SSS§r/§5Limit";
            template = "§6+%.1f§r/§4+%.1f§r/§5+%.1f";
        }else{
            header = "§6B-SS§r/§4SSS§r/§5Limit";
            template = "§6+%.1f§r/§4+%.1f§r/§5+%.1f";
        }

        float baseModif = getBaseAttackModifiers(tag);

        float maxBonus = RefineBase + repair;
        float level = par2EntityPlayer.experienceLevel;
        float ba = baseModif;
        float sss = (baseModif + Math.min(maxBonus,level));

        par3List.add(header);
        par3List.add(String.format(template,ba , sss , (baseModif + maxBonus)));

    }

    public void addInformationSpecialEffec(ItemStack par1ItemStack,
                                        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

        NBTTagCompound etag = getSpecialEffect(par1ItemStack);

        Set<String> tagKeys = etag.getKeySet();

        if(tagKeys.size() == 0) return;

        int playerLevel = par2EntityPlayer.experienceLevel;

        par3List.add("");

        for(String key : tagKeys){
            int reqiredLevel = etag.getInteger(key);

            par3List.add(
                    I18n.translateToLocal("slashblade.seffect.name." + key)
                    + "§r "
                    + (reqiredLevel <= playerLevel ? "§c" : "§8") + reqiredLevel);
        }
    }
    
	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {


		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationOwner(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationSwordClass(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationKillCount(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationProudSoul(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationSpecialAttack(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationRepairCount(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationRangeAttack(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationSpecialEffec(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationMaxAttack(par1ItemStack, par2EntityPlayer, par3List, par4);

		NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        if(tag.hasKey(adjustXStr)){
            float ax = tag.getFloat(adjustXStr);
            float ay = tag.getFloat(adjustYStr);
            float az = tag.getFloat(adjustZStr);
            par3List.add(String.format("adjust x:%.1f y:%.1f z:%.1f", ax,ay,az));
        }

	}


    public Vec3d getEntityToEntityVec(Entity root, Entity target, float yawLimit, float pitchLimit)
    {
        double d0 = (target.posX + target.motionX) - root.posX;
        double d1 = (target.posZ + target.motionZ) - root.posZ;
        double d2;

        if (target instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)target;
            d2 = entitylivingbase.posY + entitylivingbase.motionY + (double)entitylivingbase.getEyeHeight() - (root.posY + (double)root.getEyeHeight());
        }
        else
        {
            d2 = (target.getEntityBoundingBox().minY+ target.getEntityBoundingBox().maxY) / 2.0D  + target.motionY  - (root.posY + (double)root.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));


        double x,y,z;

        double yaw = Math.atan2(d1, d0) - Math.PI / 2.0f;
        double pitch = Math.atan2(d2, d3);

        y = Math.sin(pitch);
        x = -Math.sin(yaw);
        z = Math.cos(yaw);

        return new Vec3d(x, y, z).normalize();
    }

	public void ReflectionProjecTile(Entity projecTile,EntityLivingBase player){

		Entity target = null;

    	if(projecTile instanceof EntityFireball)
    		target = ((EntityFireball)projecTile).shootingEntity;
    	else if(projecTile instanceof EntityArrow)
    		target = ((EntityArrow)projecTile).shootingEntity;


    	if(target != null){
    		Vec3d vec = this.getEntityToEntityVec(projecTile,target,360.0f,360.0f);
			InductionProjecTile(projecTile,player,vec);
    	}else{
    		Vec3d vec = new Vec3d(-projecTile.motionX,-projecTile.motionY,-projecTile.motionZ);
    		vec = vec.normalize();
			InductionProjecTile(projecTile,player,vec);
//    		InductionProjecTile(projecTile,player);
    	}

	}


	public void InductionProjecTile(Entity projecTile,EntityLivingBase user){
		InductionProjecTile(projecTile,user,user.getLookVec());
	}
	public void InductionProjecTile(Entity projecTile,EntityLivingBase user,Vec3d dir){

        if (dir != null)
        {
        	//projecTile.velocityChanged = true;

        	Vec3d vector = new Vec3d(projecTile.motionX,projecTile.motionY,projecTile.motionZ);

        	projecTile.motionX = dir.xCoord;
        	projecTile.motionY = dir.yCoord;
        	projecTile.motionZ = dir.zCoord;

        	if(projecTile instanceof EntityFireball){
	        	((EntityFireball)projecTile).accelerationX = projecTile.motionX * 0.1D;
	        	((EntityFireball)projecTile).accelerationY = projecTile.motionY * 0.1D;
	        	((EntityFireball)projecTile).accelerationZ = projecTile.motionZ * 0.1D;
        	}

        	if(projecTile instanceof EntityArrow){
        		((EntityArrow)projecTile).setIsCritical(true);
        	}

        	/*
        	if(projecTile instanceof EntityThrowable){
        	}
        	/**/

        	/*
			if(projecTile instanceof IThrowableEntity){
        	}
        	/**/

        	projecTile.motionX *= 1.5;
        	projecTile.motionY *= 1.5;
        	projecTile.motionZ *= 1.5;

        }

        if (user != null)
        {
        	if(projecTile instanceof EntityFireball)
        		((EntityFireball)projecTile).shootingEntity = user;
        	else if(projecTile instanceof EntityArrow){
        		((EntityArrow)projecTile).shootingEntity = user;
        	}else if(projecTile instanceof IThrowableEntity)
        		((IThrowableEntity)projecTile).setThrower(user);
        	else if(projecTile instanceof EntityThrowable){
        		if(user instanceof EntityPlayer){
            		NBTTagCompound tag = new NBTTagCompound();
            		((EntityThrowable)projecTile).writeEntityToNBT(tag);
            		tag.setString("ownerName", ((EntityPlayer) user).getName());
            		((EntityThrowable)projecTile).readEntityFromNBT(tag);
        		}
        	}
        }
	}

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {

        /*
        EnumSet<SwordType> type = getSwordType(stack);

        double swordLen = 5.5D;
        if(type.contains(SwordType.Broken))
            swordLen = 2.8D;

        CoreProxy.proxy.getMouseOver(swordLen);
        */

        /*if(!entityLiving.worldObj.isRemote)
        {
            NBTTagCompound tag = getItemTagCompound(stack);
            ComboSequence combo = getComboSequence(tag);

            if(combo.equals(ComboSequence.Noutou)){
                System.out.println("None");
            }else if(tag.getBoolean(isChargedStr) && !tag.getBoolean(onClickStr)){
                System.out.println("Charged");
            }else if(tag.getBoolean("isRightClick")){
                System.out.println("Right");
                tag.setBoolean("isRightClick",false);
            }else if(entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).isUsingItem()){
                if(entityLiving.swingProgressInt == 0)
                    System.out.println("RL locker");
            }else{
                System.out.println("Left");
                //タイムセットで、startUsingで規定時間内に開始されたらロッカー？　でも右コンボへ派生できない
            }
        }
*/
        return false;//super.onEntitySwing(entityLiving, stack);
    } 
    public void DestructEntity(EntityLivingBase entityLiving, ItemStack stack) {

        ComboSequence comboSeq = getComboSequence(getItemTagCompound(stack));

        if(!comboSeq.equals(ComboSequence.None))
        {
            int destructedCount = 0;

            AxisAlignedBB bb = getBBofCombo(
                    stack,
                    comboSeq,
                    entityLiving);

            StylishRankManager.setNextAttackType(entityLiving ,AttackTypes.DestructObject);

            List<Entity> list = entityLiving.worldObj.getEntitiesInAABBexcluding(entityLiving, bb, EntitySelectorDestructable.getInstance());
            for(Entity curEntity : list){

                boolean isDestruction = true;

                EnumSet<SwordType> swordType =getSwordType(stack);

                if(curEntity instanceof EntityFireball){
                    if((((EntityFireball)curEntity).shootingEntity != null && ((EntityFireball)curEntity).shootingEntity.getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }else if(!swordType.contains(SwordType.Bewitched)){
                        isDestruction = !curEntity.attackEntityFrom(DamageSource.causeMobDamage(entityLiving),this.defaultBaseAttackModifier);
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            InductionProjecTile(curEntity,entityLiving);
                        }
                        isDestruction = false;
                    }

                }else if(curEntity instanceof EntityArrow){
                    if((((EntityArrow)curEntity).shootingEntity != null && ((EntityArrow)curEntity).shootingEntity.getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            Entity target = null;

                            NBTTagCompound tag = stack.getTagCompound();
                            int eId = TargetEntityId.get(tag);
                            if(eId != 0){
                                Entity tmp = entityLiving.worldObj.getEntityByID(eId);
                                if(tmp != null){
                                    if(tmp.getDistanceToEntity(entityLiving) < 30.0f)
                                        target = tmp;
                                }
                            }
                            if(target != null && target instanceof EntityCreeper){
                                InductionProjecTile(curEntity, null, entityLiving.getLookVec());
                            }else{
                                InductionProjecTile(curEntity, entityLiving);
                            }
                        }
                        isDestruction = false;
                    }
                }else if(curEntity instanceof IThrowableEntity){
                    if((((IThrowableEntity)curEntity).getThrower() != null && ((IThrowableEntity)curEntity).getThrower().getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            InductionProjecTile(curEntity,entityLiving);
                        }
                        isDestruction = false;
                    }
                }else if(curEntity instanceof EntityThrowable){
                    if((((EntityThrowable)curEntity).getThrower() != null && ((EntityThrowable)curEntity).getThrower().getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            InductionProjecTile(curEntity,entityLiving);
                        }
                        isDestruction = false;
                    }
                }

                if(!isDestruction)
                    continue;
                else{
                    curEntity.motionX = 0;
                    curEntity.motionY = 0;
                    curEntity.motionZ = 0;
                    curEntity.setDead();

                    for (int var1 = 0; var1 < 10; ++var1)
                    {
                        Random rand = entityLiving.getRNG();
                        double var2 = rand.nextGaussian() * 0.02D;
                        double var4 = rand.nextGaussian() * 0.02D;
                        double var6 = rand.nextGaussian() * 0.02D;
                        double var8 = 10.0D;
                        entityLiving.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL
                                , curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8
                                , curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8
                                , curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8
                                , var2, var4, var6);
                    }

                    destructedCount++;
                }

                StylishRankManager.doAttack(entityLiving);
            }

            if(0 < destructedCount){
                ItemSlashBlade.damageItem(stack, 1,entityLiving);
            }
        }
    }


    public RayTraceResult rayTrace(EntityLivingBase owner, double par1, float par3)
    {
        Vec3d vec3 = getPosition(owner);
        Vec3d vec31 = owner.getLook(par3);
        Vec3d vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        return owner.worldObj.rayTraceBlocks(vec3, vec32, false, false, true);
    }
    public Vec3d getPosition(EntityLivingBase owner)
    {
        return new Vec3d(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ);
    }

    public Entity getRayTrace(EntityLivingBase owner, double reachMax){
        return getRayTrace(owner,reachMax, 0.0f);
    }

    public Entity getRayTrace(EntityLivingBase owner, double reachMax, float expandBorder){
        Entity pointedEntity;
        float par1 = 1.0f;

        RayTraceResult objectMouseOver = rayTrace(owner, reachMax, par1);
        double reachMin = reachMax;
        Vec3d entityPos = getPosition(owner);

        if (objectMouseOver != null)
        {
            reachMin = objectMouseOver.hitVec.distanceTo(entityPos);
        }

        Vec3d lookVec = owner.getLook(par1);
        Vec3d reachVec = entityPos.addVector(lookVec.xCoord * reachMax, lookVec.yCoord * reachMax, lookVec.zCoord * reachMax);
        pointedEntity = null;
        float expandFactor = 1.0F;
        List<Entity> list = owner.worldObj.getEntitiesWithinAABBExcludingEntity(
                owner
                , owner.getEntityBoundingBox()
                        .addCoord(lookVec.xCoord * reachMax, lookVec.yCoord * reachMax, lookVec.zCoord * reachMax)
                        .expand((double) expandFactor, (double) expandFactor, (double) expandFactor));
        double tmpDistance = reachMin;

        for(Entity entity : list){
            if (entity == null || !entity.canBeCollidedWith()) continue;

            if(0.01f < expandBorder && (!owner.canEntityBeSeen(entity) || !EntitySelectorAttackable.getInstance().apply(entity))) continue;

            float borderSize = entity.getCollisionBorderSize() + expandBorder;
            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double) borderSize, (double) borderSize, (double) borderSize);
            RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(entityPos, reachVec);

            if (axisalignedbb.isVecInside(entityPos))
            {
                if (0.0D < tmpDistance || tmpDistance == 0.0D)
                {
                    pointedEntity = entity;
                    tmpDistance = 0.0D;
                }
            }
            else if (movingobjectposition != null)
            {
                double d3 = entityPos.distanceTo(movingobjectposition.hitVec);

                if (d3 < tmpDistance || tmpDistance == 0.0D)
                {
                    if (entity == owner.getRidingEntity() && !entity.canRiderInteract())
                    {
                        if (tmpDistance == 0.0D)
                        {
                            pointedEntity = entity;
                        }
                    }
                    else
                    {
                        pointedEntity = entity;
                        tmpDistance = d3;
                    }
                }
            }
        }

        return pointedEntity;
    }

    private String[] repairMaterialOreDic = null;
    public ItemSlashBlade setRepairMaterialOreDic(String... material){
    	this.repairMaterialOreDic = material;
    	return this;
    }

    private ItemStack repairMaterial = null;
    public ItemSlashBlade setRepairMaterial(ItemStack item){
    	this.repairMaterial = item;
    	return this;
    }
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
    	boolean result = false;
    	if(par2ItemStack.getItem() == SlashBlade.proudSoul){
    		result = true;
    	}

    	if(!result && this.repairMaterial != null)
    		result =par2ItemStack.isItemEqual(this.repairMaterial);

    	if(!result && this.repairMaterialOreDic != null)
    	{
    		for(String oreName : this.repairMaterialOreDic){
        		List<ItemStack> list = OreDictionary.getOres(oreName);
        		for(ItemStack curItem : list){
                    if(curItem.getItemDamage() == OreDictionary.WILDCARD_VALUE){
                        result = curItem.getItem() == par2ItemStack.getItem();
                    }else{
                        result = curItem.isItemEqual(par2ItemStack);
                    }
        			if(result)
        				break;
        		}
    		}
    	}
    	return result;

        //return this.toolMaterial.getToolCraftingMaterial() == par2ItemStack.itemID ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    public void doSwingItem(ItemStack stack, EntityLivingBase entity){



        if(entity.worldObj.isRemote){
            entity.swingingHand = EnumHand.MAIN_HAND;
            entity.isSwingInProgress = true;
            entity.swingProgressInt = 0;
        }else{
            //entity.swingItem();
            entity.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public static void setBaseAttackModifier(NBTTagCompound tag,float modif){
        BaseAttackModifier.set(tag, modif);
        AttackAmplifier.set(tag, 0.01f);
    }
    public float getBaseAttackModifiers(NBTTagCompound tag){
        if(BaseAttackModifier.exists(tag)){
            return BaseAttackModifier.get(tag);
        }else{
            return defaultBaseAttackModifier;
        }
    }

    public boolean isDestructable(ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);
        return IsDestructable.get(tag);
    }
    static final String IsManagedDamage = "IsManagedDamage";

    @Override
    public void setDamage(ItemStack stack, int damage) {

        if(damage != OreDictionary.WILDCARD_VALUE)
        {
            NBTTagCompound tag = getItemTagCompound(stack);
            EnumSet<SwordType> types = getSwordType(stack);
            int maxDamage = stack.getMaxDamage();

            if(damage <= 0 && !types.contains(SwordType.Sealed)){
                IsBroken.set(tag, false);

            }else if(maxDamage < damage){
                if(IsBroken.get(tag) || !tag.getBoolean(IsManagedDamage)) {
                    damage = Math.min(damage, maxDamage);
                }
            }
        }
        super.setDamage(stack,damage);
    }


    static public void damageItem(ItemStack stack, int damage, EntityLivingBase user) {

        NBTTagCompound tag = getItemTagCompound(stack);
        tag.setBoolean(IsManagedDamage, true);
        stack.damageItem(damage, user);
        tag.setBoolean(IsManagedDamage, false);

        if(stack.stackSize <= 0) {
            boolean setBroken = true;
            boolean doDrop = true;
            ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

            if(!blade.isDestructable(stack)){
                stack.stackSize = 1;
                stack.setItemDamage(stack.getMaxDamage());

                if(blade instanceof ItemSlashBladeWrapper){

                    doDrop = ((ItemSlashBladeWrapper) blade).hasWrapedItem(stack);
                    if(!ItemSlashBladeWrapper.TrueItemName.exists(tag)){
                        ((ItemSlashBladeWrapper)blade).removeWrapItem(stack);
                        setBroken = false;
                    }
                }

                if(blade == SlashBlade.bladeSilverBambooLight){
                    AchievementList.triggerAchievement((EntityPlayer) user, "saya");

                    stack.setItem(SlashBlade.wrapBlade);
                    setBroken = false;
                    stack.setItemDamage(0);
                }

                if(blade == SlashBlade.bladeWhiteSheath && user instanceof EntityPlayer){
                    AchievementList.triggerAchievement((EntityPlayer) user, "brokenWhiteSheath");
                }
            }

            if(doDrop && !IsBroken.get(tag))
                blade.dropItemDestructed(user, stack);

            IsBroken.set(tag,setBroken);
        }
    }

    public void attackTargetEntity(ItemStack stack, Entity target, EntityPlayer player, Boolean isRightClick){
        NBTTagCompound tag = getItemTagCompound(stack);
        OnClick.set(tag, isRightClick);
        ComboSequence combo = getComboSequence(tag);

        ItemStack mainHand = player.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offhand = player.getHeldItem(EnumHand.OFF_HAND);
        NBTTagCompound offTag = null;
        if(offhand != null)
            offTag = getItemTagCompound(offhand);

        if(combo.mainHandCombo != null && offTag != null) {
            OnClick.set(offTag,true);
            player.setHeldItem(EnumHand.MAIN_HAND, offhand);
        }

        player.attackTargetEntityWithCurrentItem(target);

        if(combo.mainHandCombo != null && offTag != null) {
            damageItem(offhand, 1, player);
            if(target instanceof EntityLivingBase)
                stack.hitEntity((EntityLivingBase) target, player);
            OnClick.set(offTag,false);

            if(offhand.stackSize <= 0)
                player.setHeldItem(EnumHand.OFF_HAND, null);
        }

        player.setHeldItem(EnumHand.MAIN_HAND, mainHand);

        OnClick.set(tag, false);
    }

    public static Map<Integer,SpecialAttackBase> specialAttacks = createSpacialAttaksMap();
    public static SpecialAttackBase defaultSA;
    static Map<Integer,SpecialAttackBase> createSpacialAttaksMap(){
        Map<Integer,SpecialAttackBase> saMap = Maps.newHashMap();
        saMap.put(0,defaultSA = new SlashDimension());
        saMap.put(1,new Drive(0.75f,20,false,ComboSequence.Kiriage));
        saMap.put(2,new WaveEdge());
        saMap.put(3, new Drive(1.5f, 10, true, ComboSequence.Iai));
        saMap.put(4, new Spear());
        saMap.put(5, new CircleSlash());
        saMap.put(6, new BlisteringWitherSwords());
        saMap.put(7, new SakuraEnd());
        saMap.put(8, new MaximumBet());
        return saMap;
    }

    public SpecialAttackBase getSpecialAttack(ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);
        int key = SpecialAttackType.get(tag);
        return specialAttacks.containsKey(key) ? specialAttacks.get(key) : defaultSA;
    }

    public void doRangeAttack(ItemStack item, EntityLivingBase entity, MessageRangeAttack.RangeAttackState mode) {
        World w = entity.worldObj;
        NBTTagCompound tag = getItemTagCompound(item);
        EnumSet<SwordType> types = getSwordType(item);


        if (!types.contains(SwordType.Bewitched)) return;
        if(types.contains(SwordType.Broken)) return;

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, item);

        if(level <= 0) return;

        if (w.isRemote) {
            NetworkManager.INSTANCE.sendToServer(new MessageRangeAttack(mode));
            return;
        }
        int rank = StylishRankManager.getStylishRank(entity);

        switch(mode) {
            case UPKEY: {
                if(entity.getEntityData().hasKey("SB.BSHOLDLIMIT")){
                    long holdLimit = entity.getEntityData().getLong("SB.BSHOLDLIMIT");
                    long currentTime = entity.getEntityWorld().getTotalWorldTime();

                    if(currentTime < holdLimit) {
                        entity.getEntityData().setLong("SB.BSHOLDLIMIT", currentTime);
                        return;
                    }
                }

                if (!ProudSoul.tryAdd(tag, -1, false)) return;

                if (rank < 3)
                    level = Math.min(1, level);

                float magicDamage = level;

                entity.worldObj.playSound((EntityPlayer) null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.35F, 1.0F);

                if (tag.getInteger("RangeAttackType") == 0) {
                    EntitySummonedSwordBase entityDrive = new EntitySummonedSwordBase(w, entity, magicDamage, 90.0f);
                    if (entityDrive != null) {
                        entityDrive.setLifeTime(30);

                        int targetid = ItemSlashBlade.TargetEntityId.get(tag);
                        entityDrive.setTargetEntityId(targetid);

                        if (SummonedSwordColor.exists(tag))
                            entityDrive.setColor(SummonedSwordColor.get(tag));

                        ScheduleEntitySpawner.getInstance().offer(entityDrive);
                        //w.spawnEntityInWorld(entityDrive);

                        if (entity instanceof EntityPlayer)
                            AchievementList.triggerAchievement((EntityPlayer) entity, "phantomSword");

                    }

                } else {
                    EntitySummonedBlade summonedBlade = new EntitySummonedBlade(w, entity, magicDamage, 90.0f);
                    if (summonedBlade != null) {
                        summonedBlade.setLifeTime(100);
                        summonedBlade.setInterval(10);

                        if (SummonedSwordColor.exists(tag))
                            summonedBlade.setColor(SummonedSwordColor.get(tag));

                        int targetid = ItemSlashBlade.TargetEntityId.get(tag);
                        summonedBlade.setTargetEntityId(targetid);

                        if (SummonedSwordColor.exists(tag))
                            summonedBlade.setColor(SummonedSwordColor.get(tag));

                        ScheduleEntitySpawner.getInstance().offer(summonedBlade);
                    }
                }
                break;
            }
            case BLISTERING: {

                if (!ProudSoul.tryAdd(tag, -10, false)) return;

                long currentTime = entity.getEntityWorld().getTotalWorldTime();
                final int holdLimit = 400;
                entity.getEntityData().setLong("SB.BSHOLDLIMIT", currentTime + holdLimit);

                entity.worldObj.playSound((EntityPlayer)null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.7F, 1.0F);

                int count = 4;
                if (3 < rank)
                    count +=2;
                if (5 <= rank)
                    count +=2;

                float magicDamage = level * 2;

                for(int i = 0; i<count;i++){

                    EntityBlisteringSwords summonedSword = new EntityBlisteringSwords(w, entity, magicDamage, 90.0f, i);
                    if (summonedSword != null) {
                        summonedSword.setLifeTime(30);
                        summonedSword.setIsJudgement(types.contains(SwordType.FiercerEdge));

                        int targetid = ItemSlashBlade.TargetEntityId.get(tag);
                        summonedSword.setTargetEntityId(targetid);

                        if (SummonedSwordColor.exists(tag))
                            summonedSword.setColor(SummonedSwordColor.get(tag));

                        ScheduleEntitySpawner.getInstance().offer(summonedSword);
                        //w.spawnEntityInWorld(entityDrive);

                        /*
                        if (entity instanceof EntityPlayer)
                            AchievementList.triggerAchievement((EntityPlayer) entity, "phantomSword");
                        */
                    }
                }

                break;
            }
            case SPIRAL: {


                int currentTime = (int)entity.getEntityWorld().getWorldTime();
                final int holdLimit = 200;

                if(entity.getEntityData().hasKey("SB.SPHOLDID")){
                    if(currentTime < (entity.getEntityData().getInteger("SB.SPHOLDID") + holdLimit)){
                        entity.getEntityData().removeTag("SB.SPHOLDID");
                        return;
                    }
                }

                if (!ProudSoul.tryAdd(tag, -10, false)) return;

                entity.worldObj.playSound((EntityPlayer) null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.7F, 1.0F);

                int count = 6;

                if (rank < 3)
                    level = Math.min(1, level);

                float magicDamage = level;

                float arc = 360.0f / count;

                entity.getEntityData().setInteger("SB.SPHOLDID", currentTime);

                for (int i = 0; i < count; i++) {

                    float offset = i * arc;

                    EntitySpiralSwords summonedSword = new EntitySpiralSwords(w, entity, magicDamage, 0, offset);
                    if (summonedSword != null) {
                        summonedSword.setHoldId(currentTime);
                        summonedSword.setInterval(holdLimit);
                        summonedSword.setLifeTime(holdLimit);

                        if (SummonedSwordColor.exists(tag))
                            summonedSword.setColor(SummonedSwordColor.get(tag));

                        ScheduleEntitySpawner.getInstance().offer(summonedSword);
                        //w.spawnEntityInWorld(entityDrive);

                    }
                }
                /*
                if (entity instanceof EntityPlayer)
                    AchievementList.triggerAchievement((EntityPlayer) entity, "phantomSword");
                */
                break;
            }
            case STORM: {

                int targetId = TargetEntityId.get(tag);
                if (targetId == 0) return;

                if (!ProudSoul.tryAdd(tag, -10, false)) return;

                entity.worldObj.playSound((EntityPlayer) null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.7F, 1.0F);

                int count = 6;

                if (rank < 3)
                    level = Math.min(1, level);

                float magicDamage = level / 2.0f;

                float arc = 360.0f / count;

                final int holdLimit = (int) (20 * 2);
                for (int i = 0; i < count; i++) {

                    float offset = i * arc;

                    EntityStormSwords summonedSword = new EntityStormSwords(w, entity, magicDamage, 0, offset, targetId);
                    if (summonedSword != null) {
                        summonedSword.setInterval(holdLimit);
                        summonedSword.setLifeTime(holdLimit + 30);

                        if (SummonedSwordColor.exists(tag))
                            summonedSword.setColor(SummonedSwordColor.get(tag));

                        ScheduleEntitySpawner.getInstance().offer(summonedSword);
                        //w.spawnEntityInWorld(entityDrive);

                    }
                }
                /*
                if (entity instanceof EntityPlayer)
                    AchievementList.triggerAchievement((EntityPlayer) entity, "phantomSword");
                */
                break;
            }
            case HEAVY_RAIN: {

                if (!ProudSoul.tryAdd(tag, -10, false)) return;

                entity.worldObj.playSound((EntityPlayer) null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.7F, 1.0F);

                int count = 10;
                int multiplier = 2;
                if (5 <= rank)
                    multiplier += 1;

                float magicDamage = 1;
                int targetid = ItemSlashBlade.TargetEntityId.get(tag);

                for (int i = 0; i < count; i++) {
                    for (int j = 0; j < multiplier; j++) {

                        EntityHeavyRainSwords summonedSword = new EntityHeavyRainSwords(w, entity, magicDamage, entity.getRNG().nextFloat() * 360.0f, i , targetid);
                        if (summonedSword != null) {
                            summonedSword.setLifeTime(30+i);

                            if (SummonedSwordColor.exists(tag))
                                summonedSword.setColor(SummonedSwordColor.get(tag));

                            ScheduleEntitySpawner.getInstance().offer(summonedSword);
                            //w.spawnEntityInWorld(entityDrive);

                        }
                    }
                }
                /*
                if (entity instanceof EntityPlayer)
                    AchievementList.triggerAchievement((EntityPlayer) entity, "phantomSword");
                */
                break;
            }
        }
    }



    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        NBTTagCompound tag = getItemTagCompound(stack);

        if(tag.hasKey("rarityType")){
            int type = tag.getByte("rarityType");
            switch (type){
                case 1:
                    return EnumRarity.COMMON;
                case 2:
                    return EnumRarity.UNCOMMON;
                case 3:
                    return EnumRarity.RARE;
                case 4:
                    return EnumRarity.EPIC;
                default:
            }
        }


        EnumSet<SwordType> types = getSwordType(stack);
        if(stack.isItemEnchanted()){
            if(types.contains(SwordType.Bewitched) || types.contains(SwordType.FiercerEdge)){
                if(tag.getBoolean("isDefaultBewitched"))
                    return EnumRarity.EPIC;
                else
                    return EnumRarity.RARE;
            }else{
                return EnumRarity.UNCOMMON;
            }
        }else{
            if(tag.getBoolean("isDefaultBewitched"))
                return EnumRarity.UNCOMMON;
            else
                return EnumRarity.COMMON;
        }
    }

    @Override
    public void onCreated(ItemStack p_77622_1_, World p_77622_2_, EntityPlayer p_77622_3_) {
        super.onCreated(p_77622_1_, p_77622_2_, p_77622_3_);

        AchievementList.triggerCraftingAchievement(p_77622_1_, p_77622_3_);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {

        if(entityItem.worldObj.isRemote) {
            if(entityItem.serverPosX == 0 &&
                    entityItem.serverPosY == 0 &&
                    entityItem.serverPosZ == 0) {
                entityItem.setDead();
            }
            return false;
        }


        if(entityItem.getEntityData().getBoolean("noBladeStand"))
            return false;

        boolean forceDrop = entityItem.getTags().contains("SB.DeathDrop");

        ItemStack stack = entityItem.getEntityItem();

        if(!forceDrop && stack.getItem() instanceof ItemSlashBladeWrapper){
            if(!ItemSlashBladeWrapper.hasWrapedItem(stack))
                return false;
        }


        if(forceDrop || stack.getRarity() != EnumRarity.COMMON || stack.hasDisplayName() || stack.hasTagCompound() && ItemSlashBladeNamed.TrueItemName.exists(stack.getTagCompound())){

            EntityBladeStand e = new EntityBladeStand(entityItem.worldObj, entityItem.posX, entityItem.posY, entityItem.posZ, stack.copy());

            e.setFlip(e.getRand().nextInt(2));

            if(forceDrop)
                e.setGlowing(true);

            e.moveEntity(entityItem.motionX * 2, entityItem.motionY * 2, entityItem.motionZ * 2);

            if(!entityItem.worldObj.isRemote)
                entityItem.worldObj.spawnEntityInWorld(e);

            entityItem.setDead();
            return true;
        }else{
            return false;
        }
    }

    public static NBTTagCompound getSpecialEffect(ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);

        NBTTagCompound result = tag.getCompoundTag("SB.SEffect");

        if(!tag.hasKey("SB.SEffect")){
            tag.setTag("SB.SEffect",result);
        }

        return result;
    }

    static void incrementProudSoul(ItemStack stack, EntityLivingBase target,EntityLivingBase player){
        if(player instanceof EntityPlayer) {
            Method getExperiencePoints = ReflectionHelper.findMethod(EntityLivingBase.class, target, new String[]{"getExperiencePoints", "func_70693_a"}, EntityPlayer.class);
            try {
                int exp = (Integer)getExperiencePoints.invoke(target, (EntityPlayer) player);
                exp = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(target, (EntityPlayer) player, exp);

                float rank = StylishRankManager.getStylishRank(player);

                exp *= 1.0 + rank * 0.1;

                NBTTagCompound tag = getItemTagCompound(stack);
                PrevExp.set(tag,exp);
                ProudSoul.add(tag,exp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;//super.showDurabilityBar(stack);
    }

    @Override
    public int getMetadata(ItemStack stack) {
        NBTTagCompound tag = getItemTagCompound(stack);
        if(tag.getBoolean("IsRender")){
            tag.removeTag("IsRender");
            return 0;
        }else {
            return super.getMetadata(stack);
        }
    }
}
