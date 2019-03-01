package mods.flammpfeil.slashblade.client.renderer.entity;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.client.util.LightSetup;
import mods.flammpfeil.slashblade.client.util.MSAutoCloser;
import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.obj.GroupObject;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by Furia on 14/08/15.
 */
public class BladeStandRender extends Render{

    static public WavefrontObject standModel = null;

    static public ResourceLocationRaw modelLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/stand/stand.obj");
    static public ResourceLocationRaw textureLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/stand/stand.png");

    public BladeStandRender(RenderManager renderManager) {
        super(renderManager);
    }

    private TextureManager engine(){
        return this.renderManager.textureManager;
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

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(renderOutlines){
            GlStateManager.enableColorMaterial();
            float h = (entity.ticksExisted % 80) / 80.0f;
            GlStateManager.enableOutlineMode(Color.getHSBColor(h,0.5f,1.0f).getRGB());
        }

        renderModel(entity, x, y, z, yaw, partialRenderTick);

        if(renderOutlines){
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        if(entity.isBurning())
            renderEntityOnFire(entity, x, y, z, partialRenderTick);
    }
    public void renderModel(Entity entity, double x, double y, double z, float yaw, float partialRenderTick) {
        if(standModel == null){
            standModel = new WavefrontObject(modelLocation);
        }

        this.bindEntityTexture(entity);

        EntityBladeStand e = (EntityBladeStand)entity;

        try(MSAutoCloser mac = MSAutoCloser.pushMatrix()) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            float scale = 0.00675f;
            //=================stand init==========

            EntityBladeStand.StandType type = EntityBladeStand.getType(e);

            boolean HFlip = false;
            boolean VFlip = false;

            switch (e.getFlip()) {
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

            GL11.glTranslatef((float) x, (float) y + 0.5f, (float) z);
            GL11.glRotatef(yaw, 0, -1, 0);

            if (type != EntityBladeStand.StandType.Naked) {
                try (MSAutoCloser msac = MSAutoCloser.pushMatrix()) {
                    if (type == EntityBladeStand.StandType.Wall)
                        GL11.glTranslatef(0, -0.5f, 0);

                    GL11.glScalef(scale, scale, scale);
                    String renderTarget = nameMap.get(type);
                    standModel.renderPart(renderTarget);
                }
            }

            ItemStack blade = e.getBlade();
            if (!blade.isEmpty()) try (MSAutoCloser msacc = MSAutoCloser.pushMatrix()) {
                GL11.glShadeModel(GL11.GL_SMOOTH);

                ItemSlashBlade item = (ItemSlashBlade) blade.getItem();

                EnumSet<ItemSlashBlade.SwordType> types = item.getSwordType(blade);

                WavefrontObject model = BladeModelManager.getInstance().getModel(item.getModelLocation(blade));
                ResourceLocationRaw resourceTexture = item.getModelTexture(blade);

                engine().bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);


                boolean isProcessed = false;

                if (model instanceof WavefrontObject) {
                    WavefrontObject obj = (WavefrontObject) model;

                    StringBuilder sb = new StringBuilder();

                    sb.append("stand_");
                    sb.append(StandTypeName.get(type));
                    if (types.contains(ItemSlashBlade.SwordType.Broken))
                        sb.append("_damaged");
                    if (types.contains(ItemSlashBlade.SwordType.NoScabbard))
                        sb.append("_ns");

                    String targetBase = sb.toString();

                    sb.append(HFlip ? "_r" : "_l");
                    sb.append(VFlip ? "_u" : "_d");

                    String targetFull = sb.toString();


                    String renderTarget = null;

                    for (GroupObject go : obj.groupObjects) {
                        if (go.name.toLowerCase().equals(targetBase) || go.name.toLowerCase().equals(targetFull)) {
                            renderTarget = go.name;
                            break;
                        }
                    }

                    if (renderTarget != null) {
                        try (MSAutoCloser msac = MSAutoCloser.pushMatrix()) {
                            GL11.glScalef(scale, scale, scale);

                            model.renderPart(renderTarget);

                            GlStateManager.disableLighting();
                            try(LightSetup ls = LightSetup.setupAdd()){
                                model.renderPart(renderTarget + "_luminous");
                            }
                            GlStateManager.enableLighting();
                        }
                        isProcessed = true;
                    }

                }

                //================== render edge ==============
                if (!isProcessed && (!(item instanceof ItemSlashBladeWrapper) || ItemSlashBladeWrapper.hasWrapedItem(blade))) {
                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix()) {
                        //==================init================

                        float hFlipFactor = HFlip ? -1f : 1f;

                        switch (type) {
                            case Naked:

                                GL11.glScalef(scale, scale, scale);

                                if (!types.contains(ItemSlashBlade.SwordType.Broken))
                                    GL11.glTranslatef(0, 200.0f, 0);
                                else
                                    GL11.glTranslatef(0, 20.0f, 0);

                                GL11.glRotatef(96.0f, 0, 0, 1);

                                if (VFlip) {
                                    GL11.glTranslatef(0, 15f, 0);
                                    GL11.glRotatef(7.0f, 0, 0, 1);
                                    GL11.glRotatef(180.0f, 1, 0, 0);
                                }

                                break;

                            case Dual:
                                GL11.glTranslatef(0.8f * hFlipFactor, 0.125f, 0);
                                GL11.glScalef(scale, scale, scale);
                                GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);

                                if (HFlip)
                                    GL11.glRotatef(180.0f, 0, 1, 0);

                                break;

                            case Wall:
                                GL11.glTranslatef(0, -0.5f, -0.375f);
                            case Single:
                                GL11.glTranslatef(0.8f * hFlipFactor, 0.0f, 0);
                                GL11.glScalef(scale, scale, scale);
                                GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);

                                if (HFlip)
                                    GL11.glRotatef(180.0f, 0, 1, 0);

                                if (VFlip) {
                                    GL11.glTranslatef(0, 15f, 0);
                                    GL11.glRotatef(7.0f, 0, 0, 1);
                                    GL11.glRotatef(180.0f, 1, 0, 0);
                                }

                                break;

                            case Upright:
                                if (VFlip) {
                                    GL11.glRotatef(2f, 0, 0, 1);
                                    GL11.glTranslatef(0.05f, 0, 0);
                                }

                                if (!HFlip) {
                                    GL11.glScalef(scale, scale, scale);
                                    GL11.glTranslatef(-17.277f, 235.960f, 0);
                                    GL11.glRotatef(96.0f, 0, 0, 1);
                                } else {
                                    GL11.glScalef(scale, scale, scale);
                                    GL11.glTranslatef(27.597f, -21.674f, 0);
                                    GL11.glRotatef(103.35f, 0, 0, 1);
                                }

                                if (HFlip)
                                    GL11.glRotatef(180.0f, 0, 1, 0);

                                if (VFlip) {
                                    GL11.glTranslatef(0, 15f, 0);
                                    GL11.glRotatef(7.0f, 0, 0, 1);
                                    GL11.glRotatef(180.0f, 1, 0, 0);
                                }

                                break;
                        }

                        //===========render==========

                        String renderTarget;
                        if (types.contains(ItemSlashBlade.SwordType.Broken))
                            renderTarget = "blade_damaged";
                        else
                            renderTarget = "blade";

                        model.renderPart(renderTarget);

                        GlStateManager.disableLighting();
                        GL11.glEnable(GL11.GL_BLEND);

                        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                        try (LightSetup ls = LightSetup.setup()) {
                            model.renderPart(renderTarget + "_luminous");
                        }
                        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                        GlStateManager.enableLighting();
                    }
                }


