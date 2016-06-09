package mods.flammpfeil.slashblade.client.renderer;

import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.nio.FloatBuffer;

/**
 * Created by Furia on 2016/06/09.
 */
public class GlStateManager {
    private static final FloatBuffer FLOAT_BUFFER_COLOR = BufferUtils.createFloatBuffer(4);

    public static void enableColorMaterial() {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
    }
    public static void disableColorMaterial() {
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
    }

    public static void enableOutlineMode(int p_187431_0_) {
        FLOAT_BUFFER_COLOR.put(0, (float)(p_187431_0_ >> 16 & 255) / 255.0F);
        FLOAT_BUFFER_COLOR.put(1, (float)(p_187431_0_ >> 8 & 255) / 255.0F);
        FLOAT_BUFFER_COLOR.put(2, (float)(p_187431_0_ >> 0 & 255) / 255.0F);
        FLOAT_BUFFER_COLOR.put(3, (float)(p_187431_0_ >> 24 & 255) / 255.0F);
        GL11.glTexEnv(8960, 8705, FLOAT_BUFFER_COLOR);
        GL11.glTexEnvi(8960, 8704, 34160);
        GL11.glTexEnvi(8960, 34161, 7681);
        GL11.glTexEnvi(8960, 34176, 34166);
        GL11.glTexEnvi(8960, 34192, 768);
        GL11.glTexEnvi(8960, 34162, 7681);
        GL11.glTexEnvi(8960, 34184, 5890);
        GL11.glTexEnvi(8960, 34200, 770);
    }
    public static void disableOutlineMode()
    {
        GL11.glTexEnvi(8960, 8704, 8448);
        GL11.glTexEnvi(8960, 34161, 8448);
        GL11.glTexEnvi(8960, 34162, 8448);
        GL11.glTexEnvi(8960, 34176, 5890);
        GL11.glTexEnvi(8960, 34184, 5890);
        GL11.glTexEnvi(8960, 34192, 768);
        GL11.glTexEnvi(8960, 34200, 770);
    }

    public static void enableLighting() {
        GL11.glEnable(GL11.GL_LIGHTING);
    }
    public static void disableLighting() {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void enableBlend() {
        GL11.glEnable(GL11.GL_BLEND);
    }
    public static void disableBlend() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void depthMask(boolean flagIn)
    {
        GL11.glDepthMask(flagIn);
    }

    public static void setActiveTexture(int texture)
    {
        OpenGlHelper.setActiveTexture(texture);
    }

    public static void enableTexture2D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    public static void disableTexture2D() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void pushAttrib()
    {
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_ENABLE_BIT);
    }
    public static void popAttrib()
    {
        GL11.glPopAttrib();
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }
    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void translate(float x, float y, float z)
    {
        GL11.glTranslatef(x, y, z);
    }
    public static void scale(float x, float y, float z)
    {
        GL11.glScalef(x, y, z);
    }
    public static void rotate(float angle, float x, float y, float z)
    {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        GL11.glBlendFunc(srcFactor, dstFactor);
    }

    public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha)
    {
        GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
    }

    public static void depthFunc(int depthFunc) {
        GL11.glDepthFunc(depthFunc);
    }

    public static void disableFog() {
        GL11.glDisable(GL11.GL_FOG);
    }
    public static void enableFog() {
        GL11.glEnable(GL11.GL_FOG);
    }

    public static void disableAlpha() {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }
    public static void enableAlpha() {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void disableDepth() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }
    public static void enableDepth() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void glBlendEquation(int func) {
        GL14.glBlendEquation(func);
    }

    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }
}
