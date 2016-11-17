package mods.flammpfeil.slashblade.client.renderer.entity;


import mods.flammpfeil.slashblade.entity.EntitySummonedSword;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 14/05/08.
 */
@SideOnly(Side.CLIENT)
public class RenderPhantomSword extends Render {
    private static double[][] dVec = {
            {0.0000,0.0000,417.7431},
            {0.0000,-44.6113,0.0000},
            {38.9907,0.0000,50.0000},
            {0.0000,44.6113,0.0000},
            {-38.9907,0.0000,50.0000},
            {38.9907,0.0000,-50.0000},
            {-38.9907,0.0000,-50.0000},
            {0.0000,0.0000,-214.0305},
            {159.1439,0.0000,-49.6611},
            {-159.1439,0.0000,-49.6611}}; // 頂点13

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

    public RenderPhantomSword(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
    {
        if (entity instanceof EntitySummonedSword)
        {
            doDriveRender((EntitySummonedSword) entity, d0, d1, d2, f, f1);
        }
    }

    @Override
    protected ResourceLocationRaw getEntityTexture(Entity var1) {
        return null;
    }

    private void doDriveRender(EntitySummonedSword entitySummonedSword, double dX, double dY, double dZ, float f, float f1)
    {
        Tessellator tessellator = Tessellator.getInstance();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();

        GL11.glTranslatef((float)dX, (float)dY+0.5f, (float)dZ);
        GL11.glRotatef(entitySummonedSword.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-entitySummonedSword.rotationPitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(entitySummonedSword.getRoll(),0,0,1);
        //GL11.glRotatef(fRot, 0.0F, 1.0F, 0.0F);

        float scale = 0.0045f;
        GL11.glScalef(scale, scale, scale);
        GL11.glScalef(0.5f, 0.5f, 1.0f);

        //■スタート
        float lifetime = entitySummonedSword.getLifeTime();
        float ticks = entitySummonedSword.ticksExisted;
        VertexBuffer wr = tessellator.getBuffer();
        wr.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        //◆頂点登録 開始
        double dScale = 1.0;
        for(int idx = 0; idx < nVecPos.length; idx++)
        {
            //tessellator.setColorRGBA_F(fScale, 1.0F, 1.0F, 0.2F + (float)idx*0.02F);
            wr.pos(dVec[nVecPos[idx][0]][0] * dScale, dVec[nVecPos[idx][0]][1] * dScale, dVec[nVecPos[idx][0]][2] * dScale).color(0.2F, 0.2F, 1.0F, 1.0f).endVertex();
            wr.pos(dVec[nVecPos[idx][1]][0] * dScale, dVec[nVecPos[idx][1]][1] * dScale, dVec[nVecPos[idx][1]][2] * dScale).color(0.2F, 0.2F, 1.0F, 1.0f).endVertex();
            wr.pos(dVec[nVecPos[idx][2]][0] * dScale, dVec[nVecPos[idx][2]][1] * dScale, dVec[nVecPos[idx][2]][2] * dScale).color(0.2F, 0.2F, 1.0F, 1.0f).endVertex();
        }

        //◆頂点登録 終了

        tessellator.draw();

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
