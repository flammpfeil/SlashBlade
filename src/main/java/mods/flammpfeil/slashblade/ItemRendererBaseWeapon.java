package mods.flammpfeil.slashblade;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade.ComboSequence;
import mods.flammpfeil.slashblade.ItemSlashBlade.SwordType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;
import java.util.Map;

public class ItemRendererBaseWeapon implements IItemRenderer {

	static TextureManager engine(){
		return FMLClientHandler.instance().getClient().renderEngine;
	}

    private static final ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    static IModelCustom modelBlade = null;

    static ResourceLocation resourceModel = new ResourceLocation("flammpfeil.slashblade","model/blade.obj");

    public ItemRendererBaseWeapon(){
        if(modelBlade == null)
            modelBlade = AdvancedModelLoader.loadModel(resourceModel);
    }

    static public Map<ResourceLocation,IModelCustom> models= Maps.newHashMap();
    static public IModelCustom getModel(ResourceLocation loc){
        IModelCustom result;
        if(models.containsKey(loc)){
            result = models.get(loc);
        }else{
            try{
                result = AdvancedModelLoader.loadModel(loc);
            }catch(ModelFormatException e){
                result = null;
            }

            models.put(loc,result);
        }

        return result != null ? result : modelBlade;
    }

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		switch (type) {
		case ENTITY:
			return true;
		case EQUIPPED:
			return true;
		case INVENTORY:
			return true;
        case EQUIPPED_FIRST_PERSON:
            return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
        boolean result = false;

        switch (helper) {
            case ENTITY_ROTATION:
                result = true;
                break;
            case INVENTORY_BLOCK:
                result = false;
                break;
            default:
                break;
        }
        if(!result){
            switch (type){
                case ENTITY:
                    result = false;
                    break;
                case EQUIPPED_FIRST_PERSON:
                    result = false;
                    break;
                case EQUIPPED:
                    result = false;
                    break;
                case INVENTORY:
                    result = false;
                    break;
                default:
                    result = true;
                    break;
            }
        }

        return result;
	}

