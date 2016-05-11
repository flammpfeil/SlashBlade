package mods.flammpfeil.slashblade.client.renderer.entity;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.flammpfeil.slashblade.entity.EntitySummonedBlade;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 14/05/08.
 */
@SideOnly(Side.CLIENT)
public class RenderSummonedBlade extends Render {
    private static double[][] dVec = {
            {-3.2303,0.0000,-16.7280},
            {3.2303,0.0000,16.7280},
            {0.0000,3.6057,0.0000},
            {5.5773,0.0000,4.8152},
            {4.0270,0.0000,-6.1705},
            {0.0000,-3.6057,0.0000},
            {71.3027,-0.0000,5.5882},
            {50.1008,-0.0000,0.6734},
            {29.0129,0.0000,0.3956},
            {27.5958,0.0000,-9.6468},
            {55.5425,-0.0000,-6.8644},
            {28.3044,2.6732,-4.6256},
            {52.8217,1.4490,-3.0955},
            {28.3044,-2.6732,-4.6256},
            {52.8217,-1.4490,-3.0955},
            {-5.5773,0.0000,-4.8152},
            {-4.0270,0.0000,6.1705},
            {-71.3027,-0.0000,-5.5882},
            {-50.1008,-0.0000,-0.6734},
            {-29.0129,0.0000,-0.3956},
            {-27.5958,0.0000,9.6469},
            {-55.5425,-0.0000,6.8644},
            {-28.3044,2.6732,4.6256},
            {-52.8217,1.4490,3.0955},
            {-28.3044,-2.6732,4.6256},
            {-52.8217,-1.4490,3.0955}};

    private static int[][] nVecPos = {
            {2,3,1},
            {2,0,4},
            {1,3,5},
            {4,0,5},
            {9,11,4},
            {8,13,3},
            {7,12,6},
            {12,10,6},
            {10,14,6},
            {14,7,6},
            {11,12,7},
            {11,7,8},
            {9,10,12},
            {9,12,11},
            {9,13,14},
            {9,14,10},
            {13,8,7},
            {13,7,14},
            {13,9,4},
            {13,4,5},
            {13,5,3},
            {11,8,3},
            {2,4,11},
            {2,11,3},
            {2,15,0},
            {2,1,16},
            {0,15,5},
            {16,1,5},
            {20,22,16},
            {19,24,15},
            {18,23,17},
            {23,21,17},
            {21,25,17},
            {25,18,17},
            {22,23,18},
            {22,18,19},
            {20,21,23},
            {20,23,22},
            {20,24,25},
            {20,25,21},
            {24,19,18},
            {24,18,25},
            {24,20,16},
            {24,16,5},
            {24,5,15},
            {22,19,15},
            {2,16,22},
            {2,22,15}};

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
    {
        if (entity instanceof EntitySummonedBlade)
        {
            doDriveRender((EntitySummonedBlade) entity, d0, d1, d2, f, f1);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return null;
    }

    private void doDriveRender(EntitySummonedBlade entitySummonedBlade, double dX, double dY, double dZ, float f, float f1)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);

        int color = entitySummonedBlade.getColor();

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

        GL11.glTranslatef((float)dX, (float)dY+0.5f, (float)dZ);
        GL11.glRotatef(entitySummonedBlade.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-entitySummonedBlade.rotationPitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(entitySummonedBlade.getRoll(),0,0,1);

        float time = entitySummonedBlade.hitTime != 0
                ? entitySummonedBlade.hitTime % 6 + entitySummonedBlade.hitStopFactor
                : entitySummonedBlade.worldObj.getWorldTime() % 6 + f1;
        GL11.glRotatef(time * 60.0f ,0,1,0);

        //GL11.glRotatef(fRot, 0.0F, 1.0F, 0.0F);

        float scale = 0.01f;
        GL11.glScalef(scale, scale, scale);
        //GL11.glScalef(0.5f, 0.5f, 1.0f);

        //■スタート
        float lifetime = entitySummonedBlade.getLifeTime();
        float ticks = entitySummonedBlade.ticksExisted;
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

}
