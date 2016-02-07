package mods.flammpfeil.slashblade.client.renderer.entity.layers;

import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

/**
 * Created by Furia on 2016/02/06.
 */
public class LayerSlashBlade implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private final RendererLivingEntity<?> render;

    public LayerSlashBlade(RendererLivingEntity<?> livingEntityRendererIn)
    {
        this.render = livingEntityRendererIn;
    }

    float lerp(float start, float end, float percent){
        return (start + percent*(end - start));
    }

    float lerpDegrees(float start, float end, float percent){
        float diff = end - start;

        while (diff < -180.0F)
            diff += 360.0F;

        while (diff >= 180.0F)
            diff -= 360.0F;

        return start + percent * diff;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ticksExisted, float yawDiff, float rotationPitch, float scale) {
        GlStateManager.pushMatrix();

        GL11.glScalef(1.0F, 1.0F, 1.0F);

        float f5 = 0.0625F;
        //GL11.glTranslatef(0.0F, -24.0F * f5 - 0.0078125F, 0.0F);

        render(entitylivingbaseIn, partialTicks);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }




    void render(EntityLivingBase entity,float partialTicks)
    {
        boolean adjust = true;

        if(entity == null)
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

        ItemStack stack = entity.getHeldItem();
        if(stack == null) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        ItemSlashBlade itemBlade = ((ItemSlashBlade)stack.getItem());

        WavefrontObject model = BladeModelManager.getInstance().getModel(ItemSlashBlade.getModelLocation(stack));
        ResourceLocation resourceTexture = ItemSlashBlade.getModelTexture(stack);

        EnumSet<ItemSlashBlade.SwordType> swordType = itemBlade.getSwordType(stack);

        if(swordType.contains(ItemSlashBlade.SwordType.NoScabbard)){
            //todo :LayerHeldItem的にrenderingする
            doHeldItemRenderLayer(entity,swordType,model);
            return;
        }

        boolean isEnchanted = swordType.contains(ItemSlashBlade.SwordType.Enchanted);
        boolean isBewitched = swordType.contains(ItemSlashBlade.SwordType.Bewitched);


        int charge;
        if(entity instanceof EntityPlayer)
            charge = ((EntityPlayer) entity).getItemInUseDuration();
        else
            charge = 0;

        float ax = 0;
        float ay = 0;
        float az = 0;

        boolean isBroken = swordType.contains(ItemSlashBlade.SwordType.Broken);
        ItemSlashBlade.ComboSequence combo = ItemSlashBlade.ComboSequence.None;
        if(stack.hasTagCompound()){
            NBTTagCompound tag = stack.getTagCompound();

            combo = itemBlade.getComboSequence(tag);

            if(adjust){
                ax = tag.getFloat(ItemSlashBlade.adjustXStr)/10.0f;
                ay = -tag.getFloat(ItemSlashBlade.adjustYStr)/10.0f;
                az = -tag.getFloat(ItemSlashBlade.adjustZStr)/10.0f;
            }
        }


        float progress = entity.getSwingProgress(partialTicks);

        if((!combo.equals(ItemSlashBlade.ComboSequence.None)) && entity.swingProgress == 0.0f)
            progress = 1.0f;

        progress *= 1.2;
        if(1.0f < progress)
            progress = 1.0f;

        //progress = (entity.ticksExisted % 10) / 10.0f;

        switch(combo){
            case SIai:
            case Iai:
                progress = 1.0f - (Math.abs(progress-0.5f) * 2.0f);

                break;

            case HiraTuki:
                progress = 1.0f;

                break;

            default :
                progress = 1.0f - progress;
                progress = 1.0f - (float)Math.pow(progress,2.0);

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

            //体格補正 configより
            GL11.glTranslatef(ax,ay,az);

            //腰位置へ
            GL11.glTranslatef(0.25f,0.4f,-0.5f);


            {
                //全体スケール補正
                float scale = (float)(0.075f);
                GL11.glScalef(scale, scale, scale);
            }

            //先を後ろへ
            GL11.glRotatef(60.0f, 1, 0, 0);

            //先を外へ
            GL11.glRotatef(-20.0f, 0, 0, 1);

            //刃を下に向ける（太刀差し
            GL11.glRotatef(90.0f, 0, 1.0f, 0);


            float xoffset = 10.0f;
            float yoffset = 8.0f;

            //-----------------------------------------------------------------------------------------------------------------------
            GL11.glPushMatrix();{


            if(!combo.equals(ItemSlashBlade.ComboSequence.None)){

                float tmp = progress;

                if(combo.swingAmplitude < 0){
                    progress = 1.0f - progress;
                }
                //GL11.glRotatef(-90, 0.0f, 1.0f, 0.0f);

                if(combo.equals(ItemSlashBlade.ComboSequence.HiraTuki)){
                    GL11.glTranslatef(0.0f,0.0f,-26.0f);
                }

                if(combo.equals(ItemSlashBlade.ComboSequence.Kiriorosi)){


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

                    if(0 < combo.swingAmplitude){
                        GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    }else{
                        GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    }

                    GL11.glTranslatef(0.0f, yoffset, 0.0f);
                    GL11.glTranslatef(xoffset , 0.0f, 0.0f );
                    GL11.glRotatef(180.0f, 0, 1, 0);
                }else if(combo.swingDirection < 0){


                    GL11.glRotatef(20.0f, -1.0f, 0, 0);
                    GL11.glRotatef(-30.0f, 0, 0, -1.0f);


                    GL11.glTranslatef(0.0f,0.0f,-12.0f);
                    //GL11.glRotatef(-30.0f,1,0,0);


                    GL11.glRotatef((90 + combo.swingDirection), 0.0f, -1.0f, 0.0f);


                    GL11.glRotatef((1.0f - progress) * -(180.0f + 60.0f), 0.0f, 0.0f, -1.0f);
                        /*
                        GL11.glTranslatef(0.0f, (1.0f-progress) * -5.0f, 0.0f);
                        GL11.glTranslatef((1.0f-progress) * 10.0f, 0.0f, 0.0f);
                        */

                    GL11.glTranslatef(-xoffset , 0.0f, 0.0f );
                    GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                    progress = 1.0f;

                    if(0 < combo.swingAmplitude){
                        GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    }else{
                        GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    }

                    GL11.glTranslatef(0.0f, yoffset, 0.0f);
                    GL11.glTranslatef(xoffset, 0.0f, 0.0f);
                    //GL11.glRotatef(180.0f, 0, 1, 0);
                }else{

                    GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
                    GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


                    GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


                    GL11.glTranslatef(-xoffset , 0.0f, 0.0f );


                    GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                    if(0 < combo.swingAmplitude){
                        GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    }else{
                        GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                    }

                    GL11.glTranslatef(0.0f, yoffset, 0.0f);
                    GL11.glTranslatef(xoffset , 0.0f, 0.0f );
                }


                progress = tmp;
            }

            if(isBroken)
                renderTarget = "blade_damaged";
            else
                renderTarget = "blade";


            float scaleLocal = 0.095f;
            GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
            GL11.glRotatef(-90.0f, 0, 0, 1);
            this.render.bindTexture(resourceTexture);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

            model.renderPart(renderTarget);

            if(!combo.useScabbard){
                model.renderPart(renderTarget + "_unsheathe");
            }

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            float lastx = OpenGlHelper.lastBrightnessX;
            float lasty = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            model.renderPart(renderTarget + "_luminous");
            if(!combo.useScabbard){
                model.renderPart(renderTarget + "_unsheathe_luminous");
            }

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

            GL11.glEnable(GL11.GL_LIGHTING);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            if (stack.hasEffect())
            {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.render.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f7 = 0.76F;

                Face.setColor(0xFF8040CC);

                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
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

        }GL11.glPopMatrix();

            //-----------------------------------------------------------------------------------------------------------------------

            GL11.glPushMatrix();{


            if((!combo.equals(ItemSlashBlade.ComboSequence.None)) && combo.useScabbard){


                if(combo.swingAmplitude < 0){
                    progress = 1.0f - progress;
                }

                GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
                GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


                GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


                GL11.glTranslatef(-xoffset , 0.0f, 0.0f );


                GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                if(0 < combo.swingAmplitude){
                    GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                }else{
                    GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                }

                GL11.glTranslatef(0.0f, yoffset, 0.0f);
                GL11.glTranslatef(xoffset , 0.0f, 0.0f );

            }


            GL11.glPushMatrix();

            float scaleLocal = 0.095f;
            GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
            GL11.glRotatef(-90.0f, 0, 0, 1);
            this.render.bindTexture(resourceTexture);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

            renderTarget = "sheath";
            model.renderPart(renderTarget);

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

            if (stack.hasEffect())
            {

                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                render.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f7 = 0.76F;

                Face.setColor(0xFF8040CC);

                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
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

            if(!isBroken && isEnchanted && (ItemSlashBlade.RequiredChargeTick < charge || combo.isCharged)){
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

                float ff1 = (float)entity.ticksExisted + partialTicks;
                render.bindTexture(armoredCreeperTextures);
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
                GL11.glScalef(scaleLocal,scaleLocal,scaleLocal);
                GL11.glRotatef(-90.0f,0,0,1);
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
        }GL11.glPopMatrix();

            //-----------------------------------------------------------------------------------------------------------------------
            GL11.glShadeModel(GL11.GL_FLAT);
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public void doHeldItemRenderLayer(EntityLivingBase entitylivingbaseIn, EnumSet<ItemSlashBlade.SwordType> types, WavefrontObject model)
    {
        if(!(render.getMainModel() instanceof ModelBiped))
            return;

        ItemStack itemstack = entitylivingbaseIn.getHeldItem();

        if (itemstack != null)
        {
            GlStateManager.pushMatrix();

            if (render.getMainModel().isChild)
            {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }


            ((ModelBiped)render.getMainModel()).postRenderArm(0.0625F);
            GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);


            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            if (entitylivingbaseIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.203125F, 0.0F);
            }

            {
                ResourceLocation resourceTexture = ItemSlashBlade.getModelTexture(itemstack);
                this.render.bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

                GL11.glTranslatef(0.0f, 0.15f, 0.0f);
                float scale = 0.008f;
                GL11.glScalef(scale,scale,scale);
                GL11.glRotatef(-90, 0, 1, 0);

                String renderTargets[];
                if(/*data[1] instanceof EntityPlayer
                    || */types.contains(ItemSlashBlade.SwordType.NoScabbard)){

                    if(types.contains(ItemSlashBlade.SwordType.Broken)){
                        renderTargets = new String[]{"blade_damaged"};
                    }else{
                        renderTargets = new String[]{"blade"};
                    }
                }else{
                    renderTargets = new String[]{"sheath", "blade"};
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
            }

            GlStateManager.popMatrix();
        }
    }
}
