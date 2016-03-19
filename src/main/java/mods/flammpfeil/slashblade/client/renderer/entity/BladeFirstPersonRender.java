package mods.flammpfeil.slashblade.client.renderer.entity;

import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.util.EnumSet;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeFirstPersonRender {
    private static final ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderFirstPersonHand(RenderHandEvent event) {
        if(event.isCanceled()) return;


        Minecraft mc = Minecraft.getMinecraft();

        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
        if (!(mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())) {
            return;
        }

        EntityPlayerSP player = mc.thePlayer;

        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (stack == null) return;
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;

        ItemSlashBlade itemBlade = ((ItemSlashBlade) stack.getItem());

        EnumSet<ItemSlashBlade.SwordType> swordType = itemBlade.getSwordType(stack);

        GlStateManager.clear(256);

        int xOffset = event.renderPass;

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        float f = 0.07F;

        if (mc.gameSettings.anaglyph) {
            GlStateManager.translate((float) (-(xOffset * 2 - 1)) * f, 0.0F, 0.0F);
        }

        Project.gluPerspective(70.0F, (float) mc.displayWidth / (float) mc.displayHeight, 0.05F, (float) (16) * 2.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();

        if (mc.gameSettings.anaglyph) {
            GlStateManager.translate((float) (xOffset * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        GlStateManager.pushMatrix();


        //if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())
        {
            mc.entityRenderer.enableLightmap();

            float partialTicks = event.partialTicks;

            if (swordType.contains(ItemSlashBlade.SwordType.NoScabbard)) {
                //todo :LayerHeldItem�I��rendering����


                //todo :primary hand select

                func_178110_a(player, partialTicks);

                float f1 = player.getSwingProgress(partialTicks);
                func_178105_d(f1);
                this.transformFirstPersonItem(0, f1);

                renderNakedBlade(player, partialTicks);
                /*
                mc.getItemRenderer().renderItemInFirstPerson(partialTicks);
                /**/
            } else {

                func_178109_a(player);

                GL11.glTranslatef(-0.35F, -0.1f, -0.8f);
                GL11.glRotatef(-3.0F, 1.0F, 0.0F, 0.0f);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

                Face.resetColor();
                render(player, partialTicks);
            }

            mc.entityRenderer.disableLightmap();
        }

        GlStateManager.popMatrix();
    }

    void renderNakedBlade(EntityLivingBase entity, float partialTicks){


        ItemStack stack = entity.getHeldItem(EnumHand.MAIN_HAND);
        if (stack == null) return;
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;

        ItemSlashBlade itemBlade = ((ItemSlashBlade) stack.getItem());

        WavefrontObject model = BladeModelManager.getInstance().getModel(ItemSlashBlade.getModelLocation(stack));
        ResourceLocation resourceTexture = ItemSlashBlade.getModelTexture(stack);

        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceTexture);

        EnumSet<ItemSlashBlade.SwordType> swordType = itemBlade.getSwordType(stack);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

        GL11.glTranslatef(0.0f,-0.2f,0);
        float scale = 0.015f;
        GL11.glScalef(scale,scale,scale);
        GL11.glRotatef(-90, 0, 0, 1);
        GL11.glRotatef(-30, 1, 0, 0);

        String renderTargets[];
        if(swordType.contains(ItemSlashBlade.SwordType.Broken)){
            renderTargets = new String[]{"blade_damaged"};
        }else{
            renderTargets = new String[]{"blade"};
        }

        model.renderOnly(renderTargets);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        float lastx = OpenGlHelper.lastBrightnessX;
        float lasty = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        for(String renderTarget : renderTargets)
            model.renderPart(renderTarget + "_luminous");

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

        GL11.glEnable(GL11.GL_LIGHTING);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void func_178109_a(AbstractClientPlayer clientPlayer)
    {
        int i = Minecraft.getMinecraft().theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }
    private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks) {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    private void func_178105_d(float p_178105_1_) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI);
        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI * 2.0F);
        float f2 = -0.2F * MathHelper.sin(p_178105_1_ * (float) Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    void render(EntityLivingBase entity, float partialTicks) {
        boolean adjust = false;

        if (entity == null)
            return;


        /*
        if(stack == null || !(stack.getItem() instanceof ItemSlashBlade)){
            if(entity instanceof EntityPlayer){
                ItemStack firstItem = ((EntityPlayer)entity).inventory.getStackInSlot(0);
                if(adjust && firstItem != null && (firstItem.getItem() instanceof ItemSlashBlade)){
                    renderBack(firstItem,(EntityPlayer)entity);
                }
            }
            return;
        }
        */

        ItemStack stack = entity.getHeldItem(EnumHand.MAIN_HAND);
        if (stack == null) return;
        if (!(stack.getItem() instanceof ItemSlashBlade)) return;

        ItemSlashBlade itemBlade = ((ItemSlashBlade) stack.getItem());

        WavefrontObject model = BladeModelManager.getInstance().getModel(ItemSlashBlade.getModelLocation(stack));
        ResourceLocation resourceTexture = ItemSlashBlade.getModelTexture(stack);

        EnumSet<ItemSlashBlade.SwordType> swordType = itemBlade.getSwordType(stack);

        boolean isEnchanted = swordType.contains(ItemSlashBlade.SwordType.Enchanted);

        int charge;
        if (entity instanceof EntityPlayer)
            charge = ((EntityPlayer) entity).getItemInUseMaxCount();
        else
            charge = 0;

        float ax = 0;
        float ay = 0;
        float az = 0;

        boolean isBroken = swordType.contains(ItemSlashBlade.SwordType.Broken);
        ItemSlashBlade.ComboSequence combo = ItemSlashBlade.ComboSequence.None;
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();

            combo = itemBlade.getComboSequence(tag);

            if (adjust) {
                ax = tag.getFloat(ItemSlashBlade.adjustXStr) / 10.0f;
                ay = -tag.getFloat(ItemSlashBlade.adjustYStr) / 10.0f;
                az = -tag.getFloat(ItemSlashBlade.adjustZStr) / 10.0f;
            }
        }


        float progress = entity.getSwingProgress(partialTicks);

        if ((!combo.equals(ItemSlashBlade.ComboSequence.None)) && entity.swingProgress == 0.0f)
            progress = 1.0f;

        progress *= 1.2;
        if (1.0f < progress)
            progress = 1.0f;

        //progress = (entity.ticksExisted % 10) / 10.0f;

        switch (combo) {
            case SIai:
            case Iai:
                progress = 1.0f - (Math.abs(progress - 0.5f) * 2.0f);

                break;

            case HiraTuki:
                progress = 1.0f;

                break;

            default:
                progress = 1.0f - progress;
                progress = 1.0f - (float) Math.pow(progress, 2.0);

                break;
        }

        /*
		if(!isBroken && isEnchanted && ItemSlashBlade.RequiredChargeTick < charge){
			progress = 0.0f;
			combo = ComboSequence.None;
		}
        */


        String renderTarget;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);

            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            GL11.glColor3f(1.0F, 1.0F, 1.0F);

            //�̊i�␳ config���
            GL11.glTranslatef(ax, ay, az);

            //���ʒu��
            GL11.glTranslatef(0.25f, 0.4f, -0.5f);


            {
                //�S�̃X�P�[���␳
                float scale = (float) (0.075f);
                GL11.glScalef(scale, scale, scale);
            }

            //�������
            GL11.glRotatef(60.0f, 1, 0, 0);

            //����O��
            GL11.glRotatef(-20.0f, 0, 0, 1);

            //�n�����Ɍ�����i��������
            GL11.glRotatef(90.0f, 0, 1.0f, 0);


            float xoffset = 10.0f;
            float yoffset = 8.0f;

            //-----------------------------------------------------------------------------------------------------------------------
            GL11.glPushMatrix();
            {

                if (!combo.equals(ItemSlashBlade.ComboSequence.None)) {

                    float tmp = progress;

                    if (combo.swingAmplitude < 0) {
                        progress = 1.0f - progress;
                    }
                    //GL11.glRotatef(-90, 0.0f, 1.0f, 0.0f);

                    if (combo.equals(ItemSlashBlade.ComboSequence.HiraTuki)) {
                        GL11.glTranslatef(0.0f, 0.0f, -26.0f);
                    }

                    if (combo.equals(ItemSlashBlade.ComboSequence.Kiriorosi)) {


                        GL11.glRotatef(20.0f, -1.0f, 0, 0);
                        GL11.glRotatef(-30.0f, 0, 0, -1.0f);


                        GL11.glTranslatef(0.0f, 0.0f, -8.0f);
                        //GL11.glRotatef(-30.0f,1,0,0);


                        GL11.glRotatef((90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);

                        GL11.glRotatef((1.0f - progress) * -90.0f, 0.0f, 0.0f, -1.0f);
                        GL11.glTranslatef(0.0f, (1.0f - progress) * -5.0f, 0.0f);
                        GL11.glTranslatef((1.0f - progress) * 10.0f, 0.0f, 0.0f);

                        GL11.glTranslatef(-xoffset, 0.0f, 0.0f);
                        GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                        progress = 1.0f;

                        if (0 < combo.swingAmplitude) {
                            GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                        } else {
                            GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                        }

                        GL11.glTranslatef(0.0f, yoffset, 0.0f);
                        GL11.glTranslatef(xoffset, 0.0f, 0.0f);
                        GL11.glRotatef(180.0f, 0, 1, 0);
                    } else if (combo.swingDirection < 0) {


                        GL11.glRotatef(20.0f, -1.0f, 0, 0);
                        GL11.glRotatef(-30.0f, 0, 0, -1.0f);


                        GL11.glTranslatef(0.0f, 0.0f, -12.0f);
                        //GL11.glRotatef(-30.0f,1,0,0);


                        GL11.glRotatef((90 + combo.swingDirection), 0.0f, -1.0f, 0.0f);


                        GL11.glRotatef((1.0f - progress) * -(180.0f + 60.0f), 0.0f, 0.0f, -1.0f);
                        /*
                        GL11.glTranslatef(0.0f, (1.0f-progress) * -5.0f, 0.0f);
                        GL11.glTranslatef((1.0f-progress) * 10.0f, 0.0f, 0.0f);
                        */

                        GL11.glTranslatef(-xoffset, 0.0f, 0.0f);
                        GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                        progress = 1.0f;

                        if (0 < combo.swingAmplitude) {
                            GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                        } else {
                            GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                        }

                        GL11.glTranslatef(0.0f, yoffset, 0.0f);
                        GL11.glTranslatef(xoffset, 0.0f, 0.0f);
                        //GL11.glRotatef(180.0f, 0, 1, 0);
                    } else {

                        GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
                        GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


                        GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


                        GL11.glTranslatef(-xoffset, 0.0f, 0.0f);


                        GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                        if (0 < combo.swingAmplitude) {
                            GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                        } else {
                            GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                        }

                        GL11.glTranslatef(0.0f, yoffset, 0.0f);
                        GL11.glTranslatef(xoffset, 0.0f, 0.0f);
                    }


                    progress = tmp;
                }

                if (isBroken)
                    renderTarget = "blade_damaged";
                else
                    renderTarget = "blade";


                float scaleLocal = 0.095f;
                GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                GL11.glRotatef(-90.0f, 0, 0, 1);
                Minecraft.getMinecraft().getTextureManager().bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

                model.renderPart(renderTarget);

                if (!combo.useScabbard) {
                    model.renderPart(renderTarget + "_unsheathe");
                }

                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS); //-------------

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                float lastx = OpenGlHelper.lastBrightnessX;
                float lasty = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                model.renderPart(renderTarget + "_luminous");
                if (!combo.useScabbard) {
                    model.renderPart(renderTarget + "_unsheathe_luminous");
                }

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                GL11.glEnable(GL11.GL_LIGHTING);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                if (stack.hasEffect()) {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f7 = 0.76F;

                    Face.setColor(0xFF8040CC);

                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f8 = 0.125F;
                    GL11.glScalef(f8, f8, f8);
                    float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f9, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f8, f8, f8);
                    f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f9, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);

                    Face.resetColor();

                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }

                GL11.glPopAttrib(); //-------------
            }
            GL11.glPopMatrix();

            //-----------------------------------------------------------------------------------------------------------------------

            GL11.glPushMatrix();
            {


                if ((!combo.equals(ItemSlashBlade.ComboSequence.None)) && combo.useScabbard) {


                    if (combo.swingAmplitude < 0) {
                        progress = 1.0f - progress;
                    }

                    GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
                    GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


                    GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


                    GL11.glTranslatef(-xoffset, 0.0f, 0.0f);


                    GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                    if (0 < combo.swingAmplitude) {
                        GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    } else {
                        GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    }

                    GL11.glTranslatef(0.0f, yoffset, 0.0f);
                    GL11.glTranslatef(xoffset, 0.0f, 0.0f);

                }


                GL11.glPushMatrix();

                float scaleLocal = 0.095f;
                GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                GL11.glRotatef(-90.0f, 0, 0, 1);
                Minecraft.getMinecraft().getTextureManager().bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

                /*
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);

                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
*/
                renderTarget = "sheath";
                model.renderPart(renderTarget);



                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS); //-------------

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                float lastx = OpenGlHelper.lastBrightnessX;
                float lasty = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                model.renderPart(renderTarget + "_luminous");

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                GL11.glEnable(GL11.GL_LIGHTING);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                if (stack.hasEffect()) {

                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f7 = 0.76F;

                    Face.setColor(0xFF8040CC);

                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f8 = 0.125F;
                    GL11.glScalef(f8, f8, f8);
                    float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f9, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f8, f8, f8);
                    f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f9, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);

                    Face.resetColor();

                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }

                GL11.glPopMatrix();

                if (!isBroken && isEnchanted && (ItemSlashBlade.RequiredChargeTick < charge || combo.isCharged)) {
                    GL11.glPushMatrix();

                    GL11.glPushMatrix();

                    GL11.glEnable(GL11.GL_BLEND);
                    float f4 = 3.0F;
                    GL11.glColor4f(f4, f4, f4, 3.0F);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);


                    GL11.glPushMatrix();
                    GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                    GL11.glRotatef(-90.0f, 0, 0, 1);
                    model.renderPart("sheath");

                    GL11.glPopMatrix();

                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();

                    float ff1 = (float) entity.ticksExisted + partialTicks;
                    Minecraft.getMinecraft().getTextureManager().bindTexture(armoredCreeperTextures);
                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glLoadIdentity();
                    float f2 = ff1 * 0.03F;
                    float f3 = ff1 * 0.02F;
                    GL11.glTranslatef(f2, -f3, 0.0F);
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glEnable(GL11.GL_BLEND);
                    f4 = 1.0F;
                    GL11.glColor4f(f4, f4, f4, 1.0F);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

                    //GL11.glTranslatef(-1f, 0.0f, -0.5f);

                    GL11.glPushMatrix();
                    GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                    GL11.glRotatef(-90.0f, 0, 0, 1);
                    model.renderPart("effect");

                    GL11.glPopMatrix();

                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glLoadIdentity();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glColor4f(1, 1, 1, 1.0F);
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);


                    GL11.glPopMatrix();
                }


                GL11.glPopAttrib(); //-------------
            }
            GL11.glPopMatrix();

            //-----------------------------------------------------------------------------------------------------------------------
            GL11.glShadeModel(GL11.GL_FLAT);
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