    private void renderItemLocal(ItemRenderType type, ItemStack item, Object... data) {
        boolean isBroken = false;
        EnumSet<SwordType> types = ((ItemSlashBlade)item.getItem()).getSwordType(item);
        isBroken = types.contains(SwordType.Broken);

        IModelCustom model = getModel(ItemSlashBlade.getModelLocation(item));
        ResourceLocation resourceTexture = ((ItemSlashBlade)item.getItem()).getModelTexture(item);

        boolean isHandled = false;

        switch (type) {
            case ENTITY:
            {
                GL11.glTranslatef(0.0f, 0.32f, 0.04f);
                float scale = 0.01f;
                GL11.glScalef(scale, scale, scale);

                isHandled = true;
                break;
            }
            case INVENTORY:
            {
                GL11.glTranslatef(08.0f, 8.0f, 0.0f);
                GL11.glRotatef(180, 0, 0, 1);
                float scale = 0.13f;
                GL11.glScalef(-scale,scale,scale);

                isHandled = true;
                break;
            }
            case EQUIPPED:
                if(data[1] instanceof EntityPlayer
                        && !types.contains(SwordType.NoScabbard)){
                    return;
                }
                break;

            case EQUIPPED_FIRST_PERSON:
            {
                if(!types.contains(SwordType.NoScabbard)){
                    engine().bindTexture(resourceTexture);
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glPushMatrix();
                    GL11.glPushMatrix();
                    GL11.glPushMatrix();

                    GL11.glTranslatef(-0.35F,-0.1f,-0.8f);
                    //GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-3.0F, 1.0F, 0.0F, 0.0f);
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

                    float partialRenderTick = ticks;
                    EntityPlayer player = (EntityPlayer)data[1];
                    render(player, partialRenderTick,false);
                    return;
                }
            }

            default:
                break;
        }

        if(isHandled){


            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDisable(GL11.GL_CULL_FACE);

            GL11.glColor4f(1, 1, 1, 1.0F);

            GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, ad renderEffect can derp them up.
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            engine().bindTexture(resourceTexture);

            String renderTarget;
            if(isBroken)
                renderTarget = "item_damaged";
            else if(!types.contains(SwordType.NoScabbard)){
                renderTarget = "item_blade";
            }else{
                renderTarget = "item_bladens";
            }

            model.renderPart(renderTarget);

            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);

            GL11.glEnable(GL11.GL_CULL_FACE);

            GL11.glPopAttrib();

            if (item.hasEffect(0))
            {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                engine().bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glPopAttrib();
            }

        }
        else
        {
            engine().bindTexture(resourceTexture);

            GL11.glTranslatef(0.8f,0.2f,0);
            float scale = 0.008f;
            GL11.glScalef(scale,scale,scale);
            GL11.glRotatef(-60, 0, 0, 1);

            String renderTargets[];
            if(data[1] instanceof EntityPlayer
                    || types.contains(SwordType.NoScabbard)){

                if(isBroken){
                    renderTargets = new String[]{"blade_damaged"};
                }else{
                    renderTargets = new String[]{"blade"};
                }
            }else{
                renderTargets = new String[]{"sheath", "blade"};
            }

            model.renderOnly(renderTargets);
        }

    }
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

        GL11.glPushMatrix();

        renderItemLocal(type, item, data);

        GL11.glPopMatrix();
	}
	static float ticks = 0.0f;
    @SubscribeEvent
    public void HandleRenderWorldLastEvent(RenderWorldLastEvent event){
        ticks = event.partialTicks;
    }

	@SubscribeEvent
	public void RenderPlayerEventPre(RenderPlayerEvent.Specials.Pre event){
		float partialRenderTick = event.partialRenderTick;
		EntityPlayer player = event.entityPlayer;
		render(player,partialRenderTick);
	}


    static private float interpolateRotation(float par1, float par2, float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }

    static public void render(EntityLivingBase entity,float partialRenderTick){
        render(entity,partialRenderTick,false);
    }
	static public void render(EntityLivingBase entity,float partialRenderTick, boolean adjust)
	{
        if(entity == null || !(entity instanceof EntityPlayer))
			return;

        EntityPlayer player = (EntityPlayer)entity;

		ItemStack item = player.getCurrentEquippedItem();


		if(item == null || !(item.getItem() instanceof ItemSlashBlade))
			return;


		ItemSlashBlade iSlashBlade = ((ItemSlashBlade)item.getItem());

        IModelCustom model = getModel(ItemSlashBlade.getModelLocation(item));

        ResourceLocation resourceTexture = ItemSlashBlade.getModelTexture(item);

		EnumSet<SwordType> swordType = iSlashBlade.getSwordType(item);

        if(swordType.contains(SwordType.NoScabbard)){
            return;
        }

		boolean isEnchanted = swordType.contains(SwordType.Enchanted);
		boolean isBewitched = swordType.contains(SwordType.Bewitched);


		int charge = player.getItemInUseDuration();


        float ax = 0;
        float ay = 0;
        float az = 0;

		boolean isBroken = swordType.contains(SwordType.Broken);
		ItemSlashBlade.ComboSequence combo = ComboSequence.None;
		if(item.hasTagCompound()){
			NBTTagCompound tag = item.getTagCompound();

			combo = iSlashBlade.getComboSequence(tag);

            if(adjust){
                ax = tag.getFloat(ItemSlashBlade.adjustXStr)/10.0f;
                ay = -tag.getFloat(ItemSlashBlade.adjustYStr)/10.0f;
                az = -tag.getFloat(ItemSlashBlade.adjustZStr)/10.0f;
            }
		}


		float progress = player.getSwingProgress(partialRenderTick);

		if((!combo.equals(ComboSequence.None)) && player.swingProgress == 0.0f)
			progress = 1.0f;

		progress *= 1.2;
		if(1.0f < progress)
			progress = 1.0f;

		//progress = (player.ticksExisted % 10) / 10.0f;

		switch(combo){
		case Iai:
			progress = 1.0f - (Math.abs(progress-0.5f) * 2.0f);

			break;

		default :
			progress = 1.0f - progress;
			progress = 1.0f - (float)Math.pow(progress,2.0);

			break;
		}

		if(!isBroken && isEnchanted && ItemSlashBlade.RequiredChargeTick < charge){
			progress = 0.0f;
			combo = ComboSequence.None;
		}




        String renderTarget;

		GL11.glPushMatrix();
        {
            GL11.glColor3f(1.0F, 1.0F, 1.0F);

			//体格補正 configより
            GL11.glTranslatef(ax,ay,az);

			//腰位置へ
			GL11.glTranslatef(0.25f,0.4f,-0.5f);


			{
				//全体スケール補正
				float scale = (float)(0.075f);
				GL11.glScalef(scale, scale, scale);
			}

			//先を後ろへ
			GL11.glRotatef(60.0f, 1, 0, 0);

			//先を外へ
			GL11.glRotatef(-20.0f, 0, 0, 1);

			//刃を下に向ける（太刀差し
			GL11.glRotatef(90.0f, 0, 1.0f, 0);


			float xoffset = 10.0f;
			float yoffset = 8.0f;

			//-----------------------------------------------------------------------------------------------------------------------
			GL11.glPushMatrix();{


				if(!combo.equals(ComboSequence.None)){

					float tmp = progress;

					if(combo.swingAmplitude < 0){
						progress = 1.0f - progress;
					}
					//GL11.glRotatef(-90, 0.0f, 1.0f, 0.0f);

					if(combo.equals(ComboSequence.Kiriorosi)){


						GL11.glRotatef(20.0f, -1.0f, 0, 0);
						GL11.glRotatef(-30.0f, 0, 0, -1.0f);


                        GL11.glTranslatef(0.0f,0.0f,-8.0f);
                        //GL11.glRotatef(-30.0f,1,0,0);


						GL11.glRotatef((90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);

						GL11.glRotatef((1.0f-progress) * -90.0f, 0.0f, 0.0f, -1.0f);
						GL11.glTranslatef(0.0f, (1.0f-progress) * -5.0f, 0.0f);
						GL11.glTranslatef((1.0f-progress) * 10.0f, 0.0f, 0.0f);

						GL11.glTranslatef(-xoffset , 0.0f, 0.0f );
						GL11.glTranslatef(0.0f, -yoffset, 0.0f);

						progress = 1.0f;

						if(0 < combo.swingAmplitude){
							GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
						}else{
							GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
						}

						GL11.glTranslatef(0.0f, yoffset, 0.0f);
						GL11.glTranslatef(xoffset , 0.0f, 0.0f );
                        GL11.glRotatef(180.0f, 0, 1, 0);
					}else{

						GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
						GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


						GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


						GL11.glTranslatef(-xoffset , 0.0f, 0.0f );


						GL11.glTranslatef(0.0f, -yoffset, 0.0f);

						if(0 < combo.swingAmplitude){
							GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
						}else{
							GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
						}

						GL11.glTranslatef(0.0f, yoffset, 0.0f);
						GL11.glTranslatef(xoffset , 0.0f, 0.0f );
					}


					progress = tmp;
				}

            if(isBroken)
                renderTarget = "blade_damaged";
            else
                renderTarget = "blade";


            float scaleLocal = 0.095f;
            GL11.glScalef(scaleLocal,scaleLocal,scaleLocal);
            GL11.glRotatef(-90.0f, 0, 0, 1);
            engine().bindTexture(resourceTexture);
            model.renderPart(renderTarget);

            if (item.hasEffect(0))
            {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                engine().bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glPopAttrib();
            }

			}GL11.glPopMatrix();

			//-----------------------------------------------------------------------------------------------------------------------

			GL11.glPushMatrix();{


				if((!combo.equals(ComboSequence.None)) && combo.useScabbard){


					if(combo.swingAmplitude < 0){
						progress = 1.0f - progress;
					}

					GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
					GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


					GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


					GL11.glTranslatef(-xoffset , 0.0f, 0.0f );


					GL11.glTranslatef(0.0f, -yoffset, 0.0f);

					if(0 < combo.swingAmplitude){
						GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
					}else{
						GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
					}

					GL11.glTranslatef(0.0f, yoffset, 0.0f);
					GL11.glTranslatef(xoffset , 0.0f, 0.0f );

				}


				GL11.glPushMatrix();

            float scaleLocal = 0.095f;
            GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
            GL11.glRotatef(-90.0f, 0, 0, 1);
            engine().bindTexture(resourceTexture);

            renderTarget = "sheath";
            model.renderPart(renderTarget);

            if (item.hasEffect(0))
            {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                engine().bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glPopAttrib();
            }

				GL11.glPopMatrix();

				if(!isBroken && isEnchanted && (ItemSlashBlade.RequiredChargeTick < charge || combo.isCharged)){
					GL11.glPushMatrix();
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

						GL11.glPushMatrix();

			                GL11.glEnable(GL11.GL_BLEND);
			                float f4 = 3.0F;
			                GL11.glColor4f(f4, f4, f4, 3.0F);
			                GL11.glDisable(GL11.GL_LIGHTING);
			                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);


                            GL11.glPushMatrix();
                                GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                                GL11.glRotatef(-90.0f, 0, 0, 1);
                                model.renderPart("sheath");

                            GL11.glPopMatrix();

                            GL11.glEnable(GL11.GL_LIGHTING);
                            GL11.glDisable(GL11.GL_BLEND);
                        GL11.glPopMatrix();

		                float ff1 = (float)player.ticksExisted + partialRenderTick;
		                engine().bindTexture(armoredCreeperTextures);
		                GL11.glMatrixMode(GL11.GL_TEXTURE);
		                GL11.glLoadIdentity();
		                float f2 = ff1 * 0.03F;
		                float f3 = ff1 * 0.02F;
		                GL11.glTranslatef(f2, -f3, 0.0F);
		                GL11.glMatrixMode(GL11.GL_MODELVIEW);
		                GL11.glEnable(GL11.GL_BLEND);
		                f4 = 1.0F;
		                GL11.glColor4f(f4, f4, f4, 1.0F);
		                GL11.glDisable(GL11.GL_LIGHTING);
		                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

                        //GL11.glTranslatef(-1f, 0.0f, -0.5f);

                        GL11.glPushMatrix();
                            GL11.glScalef(scaleLocal,scaleLocal,scaleLocal);
                            GL11.glRotatef(-90.0f,0,0,1);
                            model.renderPart("effect");

                        GL11.glPopMatrix();

		                GL11.glMatrixMode(GL11.GL_TEXTURE);
		                GL11.glLoadIdentity();
		                GL11.glMatrixMode(GL11.GL_MODELVIEW);
		                GL11.glEnable(GL11.GL_LIGHTING);
		                GL11.glDisable(GL11.GL_BLEND);

						GL11.glPopAttrib();

					GL11.glPopMatrix();
				}
			}GL11.glPopMatrix();

			//-----------------------------------------------------------------------------------------------------------------------
		}GL11.glPopMatrix();
	}
}