                //================= render scabbard =================
                if (!isProcessed && (!types.contains(ItemSlashBlade.SwordType.NoScabbard))) {

                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix()) {

                        //==================init================

                        float hFlipFactor = HFlip ? -1f : 1f;
                        float vFlipFactor = VFlip ? -1f : 1f;

                        switch (type) {
                            case Naked:
                                if (HFlip) {
                                    GL11.glScalef(0, 0, 0);
                                }
                                GL11.glTranslatef(1.5f, -0.45f, 0.5f);
                                GL11.glScalef(scale, scale, scale);
                                GL11.glRotatef(90.0f, 1, 0, 0);

                                if (HFlip) {
                                    GL11.glRotatef(180.0f, 0, 1, 0);
                                }
                                break;
                            case Dual:
                                GL11.glTranslatef(1.1f * hFlipFactor, -0.17722f, 0);
                                GL11.glScalef(scale, scale, scale);
                                GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);
                                break;
                            case Wall:
                                GL11.glTranslatef(0, -0.5f, -0.375f);
                            case Single:
                                GL11.glTranslatef(0.8f * hFlipFactor, 0.0f, 0);
                                GL11.glScalef(scale, scale, scale);
                                GL11.glRotatef(-3.5f * hFlipFactor, 0, 0, 1);
                                break;
                            case Upright:

                                if (VFlip) {
                                    GL11.glRotatef(2f, 0, 0, 1);
                                    GL11.glTranslatef(0.05f, 0, 0);
                                }

                                if (!HFlip) {
                                    GL11.glScalef(scale, scale, scale);
                                    GL11.glTranslatef(-17.277f, 235.960f, 0);
                                    GL11.glRotatef(96.0f, 0, 0, 1);
                                } else {
                                    GL11.glScalef(scale, scale, scale);
                                    GL11.glTranslatef(27.597f, -21.674f, 0);
                                    GL11.glRotatef(103.35f, 0, 0, 1);
                                }

                                break;
                        }

