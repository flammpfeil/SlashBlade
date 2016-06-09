package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Created by Furia on 14/08/15.
 */
public class GrimGripRender extends RenderEx{

    static public IModelCustom model = null;

    static public ResourceLocation modelLocation = new ResourceLocation("flammpfeil.slashblade","model/util/grim_grip.obj");
    static public ResourceLocation textureLocation = new ResourceLocation("flammpfeil.slashblade","model/util/grim_grip.png");

    private TextureManager engine(){
        return this.renderManager.renderEngine;
    }

    @Override
    public void doRenderEx(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(renderOutlines){
            GlStateManager.disableLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

            GlStateManager.enableColorMaterial();
            float cycleTicks = 40.0f;
            float b = Math.abs((entity.ticksExisted % cycleTicks) / cycleTicks - 0.5f) + 0.5f;
            GlStateManager.enableOutlineMode(Color.getHSBColor(0, 0.0f,b).getRGB());
        }

        renderModel(entity, x, y, z, yaw, partialRenderTick);

        if(renderOutlines){
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();

            GlStateManager.enableLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

    }
    public void renderModel(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(model == null){
            model = AdvancedModelLoader.loadModel(modelLocation);
        }

        this.bindEntityTexture(entity);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);


        int color = 0x5555FF;// entityPhantomSword.getColor();

        boolean inverse = color < 0;

        color = Math.abs(color) | 0xFF000000;

        if(!inverse){
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }
        else{
            GlStateManager.blendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
        }

        GL11.glTranslatef((float) x, (float) y, (float) z);

        float rotParTicks = 40.0f;
        float rot = ((entity.ticksExisted % rotParTicks) / rotParTicks) * 360.f + partialRenderTick * (360.0f / rotParTicks);

        GL11.glRotatef(rot, 0, 1, 0);

        float scale = 0.005f;
        GL11.glScalef(scale, scale, scale);


        float lastx = OpenGlHelper.lastBrightnessX;
        float lasty = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        Tessellator.instance.setColorOpaque_I(color);

        model.renderAll();

        //Tessellator.instance.setColorOpaque_I(-1);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);


        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return textureLocation;
    }
}
