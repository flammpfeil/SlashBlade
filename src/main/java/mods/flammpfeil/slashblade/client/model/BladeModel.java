package mods.flammpfeil.slashblade.client.model;

import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ISmartItemModel;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeModel implements ISmartItemModel {

    static final List<BakedQuad> emptyList = Lists.newArrayList();

    ItemStack targetStack = null;
    ItemSlashBlade itemBlade = null;

    int renderPath = 0;

    ItemCameraTransforms.TransformType type = ItemCameraTransforms.TransformType.NONE;

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        if(stack != null && stack.getItem() instanceof ItemSlashBlade) {
            this.targetStack = stack;
            this.itemBlade = (ItemSlashBlade) stack.getItem();
        }else{
            targetStack = null;
            itemBlade = null;
        }

        return this;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
        return emptyList;
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {

        if(type == ItemCameraTransforms.TransformType.THIRD_PERSON
                || type == ItemCameraTransforms.TransformType.FIRST_PERSON) return emptyList;

        //clear drawstate
        Tessellator.getInstance().draw();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        //GL11.glPushClientAttrib(GL11.GL_ALL_ATTRIB_BITS);

        if(renderPath++ >= 1) {
            Face.setColor(0xFF8040CC);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GlStateManager.scale(0.1F, 0.1F, 0.1F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }else{
            Face.resetColor();
        }

        GL11.glPushMatrix();

        GL11.glTranslatef(0.5f, 0.5f, 0.5f);

        float scale = 0.0095f;
        if(type == ItemCameraTransforms.TransformType.GUI)
            scale = 0.008f;
        GL11.glScalef(scale, scale, scale);

        EnumSet<ItemSlashBlade.SwordType> types = itemBlade.getSwordType(targetStack);
        WavefrontObject model = BladeModelManager.getInstance().getModel(itemBlade.getModelLocation(targetStack));

/*
        //texture param update
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
*/

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

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glEnable(GL11.GL_CULL_FACE);


        GL11.glPopMatrix();
        //GL11.glPopClientAttrib();
        GL11.glPopAttrib();

        Face.resetColor();
        //reset drawstate
        Tessellator.getInstance().getWorldRenderer().begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        return emptyList;
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
        renderPath = 0;

        //textuer bind
        ResourceLocation resourceTexture = itemBlade.getModelTexture(targetStack);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceTexture);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDisable(GL11.GL_CULL_FACE);


        GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, ad renderEffect can derp them up.
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);


        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT){

            @Override
            public void applyTransform(TransformType type) {
                super.applyTransform(type);

                BladeModel.this.type = type;
            }
        };
    }
}
