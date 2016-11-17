package mods.flammpfeil.slashblade.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.event.ModelRegister;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Color3b;
import javax.vecmath.Color4b;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;
import java.awt.*;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeModel implements IPerspectiveAwareModel {

    List<BakedQuad> emptyList = null;
    List<BakedQuad> getEmptyList(){
        if(emptyList == null)
            emptyList = Lists.newArrayList(new BakedQuad(new int[28], 0, EnumFacing.UP, getParticleTexture(), false, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
        return emptyList;
    }

    ItemStack proudsoul = null;
    ItemModelMesher modelMesher = null;
    List<BakedQuad> getDefaultQuards(){
        if(modelMesher == null) {
            modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
            proudsoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
        }
        return modelMesher.getItemModel(proudsoul).getQuads(null,null,0);
    }

    static ItemStack targetStack = null;
    static ItemSlashBlade itemBlade = null;
    static EntityLivingBase user = null;

    /**
     * 0 : type set
     * 1 : texture set
     */
    static int renderPath = 0;
    static int drawStep = -1;

    static ItemCameraTransforms.TransformType type = ItemCameraTransforms.TransformType.NONE;


    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(ImmutableList.<ItemOverride>of()){
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {

                if(targetStack != null && ItemSlashBlade.getItemTagCompound(targetStack).getBoolean("IsRender")){
                    //skip
                }else if(stack != null && stack.getItem() instanceof ItemSlashBlade) {
                    targetStack = stack;
                    ItemSlashBlade.getItemTagCompound(targetStack).setBoolean("IsRender",true);
                    itemBlade = (ItemSlashBlade) stack.getItem();
                    user = entity;
                }else{
                    targetStack = null;
                    itemBlade = null;
                    user = null;
                }

                return super.handleItemState(originalModel, stack, world, entity);
            }

        };
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        //todo : BakedQuard create 3 convert to 4 vertices
        if(side != null)
            return getEmptyList();

        //no texture;
        if(drawStep == 0) return getDefaultQuards();

        /*
        if(type == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || type == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                || type == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || type == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) return getEmptyList();

        //clear drawstate
        try{
            Tessellator.getInstance().draw();
        }catch(Exception e){
            return getDefaultQuards();
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        //GL11.glPushClientAttrib(GL11.GL_ALL_ATTRIB_BITS);

        if(renderPath++ >= 1) {
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
        if(type == ItemCameraTransforms.TransformType.GUI)
            scale = 0.008f;
        GL11.glScalef(scale, scale, scale);

        EnumSet<ItemSlashBlade.SwordType> types = itemBlade.getSwordType(targetStack);
        WavefrontObject model = BladeModelManager.getInstance().getModel(itemBlade.getModelLocation(targetStack));

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

        if(renderPath == 1 && type == ItemCameraTransforms.TransformType.GUI){
            model = BladeModelManager.getInstance().getModel(BladeModelManager.resourceDurabilityModel);
            Minecraft.getMinecraft().getTextureManager().bindTexture(BladeModelManager.resourceDurabilityTexture);

            double par = itemBlade.getDurabilityForDisplay(targetStack);

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

            GlStateManager.translate(0.0F, 0.0F, -2.0f * itemBlade.getDurabilityForDisplay(targetStack));
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
        //reset drawstate
        Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        */
        return getEmptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        drawStep = 1;
        renderPath = 0;

        /*
        //textuer bind
        ResourceLocation resourceTexture = itemBlade.getModelTexture(targetStack);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceTexture);
        */

        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(SlashBlade.proudSoul);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(ItemCameraTransforms.DEFAULT){
            @Override
            public ItemTransformVec3f getTransform(TransformType srctype) {
                type = srctype;
                return super.getTransform(srctype);
            }
        } ;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        this.type = cameraTransformType;

        drawStep = 0;
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, ModelRotation.X0_Y0, cameraTransformType);
    }
}
