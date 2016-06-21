package mods.flammpfeil.slashblade.client.model;

import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.tileentity.DummyTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Color4f;
import java.awt.*;
import java.util.EnumSet;

/**
 * Created by Furia on 2016/06/21.
 */
public class BladeSpecialRender extends TileEntitySpecialRenderer<DummyTileEntity> {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Override
    public void renderTileEntityAt(DummyTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        if(te != null) return;

        if(BladeModel.targetStack == null)
            return;

        ResourceLocation resourceTexture = BladeModel.itemBlade.getModelTexture(BladeModel.targetStack);
        bindTexture(resourceTexture);

        render();

        if(BladeModel.targetStack.hasEffect()){
            renderEffect();
        }

    }

    private void renderEffect()
    {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        this.render();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        this.render();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
    }

    private void render(){

        if(BladeModel.type == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || BladeModel.type == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                || BladeModel.type == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || BladeModel.type == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) return;


        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        //GL11.glPushClientAttrib(GL11.GL_ALL_ATTRIB_BITS);

        if(BladeModel.renderPath++ >= 1) {
            Face.setColor(0xFF8040CC);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GlStateManager.scale(0.1F, 0.1F, 0.1F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }else{
            Face.resetColor();

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDisable(GL11.GL_CULL_FACE);


            GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, ad renderEffect can derp them up.
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);
        }

        GL11.glPushMatrix();

        GL11.glTranslatef(0.5f, 0.5f, 0.5f);

        float scale = 0.0095f;
        if(BladeModel.type == ItemCameraTransforms.TransformType.GUI)
            scale = 0.008f;
        GL11.glScalef(scale, scale, scale);

        EnumSet<ItemSlashBlade.SwordType> types = BladeModel.itemBlade.getSwordType(BladeModel.targetStack);
        WavefrontObject model = BladeModelManager.getInstance().getModel(BladeModel.itemBlade.getModelLocation(BladeModel.targetStack));

        String renderTarget;
        if(types.contains(ItemSlashBlade.SwordType.Broken))
            renderTarget = "item_damaged";
        else if(!types.contains(ItemSlashBlade.SwordType.NoScabbard)){
            renderTarget = "item_blade";
        }else{
            renderTarget = "item_bladens";
        }

        model.renderPart(renderTarget);


        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        float lastx = OpenGlHelper.lastBrightnessX;
        float lasty = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        model.renderPart(renderTarget + "_luminous");

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        if(BladeModel.renderPath == 1 && BladeModel.type == ItemCameraTransforms.TransformType.GUI){
            model = BladeModelManager.getInstance().getModel(BladeModelManager.resourceDurabilityModel);
            bindTexture(BladeModelManager.resourceDurabilityTexture);

            double par = BladeModel.itemBlade.getDurabilityForDisplay(BladeModel.targetStack);

            GlStateManager.translate(0.0F, 0.0F, 0.1f);

            Color4f aCol = new Color4f(0.25f,0.25f,0.25f,1.0f);
            Color4f bCol = new Color4f(new Color(0xA52C63));
            aCol.interpolate(bCol,(float)par);

            Face.setColor(aCol.get().getRGB());
            model.renderPart("base");
            Face.resetColor();

            boolean isBroken = types.contains(ItemSlashBlade.SwordType.Broken);

            if(isBroken){
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GlStateManager.translate(0.0F, 0.5F, 0.0f);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }

            GlStateManager.translate(0.0F, 0.0F, -2.0f * BladeModel.itemBlade.getDurabilityForDisplay(BladeModel.targetStack));
            model.renderPart("color");

            if(isBroken){
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GlStateManager.loadIdentity();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glEnable(GL11.GL_CULL_FACE);


        GL11.glPopMatrix();
        //GL11.glPopClientAttrib();
        GL11.glPopAttrib();

        Face.resetColor();
    }
}
