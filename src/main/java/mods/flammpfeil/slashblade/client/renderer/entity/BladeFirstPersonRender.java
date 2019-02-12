package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.ProjectileBarrier;
import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.entity.layers.LayerSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.Project;

import java.util.EnumSet;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeFirstPersonRender {

    private LayerSlashBlade layer;

    private BladeFirstPersonRender(){
        Minecraft mc = Minecraft.getMinecraft();

        layer = new LayerSlashBlade((RenderLivingBase)mc.getRenderManager().getEntityRenderObject(mc.player));}

    private static final class SingletonHolder {
        private static final BladeFirstPersonRender instance = new BladeFirstPersonRender();
    }

    public static BladeFirstPersonRender getInstance(){
        return BladeFirstPersonRender.SingletonHolder.instance;
    }

    public void render(){

        Minecraft mc = Minecraft.getMinecraft();

        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
        if (!(mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())) {
            return;
        }

        EntityPlayerSP player = mc.player;

        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.loadIdentity();

        Face.resetColor();

        GL11.glTranslatef(-0.25F, 0.2f, -0.5f);
        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0f);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0f);

        GL11.glTranslatef(0.0f, 0.25f, 0);
        GL11.glRotatef(-25.0F, 0.9F, 0.1F, 0.0F);
        GL11.glScalef(1.2F, 1.0F, 1.0F);

        float partialTicks = mc.getRenderPartialTicks();
        layer.doRenderLayer(mc.player, 0, 0, partialTicks, 0, 0, 0, 0);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
    private void lightSetup(AbstractClientPlayer clientPlayer)
    {
        int i = Minecraft.getMinecraft().world.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }
}
