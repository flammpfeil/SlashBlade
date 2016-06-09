package mods.flammpfeil.slashblade.client.renderer;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.ItemRendererBaseWeapon;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.entity.RenderEx;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntityEx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by Furia on 14/08/15.
 */
public class BladeStandRender extends RenderEx{

    static public IModelCustom standModel = null;
    static public ResourceLocation modelLocation = new ResourceLocation("flammpfeil.slashblade","model/stand/stand.obj");
    static public ResourceLocation textureLocation = new ResourceLocation("flammpfeil.slashblade","model/stand/stand.png");

    private TextureManager engine(){
        return this.renderManager.renderEngine;
    }

    public static Map<EntityBladeStand.StandType,String> nameMap = createNameMap();
    private static <K, V> Map<K, V> createNameMap(){
        nameMap = Maps.newHashMap();
        nameMap.put(EntityBladeStand.StandType.Dual, "A");
        nameMap.put(EntityBladeStand.StandType.Single, "B");
        nameMap.put(EntityBladeStand.StandType.Upright, "C");
        nameMap.put(EntityBladeStand.StandType.Wall, "D");
        return (Map<K, V>)nameMap;
    }

    public static Map<EntityBladeStand.StandType,String> StandTypeName = createStandTypeNameMap();
    private static <K, V> Map<K, V> createStandTypeNameMap(){
        StandTypeName = Maps.newHashMap();
        StandTypeName.put(EntityBladeStand.StandType.Dual, "dual");
        StandTypeName.put(EntityBladeStand.StandType.Single, "single");
        StandTypeName.put(EntityBladeStand.StandType.Upright, "upright");
        StandTypeName.put(EntityBladeStand.StandType.Wall, "wall");
        return (Map<K, V>)StandTypeName;
    }

