package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.util.LightSetup;
import mods.flammpfeil.slashblade.client.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntitySlashDimension;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

/**
 * Created by Furia on 14/08/15.
 */
public class RenderSlashDimension extends Render{

    static public WavefrontObject model = null;

    static public ResourceLocationRaw modelLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/util/slashdim.obj");
    static public ResourceLocationRaw textureLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/util/slashdim.png");

    public RenderSlashDimension(RenderManager renderManager) {
        super(renderManager);
    }

    private TextureManager engine(){
        return this.renderManager.textureManager;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(renderOutlines){
            GlStateManager.enableColorMaterial();
            float cycleTicks = 40.0f;
            float b = Math.abs((entity.ticksExisted % cycleTicks) / cycleTicks - 0.5f) + 0.5f;
            GlStateManager.enableOutlineMode(Color.getHSBColor(0, 0.0f,b).getRGB());
        }

        renderModel(entity, x, y, z, yaw, partialRenderTick);

        if(renderOutlines){
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

    }
    public void renderModel(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(model == null){
            model = new WavefrontObject(modelLocation);
        }

        this.bindEntityTexture(entity);


        try(MSAutoCloser msacr = MSAutoCloser.pushMatrix()) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            GlStateManager.disableLighting();
            GlStateManager.enableBlend();


            int color = 0x5555FF;

            int lifetime = 20;

            if (entity instanceof EntitySlashDimension) {
                color = ((EntitySlashDimension) entity).getColor();
                lifetime = ((EntitySlashDimension) entity).getLifeTime();
            }

            boolean inverse = color < 0;

            double deathTime = lifetime;
            double baseAlpha = Math.sin(Math.PI * 0.5 * (Math.min(deathTime, (lifetime - (entity.ticksExisted) - partialRenderTick)) / deathTime));
            int seed = entity.getEntityData().getInt("seed");

            //todo: lifetime

            int baseColor = color;
            Color col = new Color(baseColor);
            float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
            baseColor = Color.getHSBColor(0.5f + hsb[0], hsb[1], 0.2f/*hsb[2]*/).getRGB() & 0xFFFFFF;
            baseColor = baseColor | (int) (0x66 * baseAlpha) << 24;

            GL11.glTranslatef((float) x, (float) y, (float) z);

            float rotParTicks = 40.0f;
            //float rot = ((entity.ticksExisted % rotParTicks) / rotParTicks) * 360.f + partialRenderTick * (360.0f / rotParTicks);

            float scale = 0.01f;
            GL11.glScalef(scale, scale, scale);


            try (LightSetup ls = LightSetup.setup()) {

                Face.setColor(baseColor);

                OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);

                try (MSAutoCloser msac = MSAutoCloser.pushMatrix()) {
                    for (int i = 0; i < 5; i++) {
                        GL11.glScaled(0.95, 0.95, 0.95);
                        model.renderPart("base");

                    }
                }

                int loop = 3;
                for (int i = 0; i < loop; i++) {
                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix()) {
                        float ticks = 15;
                        float wave = (entity.ticksExisted + (ticks / (float) loop * i) + partialRenderTick) % ticks;
                        double waveScale = 1.0 + 0.03 * wave;
                        GL11.glScaled(waveScale, waveScale, waveScale);
                        Face.setColor((baseColor & 0xFFFFFF) | (int) (0x88 * ((ticks - wave) / ticks)) << 24);
                        model.renderPart("base");
                    }
                }

                GlStateManager.blendEquation(GL14.GL_FUNC_ADD);


                int windCount = 5;
                for (int i = 0; i < windCount; i++) {
                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix()) {

                        GlStateManager.rotatef((float) (360.0 / windCount) * i, 1, 0, 0);
                        GlStateManager.rotatef(30.0f, 0, 1, 0);

                        double rotWind = 360.0 / 20.0;

                        double offsetBase = 7;

                        double offset = i * offsetBase;

                        double motionLen = offsetBase * (windCount - 1);

                        double ticks = entity.ticksExisted + partialRenderTick + seed;
                        double offsetTicks = ticks + offset;
                        double progress = (offsetTicks % motionLen) / motionLen;

                        double rad = (Math.PI) * 2.0;
                        rad *= progress;

                        Face.setColor(color & 0xFFFFFF | (int) (Math.min(0, 0xFF * Math.sin(rad))) << 24);

                        double windScale = 0.4 + progress;
                        GlStateManager.scaled(windScale, windScale, windScale);

                        GlStateManager.rotatef((float) (rotWind * offsetTicks), 0, 0, 1);
                        model.renderPart("wind");
                    }
                }

                Face.resetColor();
            }


            GlStateManager.disableBlend();
            GlStateManager.enableLighting();

            GlStateManager.shadeModel(GL11.GL_FLAT);
            GL11.glPopAttrib();
        }
    }

    @Override
    protected ResourceLocationRaw getEntityTexture(Entity p_110775_1_) {
        return textureLocation;
    }
}
