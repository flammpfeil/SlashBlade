package mods.flammpfeil.slashblade.client.renderer;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.entity.EntityPhantomSwordBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 14/05/08.
 */
@SideOnly(Side.CLIENT)
public class RenderPhantomSwordBase extends Render {
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

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
    {
        if (entity instanceof EntityPhantomSwordBase)
        {
            doDriveRender((EntityPhantomSwordBase) entity, d0, d1, d2, f, f1);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return null;
    }

    private void doDriveRender(EntityPhantomSwordBase entityPhantomSword, double dX, double dY, double dZ, float f, float f1)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);

        int color = entityPhantomSword.getColor();

        boolean inverse = color < 0;

        color = Math.abs(color);

        if(!inverse){
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }
        else{
            GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
        }
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();

        GL11.glTranslatef((float)dX, (float)dY /*+0.5f */, (float)dZ);
        GL11.glRotatef(lerpDegrees(entityPhantomSword.prevRotationYaw, entityPhantomSword.rotationYaw, f1), 0.0F, 1.0F, 0.0F); //yaw
        GL11.glRotatef(-lerpDegrees(entityPhantomSword.prevRotationPitch, entityPhantomSword.rotationPitch, f1), 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(entityPhantomSword.getRoll(),0,0,1);
        //GL11.glRotatef(fRot, 0.0F, 1.0F, 0.0F);

        float scale = 0.0045f;
        GL11.glScalef(scale, scale, scale);
        GL11.glScalef(0.5f, 0.5f, 1.0f);

        //■スタート
        float lifetime = entityPhantomSword.getLifeTime();
        float ticks = entityPhantomSword.ticksExisted;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        tessellator.setColorRGBA_I(color, 255);

        //◆頂点登録 開始
        double dScale = 1.0;
        for(int idx = 0; idx < nVecPos.length; idx++)
        {
            //tessellator.setColorRGBA_F(fScale, 1.0F, 1.0F, 0.2F + (float)idx*0.02F);
            tessellator.addVertex(dVec[nVecPos[idx][0]][0] * dScale, dVec[nVecPos[idx][0]][1] * dScale, dVec[nVecPos[idx][0]][2] * dScale);
            tessellator.addVertex(dVec[nVecPos[idx][1]][0] * dScale, dVec[nVecPos[idx][1]][1] * dScale, dVec[nVecPos[idx][1]][2] * dScale);
            tessellator.addVertex(dVec[nVecPos[idx][2]][0] * dScale, dVec[nVecPos[idx][2]][1] * dScale, dVec[nVecPos[idx][2]][2] * dScale);
        }

        //◆頂点登録 終了

        tessellator.draw();

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
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
