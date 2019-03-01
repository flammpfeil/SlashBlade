package mods.flammpfeil.slashblade.client.renderer.entity;


import mods.flammpfeil.slashblade.client.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntitySummonedBlade;
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
public class RenderSummonedBlade extends RenderPhantomSwordBase {

    @Override
    protected double[][] getVec() {
        return dVec;
    }
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

    @Override
    protected int[][] getFace() {
        return nVecPos;
    }
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

    public RenderSummonedBlade(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void transform(EntitySummonedSwordBase entity, double dX, double dY, double dZ, float f, float f1){
        super.transform(entity, dX, dY, dZ, f, f1);

        GlStateManager.scalef(2.0f, 2.0f, 1.0f);
        float scale = 2.2f;
        GlStateManager.scalef(2.2f, scale, scale);

        EntitySummonedBlade entitySummonedBlade = (EntitySummonedBlade) entity;
        float time = entitySummonedBlade.hitTime != 0
                ? entitySummonedBlade.hitTime % 6 + entitySummonedBlade.hitStopFactor
                : entitySummonedBlade.getEntityWorld().getGameTime() % 6 + f1;
        GL11.glRotatef(time * 60.0f, 0, 1, 0);
    }
}
