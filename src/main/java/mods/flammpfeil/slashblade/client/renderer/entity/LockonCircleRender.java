package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.GroupObject;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Created by Furia on 2016/07/12.
 */
public class LockonCircleRender {

    static final ResourceLocationRaw modelLoc = new ResourceLocationRaw("flammpfeil.slashblade","model/util/lockon.obj");
    static final ResourceLocationRaw textureLoc = new ResourceLocationRaw("flammpfeil.slashblade","model/util/lockon.png");

    @SubscribeEvent
    public void onRenderLiving(RenderWorldLastEvent event){
        EntityLivingBase player = Minecraft.getMinecraft().player;
        if(player == null) return;

        ItemStack stack = player.getHeldItemMainhand();
        if(stack.isEmpty()) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);

        int entityId = ItemSlashBlade.TargetEntityId.get(tag);
        if(entityId == 0) return;

        Entity target = player.world.getEntityByID(entityId);
        if(target == null) return;
        if(!(target instanceof EntityLivingBase)) return;

        if(!target.isEntityAlive()) return;

        float health = 1.0f - ((EntityLivingBase)target).getHealth() / ((EntityLivingBase)target).getMaxHealth();

        float partialTicks = event.getPartialTicks();

        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;

        int color = 0x3333FF;
        if (ItemSlashBlade.SummonedSwordColor.exists(tag)) {
            color = ItemSlashBlade.SummonedSwordColor.get(tag);
            if(color < 0)
                color = -color;
        }

        GL11.glPushMatrix();

        Vec3d pos = lerp(new Vec3d(target.prevPosX, target.prevPosY, target.prevPosZ),target.getPositionVector(), partialTicks)
                .addVector(0,target.height / 2.0, 0)
                .addVector(-d3,-d4,-d5);
        GL11.glTranslated(pos.x, pos.y, pos.z);
        float scale = 0.00625f;
        GL11.glScalef(scale, scale, scale);
        double rotYaw = lerp(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
        double rotPitch = lerp(entity.prevRotationPitch, entity.rotationPitch, partialTicks);
        GL11.glRotated(rotYaw + 180.0,0,-1,0);
        GL11.glRotated(rotPitch,-1,0,0);


        GlStateManager.disableCull();

        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0.05F);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_ALWAYS);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);


        boolean useCustom = false;

        WavefrontObject model = BladeModelManager.getInstance().getModel(ItemSlashBlade.getModelLocation(stack));
        ResourceLocationRaw resourceTexture;

        for(GroupObject group : model.groupObjects){
            if(group.name.equals("lockonBase")) {
                useCustom = true;
                break;
            }
        }
        if(useCustom) {
            resourceTexture = ItemSlashBlade.getModelTexture(stack);
            color = 0xFFFFFF;
        }else {
            model = BladeModelManager.getInstance().getModel(modelLoc);
            resourceTexture = textureLoc;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceTexture);

        Face.setColor(0xAA000000 | color);
        model.renderPart("lockonBase");

        GL11.glPushMatrix();
        GL11.glTranslatef(0,0, health * 10.0f);
        Face.setColor(0x00000000);
        model.renderPart("lockonHealthMask");
        GL11.glPopMatrix();

        Face.setColor(0xAA000000 | color);

        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        model.renderPart("lockonHealth");
        Face.resetColor();


        GlStateManager.alphaFunc(GL11.GL_GEQUAL, 0.1F);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();

        GL11.glPopMatrix();
    }

    public double lerp(double a, double b, double amt) {
        return a*(1.0-amt)+b*amt;
    }
    public float lerp(float a, float b, float amt) {
        return a*(1.0f-amt)+b*amt;
    }
    public Vec3d lerp(Vec3d a, Vec3d b, double amt) {
        return a.scale(1.0-amt).add(b.scale(amt));
    }
}
