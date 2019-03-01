package mods.flammpfeil.slashblade.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Random;

/**
 * Created by Furia on 2016/02/07.
 */
public class BladeModel implements IBakedModel {

    List<BakedQuad> emptyList = null;
    List<BakedQuad> getEmptyList(){
        if(emptyList == null)
            emptyList = Lists.newArrayList(new BakedQuad(new int[28], 0, EnumFacing.UP, getParticleTexture(), false, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM));
        return emptyList;
    }

    ItemStack proudsoul = ItemStack.EMPTY;
    ItemModelMesher modelMesher = null;
    List<BakedQuad> getDefaultQuards(){
        if(modelMesher == null) {
            modelMesher = Minecraft.getInstance().getItemRenderer().getItemModelMesher();
            proudsoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
        }
        return modelMesher.getItemModel(proudsoul).getQuads(null,null,new Random(0));
    }

    static ItemStack targetStack = ItemStack.EMPTY;
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
        return new ItemOverrideList(null, null, null, ImmutableList.<ItemOverride>of()){

            @Override
            public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {

                if(stack != null && stack.getItem() instanceof ItemSlashBlade) {
                    targetStack = stack;
                    ItemSlashBlade.getItemTagCompound(targetStack).setBoolean("IsRender",true);
                    itemBlade = (ItemSlashBlade) stack.getItem();
                    user = entity == null ? user : entity;
                }else{
                    targetStack = ItemStack.EMPTY;
                    itemBlade = null;
                    user = null;
                }

                return super.getModelWithOverrides(originalModel, stack, world, entity);
            }

        };
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, Random rand) {
        //todo : BakedQuard create 3 convert to 4 vertices
        if(side != null)
            return getEmptyList();

        //no texture;
        if(drawStep == 0) return getDefaultQuards();

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
        ResourceLocationRaw resourceTexture = itemBlade.getModelTexture(targetStack);
        Minecraft.getInstance().getTextureManager().bindTexture(resourceTexture);
        */

        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(SlashBlade.proudSoul);
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
        return PerspectiveMapWrapper.handlePerspective(this, ModelRotation.X0_Y0, cameraTransformType);
    }
}
