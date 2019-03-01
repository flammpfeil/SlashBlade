package mods.flammpfeil.slashblade.client.renderer.entity.layers;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityLivingRenderHandler {

    static LayerSlashBlade layer = null;

    static LayerSlashBlade getLayer(RenderLivingBase render){
        if(layer == null){
            layer = new LayerSlashBlade(render);
        }
        return layer;
    }

    @SubscribeEvent
    public void RenderPlayerHandler(RenderPlayerEvent.Pre event){

        if(!SlashBlade.UseRenderLivingEvent)
            return;

        layer = getLayer(event.getRenderer());

        GlStateManager.pushMatrix();
        GlStateManager.translated(event.getX(),event.getY(),event.getZ());

        EntityLivingBase entity = event.getEntityLiving();

        if (entity.isSneaking())
        {
            GlStateManager.translatef(0.0F, -0.125F, 0.0F);
        }

        if(entity instanceof AbstractClientPlayer){
            AbstractClientPlayer player = (AbstractClientPlayer)entity;
            float yaw = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, event.getPartialRenderTick());
            applyRotations(player , yaw, event.getPartialRenderTick());
        }

        layer.render(event.getEntityLiving(), 0, 0, event.getPartialRenderTick(), 0, 0, 0, 0);

        GlStateManager.popMatrix();
    }
    protected void applyRotations(AbstractClientPlayer entityLiving, float rotationYaw, float partialTicks)
    {
        if (entityLiving.isAlive() && entityLiving.isPlayerSleeping())
        {
            GlStateManager.rotatef(entityLiving.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(90.0f, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else if (entityLiving.isElytraFlying())
        {
            GlStateManager.rotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
            float f = (float)entityLiving.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(f1 * (-90.0F - entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
            Vec3d vec3d = entityLiving.getLook(partialTicks);
            double d0 = entityLiving.motionX * entityLiving.motionX + entityLiving.motionZ * entityLiving.motionZ;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (entityLiving.motionX * vec3d.x + entityLiving.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = entityLiving.motionX * vec3d.z - entityLiving.motionZ * vec3d.x;
                GlStateManager.rotatef((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
        else
        {
            GlStateManager.rotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
        GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
        GlStateManager.translatef(0.0F, -1.501F, 0.0F);
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
