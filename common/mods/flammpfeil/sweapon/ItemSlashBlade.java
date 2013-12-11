package mods.flammpfeil.sweapon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

public class ItemSlashBlade extends ItemSword {

	public float atackAmplifier = 0;

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
	@Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
        par1ItemStack.damageItem(1, par3EntityLivingBase);
        return true;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
	@Override
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)(this.atackAmplifier + EnumToolMaterial.EMERALD.getDamageVsEntity()), 0));
        return multimap;
    }

	@Override
	public void registerIcons(IconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("flammpfeil.slashblade:proudsoul");
	}

	public ItemSlashBlade(int par1, EnumToolMaterial par2EnumToolMaterial) {
		super(par1, par2EnumToolMaterial);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {
		return false;// super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack sitem, World par2World,
			EntityPlayer par3EntityPlayer) {


		NBTTagCompound tag;
		if(sitem.hasTagCompound()){
			tag = sitem.getTagCompound();
		}else{
			tag = new NBTTagCompound();
			sitem.setTagCompound(tag);
		}

		if(!par3EntityPlayer.isEating())
			tag.setBoolean("onClick", true);

		return super.onItemRightClick(sitem, par2World, par3EntityPlayer);
	}

	/*
	@Override
	public boolean itemInteractionForEntity(ItemStack sitem,
			EntityLiving par2EntityLiving) {

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

			//含む
			if((curEntity instanceof IMob
				|| curEntity instanceof EntityDragonPart
				))
				result.add(curEntity);
		}
		return result;
	}


	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer, int par4) {

		int var6 = this.getMaxItemUseDuration(par1ItemStack) - par4;

		boolean isEnchanted = par1ItemStack.isItemEnchanted();
		boolean isBewitched = par1ItemStack.hasDisplayName() && isEnchanted;

		if(15 < var6 && isEnchanted){

			NBTTagCompound tag;
			if(par1ItemStack.hasTagCompound()){
				tag = par1ItemStack.getTagCompound();
			}else{
				tag = new NBTTagCompound();
				par1ItemStack.setTagCompound(tag);
			}

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


		}
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

        if(isBewitched && par3Entity instanceof EntityPlayer){
        	int tmp = ((EntityPlayer)par3Entity).experienceLevel;
        	tmp = 1 + (int)( tmp < 15 ? tmp * 0.5 : tmp < 30 ? 3+tmp*0.45 : 7+0.4 * tmp);
        	atackAmplifier = tmp;

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
			            pl.addExhaustion(0.05F);
						repair = 10;
						descExp = 5;
					}else{
						repair = 2;
						descExp = 1;
			            pl.addExhaustion(0.05F);
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

		if(par3Entity instanceof EntityLivingBase){
			EntityLivingBase el = (EntityLivingBase)par3Entity;

			if(isCurrent){
				long prevAtackTime = tag.getLong("prevAtackTime");
				long currentTime =par2World.getTotalWorldTime();

				if(tag.getBoolean("onClick")){

					//sitem.setItemDamage(1320);
					if(prevAtackTime + 3 < currentTime){
						tag.setBoolean("onClick", false);
						tag.setLong("prevAtackTime", currentTime);


						int combo = tag.getInteger("comboSeq");
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

							//含む
							if(!(curEntity instanceof IMob
								|| curEntity instanceof EntityDragonPart
								))
								continue;

							if(el instanceof EntityPlayer && combo >= 3){

								curEntity.hurtResistantTime = 0;
								((EntityPlayer)el).attackTargetEntityWithCurrentItem(curEntity);
								((EntityPlayer)el).onCriticalHit(curEntity);

								float knockbackFactor = 2.0f;
								curEntity.addVelocity((double)(-MathHelper.sin(el.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackFactor * 0.5F), 0.3D, (double)(MathHelper.cos(el.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackFactor * 0.5F));


								float damagePer = 1.0f - (curDamage / (float)sitem.getMaxDamage());
								if(isBewitched && !isBroken && damagePer < 0.2f){
									tag.setBoolean("isBroken", true);
									el.renderBrokenItemStack(sitem);

									if(!par2World.isRemote)
										el.entityDropItem(new ItemStack(SlashBlade.proudSoul,1), 0.0F);
								}
							}else{

								float atack = 1; //stone like
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


								curEntity.hurtResistantTime = 0;/*
				                if (curEntity instanceof EntityLivingBase){
				                	((EntityLivingBase) curEntity).setHealth(((EntityLivingBase) curEntity).getHealth()-atack);
				                	curEntity.playSound("damage.hit", 1.0f, 1.0f);
				                }
								else */
									curEntity.attackEntityFrom(DamageSource.causeMobDamage(el), atack);

								curEntity.setVelocity(0,0,0);


				                if (curEntity instanceof EntityLivingBase){
				                	((EntityLivingBase)curEntity).setRevengeTarget(null);
				                }
							}
						}

					}
				}else{
					if(prevAtackTime +12 < currentTime){
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
						Object[] olist = list.toArray();
						for(Object curObj : olist){
							if(!(curObj instanceof Entity)){
								continue;
							}
							Entity curEntity = (Entity)curObj;
							if(!(curEntity instanceof EntityCreature || (curEntity instanceof EntityLiving && curEntity instanceof IBossDisplayData)))
								continue;

							if(curEntity instanceof EntityAgeable)
								continue;


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
