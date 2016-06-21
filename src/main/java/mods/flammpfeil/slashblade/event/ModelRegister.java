package mods.flammpfeil.slashblade.event;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.model.BladeModel;
import mods.flammpfeil.slashblade.client.model.BladeSpecialRender;
import mods.flammpfeil.slashblade.tileentity.DummyTileEntity;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.OBJState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.IOException;

/**
 * Created by Furia on 2016/02/07.
 */
public class ModelRegister {

    static final ModelResourceLocation modelLoc = new ModelResourceLocation("flammpfeil.slashblade:model/named/blade.obj");
    public static final ModelResourceLocation dummyLoc = new ModelResourceLocation("flammpfeil.slashblade:emptymodel");

    public ModelRegister() {
        MinecraftForge.EVENT_BUS.register(this);

        ModelLoader.setCustomModelResourceLocation(SlashBlade.weapon, 0, modelLoc);
        ModelLoader.setCustomModelResourceLocation(SlashBlade.bladeWood, 0, modelLoc);
        ModelLoader.setCustomModelResourceLocation(SlashBlade.bladeBambooLight, 0, modelLoc);
        ModelLoader.setCustomModelResourceLocation(SlashBlade.bladeSilverBambooLight, 0, modelLoc);
        ModelLoader.setCustomModelResourceLocation(SlashBlade.bladeWhiteSheath, 0, modelLoc);
        ModelLoader.setCustomModelResourceLocation(SlashBlade.wrapBlade, 0, modelLoc);
        ModelLoader.setCustomModelResourceLocation(SlashBlade.bladeNamed, 0, modelLoc);

        ForgeHooksClient.registerTESRItemStack(SlashBlade.weapon, 0, DummyTileEntity.class);
        ForgeHooksClient.registerTESRItemStack(SlashBlade.bladeWood, 0, DummyTileEntity.class);
        ForgeHooksClient.registerTESRItemStack(SlashBlade.bladeBambooLight, 0, DummyTileEntity.class);
        ForgeHooksClient.registerTESRItemStack(SlashBlade.bladeSilverBambooLight, 0, DummyTileEntity.class);
        ForgeHooksClient.registerTESRItemStack(SlashBlade.bladeWhiteSheath, 0, DummyTileEntity.class);
        ForgeHooksClient.registerTESRItemStack(SlashBlade.wrapBlade, 0, DummyTileEntity.class);
        ForgeHooksClient.registerTESRItemStack(SlashBlade.bladeNamed, 0, DummyTileEntity.class);

        ClientRegistry.bindTileEntitySpecialRenderer(DummyTileEntity.class, new BladeSpecialRender());
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event){
        event.modelRegistry.putObject(modelLoc, new BladeModel());

        String[] objnames = {
                "soul.obj",
                "ingot.obj",
                "sphere.obj",
                "tiny.obj",
                "crystal.obj",
                "trapezohedron.obj"};

        try {
            final SimpleModelState state = new SimpleModelState(ImmutableMap.of(
                    ItemCameraTransforms.TransformType.GROUND, new TRSRTransformation(
                            new Vector3f(0, 0, 0),
                            null,
                            new Vector3f(1.7f, 1.7f, 1.7f),
                            null),
                    ItemCameraTransforms.TransformType.GUI, new TRSRTransformation(
                            new Vector3f(0, 0, 0),
                            TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, 45+90, 0)),
                            new Vector3f(1.7f, 1.7f, 1.7f),
                            null),
                    ItemCameraTransforms.TransformType.FIXED, new TRSRTransformation(
                            new Vector3f(0, 0, -2f / 16),
                            TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, 180, 0)),
                            new Vector3f(1.7f, 1.7f, 1.7f),
                            null),
                    ItemCameraTransforms.TransformType.THIRD_PERSON, new TRSRTransformation(
                            new Vector3f(0, 1f / 16, -3f / 16),
                            TRSRTransformation.quatFromYXZDegrees(new Vector3f(-90, 0, 0)),
                            new Vector3f(0.55f, 0.55f, 0.55f),
                            null)
            ));

            for(String name : objnames){
                ModelResourceLocation loc = new ModelResourceLocation(SlashBlade.modid + ":" + name);
                IFlexibleBakedModel model = event.modelLoader.getModel(loc)
                        .bake(new OBJState(Lists.newArrayList(OBJModel.Group.ALL), true),
                                DefaultVertexFormats.ITEM, event.modelLoader.defaultTextureGetter());
                model = new IPerspectiveAwareModel.MapWrapper(model,state) {
                    @Override
                    public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
                        TRSRTransformation tr = state.apply(Optional.of(cameraTransformType)).or(TRSRTransformation.identity());
                        if(tr != TRSRTransformation.identity())
                        {
                            return Pair.of(this, tr.getMatrix());
                        }
                        return Pair.of(this, null);
                    }
                };
                event.modelRegistry.putObject(loc, model);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
