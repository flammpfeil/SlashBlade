package mods.flammpfeil.slashblade.client.renderer.entity;


import mods.flammpfeil.slashblade.client.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 14/05/08.
 */
@OnlyIn(Dist.CLIENT)
public class RenderPhantomSwordBase extends Render {
    protected double[][] getVec() {
        return dVec;
    }
    private static double[][] dVec = {
            {0.0000,0.0000,417.7431},
            {0.0000,-44.6113,-30.0000},
            {38.9907,0.0000,-20.0000},
            {0.0000,44.6113,-30.0000},
            {-38.9907,0.0000,-20.0000},
            {30.9907,0.0000,-50.0000},
            {-30.9907,0.0000,-50.0000},
            {0.0000,0.0000,-214.0305},
            {159.1439,0.0000,-30.0000},
            {-159.1439,0.0000,-30.0000}}; // 頂点13

    protected int[][] getFace() {
        return nVecPos;
    }
    private static int[][] nVecPos = {
            {0,2,1},
            {0,3,2},
            {0,4,3},
            {0,1,4},
            {1,5,7},
            {5,3,7},
            {3,6,7},
            {6,1,7},
            {2,8,1},
            {5,8,3},
            {4,9,3},
            {6,9,1},
            {1,8,5},
            {1,9,4},
            {3,8,2},
            {3,9,6}}; //面12

    public RenderPhantomSwordBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
    {
        if (entity instanceof EntitySummonedSwordBase)
        {
            doDriveRender((EntitySummonedSwordBase) entity, d0, d1, d2, f, f1);
        }
    }

    @Override
    protected ResourceLocationRaw getEntityTexture(Entity var1) {
        return null;
    }

    protected void transform(EntitySummonedSwordBase entity, double dX, double dY, double dZ, float f, float f1){
        GlStateManager.translatef((float) dX, (float) dY + 0.5f, (float) dZ);

        //f
        GlStateManager.rotatef(lerpDegrees(entity.prevRotationYaw, entity.rotationYaw, f1), 0.0F, 1.0F, 0.0F); //yaw
        GlStateManager.rotatef(-lerpDegrees(entity.prevRotationPitch, entity.rotationPitch, f1), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(entity.getRoll(), 0, 0, 1);

        float scale = 0.0045f;
        GlStateManager.scalef(scale, scale, scale);
        GlStateManager.scalef(0.5f, 0.5f, 1.0f);
    }

    protected void doDriveRender(EntitySummonedSwordBase entityPhantomSword, double dX, double dY, double dZ, float f, float f1)
    {
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        int color = entityPhantomSword.getColor();
        boolean inverse = color < 0;
        color = Math.abs(color);
        if(!inverse){
            OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        }
        else{
            OpenGlHelper.glBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_ZERO);
        }

        try(MSAutoCloser msac = MSAutoCloser.pushMatrix()) {
            transform(entityPhantomSword, dX, dY, dZ, f, f1);

            //■スタート
            float lifetime = entityPhantomSword.getLifeTime();
            float ticks = entityPhantomSword.ticksExisted;
            BufferBuilder wr = tessellator.getBuffer();
            wr.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

            int r = color >> 16 & 255;
            int g = color >> 8 & 255;
            int b = color & 255;

            //◆頂点登録 開始
            double dScale = 1.0;
            for (int idx = 0; idx < nVecPos.length; idx++) {
                //tessellator.setColorRGBA_F(fScale, 1.0F, 1.0F, 0.2F + (float)idx*0.02F);
                wr.pos(dVec[nVecPos[idx][0]][0] * dScale, dVec[nVecPos[idx][0]][1] * dScale, dVec[nVecPos[idx][0]][2] * dScale).color(r, g, b, 255).endVertex();
                wr.pos(dVec[nVecPos[idx][1]][0] * dScale, dVec[nVecPos[idx][1]][1] * dScale, dVec[nVecPos[idx][1]][2] * dScale).color(r, g, b, 255).endVertex();
                wr.pos(dVec[nVecPos[idx][2]][0] * dScale, dVec[nVecPos[idx][2]][1] * dScale, dVec[nVecPos[idx][2]][2] * dScale).color(r, g, b, 255).endVertex();
            }

            //◆頂点登録 終了

            tessellator.draw();
        }
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    float lerp(float start, float end, float percent){
        return (start + percent*(end - start));
    }

    float lerpDegrees(float start, float end, float percent){
        float diff = end - start;

        while (diff < -180.0F)
            diff += 360.0F;

        while (diff >= 180.0F)
            diff -= 360.0F;

        return start + percent * diff;
    }
}
