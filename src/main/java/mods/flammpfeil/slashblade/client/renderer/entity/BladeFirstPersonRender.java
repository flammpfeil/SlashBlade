package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.ability.ProjectileBarrier;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.entity.layers.LayerSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeFirstPersonRender {
    private LayerSlashBlade layer;
    private BladeFirstPersonRender(){
        Minecraft mc = Minecraft.getInstance();
        layer = new LayerSlashBlade((RenderLivingBase)mc.getRenderManager().getEntityRenderObject(mc.player));
    }
    private static final class SingletonHolder {
        private static final BladeFirstPersonRender instance = new BladeFirstPersonRender();
    }
    public static BladeFirstPersonRender getInstance(){
        return BladeFirstPersonRender.SingletonHolder.instance;
    }

    public void render(){
        Minecraft mc = Minecraft.getInstance();
        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
        if (!(mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectatorMode())) {
            return;
        }
        EntityPlayerSP player = mc.player;
        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;
        GlStateManager.pushLightingAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Face.resetColor();
        if(ProjectileBarrier.isAvailable(player, stack, player.getItemInUseCount())){
            GlStateManager.translatef(0, 0.2f, -1.0f);
            GlStateManager.rotatef(-25.0F, 1.0F, 0.0F, 0.0f);
        }else{
            GlStateManager.translatef(-0.25F, 0.2f, -0.5f);
            GlStateManager.rotatef(-10.0F, 1.0F, 0.0F, 0.0f);
        }
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0f);
        GlStateManager.translatef(0.0f, 0.25f, 0);
        GlStateManager.rotatef(-25.0F, 0.9F, 0.1F, 0.0F);
        GlStateManager.scalef(1.2F, 1.0F, 1.0F);
        float partialTicks = mc.getRenderPartialTicks();
        layer.disableOffhandRendering();
        layer.render(mc.player, 0, 0, partialTicks, 0, 0, 0, 0);
        layer.enableOffhandRendering();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void renderVR() {

        Minecraft mc = Minecraft.getInstance();
        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
        if (!(mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectatorMode())) {
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



        GlStateManager.pushLightingAttrib();
        GlStateManager.pushMatrix();



        GlStateManager.scalef(0.3f, 0.3F, 0.3F);
        //GlStateManager.translatef(0.375*-f, 0, .75);

        GlStateManager.translated(-0.45, -0.85, -1.5);

        GlStateManager.rotatef(-180, 1, 0,0);

        float partialTicks = mc.getRenderPartialTicks();
        GlStateManager.rotatef(interpolateRotation(player.prevRotationPitch, player.rotationPitch, partialTicks), 1 , 0 , 0);

        GlStateManager.rotatef(180, 0, 1, 0);


        GlStateManager.scalef(1.25f, 1.25F, 1.25F);


        Face.resetColor();
        layer.disableOffhandRendering();
        layer.render(mc.player, 0, 0, partialTicks, 0, 0, 0, 0);
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