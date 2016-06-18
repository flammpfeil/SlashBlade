package mods.flammpfeil.slashblade.client.renderer.entity;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.GroupObject;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntitySlashDimension;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by Furia on 14/08/15.
 */
public class RenderSlashDimension extends Render{

    static public WavefrontObject model = null;

    static public ResourceLocation modelLocation = new ResourceLocation("flammpfeil.slashblade","model/util/slashdim.obj");
    static public ResourceLocation textureLocation = new ResourceLocation("flammpfeil.slashblade","model/util/slashdim.png");

    public RenderSlashDimension(RenderManager renderManager) {
        super(renderManager);
    }

    private TextureManager engine(){
        return this.renderManager.renderEngine;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
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
            model = new WavefrontObject(modelLocation);
        }

        this.bindEntityTexture(entity);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);


        int color = 0x5555FF;

        int lifetime = 20;

        if(entity instanceof EntitySlashDimension) {
            color = ((EntitySlashDimension) entity).getColor();
            lifetime = ((EntitySlashDimension)entity).getLifeTime();
        }

        boolean inverse = color < 0;

        double deathTime = lifetime;
        double baseAlpha = Math.sin(Math.PI * 0.5 * (Math.min(deathTime, (lifetime - (entity.ticksExisted) - partialRenderTick)) / deathTime));
        int seed = entity.getEntityData().getInteger("seed");

        //todo: lifetime

        int baseColor = color;
        Color col = new Color(baseColor);
        float[] hsb = Color.RGBtoHSB(col.getRed(),col.getGreen(),col.getBlue(), null);
        baseColor = Color.getHSBColor(0.5f + hsb[0],hsb[1], 0.2f/*hsb[2]*/).getRGB() & 0xFFFFFF;
        baseColor = baseColor | (int)(0x66 * baseAlpha) << 24;

        GL11.glTranslatef((float) x, (float) y, (float) z);

        float rotParTicks = 40.0f;
        float rot = ((entity.ticksExisted % rotParTicks) / rotParTicks) * 360.f + partialRenderTick * (360.0f / rotParTicks);

        //GL11.glRotatef(rot, 0, 1, 0);

        float scale = 0.01f;
        GL11.glScalef(scale, scale, scale);


        float lastx = OpenGlHelper.lastBrightnessX;
        float lasty = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        Face.setColor(baseColor);

        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
        //GlStateManager.glBlendEquation(GL14.GL_FUNC_SUBTRACT);

        GL11.glPushMatrix();
        for(int i=0; i<5; i++){
            GL11.glScaled(0.95, 0.95, 0.95);
            model.renderPart("base");
        }
        GL11.glPopMatrix();


        int loop = 3;
        for(int i=0; i<loop; i++) {
            GL11.glPushMatrix();
            float ticks = 15;
            float wave = (entity.ticksExisted + (ticks / (float)loop * i) + partialRenderTick) % ticks;
            double waveScale = 1.0 + 0.03 * wave;
            GL11.glScaled(waveScale, waveScale, waveScale);
            Face.setColor((baseColor & 0xFFFFFF) | (int) (0x88 * ((ticks - wave) / ticks)) << 24);
            model.renderPart("base");
            GL11.glPopMatrix();
        }

        GlStateManager.glBlendEquation(GL14.GL_FUNC_ADD);


        int windCount = 5;
        for(int i = 0; i < windCount; i++){
            GL11.glPushMatrix();

            GL11.glRotated((360.0 / windCount) * i, 1, 0, 0);
            GL11.glRotated(30.0f , 0, 1, 0);

            double rotWind = 360.0 / 20.0;

            double offsetBase = 7;

            double offset = i * offsetBase;

            double motionLen = offsetBase * (windCount - 1);

            double ticks = entity.ticksExisted + partialRenderTick + seed;
            double offsetTicks = ticks + offset;
            double progress = (offsetTicks % motionLen) / motionLen;

            double rad = (Math.PI) * 2.0;
            rad *= progress;

            Face.setColor(color & 0xFFFFFF | (int)(Math.min(0,0xFF * Math.sin(rad))) << 24);

            double windScale = 0.4 + progress;
            GL11.glScaled(windScale,windScale,windScale);

            GL11.glRotated(rotWind * offsetTicks, 0, 0, 1);
            model.renderPart("wind");

            GL11.glPopMatrix();
        }

        Face.resetColor();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);


        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return textureLocation;
    }
}
