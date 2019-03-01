package mods.flammpfeil.slashblade.client.renderer.entity;


import mods.flammpfeil.slashblade.client.util.MSAutoCloser;
import mods.flammpfeil.slashblade.entity.EntitySpinningSword;
import mods.flammpfeil.slashblade.entity.EntitySummonedBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 14/05/08.
 */
@OnlyIn(Dist.CLIENT)
public class RenderSpinningSword extends RenderPhantomSwordBase {
    @Override
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

    @Override
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

    public RenderSpinningSword(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void transform(EntitySummonedSwordBase entity, double dX, double dY, double dZ, float f, float f1){
        GlStateManager.translatef(0,0.5f,0);

        super.transform(entity, dX, dY, dZ, f, f1);

        float scale = 0.55555f;
        GL11.glScalef(scale, scale, scale);

        float time = entity.getEntityWorld().getGameTime() % 8 + f1;
        GL11.glRotatef(time * -45.0f, 0, 1, 0);

        GL11.glTranslatef(0, 0, 30);
    }

}
