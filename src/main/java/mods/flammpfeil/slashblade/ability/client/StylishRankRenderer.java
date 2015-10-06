package mods.flammpfeil.slashblade.ability.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 14/08/01.
 */
public class StylishRankRenderer {

    static ResourceLocation RankImg = new ResourceLocation("flammpfeil.slashblade","textures/gui/rank.png");

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        Minecraft mc;
        EntityPlayer player;
        long time;

        mc = FMLClientHandler.instance().getClient();
        World world = mc.theWorld;
        if (event.phase == TickEvent.Phase.START || !(Minecraft.getMinecraft().renderViewEntity instanceof EntityPlayer))
            return;

        player = (EntityPlayer) Minecraft.getMinecraft().renderViewEntity;
        time = System.currentTimeMillis();

        if (player != null && mc.inGameHasFocus) {
            Minecraft _tmp2 = mc;
            if (Minecraft.isGuiEnabled()) {
                renderRankHud(event.renderTickTime, player, time);
            }
        }
    }

    private void renderRankHud(Float partialTicks, EntityPlayer player, long time) {
        Minecraft mc = Minecraft.getMinecraft();
        int rank = StylishRankManager.getStylishRank(player);

        //rank = 2;

        if(rank <= 0)
            return;

        GL11.glPushMatrix(); //1 store
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        //ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix(); //2 store
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, 1000D, 3000D);
        //GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();

        int k = mc.displayWidth;//sr.getScaledWidth();
        int l = mc.displayHeight;//sr.getScaledHeight();

        GL11.glTranslatef(k * 2 / 3, l / 5, -2000F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);

        mc.renderEngine.bindTexture(RankImg);

        long lastUpdate = StylishRankManager.LastRankPointUpdate.get(player.getEntityData());
        long now = player.worldObj.getTotalWorldTime();

        boolean showTextRank = false;
        if(now < lastUpdate + 20*3)
            showTextRank = true;

        GL11.glPushMatrix(); //3 store
        if(now < (lastUpdate+120)){
            int rankOffset = 32 * (rank - 1);
            int textOffset = showTextRank ? 128 : 0;

            GL11.glScalef(3,3,3);
            drawTexturedQuad(0, 0, textOffset, rankOffset, 128, 32, -90D);

            drawTexturedQuad(0 , 32, 0 ,256-16, 64, 16, -90D);
            drawTexturedQuad(16, 32, 16,256-32, (int)(32 * StylishRankManager.getCurrentProgress(player)), 16, -95D);
        }
        GL11.glPopMatrix(); //3 restore


        GL11.glMatrixMode(5889);
        GL11.glPopMatrix(); //2 restore
        GL11.glMatrixMode(5888);

        GL11.glDisable(3042);
        GL11.glPopAttrib();
        GL11.glPopMatrix(); //1 restore
    }

    public static void drawTexturedQuad(int par1, int par2, int par3, int par4, int par5, int par6, double zLevel) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(par1 + 0, par2 + par6, zLevel, (float) (par3 + 0) * var7, (float) (par4 + par6) * var8);
        tessellator.addVertexWithUV(par1 + par5, par2 + par6, zLevel, (float) (par3 + par5) * var7, (float) (par4 + par6) * var8);
        tessellator.addVertexWithUV(par1 + par5, par2 + 0, zLevel, (float) (par3 + par5) * var7, (float) (par4 + 0) * var8);
        tessellator.addVertexWithUV(par1 + 0, par2 + 0, zLevel, (float) (par3 + 0) * var7, (float) (par4 + 0) * var8);
        tessellator.draw();
    }
}
