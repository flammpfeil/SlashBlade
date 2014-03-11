package mods.flammpfeil.slashblade;

import mods.flammpfeil.slashblade.ItemSlashBlade.SwordType;
import mods.flammpfeil.slashblade.ItemSlashBlade.ComboSequence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;

import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.util.EnumSet;

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

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		switch (type) {
		case ENTITY:
			return true;
		case EQUIPPED:
			return true;
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		switch (helper) {
		case ENTITY_ROTATION:
			return true;
		default:
			return false;
		}
	}

    private void renderItemLocal(ItemRenderType type, ItemStack item, Object... data) {
        boolean isBroken = false;
        if(item.hasTagCompound()){
            NBTTagCompound tag = item.getTagCompound();

            isBroken = tag.getBoolean(ItemSlashBlade.isBrokenStr);
        }

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
                if(data[1] instanceof EntityPlayer){
                    return;
                }
                break;

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
            else
                renderTarget = "item_blade";

            modelBlade.renderPart(renderTarget);

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
                modelBlade.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                modelBlade.renderPart(renderTarget);
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
            modelBlade.renderOnly("sheath", "blade");

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

	static public void render(EntityLivingBase entity,float partialRenderTick)
	{
        if(entity == null || !(entity instanceof EntityPlayer))
			return;

        EntityPlayer player = (EntityPlayer)entity;

		ItemStack item = player.getCurrentEquippedItem();

		if(item == null || !(item.getItem() instanceof ItemSlashBlade))
			return;


		ItemSlashBlade iSlashBlade = ((ItemSlashBlade)item.getItem());

        ResourceLocation resourceTexture = iSlashBlade.getModelTexture(item);

		EnumSet<SwordType> swordType = iSlashBlade.getSwordType(item);

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

            ax = tag.getFloat(ItemSlashBlade.adjustXStr)/10.0f;
            ay = -tag.getFloat(ItemSlashBlade.adjustYStr)/10.0f;
            az = -tag.getFloat(ItemSlashBlade.adjustZStr)/10.0f;
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
            modelBlade.renderPart(renderTarget);

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
                modelBlade.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                modelBlade.renderPart(renderTarget);
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
            modelBlade.renderPart(renderTarget);

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
                modelBlade.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                modelBlade.renderPart(renderTarget);
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
                                modelBlade.renderPart("sheath");

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
                            modelBlade.renderPart("effect");

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
