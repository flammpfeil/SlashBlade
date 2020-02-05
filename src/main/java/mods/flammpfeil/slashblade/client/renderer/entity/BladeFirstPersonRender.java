package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.ProjectileBarrier;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.entity.layers.LayerSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeFirstPersonRender {
    private LayerSlashBlade layer;
    private BladeFirstPersonRender(){
        Minecraft mc = Minecraft.getMinecraft();
        layer = new LayerSlashBlade((RenderLivingBase)mc.getRenderManager().getEntityRenderObject(mc.player));
    }
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

        float partialTicks = mc.getRenderPartialTicks();

        if(!SlashBlade.FPVDisabledSyncPitch){
            float pitch = interpolateRotation(player.prevRotationPitch, player.rotationPitch, partialTicks);
            GL11.glRotatef(pitch, 1, 0 ,0);
        }

        if(SlashBlade.FPVOldStryleLike){

            if(ProjectileBarrier.isAvailable(player, stack, player.getItemInUseCount())){
                GlStateManager.translate(0, 0.1f, -0.8f);
                GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0f);
            }else{
                GlStateManager.translate(-0.35F, -0.1f, -0.8f);
                GlStateManager.rotate(-3.0F, 1.0F, 0.0F, 0.0f);
            }
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

            GlStateManager.translate(0.0f, 0.25f, 0);
            GlStateManager.rotate(-25.0F, 0.9F, 0.1F, 0.0F);
            GlStateManager.scale(1.2F, 1.0F, 1.0F);
        }else{
            if(ProjectileBarrier.isAvailable(player, stack, player.getItemInUseCount())){
                GlStateManager.translate(0, 0.2f, -1.0f);
                GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0f);
            }else{
                GlStateManager.translate(-0.25F, 0.2f, -0.5f);
                GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0f);
            }
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0f);
            GlStateManager.translate(0.0f, 0.25f, 0);
            GlStateManager.rotate(-25.0F, 0.9F, 0.1F, 0.0F);
            GlStateManager.scale(1.2F, 1.0F, 1.0F);
        }
        layer.disableOffhandRendering();
        if(SlashBlade.FPVNoBlur)
            layer.setNoBlur();
        layer.doRenderLayer(mc.player, 0, 0, partialTicks, 0, 0, 0, 0);
        layer.enableOffhandRendering();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void renderVR() {

        Minecraft mc = Minecraft.getMinecraft();
        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
        if (!(mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())) {
            return;
        }
        EntityPlayerSP player = mc.player;
        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;



        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();



        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();



        GlStateManager.scale(0.3f, 0.3F, 0.3F);
        //GlStateManager.translate(0.375*-f, 0, .75);

        GlStateManager.translate(-0.45, -0.85, -1.5);

        GlStateManager.rotate(-180, 1, 0,0);

        float partialTicks = mc.getRenderPartialTicks();
        GlStateManager.rotate(interpolateRotation(player.prevRotationPitch, player.rotationPitch, partialTicks), 1 , 0 , 0);

        GlStateManager.rotate(180, 0, 1, 0);


        GlStateManager.scale(1.25f, 1.25F, 1.25F);


        Face.resetColor();
        layer.disableOffhandRendering();
        layer.doRenderLayer(mc.player, 0, 0, partialTicks, 0, 0, 0, 0);
        layer.enableOffhandRendering();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();


        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();


    }

    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks)
    {
        float f;

        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return prevYawOffset + partialTicks * f;
    }
}