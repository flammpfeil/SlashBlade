package mods.flammpfeil.slashblade.client.renderer;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.ItemRendererBaseWeapon;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.EnumSet;
import java.util.Map;

/**
 * Created by Furia on 14/08/15.
 */
public class BladeStandRender extends Render{

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
    * 掛台用モデル命名規則

    stand_[dual|single|upright|wall][_damaged][_ns][_r|_l][_u|_d]


    dual
        鞘・刀の2段
    single
        鞘入り刀の1段
    upright
        縦掛け
    wall
        鞘入り刀の1段壁用


    _damaged
        折れた状態

    _ns
        鞘無し用

    ※以下は無くてもよい

    _r or _l
        横反転　※縦と必ずセットで指定
    _u or _d
        縦反転　※横と必ずセットで指定

    * */

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {

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
}
