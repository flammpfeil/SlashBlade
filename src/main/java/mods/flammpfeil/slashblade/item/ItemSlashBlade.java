package mods.flammpfeil.slashblade.item;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import mods.flammpfeil.slashblade.*;
import mods.flammpfeil.slashblade.core.CoreProxy;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.event.ModelRegister;
import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import mods.flammpfeil.slashblade.network.MessageRangeAttack;
import mods.flammpfeil.slashblade.network.NetworkManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.ability.*;
import mods.flammpfeil.slashblade.ability.StylishRankManager.*;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.specialattack.*;
import mods.flammpfeil.slashblade.stats.AchievementList;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import mods.flammpfeil.slashblade.util.InventoryUtility;
import mods.flammpfeil.slashblade.util.SilentUpdateItem;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.block.Block;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

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

	    /**
	     *
	     * @param useScabbard true:鞘も動く
	     * @param swingAmplitude 振り幅 マイナスは振り切った状態から逆に振る
	     * @param swingDirection 振る角度
	     * @param isCharged チャージエフェクト有無
	     */
        private ComboSequence(boolean useScabbard, float swingAmplitude, float swingDirection, boolean isCharged,int comboResetTicks)
        {
            Seqs.add(this.ordinal(), this);

            this.useScabbard = useScabbard;
            this.swingAmplitude = swingAmplitude;
            this.swingDirection = swingDirection;
            this.isCharged = isCharged;
            this.comboResetTicks = comboResetTicks;
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
                int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
                int lootingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, stack);

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
                        Map enchantments = EnchantmentHelper.getEnchantments(stack);

                        if(unbreakingLevel == 1)
                            enchantments.remove(Enchantment.unbreaking.effectId);
                        else
                            enchantments.put(Enchantment.unbreaking.effectId,unbreakingLevel-1);

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
            entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),10,30,true,false));
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

        int lv = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);

        int slots;

        switch(lv){

            case 0:
                return;
            case 1:
                slots = 1;
                break;
            default:
                slots = 5;
                break;
        }

        for(int i = 0; i < slots; i++){
            try{
                ((EntityLiving) entity).setEquipmentDropChance(i, 0.99f);
            }catch(Exception e){
            }
        }
    }

    public void setImpactEffect(ItemStack stack, EntityLivingBase target,EntityLivingBase user, ComboSequence comboSec){

        if(SlashBladeHooks.onImpactEffectHooks(stack,target,user,comboSec))
            return;

        switch (comboSec) {
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
            case SlashEdge:
            case SIai:
            case SSlashEdge:
            case SReturnEdge:
            case SSlashBlade:
            case Iai:
                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

                {

                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack);
                    if(0 < level){
                        target.addVelocity(0.0, 0.3D, 0.0);
                    }else{
                        target.addVelocity(0.0, 0.2D, 0.0);
                    }
                }

                setDaunting(target);

                break;

            case Saya1:
            case Saya2:

                target.motionX = 0;
                target.motionY = 0;
                target.motionZ = 0;

                setDaunting(target);
                setArmorDrop(stack,target);
                break;

            case SlashDim:

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
                if(0 < level){
                    target.motionX = 0;
                    target.motionY = 0;
                    target.motionZ = 0;
                    target.addVelocity(
                            (double) (MathHelper.sin(user.rotationYaw * (float) Math.PI / 180.0F) * (float) level * 0.5F),
                            0.2D,
                            (double) (-MathHelper.cos(user.rotationYaw * (float) Math.PI / 180.0F) * (float) level * 0.5F));
                }

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

        if(!comboSec.useScabbard || IsNoScabbard.get(tag))
            par1ItemStack.damageItem(1, par3EntityLivingBase);

        StylishRankManager.doAttack(par3EntityLivingBase);

		return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        if (blockIn.getBlockHardness(worldIn, pos) != 0.0)
            stack.damageItem(1, playerIn);

        return true;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
	@Override
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", (double) defaultBaseAttackModifier, 0));
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

	public ComboSequence getNextComboSeq(ItemStack itemStack, ComboSequence current, boolean isRightClick, EntityPlayer player) {
        ComboSequence result = ComboSequence.None;

        EnumSet<SwordType> types = getSwordType(itemStack);
        if (types.contains(SwordType.NoScabbard)) {
            result = ComboSequence.None;
        } else if (!player.onGround) {
            switch (current) {
                case Iai:
                    result = ComboSequence.Battou;
                    break;

                default:
                    result = ComboSequence.Iai;
                    break;
            }

        } else if (isRightClick) {

            switch (current) {

                case Saya1:
                    result = ComboSequence.Saya2;
                    break;

                case Saya2:

                    int rank = StylishRankManager.getStylishRank(player);
                    if (rank < 5) {
                        result = ComboSequence.Battou;
                    } else {
                        result = ComboSequence.SIai;
                    }
                    break;

                case Kiriage:
                    result = ComboSequence.Kiriorosi;
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

                default:
                    result = ComboSequence.Saya1;

                    break;
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
        case Iai:
            if (!player.onGround){
                player.fallDistance = 0;

                if(!OnJumpAttacked.get(tag)){
                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack);
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
                    int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack);
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

            if(current == ComboSequence.SSlashBlade){
                doSlashBladeAttack(itemStack,player,current);
            }
        }
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {

		NBTTagCompound tag = getItemTagCompound(stack);

		if(!OnClick.get(tag) ){ // onClick中は rightClickなので無視
	        if (entity.canAttackWithItem()){
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
	public ItemStack onItemRightClick(ItemStack sitem, World par2World,
			EntityPlayer par3EntityPlayer) {

        SlashBlade.abilityJustGuard.setJustGuardState(par3EntityPlayer);

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
        NBTTagCompound tag = getItemTagCompound(sitem);
        long prevAttackTime = LastActionTime.get(tag);
        long currentTime = par3EntityPlayer.worldObj.getTotalWorldTime();
        ComboSequence comboSeq = getComboSequence(tag);
        //if(prevAttackTime + ComboInterval < currentTime)
        {
            nextAttackSequence(sitem, comboSeq, par3EntityPlayer);

            SilentUpdateItem.silentUpdateItem(par3EntityPlayer);
        }

		return super.onItemRightClick(sitem, par2World, par3EntityPlayer);
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
                        var4 = EnchantmentHelper.func_152377_a(stack, ((EntityLivingBase)curEntity).getCreatureAttribute());
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
                    player.attackTargetEntityWithCurrentItem(curEntity);
                    player.onCriticalHit(curEntity);
                    break;
            }
        }
        OnClick.set(tag, false);


        if (swordType.containsAll(SwordType.BewitchedPerfect) && comboSeq.equals(ComboSequence.Battou)) {
            stack.damageItem(10, player);
            //todo 超短距離Drive周囲にばら撒くことで居合い再現はどーか
        }
    }

    public void doAddAttack(ItemStack stack, EntityPlayer player, ComboSequence setCombo){

        NBTTagCompound tag = getItemTagCompound(stack);
        World world = player.worldObj;
        if(!world.isRemote){

            final int cost = -10;
            if(!ProudSoul.tryAdd(tag, cost, false)){
                stack.damageItem(5, player);
            }

            float baseModif = getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
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

    public void doSlashBladeAttack(ItemStack stack, EntityPlayer player, ComboSequence setCombo){

        NBTTagCompound tag = getItemTagCompound(stack);
        World world = player.worldObj;
        if(!world.isRemote){

            float baseModif = getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            float magicDamage = baseModif;
            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank){
                magicDamage += AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));
            }
            boolean disableMultiHit = rank <= 5;
            EntityDrive entityDrive = new EntityDrive(world, player, magicDamage, disableMultiHit, 90.0f - Math.abs(setCombo.swingDirection));
            if (entityDrive != null) {
                entityDrive.setInitialSpeed(0.05f);
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
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
        EnumSet<SwordType> swordType = getSwordType(stack);
        int charge = this.getMaxItemUseDuration(stack) - count;
        if(RequiredChargeTick == charge && swordType.contains(SwordType.Enchanted) && !swordType.contains(SwordType.Broken)){
            player.onCriticalHit(player);
        }
    }

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer, int par4) {

		NBTTagCompound tag = getItemTagCompound(par1ItemStack);


		int var6 = this.getMaxItemUseDuration(par1ItemStack) - par4;

		EnumSet<SwordType> swordType = getSwordType(par1ItemStack);

		if(RequiredChargeTick < var6 && swordType.contains(SwordType.Enchanted) && !swordType.contains(SwordType.Broken)){


            SilentUpdateItem.forceUpdate(par1ItemStack, par3EntityPlayer);

            doSwingItem(par1ItemStack, par3EntityPlayer);

            boolean isJust = false;

            if(var6 < (RequiredChargeTick + 4)) {
                par3EntityPlayer.onEnchantmentCritical(par3EntityPlayer);
                isJust = true;
            }

            doChargeAttack(par1ItemStack, par3EntityPlayer, isJust);

            LastActionTime.set(tag, par3EntityPlayer.worldObj.getTotalWorldTime());

		}
        /*else{

            if(!JustGuard.atJustGuard(par3EntityPlayer)){
                OnClick.set(tag, true);
                //par3EntityPlayer.motionY = 0.0;
            }
		}*/

	}

    public NBTTagCompound getAttrTag(String attrName ,AttributeModifier par0AttributeModifier)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("AttributeName",attrName);
        nbttagcompound.setString("Name", par0AttributeModifier.getName());
        nbttagcompound.setDouble("Amount", par0AttributeModifier.getAmount());
        nbttagcompound.setInteger("Operation", par0AttributeModifier.getOperation());
        nbttagcompound.setLong("UUIDMost", par0AttributeModifier.getID().getMostSignificantBits());
        nbttagcompound.setLong("UUIDLeast", par0AttributeModifier.getID().getLeastSignificantBits());
        return nbttagcompound;
    }

    public AxisAlignedBB getBBofCombo(ItemStack itemStack, ComboSequence combo, EntityLivingBase user){

    	NBTTagCompound tag = getItemTagCompound(itemStack);
    	EnumSet<SwordType> swordType = getSwordType(itemStack);

    	AxisAlignedBB bb = user.getEntityBoundingBox();

    	Vec3 vec = user.getLookVec();
        vec = new Vec3(vec.xCoord,0,vec.zCoord);
    	vec = vec.normalize();

    	switch (combo) {
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
                    getAttrTag(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),new AttributeModifier(itemModifierUUID, "Weapon modifier", (double)(attackAmplifier + baseModif), 0))
            );

            el.getAttributeMap().removeAttributeModifiers(sitem.getAttributeModifiers());
            el.getAttributeMap().applyAttributeModifiers(sitem.getAttributeModifiers());
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

				if(0<= idx && idx < 9 && 0 < el.experienceLevel){
					int repair;
					int descExp = 0;
                    int descLv = 0;
                    int addProudSoul = 0;

					if(swordType.contains(SwordType.Broken)){
						repair = Math.max(1,(int)(sitem.getMaxDamage() / 10.0));
                        ItemStack tinySoul = SlashBlade.findItemStack(SlashBlade.modid,SlashBlade.TinyBladeSoulStr,1);
                        addProudSoul = 20;
                        if(!InventoryUtility.consumeInventoryItem(el.inventory,tinySoul,false))
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
						sitem.damageItem(10, el);
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

						if(tag.getInteger(lastPosHashStr) == (int)((el.posX + el.posY + el.posZ) * 10.0)){

                            SoulEater.fire(sitem, el);

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
						if(comboSeq.useScabbard){
                            StylishRankManager.setNextAttackType(el, AttackTypes.None);
							setComboSequence(tag, ComboSequence.None);
						}else{

                            tag.setInteger(lastPosHashStr, (int) ((el.posX + el.posY + el.posZ) * 10.0));
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



		if(sitem.equals(el.getHeldItem())){

            if(!el.worldObj.isRemote){
                int eId = TargetEntityId.get(tag);

                if(el.isSneaking()){
                    if(eId == 0){



                        Entity rayEntity = getRayTrace(el,10.0f);

                        if(rayEntity !=null){
                            if(!EntitySelectorAttackable.getInstance().apply(rayEntity)){

                            }
                        }


                        if(rayEntity != null){
                            eId = rayEntity.getEntityId();

                        }else{
                            AxisAlignedBB bb = el.getEntityBoundingBox();
                            bb = bb.expand(10, 5, 10);
                            float distance = 20.0f;

                            List<Entity> list = par2World.getEntitiesInAABBexcluding(el, bb, EntitySelectorAttackable.getInstance());
                            for(Entity curEntity : list){
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

                        if(3 <= EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, sitem)){
                            Entity target = par2World.getEntityByID(eId);
                            if(target != null && target instanceof EntityWither
                                    && 10 > el.getDistanceToEntity(target)
                                    && ((EntityWither)target).getHealth() / ((EntityWither)target).getMaxHealth() > 0.5)
                            {


                                Vec3 vec = el.getLookVec();

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
                        this.faceEntity(el,target, 1000.0f,1000.0f);
                }
            }


		}
	}

    private void updateStyleAttackType(ItemStack stack, EntityLivingBase e) {
        NBTTagCompound tag = getItemTagCompound(stack);

        ComboSequence combo = getComboSequence(tag);

        switch (combo){
            case Kiriage:
                StylishRankManager.setNextAttackType(e, AttackTypes.Kiriage);
                break;

            case Kiriorosi:
                StylishRankManager.setNextAttackType(e, AttackTypes.Kiriorosi);
                break;

            case Iai:
                StylishRankManager.setNextAttackType(e, AttackTypes.Iai);
                break;

            case Battou:

                EnumSet<SwordType> swordType = getSwordType(stack);
                if(swordType.containsAll(SwordType.BewitchedPerfect)){
                    if(e instanceof EntityPlayer)
                        AchievementList.triggerAchievement((EntityPlayer)e,"bewitched");
                    StylishRankManager.setNextAttackType(e, AttackTypes.IaiBattou);
                }else if(e.onGround)
                    StylishRankManager.setNextAttackType(e, AttackTypes.Battou);
                else
                    StylishRankManager.setNextAttackType(e, AttackTypes.JumpBattou);
                break;

            case Saya1:
                StylishRankManager.setNextAttackType(e, AttackTypes.Saya1);
                break;

            case Saya2:
                StylishRankManager.setNextAttackType(e, AttackTypes.Saya2);
                break;

            case HiraTuki:
                StylishRankManager.setNextAttackType(e, AttackTypes.Kiriage);
                break;

            case SlashEdge:
                StylishRankManager.setNextAttackType(e, AttackTypes.SlashEdge);
                break;
            case ReturnEdge:
                StylishRankManager.setNextAttackType(e, AttackTypes.ReturnEdge);
                break;
            case SIai:
                StylishRankManager.setNextAttackType(e, AttackTypes.SIai);
                break;
            case SSlashEdge:
                StylishRankManager.setNextAttackType(e, AttackTypes.SSlashEdge);
                break;
            case SReturnEdge:
                StylishRankManager.setNextAttackType(e, AttackTypes.SReturnEdge);
                break;
            case SSlashBlade:
                StylishRankManager.setNextAttackType(e, AttackTypes.SSlashBlade);
                break;

        }
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
        owner.rotationPitch = (float)Math.min(Math.max(owner.rotationPitch,-30), 60);

        owner.rotationYaw = this.updateRotation(owner.rotationYaw, f2, par2);
    }

    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

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
    
    public void addInformationSwordClass(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		EnumSet<SwordType> swordType = getSwordType(par1ItemStack);
		if(swordType.contains(SwordType.Enchanted)){
			if(swordType.contains(SwordType.Bewitched)){
				par3List.add(String.format("§5%s", StatCollector.translateToLocal("flammpfeil.swaepon.info.bewitched")));
			}else{
				par3List.add(String.format("§3%s", StatCollector.translateToLocal("flammpfeil.swaepon.info.magic")));
			}
		}else{
			par3List.add(String.format("§8%s", StatCollector.translateToLocal("flammpfeil.swaepon.info.noname")));
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

            par3List.add(String.format("SA:%s",  StatCollector.translateToLocal(key)));
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
                    StatCollector.translateToLocal("slashblade.seffect.name." + key)
                    + "§r "
                    + (reqiredLevel <= playerLevel ? "§c" : "§8") + reqiredLevel);
        }
    }
    
	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {


		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationSwordClass(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationKillCount(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationProudSoul(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationSpecialAttack(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationRepairCount(par1ItemStack, par2EntityPlayer, par3List, par4);

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


    public Vec3 getEntityToEntityVec(Entity root, Entity target, float yawLimit, float pitchLimit)
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

        return new Vec3(x, y, z).normalize();
    }

	public void ReflectionProjecTile(Entity projecTile,EntityLivingBase player){

		Entity target = null;

    	if(projecTile instanceof EntityFireball)
    		target = ((EntityFireball)projecTile).shootingEntity;
    	else if(projecTile instanceof EntityArrow)
    		target = ((EntityArrow)projecTile).shootingEntity;


    	if(target != null){
    		Vec3 vec = this.getEntityToEntityVec(projecTile,target,360.0f,360.0f);
			InductionProjecTile(projecTile,player,vec);
    	}else{
    		Vec3 vec = new Vec3(-projecTile.motionX,-projecTile.motionY,-projecTile.motionZ);
    		vec = vec.normalize();
			InductionProjecTile(projecTile,player,vec);
//    		InductionProjecTile(projecTile,player);
    	}

	}


	public void InductionProjecTile(Entity projecTile,EntityLivingBase user){
		InductionProjecTile(projecTile,user,user.getLookVec());
	}
	public void InductionProjecTile(Entity projecTile,EntityLivingBase user,Vec3 dir){

        if (dir != null)
        {
        	//projecTile.velocityChanged = true;

        	Vec3 vector = new Vec3(projecTile.motionX,projecTile.motionY,projecTile.motionZ);

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

        EnumSet<SwordType> type = getSwordType(stack);

        double swordLen = 5.5D;
        if(type.contains(SwordType.Broken))
            swordLen = 2.8D;

        CoreProxy.proxy.getMouseOver(swordLen);

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
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)){
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
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)){
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
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)){
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
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)){
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
                stack.damageItem(1,entityLiving);
            }
        }
    }


    public MovingObjectPosition rayTrace(EntityLivingBase owner, double par1, float par3)
    {
        Vec3 vec3 = getPosition(owner);
        Vec3 vec31 = owner.getLook(par3);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        return owner.worldObj.rayTraceBlocks(vec3, vec32, false, false, true);
    }
    public Vec3 getPosition(EntityLivingBase owner)
    {
        return new Vec3(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ);
    }

    public Entity getRayTrace(EntityLivingBase owner, double reachMax){
        Entity pointedEntity;
        float par1 = 1.0f;

        MovingObjectPosition objectMouseOver = rayTrace(owner, reachMax, par1);
        double reachMin = reachMax;
        Vec3 entityPos = getPosition(owner);

        if (objectMouseOver != null)
        {
            reachMin = objectMouseOver.hitVec.distanceTo(entityPos);
        }

        Vec3 lookVec = owner.getLook(par1);
        Vec3 reachVec = entityPos.addVector(lookVec.xCoord * reachMax, lookVec.yCoord * reachMax, lookVec.zCoord * reachMax);
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

            float borderSize = entity.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double) borderSize, (double) borderSize, (double) borderSize);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(entityPos, reachVec);

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
                    if (entity == owner.ridingEntity && !entity.canRiderInteract())
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

    public void doSwingItem(ItemStack stack, EntityPlayer entity){
        if(entity.worldObj.isRemote){
            entity.isSwingInProgress = true;
            entity.swingProgressInt = 0;
        }else{
            entity.swingItem();
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
                if(IsBroken.get(tag)){
                    damage = Math.min(damage,maxDamage);
                }
            }
        }
        super.setDamage(stack,damage);
    }

    public void attackTargetEntity(ItemStack stack, Entity target, EntityPlayer player, Boolean isRightClick){
        NBTTagCompound tag = getItemTagCompound(stack);
        OnClick.set(tag, isRightClick);
        player.attackTargetEntityWithCurrentItem(target);
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
        return saMap;
    }

    public SpecialAttackBase getSpecialAttack(ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);
        int key = SpecialAttackType.get(tag);
        return specialAttacks.containsKey(key) ? specialAttacks.get(key) : defaultSA;
    }

    public void doRangeAttack(ItemStack item, EntityLivingBase entity, int mode) {
        World w = entity.worldObj;
        NBTTagCompound tag = getItemTagCompound(item);
        EnumSet<SwordType> types = getSwordType(item);

        if(mode == 1){
            if(types.contains(SwordType.Bewitched) && !types.contains(SwordType.Broken)){

                int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, item);
                if(0 < level && ProudSoul.tryAdd(tag,-1,false)){

                    int rank = StylishRankManager.getStylishRank(entity);
                    if(rank < 3)
                        level = Math.min(1, level);

                    float magicDamage = level;


                    if(!w.isRemote){
                        EntitySummonedSwordBase entityDrive = new EntitySummonedSwordBase(w, entity, magicDamage,90.0f);
                        if (entityDrive != null) {
                            entityDrive.setLifeTime(30);

                            int targetid = ItemSlashBlade.TargetEntityId.get(tag);
                            entityDrive.setTargetEntityId(targetid);

                            if(SummonedSwordColor.exists(tag))
                                entityDrive.setColor(SummonedSwordColor.get(tag));

                            ScheduleEntitySpawner.getInstance().offer(entityDrive);
                            //w.spawnEntityInWorld(entityDrive);

                            if(entity instanceof EntityPlayer)
                                AchievementList.triggerAchievement((EntityPlayer)entity,"phantomSword");

                        }

                    }else{
                        NetworkManager.INSTANCE.sendToServer(new MessageRangeAttack((byte)1));

                    }

                }
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
        if(entityItem.worldObj.isRemote)
            return false;

        if(entityItem.getEntityData().getBoolean("noBladeStand"))
            return false;

        ItemStack stack = entityItem.getEntityItem();

        if(stack.getItem() instanceof ItemSlashBladeWrapper){
            if(!ItemSlashBladeWrapper.hasWrapedItem(stack))
                return false;
        }

        if(stack.getRarity() != EnumRarity.COMMON || stack.hasDisplayName() || stack.hasTagCompound() && ItemSlashBladeNamed.TrueItemName.exists(stack.getTagCompound())){

            EntityBladeStand e = new EntityBladeStand(entityItem.worldObj, entityItem.posX, entityItem.posY, entityItem.posZ, stack);

            e.setFlip(e.getRand().nextInt(2));

            e.moveEntity(entityItem.motionX * 2, entityItem.motionY * 2, entityItem.motionZ * 2);

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

                NBTTagCompound tag = getItemTagCompound(stack);
                PrevExp.set(tag,exp);
                ProudSoul.add(tag,exp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
