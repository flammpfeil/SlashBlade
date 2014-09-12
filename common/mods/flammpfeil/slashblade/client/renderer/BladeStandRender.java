package mods.flammpfeil.slashblade.client.renderer;

import mods.flammpfeil.slashblade.AdvancedModelLoader;
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
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

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

    public static String[] types = {"A","B","C"};

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {

        if(standModel == null){
            standModel = AdvancedModelLoader.loadModel(modelLocation);
        }

        this.bindEntityTexture(entity);

        EntityBladeStand e = (EntityBladeStand)entity;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float scale = 0.00675f;
        //=================stand init==========

        int type = EntityBladeStand.getType(e);

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

        GL11.glTranslatef((float)x,(float)y,(float)z);
        GL11.glRotatef(yaw,0,-1,0);

        if(0 <= type){
            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, scale);
            String renderTarget = types[type];
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

            //================== render edge ==============
            if(!(item instanceof ItemSlashBladeWrapper) || ItemSlashBladeWrapper.hasWrapedItem(blade)){
                GL11.glPushMatrix();

                //==================init================

                float hFlipFactor = HFlip ? -1f : 1f;

                switch (type){
                    case -1:

                        GL11.glScalef(scale, scale, scale);

                        if(!types.contains(ItemSlashBlade.SwordType.Broken))
                            GL11.glTranslatef(0, 200.0f, 0);
                        else
                            GL11.glTranslatef(0, 20.0f, 0);

                        GL11.glRotatef(96.0f, 0, 0, 1);

                        break;

                    case 0:
                        GL11.glTranslatef(0.8f * hFlipFactor, 0.125f, 0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);

                        if(HFlip)
                            GL11.glRotatef(180.0f,0,1,0);

                        break;
                    case 1:
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

                    default:
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
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                model.renderPart(renderTarget + "_luminous");
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glPopMatrix();
            }


            //================= render scabbard =================
            if(0 <= type && !types.contains(ItemSlashBlade.SwordType.NoScabbard)){
                GL11.glPushMatrix();

                //==================init================

                float hFlipFactor = HFlip ? -1f : 1f;
                float vFlipFactor = VFlip ? -1f : 1f;

                switch (type){
                    case 0:
                        GL11.glTranslatef(1.1f * hFlipFactor,-0.17722f,0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);
                        break;
                    case 1:
                        GL11.glTranslatef(0.8f * hFlipFactor,0.0f,0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);
                        break;
                    default:

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
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                model.renderPart(renderTarget + "_luminous");
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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
