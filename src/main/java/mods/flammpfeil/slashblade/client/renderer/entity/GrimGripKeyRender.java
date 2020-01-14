package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.entity.EntityGrimGripKey;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Created by Furia on 14/08/15.
 */
public class GrimGripKeyRender extends Render{

    static public WavefrontObject model = null;

    static public ResourceLocationRaw modelLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/util/grim_grip.obj");
    static public ResourceLocationRaw textureLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/util/grim_grip.png");

    public GrimGripKeyRender(RenderManager renderManager) {
        super(renderManager);
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

        if(entity instanceof EntityGrimGripKey)
            renderModel((EntityGrimGripKey)entity, x, y, z, yaw, partialRenderTick);

        if(renderOutlines){
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();

            GlStateManager.enableLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

    }
    public void renderModel(EntityGrimGripKey entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(model == null){
            model = new WavefrontObject(modelLocation);
        }

        if(entity.isHide()) {
            boolean invisible = true;
            for(ItemStack stack : Minecraft.getMinecraft().player.getHeldEquipment()){
                if(!stack.isEmpty()
                    && (stack.getItem() instanceof ItemProudSoul)
                    && stack.getItemDamage() == 4) {
                    invisible = false;
                    break;
                }
            }
            if(invisible)
                return;
        }

        this.bindEntityTexture(entity);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);

        boolean isActive = 0 < entity.active;

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

        GL11.glTranslatef(0, -0.75f, 0);

        if(isActive){
            float rotParTicks = 40.0f;
            float rot = ((entity.ticksExisted % rotParTicks) / rotParTicks) * 360.f + partialRenderTick * (360.0f / rotParTicks);

            GL11.glRotatef(rot, 0, -1, 0);
        }

        GL11.glRotatef(180.0f, 1, 0, 0);

        float scale = 0.005f;
        GL11.glScalef(scale, scale, scale);


        float lastx = OpenGlHelper.lastBrightnessX;
        float lasty = OpenGlHelper.lastBrightnessY;
        if(isActive) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        }

        Face.setColor(color);

        model.renderAll();

        Face.resetColor();

        if(isActive) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);
        }


        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocationRaw getEntityTexture(Entity p_110775_1_) {
        return textureLocation;
    }
}
