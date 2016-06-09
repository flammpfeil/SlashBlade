package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.entity.EntityEx;
import mods.flammpfeil.slashblade.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 2016/06/09.
 */
public abstract class RenderEx extends Render{
    public boolean renderOutlines = false;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        renderOutlines = false;

        doRenderEx(entity, x, y, z, yaw, partialRenderTick);

        if(entity instanceof EntityEx){
            boolean isGlowing = ((EntityEx) entity).isGlowing();

            if(isGlowing){
                GlStateManager.depthFunc(GL11.GL_ALWAYS);
                GlStateManager.disableFog();
                RenderHelper.disableStandardItemLighting();

                renderOutlines = true;
                doRenderEx(entity, x, y, z, yaw, partialRenderTick);
                renderOutlines = false;

                RenderHelper.enableStandardItemLighting();
                GlStateManager.depthMask(false);

                GlStateManager.enableLighting();

                GlStateManager.depthMask(true);
                GlStateManager.enableFog();
                GlStateManager.enableBlend();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
                /**/
            }
        }
    }

    public abstract void doRenderEx(Entity entity, double x, double y, double z, float yaw, float partialRenderTick);
}
