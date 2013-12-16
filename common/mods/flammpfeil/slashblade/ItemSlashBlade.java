package mods.flammpfeil.slashblade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

public class ItemSlashBlade extends ItemSword {

	static public int RequiredChargeTick = 15;
	static public int ComboInterval = 3;
	static public int ComboResetTicks = 12;

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
	@Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
		if(par1ItemStack.attemptDamageItem(1, par3EntityLivingBase.getRNG())){
			par1ItemStack.setItemDamage(par1ItemStack.getMaxDamage());


			NBTTagCompound tag;
			if(par1ItemStack.hasTagCompound()){
				tag = par1ItemStack.getTagCompound();
			}else{
				tag = new NBTTagCompound();
				par1ItemStack.setTagCompound(tag);
			}

			if(!tag.getBoolean("isBroken")){

				tag.setBoolean("isBroken", true);
				par3EntityLivingBase.renderBrokenItemStack(par1ItemStack);

				if(!par3EntityLivingBase.worldObj.isRemote)
					par3EntityLivingBase.entityDropItem(new ItemStack(SlashBlade.proudSoul,1), 0.0F);
			}else{
				if(par1ItemStack.getItemDamage() == 0){
					tag.setBoolean("isBroken", false);
				}
			}
		}

		return true;
    }

    public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLivingBase par7EntityLivingBase)
    {
        if ((double)Block.blocksList[par3].getBlockHardness(par2World, par4, par5, par6) != 0.0D)
        {

    		if(par1ItemStack.attemptDamageItem(1, par7EntityLivingBase.getRNG())){
    			par1ItemStack.setItemDamage(par1ItemStack.getMaxDamage());


    			NBTTagCompound tag;
    			if(par1ItemStack.hasTagCompound()){
    				tag = par1ItemStack.getTagCompound();
    			}else{
    				tag = new NBTTagCompound();
    				par1ItemStack.setTagCompound(tag);
    			}

    			if(!tag.getBoolean("isBroken")){

    				tag.setBoolean("isBroken", true);
    				par7EntityLivingBase.renderBrokenItemStack(par1ItemStack);

    				if(!par7EntityLivingBase.worldObj.isRemote)
    					par7EntityLivingBase.entityDropItem(new ItemStack(SlashBlade.proudSoul,1), 0.0F);
    			}else{
    				if(par1ItemStack.getItemDamage() == 0){
    					tag.setBoolean("isBroken", false);
    				}
    			}
    		}
        }

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
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)(4.0F + EnumToolMaterial.EMERALD.getDamageVsEntity()), 0));
        return multimap;
    }

	@Override
	public void registerIcons(IconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("flammpfeil.slashblade:proudsoul");
	}

	public ItemSlashBlade(int par1, EnumToolMaterial par2EnumToolMaterial) {
		super(par1, par2EnumToolMaterial);
        this.setMaxDamage(50);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {


		NBTTagCompound tag;
		if(stack.hasTagCompound()){
			tag = stack.getTagCompound();
		}else{
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}

		tag.setInteger("comboSeq", 4);
		tag.setLong("prevAttackTime", player.worldObj.getTotalWorldTime());

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack sitem, World par2World,
			EntityPlayer par3EntityPlayer) {

		return super.onItemRightClick(sitem, par2World, par3EntityPlayer);
	}



/*
	@Override
	public boolean itemInteractionForEntity(ItemStack sitem,
			EntityLivingBase par2EntityLivingBase) {

		NBTTagCompound tag;
		if(sitem.hasTagCompound()){
			tag = sitem.getTagCompound();
		}else{
			tag = new NBTTagCompound();
			sitem.setTagCompound(tag);
		}


		tag.setBoolean("onClick", true);


		return super.itemInteractionForEntity(sitem, par2EntityLiving);
	}
*/
	private List<Entity> targetFilter(List<Entity> list){
		List<Entity> result = new ArrayList<Entity>();

		for(Entity curEntity : list){
			if(curEntity == null)
				continue;

			String entityStr = EntityList.getEntityString(curEntity);
			//含む
			if(((entityStr != null && SlashBlade.attackableTargets.containsKey(entityStr) && SlashBlade.attackableTargets.get(entityStr))
				|| curEntity instanceof EntityDragonPart
				))
				result.add(curEntity);
		}
		return result;
	}


	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer, int par4) {

		NBTTagCompound tag;
		if(par1ItemStack.hasTagCompound()){
			tag = par1ItemStack.getTagCompound();
		}else{
			tag = new NBTTagCompound();
			par1ItemStack.setTagCompound(tag);
		}

		int var6 = this.getMaxItemUseDuration(par1ItemStack) - par4;

		boolean isEnchanted = par1ItemStack.isItemEnchanted();
		boolean isBewitched = par1ItemStack.hasDisplayName() && isEnchanted;

		if(RequiredChargeTick < var6 && isEnchanted){

			par3EntityPlayer.swingItem();

			Entity target = null;

			float distance = 30.0f;
			for(int dist = 2; dist < 20; dist+=2){
				AxisAlignedBB bb = par3EntityPlayer.boundingBox.copy();
				Vec3 vec = par3EntityPlayer.getLookVec();
				vec = vec.normalize();
				bb = bb.expand(2.0f, 0.25f, 2.0f);
				bb = bb.offset(vec.xCoord*(float)dist,vec.yCoord*(float)dist,vec.zCoord*(float)dist);

				List<Entity> list = par2World.getEntitiesWithinAABBExcludingEntity(par3EntityPlayer, bb);
				list = targetFilter(list);
				for(Entity curEntity : list){
					float curDist = curEntity.getDistanceToEntity(par3EntityPlayer);
					if(curDist < distance)
					{
						target = curEntity;
						distance = curDist;
					}
				}
				if(target != null)
					break;
			}

			if(target != null){

				par1ItemStack.damageItem(5, par3EntityPlayer);

				//target.spawnExplosionParticle();
	            par2World.spawnParticle("largeexplode",
	            		target.posX ,
	            		target.posY + target.height,
	            		target.posZ ,
	            		3.0, 3.0, 3.0);
	            par2World.spawnParticle("largeexplode",
	            		target.posX + 1.0 ,
	            		target.posY + target.height +1.0,
	            		target.posZ ,
	            		3.0, 3.0, 3.0);
	            par2World.spawnParticle("largeexplode",
	            		target.posX  ,
	            		target.posY + target.height +0.5,
	            		target.posZ + 1.0,
	            		3.0, 3.0, 3.0);

				AxisAlignedBB bb = target.boundingBox.copy();
				bb = bb.expand(2.0f, 0.25f, 2.0f);

				List<Entity> list = par2World.getEntitiesWithinAABBExcludingEntity(par3EntityPlayer, bb);
				list = targetFilter(list);
				for(Entity curEntity : list){


					curEntity.hurtResistantTime = 0;
					par3EntityPlayer.attackTargetEntityWithCurrentItem(curEntity);
	                par3EntityPlayer.onCriticalHit(curEntity);
				}

			}
			tag.setInteger("comboSeq", 5);


		}else{
			tag.setBoolean("onClick", true);
		}

	}

    private NBTTagCompound getAttrTag(String attrName ,AttributeModifier par0AttributeModifier)
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

	@Override
	public void onUpdate(ItemStack sitem, World par2World,
			Entity par3Entity, int indexOfMainSlot, boolean isCurrent) {




		NBTTagCompound tag;
		if(sitem.hasTagCompound()){
			tag = sitem.getTagCompound();
		}else{
			tag = new NBTTagCompound();
			sitem.setTagCompound(tag);
		}
		boolean isBroken = tag.getBoolean("isBroken");
		int curDamage = sitem.getItemDamage();

		boolean isBewitched = sitem.hasDisplayName() && sitem.isItemEnchanted();

    	float tagAttackAmplifier = tag.getFloat("attackAmplifier");
		float attackAmplifier = 0;
        if(!isBroken && isBewitched && par3Entity instanceof EntityPlayer){
        	float tmp = ((EntityPlayer)par3Entity).experienceLevel;
        	tmp = 1.0f + (float)( tmp < 15.0f ? tmp * 0.5f : tmp < 30.0f ? 3.0f +tmp*0.45f : 7.0f+0.4f * tmp);
        	attackAmplifier = tmp;
        }else if(isBroken){
        	attackAmplifier = -4;
        }
        if(tagAttackAmplifier != attackAmplifier)
        {
        	tag.setFloat("attackAmplifier", attackAmplifier);

        	NBTTagList attrTag = null;
        	/*if(tag.hasKey("AttributeModifiers")){
        		attrTag = tag.getTagList("AttributeModifiers");
        	}else{
	    		attrTag = new NBTTagList();
	    		tag.setTag("AttributeModifiers",attrTag);
        	}*/

    		attrTag = new NBTTagList();
    		tag.setTag("AttributeModifiers",attrTag);

        	attrTag.appendTag(
        			getAttrTag(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),new AttributeModifier(field_111210_e, "Weapon modifier", (double)(attackAmplifier + 4.0F + EnumToolMaterial.EMERALD.getDamageVsEntity()), 0))
        			);
        }

        if(isBewitched && par3Entity instanceof EntityPlayer){
        	int nowExp = ((EntityPlayer)par3Entity).experienceTotal;

        	if(!tag.hasKey("prevExp")){
        		tag.setInteger("prevExp", nowExp);
        	}

        	int prevExp = tag.getInteger("prevExp");

        	int repair = nowExp - prevExp;
        	if(repair < 0){
        		repair = 0;
        	}else if(10 < repair ){
        		repair = 10;
        	}

			sitem.setItemDamage(Math.max(0,curDamage-repair));

    		tag.setInteger("prevExp", ((EntityPlayer)par3Entity).experienceTotal);
        }

		if(!isCurrent && !par2World.isRemote){
			if(isBewitched && par3Entity instanceof EntityPlayer && 0 < curDamage && par2World.getTotalWorldTime() % 20 == 0){
				EntityPlayer pl = (EntityPlayer)par3Entity;

				int idx = Arrays.asList(pl.inventory.mainInventory).indexOf(sitem);

				//pl.experienceTotal = 0;
				//pl.experience = 0;
				if(0<= idx && idx < 9 && 0 < pl.experienceLevel){
					int repair;
					int descExp;

					if(isBroken){
			            pl.addExhaustion(0.025F);
						repair = 10;
						descExp = 5;
					}else{
						repair = 1;
						descExp = 1;
			            pl.addExhaustion(0.025F);
					}

					if(0 < curDamage){
						sitem.setItemDamage(Math.max(0,curDamage-repair));
					}

					for(;descExp > 0;descExp--){
						pl.addExperience(-1);

						if(pl.experience < 0){
							if(pl.experienceLevel <= 0){
								pl.experience = 0;
							}else{
								pl.experienceLevel--;
								pl.experience = 1.0f - (0.9f/pl.xpBarCap());
							}
						}
					}
				}
			}
		}

		int combo = tag.getInteger("comboSeq");

		if(par3Entity instanceof EntityLivingBase){
			EntityLivingBase el = (EntityLivingBase)par3Entity;

			if(isCurrent){
				long prevAttackTime = tag.getLong("prevAttackTime");
				long currentTime =par2World.getTotalWorldTime();

				if(tag.getBoolean("onClick")){

					//sitem.setItemDamage(1320);
					if(prevAttackTime + ComboInterval < currentTime){
						tag.setBoolean("onClick", false);
						tag.setLong("prevAttackTime", currentTime);


						if(combo >= 3){
							combo = 0;
						}
						combo = combo+1;//getNextCombo(param,combo);
						tag.setInteger("comboSeq", combo);

						if(combo>=3 && isBroken && curDamage == 0){
							isBroken = false;
							tag.setBoolean("isBroken", false);
						}

						el.isSwingInProgress = true;

						AxisAlignedBB bb = el.boundingBox.copy();
						Vec3 vec = el.getLookVec();
						vec.yCoord = 0.000001;
						vec = vec.normalize();
						if(combo >= 3){
							if(isBroken){
								bb = bb.expand(1.0f, 0.0f, 1.0f);
								bb = bb.offset(vec.xCoord*1.0f,0,vec.zCoord*1.0f);
							}else if(isBewitched && curDamage == 0){
								bb = bb.expand(5.0f, 0.25f, 5.0f);
								sitem.damageItem(10, el);

								Random rand = ((EntityLivingBase) par3Entity).getRNG();
								for(int spread = 0 ; spread < 12 ;spread ++){
									float xSp = rand.nextFloat() * 2 - 1.0f;
									float zSp = rand.nextFloat() * 2 - 1.0f;
									xSp += 0.2 * Math.signum(xSp);
									zSp += 0.2 * Math.signum(zSp);
						            par2World.spawnParticle("largeexplode",
						            		el.posX + 3.0f*xSp,
						            		el.posY,
						            		el.posZ + 3.0f*zSp,
						            		1.0, 1.0, 1.0);
								}
							}else{
								bb = bb.expand(2.0f, 0.25f, 2.0f);
								bb = bb.offset(vec.xCoord*2.5f,0,vec.zCoord*2.5f);
							}
						}else{
							bb = bb.expand(1.2f, 0.25f, 1.2f);
							bb = bb.offset(vec.xCoord*2.0f,0,vec.zCoord*2.0f);
						}

						List<Entity> list = par2World.getEntitiesWithinAABBExcludingEntity(el, bb);
						Object[] olist = list.toArray();
						for(Object curObj : olist){
							if(!(curObj instanceof Entity)){
								continue;
							}
							Entity curEntity = (Entity)curObj;

							if(curEntity instanceof EntityArrow || curEntity instanceof EntityTNTPrimed){
								curEntity.setVelocity(0, 0, 0);
								curEntity.setDead();

						        for (int var1 = 0; var1 < 20; ++var1)
						        {
						        	Random rand = el.getRNG();
						            double var2 = rand.nextGaussian() * 0.02D;
						            double var4 = rand.nextGaussian() * 0.02D;
						            double var6 = rand.nextGaussian() * 0.02D;
						            double var8 = 10.0D;
						            par2World.spawnParticle("explode", curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8, curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8, curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8, var2, var4, var6);
						        }

								continue;
							}

							if(curEntity instanceof EntityFireball){
								curEntity.attackEntityFrom(DamageSource.causeMobDamage(el),1);
								continue;
							}


							String entityStr = EntityList.getEntityString(curEntity);

							//含む
							if(!(curEntity instanceof EntityDragonPart
								|| (entityStr != null && SlashBlade.attackableTargets.containsKey(entityStr) && SlashBlade.attackableTargets.get(entityStr))
								))
								continue;

							if(el instanceof EntityPlayer && combo >= 3){

								curEntity.hurtResistantTime = 0;
								((EntityPlayer)el).attackTargetEntityWithCurrentItem(curEntity);
								((EntityPlayer)el).onCriticalHit(curEntity);

								float knockbackFactor = 1.5f;
								curEntity.addVelocity((double)(-MathHelper.sin(el.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackFactor * 0.5F), 0.2D, (double)(MathHelper.cos(el.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackFactor * 0.5F));

							}else{

								float atack = 4.0f + EnumToolMaterial.STONE.getDamageVsEntity(); //stone like

								/*
								if (el.isPotionActive(Potion.damageBoost))
				                {
				                    atack += 3 << el.getActivePotionEffect(Potion.damageBoost).getAmplifier();
				                }

				                if (el.isPotionActive(Potion.weakness))
				                {
				                    atack -= 2 << el.getActivePotionEffect(Potion.weakness).getAmplifier();
				                }
				                */

								if (curEntity instanceof EntityLivingBase)
				                {
					                float var4 = 0;
				                    var4 = EnchantmentHelper.getEnchantmentModifierLiving(el, (EntityLiving)curEntity);
					                if(var4 > 0)
					                	atack += var4;
				                }

				                if(!isBroken && isBewitched && el instanceof EntityPlayer){
				                	atack += (int)(((EntityPlayer)el).experienceLevel * 0.25);
				                }


				                if (curEntity instanceof EntityLivingBase){
				                	atack = Math.min(atack,((EntityLivingBase)curEntity).getHealth()-1);
				                }


								curEntity.hurtResistantTime = 0;
								curEntity.attackEntityFrom(DamageSource.causeMobDamage(el), atack);

								curEntity.motionX = 0;
								curEntity.motionY = 0;
								curEntity.motionZ = 0;


				                if (curEntity instanceof EntityLivingBase){
				                	((EntityLivingBase)curEntity).setRevengeTarget(null);

				                	((EntityLivingBase)curEntity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),10,30,true));
				                	((EntityLivingBase)curEntity).addPotionEffect(new PotionEffect(Potion.weakness.getId(),10,30,true));
				                }
							}
						}

					}
				}else{
					if(prevAttackTime +ComboResetTicks < currentTime && (combo <= 3 || (par3Entity instanceof EntityLivingBase && ((EntityLivingBase)par3Entity).swingProgressInt == 0))){
						if(!(par3Entity instanceof EntityPlayer && ((EntityPlayer)par3Entity).isUsingItem()))
							tag.setInteger("comboSeq", 0);
					}
				}
			}else{
				if(tag.getInteger("comboSeq") != 0)
					tag.setInteger("comboSeq", 0);
			}

			if(par2World.isRemote && sitem.equals(el.getHeldItem())){

				int eId = tag.getInteger("TargetEntity");

				if(el.isSneaking()){
					if(eId == 0){
						AxisAlignedBB bb = par3Entity.boundingBox.copy();
						bb = bb.expand(10, 5, 10);
						float distance = 20.0f;
						List<Entity> list = par2World.getEntitiesWithinAABBExcludingEntity(par3Entity, bb);
						list = targetFilter(list);
						for(Entity curEntity : list){
							float curDist = curEntity.getDistanceToEntity(par3Entity);
							if(curDist < distance)
							{
								eId = curEntity.entityId;
								distance = curDist;
							}
						}
						tag.setInteger("TargetEntity", eId);
					}
					Entity target = par2World.getEntityByID(eId);
					if(target != null && el instanceof EntityLivingBase)
						this.faceEntity(el,target, 1000.0f,1000.0f);

				}else if(eId != 0){
					tag.setInteger("TargetEntity", 0);
				}
			}
		}
	}

    /**
     * Changes pitch and yaw so that the entity calling the function is facing the entity provided as an argument.
     */
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
            d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (owner.posY + (double)owner.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        owner.rotationPitch = this.updateRotation(owner.rotationPitch, f3, par3);
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

	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

		if(par1ItemStack.isItemEnchanted()){
			if(par1ItemStack.hasDisplayName()){
				par3List.add(StatCollector.translateToLocal("flammpfeil.swaepon.info.bewitched"));
			}else{
				par3List.add(StatCollector.translateToLocal("flammpfeil.swaepon.info.magic"));
			}
		}else{
			par3List.add(StatCollector.translateToLocal("flammpfeil.swaepon.info.noname"));
		}
	}

}