    /*
    * �|��p���f�������K��

    stand_[dual|single|upright|wall][_damaged][_ns][_r|_l][_u|_d]


    dual
        ��E����2�i
    single
        ����蓁��1�i
    upright
        �c�|��
    wall
        ����蓁��1�i�Ǘp


    _damaged
        �܂ꂽ���

    _ns
        �△���p

    ���ȉ��͖����Ă��悢

    _r or _l
        �����]�@���c�ƕK���Z�b�g�Ŏw��
    _u or _d
        �c���]�@�����ƕK���Z�b�g�Ŏw��

    * */
    @Override
    public void doRenderEx(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {

        if(renderOutlines){
            GlStateManager.enableColorMaterial();
            float h = (entity.ticksExisted % 80) / 80.0f;
            GlStateManager.enableOutlineMode(Color.getHSBColor(h,0.5f,1.0f).getRGB());
        }

        renderModel(entity, x, y, z, yaw, partialRenderTick);

        if(renderOutlines){
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();

            GlStateManager.enableLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        if(entity.isBurning())
            renderEntityOnFire(entity, x, y, z, partialRenderTick);
    }
    public void renderModel(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(standModel == null){
            standModel = AdvancedModelLoader.loadModel(modelLocation);
        }

        this.bindEntityTexture(entity);

        EntityBladeStand e = (EntityBladeStand)entity;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        float scale = 0.00675f;
        //=================stand init==========

        EntityBladeStand.StandType type = EntityBladeStand.getType(e);

        boolean HFlip = false;
        boolean VFlip = false;

        switch (e.getFlip()){
            case 1:
                HFlip = false;
                VFlip = true;
                break;
            case 2:
                HFlip = true;
                VFlip = false;
                break;
            case 3:
                VFlip = true;
                HFlip = true;
                break;
            default:
                HFlip = false;
                VFlip = false;
                break;
        }

        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(yaw,0,-1,0);

        if(type != EntityBladeStand.StandType.Naked){
            GL11.glPushMatrix();

            if(type== EntityBladeStand.StandType.Wall)
                GL11.glTranslatef(0,-0.5f,0);

            GL11.glScalef(scale, scale, scale);
            String renderTarget = nameMap.get(type);
            standModel.renderPart(renderTarget);

            GL11.glPopMatrix();
        }

        ItemStack blade = e.getBlade();
        if(blade != null){
            GL11.glPushMatrix();
            GL11.glShadeModel(GL11.GL_SMOOTH);

            Item item = blade.getItem();

            EnumSet<ItemSlashBlade.SwordType> types = ((ItemSlashBlade)item).getSwordType(blade);

            IModelCustom model = ItemRendererBaseWeapon.getModel(ItemSlashBlade.getModelLocation(blade));
            ResourceLocation resourceTexture = ItemSlashBlade.getModelTexture(blade);

            engine().bindTexture(resourceTexture);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glAlphaFunc(GL11.GL_GEQUAL,0.05f);


            boolean isProcessed = false;

            if(model instanceof WavefrontObject){
                WavefrontObject obj = (WavefrontObject) model;

                StringBuilder sb = new StringBuilder();

                sb.append("stand_");
                sb.append(StandTypeName.get(type));
                if(types.contains(ItemSlashBlade.SwordType.Broken))
                    sb.append("_damaged");
                if(types.contains(ItemSlashBlade.SwordType.NoScabbard))
                    sb.append("_ns");

                String targetBase = sb.toString();

                sb.append(HFlip ? "_r" : "_l");
                sb.append(VFlip ? "_u" : "_d");

                String targetFull = sb.toString();


                String renderTarget = null;

                for(GroupObject go : obj.groupObjects){
                    if(go.name.toLowerCase().equals(targetBase) || go.name.toLowerCase().equals(targetFull)) {
                        renderTarget = go.name;
                        break;
                    }
                }

                if(renderTarget != null){
                    GL11.glPushMatrix();

                    GL11.glScalef(scale, scale, scale);

                    model.renderPart(renderTarget);

                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glEnable(GL11.GL_BLEND);

                    float lastx = OpenGlHelper.lastBrightnessX;
                    float lasty = OpenGlHelper.lastBrightnessY;
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    model.renderPart(renderTarget + "_luminous");

                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GL11.glEnable(GL11.GL_LIGHTING);

                    GL11.glPopMatrix();
                    isProcessed = true;
                }

            }

            //================== render edge ==============
            if(!isProcessed && (!(item instanceof ItemSlashBladeWrapper) || ItemSlashBladeWrapper.hasWrapedItem(blade))){
                GL11.glPushMatrix();

                //==================init================

                float hFlipFactor = HFlip ? -1f : 1f;

                switch (type){
                    case Naked:

                        GL11.glScalef(scale, scale, scale);

                        if(!types.contains(ItemSlashBlade.SwordType.Broken))
                            GL11.glTranslatef(0, 200.0f, 0);
                        else
                            GL11.glTranslatef(0, 20.0f, 0);

                        GL11.glRotatef(96.0f, 0, 0, 1);

                        if(VFlip){
                            GL11.glTranslatef(0, 15f, 0);
                            GL11.glRotatef(7.0f, 0, 0, 1);
                            GL11.glRotatef(180.0f,1,0,0);
                        }

                        break;

                    case Dual:
                        GL11.glTranslatef(0.8f * hFlipFactor, 0.125f, 0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);

                        if(HFlip)
                            GL11.glRotatef(180.0f,0,1,0);

                        break;

                    case Wall:
                        GL11.glTranslatef(0, -0.5f, -0.375f);
                    case Single:
                        GL11.glTranslatef(0.8f * hFlipFactor, 0.0f, 0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);

                        if(HFlip)
                            GL11.glRotatef(180.0f,0,1,0);

                        if(VFlip){
                            GL11.glTranslatef(0, 15f, 0);
                            GL11.glRotatef(7.0f,0,0,1);
                            GL11.glRotatef(180.0f,1,0,0);
                        }

                        break;

                    case Upright:
                        if(VFlip){
                            GL11.glRotatef(2f, 0, 0, 1);
                            GL11.glTranslatef(0.05f, 0, 0);
                        }

                        if(!HFlip){
                            GL11.glScalef(scale, scale, scale);
                            GL11.glTranslatef(-17.277f, 235.960f, 0);
                            GL11.glRotatef(96.0f, 0, 0, 1);
                        }else{
                            GL11.glScalef(scale, scale, scale);
                            GL11.glTranslatef(27.597f, -21.674f, 0);
                            GL11.glRotatef(103.35f, 0, 0, 1);
                        }

                        if(HFlip)
                            GL11.glRotatef(180.0f,0,1,0);

                        if(VFlip){
                            GL11.glTranslatef(0, 15f, 0);
                            GL11.glRotatef(7.0f, 0, 0, 1);
                            GL11.glRotatef(180.0f,1,0,0);
                        }

                        break;
                }

                //===========render==========

                String renderTarget;
                if(types.contains(ItemSlashBlade.SwordType.Broken))
                    renderTarget = "blade_damaged";
                else
                    renderTarget = "blade";

                model.renderPart(renderTarget);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);

                float lastx = OpenGlHelper.lastBrightnessX;
                float lasty = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                model.renderPart(renderTarget + "_luminous");

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glEnable(GL11.GL_LIGHTING);

                GL11.glPopMatrix();
            }


            //================= render scabbard =================
            if(!isProcessed && (!types.contains(ItemSlashBlade.SwordType.NoScabbard))){
                GL11.glPushMatrix();

                //==================init================

                float hFlipFactor = HFlip ? -1f : 1f;
                float vFlipFactor = VFlip ? -1f : 1f;

                switch (type){
                    case Naked:
                        if(HFlip){
                            GL11.glScalef(0, 0, 0);
                        }
                        GL11.glTranslatef(1.5f, -0.45f, 0.5f);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(90.0f, 1, 0, 0);

                        if(HFlip){
                            GL11.glRotatef(180.0f,0,1,0);
                        }
                        break;
                    case Dual:
                        GL11.glTranslatef(1.1f * hFlipFactor,-0.17722f,0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);
                        break;
                    case Wall:
                        GL11.glTranslatef(0, -0.5f, -0.375f);
                    case Single:
                        GL11.glTranslatef(0.8f * hFlipFactor,0.0f,0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);
                        break;
                    case Upright:

                        if(VFlip){
                            GL11.glRotatef(2f, 0, 0, 1);
                            GL11.glTranslatef(0.05f, 0, 0);
                        }

                        if(!HFlip){
                            GL11.glScalef(scale, scale, scale);
                            GL11.glTranslatef(-17.277f,235.960f,0);
                            GL11.glRotatef(96.0f, 0, 0, 1);
                        }else{
                            GL11.glScalef(scale, scale, scale);
                            GL11.glTranslatef(27.597f, -21.674f, 0);
                            GL11.glRotatef(103.35f, 0, 0, 1);
                        }

                        break;
                }

                if(HFlip)
                    GL11.glRotatef(180.0f,0,1,0);

                if(VFlip){
                    GL11.glTranslatef(0, 15f, 0);
                    GL11.glRotatef(7.0f,0,0,1);
                    GL11.glRotatef(180.0f,1,0,0);
                }

                //===========render==========

                String renderTarget = "sheath";
                model.renderPart(renderTarget);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);

                float lastx = OpenGlHelper.lastBrightnessX;
                float lasty = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) 240.0f / 1.0F, (float) 240.0f / 1.0F);

                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                model.renderPart(renderTarget + "_luminous");

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                GL11.glEnable(GL11.GL_LIGHTING);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                GL11.glPopMatrix();
            }

            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glPopMatrix();
        }


        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return textureLocation;
    }


    private void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks) {
        GlStateManager.pushAttrib();

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        GlStateManager.depthMask(false);

        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
/*        TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
        TextureAtlasSprite textureatlassprite1 = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");
*/
        IIcon textureatlassprite = Blocks.fire.getFireIcon(0);
        IIcon textureatlassprite1 = Blocks.fire.getFireIcon(1);

        int i = 0;
        for(int re = 0; re < 3; re++){
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x, (float) y, (float) z);
            float f = entity.width * 1.4F;
            GlStateManager.scale(f, f, f);

            Tessellator tessellator = Tessellator.instance;
            float f1 = 0.5F;
            float f2 = 0.0F;
            float f3 = entity.height / f;
            float f4 = (float) (entity.posY - entity.boundingBox.minY);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0f, -0.3F + (float) ((int) f3) * 0.02F - re * 0.2f);

            if(0 < re) {
                float reScale = 1.0f / (re + 0.25f);
                GlStateManager.scale(reScale, 0.75f, reScale);
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ZERO);

            GlStateManager.color(0.1F, 0.0F, 1.0F, 1.0F);

            float f5 = 0.0F;
            tessellator.startDrawingQuads();

            while (f3 > 0.0F) {
                IIcon textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
                this.bindTexture(TextureMap.locationBlocksTexture);
                float f6 = textureatlassprite2.getMinU();
                float f7 = textureatlassprite2.getMinV();
                float f8 = textureatlassprite2.getMaxU();
                float f9 = textureatlassprite2.getMaxV();

                if (i / 2 % 2 == 0) {
                    float f10 = f8;
                    f8 = f6;
                    f6 = f10;
                }

                tessellator.addVertexWithUV((double) (f1 - f2),  (double) (0.0F - f4), (double) f5, (double) f8, (double) f9);
                tessellator.addVertexWithUV((double) (-f1 - f2), (double) (0.0F - f4), (double) f5, (double) f6, (double) f9);
                tessellator.addVertexWithUV((double) (-f1 - f2), (double) (1.4F - f4), (double) f5, (double) f6, (double) f7);
                tessellator.addVertexWithUV((double) (f1 - f2),  (double) (1.4F - f4), (double) f5, (double) f8, (double) f7);
                f3 -= 0.45F;
                f4 -= 0.45F;
                f1 *= 0.9F;
                f5 += 0.03F;
                ++i;
            }

            tessellator.draw();
            GlStateManager.popMatrix();
        }


        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();

        GlStateManager.popAttrib();
    }
}
