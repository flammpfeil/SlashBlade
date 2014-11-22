package mods.flammpfeil.slashblade.client.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.client.FMLClientHandler;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.mapped.CacheUtil;

import java.nio.DoubleBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Furia on 14/11/17.
 */
public class ItemRendererSpecialMaterial implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    IIcon object = new IIcon() {
        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }

        @Override
        public float getMinU() {
            return 0;
        }

        @Override
        public float getMaxU() {
            return 1;
        }

        @Override
        public float getInterpolatedU(double p_94214_1_) {
            return 0;
        }

        @Override
        public float getMinV() {
            return 0;
        }

        @Override
        public float getMaxV() {
            return 1;
        }

        @Override
        public float getInterpolatedV(double p_94207_1_) {
            return 0;
        }

        @Override
        public String getIconName() {
            return null;
        }
    };

    ResourceLocation missingResource = new ResourceLocation(SlashBlade.modid,"textures/gui/missingno");
    LoadingCache<String,ResourceLocation> resourceCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, ResourceLocation>() {
                public ResourceLocation load(String key) {
                    return new ResourceLocation(SlashBlade.modid, "textures/gui/" + key);
            }
            });

    DoubleBuffer invRenderMatrix = CacheUtil.createDoubleBuffer(16);

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        TextureManager engine = FMLClientHandler.instance().getClient().renderEngine;
        if(engine == null) return;

        GL11.glPushMatrix();

        GL11.glGetDouble(GL11.GL_MODELVIEW_MATRIX, invRenderMatrix);
        if(invRenderMatrix.get(2+2*4) == 0){
            invRenderMatrix.put(2+2*4,1);
            GL11.glLoadMatrix(invRenderMatrix);
        }
        GL11.glTranslatef(0,0,1);


        boolean renderEffect = true;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        IIcon icon = null;
        ResourceLocation resourcelocation = null;
        int damage = item.getItemDamage();
        if(damage == 0xFFFF || damage == 0x10000){
            try {
                resourcelocation = resourceCache.get(item.getDisplayName());
            } catch (ExecutionException e) {
                resourcelocation = missingResource;
            }
            icon = object;
        }else{
            resourcelocation = engine.getResourceLocation(item.getItemSpriteNumber());
            icon = item.getIconIndex();
        }
        engine.bindTexture(resourcelocation);

        RenderItem.getInstance().renderIcon(0, 0, icon, 16, 16);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        if (item.getItemDamage() != 0xFFFF)
        {
            RenderItem.getInstance().renderEffect(engine, 0, 0);
        }
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

}