                        if (HFlip)
                            GL11.glRotatef(180.0f, 0, 1, 0);

                        if (VFlip) {
                            GL11.glTranslatef(0, 15f, 0);
                            GL11.glRotatef(7.0f, 0, 0, 1);
                            GL11.glRotatef(180.0f, 1, 0, 0);
                        }

                        //===========render==========

                        String renderTarget = "sheath";
                        model.renderPart(renderTarget);

                        GlStateManager.disableLighting();
                        GL11.glEnable(GL11.GL_BLEND);

                        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                        try (LightSetup ls = LightSetup.setup()) {
                            model.renderPart(renderTarget + "_luminous");
                        }
                        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                        GlStateManager.enableLighting();
                    }
                }

                GL11.glShadeModel(GL11.GL_FLAT);
            }


            GL11.glPopAttrib();
        }
    }

    @Override
    protected ResourceLocationRaw getEntityTexture(Entity p_110775_1_) {
        return textureLocation;
    }


    private void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks) {
        GlStateManager.pushLightingAttrib();

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        GlStateManager.depthMask(false);

        TextureMap texturemap = Minecraft.getInstance().getTextureMap();
        TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
        TextureAtlasSprite textureatlassprite1 = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");
        int i = 0;
        for(int re = 0; re < 3; re++){
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) x, (float) y, (float) z);
            float f = entity.width * 1.4F;
            GlStateManager.scalef(f, f, f);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder BufferBuilder = tessellator.getBuffer();
            float f1 = 0.5F;
            float f2 = 0.0F;
            float f3 = entity.height / f;
            float f4 = (float) (entity.posY - entity.getBoundingBox().minY);
            GlStateManager.rotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, 0.0f, -0.3F + (float) ((int) f3) * 0.02F - re * 0.2f);

            if(0 < re) {
                float reScale = 1.0f / (re + 0.25f);
                GlStateManager.scalef(reScale, 0.75f, reScale);
            }

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ZERO);

            GlStateManager.color4f(0.1F, 0.0F, 1.0F, 1.0F);

            float f5 = 0.0F;
            BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

            while (f3 > 0.0F) {
                TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                float f6 = textureatlassprite2.getMinU();
                float f7 = textureatlassprite2.getMinV();
                float f8 = textureatlassprite2.getMaxU();
                float f9 = textureatlassprite2.getMaxV();

                if (i / 2 % 2 == 0) {
                    float f10 = f8;
                    f8 = f6;
                    f6 = f10;
                }

                BufferBuilder.pos((double) (f1 - f2), (double) (0.0F - f4), (double) f5).tex((double) f8, (double) f9).endVertex();
                BufferBuilder.pos((double) (-f1 - f2), (double) (0.0F - f4), (double) f5).tex((double) f6, (double) f9).endVertex();
                BufferBuilder.pos((double) (-f1 - f2), (double) (1.4F - f4), (double) f5).tex((double) f6, (double) f7).endVertex();
                BufferBuilder.pos((double) (f1 - f2), (double) (1.4F - f4), (double) f5).tex((double) f8, (double) f7).endVertex();
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
