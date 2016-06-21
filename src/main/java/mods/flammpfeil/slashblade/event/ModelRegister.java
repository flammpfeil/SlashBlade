package mods.flammpfeil.slashblade.event;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.model.BladeModel;
import mods.flammpfeil.slashblade.client.model.BladeSpecialRender;
import mods.flammpfeil.slashblade.tileentity.DummyTileEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        event.getModelRegistry().putObject(modelLoc, new BladeModel());
    }
}
