package mods.flammpfeil.slashblade.client.renderer.entity.layers;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.ProjectileBarrier;
import mods.flammpfeil.slashblade.client.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.util.EnumSet;

/**
 * Created by Furia on 2016/02/06.
 */
public class LayerSlashBlade implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocationRaw armoredCreeperTextures = new ResourceLocationRaw("textures/entity/creeper/creeper_armor.png");
    private static final ResourceLocationRaw RES_ITEM_GLINT = new ResourceLocationRaw("textures/misc/enchanted_item_glint.png");


    static public WavefrontObject trailModel = null;

    static public ResourceLocationRaw modelLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/util/trail.obj");
    static public ResourceLocationRaw textureLocation = new ResourceLocationRaw("flammpfeil.slashblade","model/util/trail.png");

    private final RenderLivingBase<?> render;

    public LayerSlashBlade(RenderLivingBase<?> livingEntityRendererIn)
    {
        this.render = livingEntityRendererIn;
    }

    float lerp(float start, float end, float percent){
        return (start + percent*(end - start));
    }

    float lerpDegrees(float start, float end, float percent){
        float diff = end - start;

        while (diff < -180.0F)
            diff += 360.0F;

        while (diff >= 180.0F)
            diff -= 360.0F;

        return start + percent * diff;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ticksExisted, float yawDiff, float rotationPitch, float scale) {
        if(trailModel == null){
            trailModel = new WavefrontObject(modelLocation);
        }

        GlStateManager.pushMatrix();

        GL11.glScalef(1.0F, 1.0F, 1.0F);

        float f5 = 0.0625F;
        //GL11.glTranslatef(0.0F, -24.0F * f5 - 0.0078125F, 0.0F);

        render(entitylivingbaseIn, partialTicks);

        GlStateManager.popMatrix();
    }

    RingState ringStates = new RingState(){
        @Override
        public void transform() {
            super.transform();

            GlStateManager.rotate(90, 0, 0, 1);
            GlStateManager.translate(5, 50, 0);

            float scale = 0.25f;
            GlStateManager.scale(scale,scale,scale);

            long ticks = Minecraft.getMinecraft().world == null ? 0 : Minecraft.getMinecraft().world.getWorldTime();
            float partialTicks = ReflectionAccessHelper.getPartialTicks();

            double rotate = (ticks % 1500 + partialTicks ) / 1500.0d * 180.0d;

            GL11.glRotated(rotate, 0, 1, 0);

        }
    };

    public static class RingState{

        static public WavefrontObject ringModel = null;
        public static final ResourceLocationRaw ringModelLoc = new ResourceLocationRaw("flammpfeil.slashblade","model/util/ring.obj");
        public static final ResourceLocationRaw ringTexLoc = new ResourceLocationRaw("flammpfeil.slashblade","model/util/ring.png");

        public void transform(){

        }

        public void renderRing(){
            if(!SlashBlade.RenderNFCSEffect) return;

            if(ringModel == null){
                ringModel = new WavefrontObject(ringModelLoc);
            }

            GlStateManager.pushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            transform();

            Minecraft.getMinecraft().getTextureManager().bindTexture(ringTexLoc);

            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.01f);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            float lastx = OpenGlHelper.lastBrightnessX;
            float lasty = OpenGlHelper.lastBrightnessY;
            //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            ringModel.renderAll();

            //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

            GL11.glEnable(GL11.GL_LIGHTING);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);


            GL11.glPopAttrib();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

    void renderBack(ItemStack item, EntityLivingBase player){
        renderBack(item, player, false, false);
    }

    void renderBack(ItemStack item, EntityLivingBase player, boolean forceNinja, boolean noBlade) {
        ItemSlashBlade iSlashBlade = ((ItemSlashBlade) item.getItem());


        WavefrontObject model = BladeModelManager.getInstance().getModel(ItemSlashBlade.getModelLocation(item));

        ResourceLocationRaw resourceTexture = ItemSlashBlade.getModelTexture(item);

        EnumSet<ItemSlashBlade.SwordType> swordType = iSlashBlade.getSwordType(item);


        boolean isNoScabbard = swordType.contains(ItemSlashBlade.SwordType.NoScabbard);

        float ax = 0;
        float ay = 0;
        float az = 0;

        boolean isBroken = swordType.contains(ItemSlashBlade.SwordType.Broken);


        int renderType = 0;

        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            ay = -tag.getFloat(ItemSlashBlade.adjustYStr) / 10.0f;

            renderType = ItemSlashBlade.StandbyRenderType.get(tag);

            if (isNoScabbard)
                renderType = 0;
        }

        if(forceNinja)
            renderType = 3;

        if (renderType == 0) {
            return;
        }

        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();


            ax = tag.getFloat(ItemSlashBlade.adjustXStr) / 10.0f;
            ay = -tag.getFloat(ItemSlashBlade.adjustYStr) / 10.0f;
            az = -tag.getFloat(ItemSlashBlade.adjustZStr) / 10.0f;
        }

        if (renderType != 1) {
            ax = 0;
            az = 0;
        }

        String renderTarget;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        {

            if (render.getMainModel().isChild)
            {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }


            ((ModelBiped)render.getMainModel()).bipedBody.postRender(0.0625F);
            //GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);

            if (player.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.203125F, 0.0F);
            }
        }

        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);

            //�̊i�␳ config���
            GL11.glTranslatef(ax, ay, az);

            switch (renderType) {
                case 2: //pso2
                    //���ʒu��
                    GL11.glTranslatef(0, 0.5f, 0.25f);


                {
                    //�S�̃X�P�[���␳
                    float scale = (float) (0.075f);
                    GL11.glScalef(scale, scale, scale);
                }
                GL11.glRotatef(83.0f, 0, 0, 1);

                GL11.glTranslatef(0, -12.5f, 0);

                break;

                case 3: //ninja
                    //���ʒu��
                    GL11.glTranslatef(0, 0.4f, 0.25f);


                {
                    //�S�̃X�P�[���␳
                    float scale = (float) (0.075f);
                    GL11.glScalef(scale, scale, scale);
                }
    /*
                        //�������
                        GL11.glRotatef(90.0f, 1, 0, 0);
                        //�������
    */
                GL11.glRotatef(-30.0f, 0, 0, 1);

                GL11.glRotatef(-180.0f, 0, 1.0f, 0);

                GL11.glTranslatef(0, -12.5f, 0);

                break;


                default:
                    //���ʒu��
                    GL11.glTranslatef(0.25f, 0.4f, -0.5f);


                {
                    //�S�̃X�P�[���␳
                    float scale = (float) (0.075f);
                    GL11.glScalef(scale, scale, scale);
                }

                //�������
                GL11.glRotatef(60.0f, 1, 0, 0);

                //����O��
                GL11.glRotatef(-20.0f, 0, 0, 1);

                //�n�����Ɍ�����i��������
                GL11.glRotatef(90.0f, 0, 1.0f, 0);
                break;
            }


            //-----------------------------------------------------------------------------------------------------------------------
            GL11.glPushMatrix();
            if(!noBlade){
                if (isBroken)
                    renderTarget = "blade_damaged";
                else
                    renderTarget = "blade";


                float scaleLocal = 0.095f;
                GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                GL11.glRotatef(-90.0f, 0, 0, 1);
                this.render.bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

                model.renderPart(renderTarget);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                float lastx = OpenGlHelper.lastBrightnessX;
                float lasty = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                model.renderPart(renderTarget + "_luminous");

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                GL11.glEnable(GL11.GL_LIGHTING);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                ringStates.renderRing();

                if (item.hasEffect() && SlashBlade.RenderEnchantEffect) {

                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    this.render.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f7 = 0.76F;

                    Face.setColor(0xFF8040CC);

                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f8 = 0.125F;
                    GL11.glScalef(f8, f8, f8);
                    float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f9, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f8, f8, f8);
                    f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f9, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);

                    Face.resetColor();

                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }

            }
            GL11.glPopMatrix();


            if (!isNoScabbard) {

                GL11.glPushMatrix();

                float scaleLocal = 0.095f;
                GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                GL11.glRotatef(-90.0f, 0, 0, 1);
                this.render.bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

                renderTarget = "sheath";
                model.renderPart(renderTarget);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                float lastx = OpenGlHelper.lastBrightnessX;
                float lasty = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                model.renderPart(renderTarget + "_luminous");

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                GL11.glEnable(GL11.GL_LIGHTING);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                if (item.hasEffect() && SlashBlade.RenderEnchantEffect) {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    this.render.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f7 = 0.76F;

                    Face.setColor(0xFF8040CC);

                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f8 = 0.125F;
                    GL11.glScalef(f8, f8, f8);
                    float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f9, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f8, f8, f8);
                    f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f9, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    model.renderPart(renderTarget);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);

                    Face.resetColor();

                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }

                GL11.glPopMatrix();
            }
            GL11.glShadeModel(GL11.GL_FLAT);
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    void render(EntityLivingBase entity,float partialTicks)
    {
        boolean adjust = true;

        if(entity == null)
            return;




        ItemStack stack = entity.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offhand = entity.getHeldItem(EnumHand.OFF_HAND);

        if(stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)){
            if(entity instanceof EntityPlayer){
                ItemStack firstItem = ((EntityPlayer)entity).inventory.getStackInSlot(0);
                if(adjust && !firstItem.isEmpty() && (firstItem.getItem() instanceof ItemSlashBlade)){
                    renderBack(firstItem,(EntityPlayer)entity);
                }
            }
            return;
        }

        if(stack.isEmpty()) return;
        if(!(stack.getItem() instanceof ItemSlashBlade)) return;

        ItemSlashBlade itemBlade = ((ItemSlashBlade)stack.getItem());

        WavefrontObject model = BladeModelManager.getInstance().getModel(ItemSlashBlade.getModelLocation(stack));
        ResourceLocationRaw resourceTexture = ItemSlashBlade.getModelTexture(stack);

        EnumSet<ItemSlashBlade.SwordType> swordType = itemBlade.getSwordType(stack);

        if(swordType.contains(ItemSlashBlade.SwordType.NoScabbard)){
            //todo :LayerHeldItem�I��rendering����
            doHeldItemRenderLayer(entity,swordType,model);
            return;
        }

        //if(Loader.isModLoaded("SmartMoving"))
        {
            ((ModelBiped)render.getMainModel()).bipedBody.postRender(0.0625F);
            //GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);
        }

        boolean isEnchanted = swordType.contains(ItemSlashBlade.SwordType.Enchanted);
        boolean isBewitched = swordType.contains(ItemSlashBlade.SwordType.Bewitched);


        int charge;
        if(entity instanceof EntityPlayer && !entity.getActiveItemStack().isEmpty() )
            charge = entity.getItemInUseMaxCount();
        else
            charge = 0;

        boolean doProjectileBarrier = ProjectileBarrier.isAvailable(entity, stack, entity.getItemInUseCount());

        float ax = 0;
        float ay = 0;
        float az = 0;

        boolean isBroken = swordType.contains(ItemSlashBlade.SwordType.Broken);
        ItemSlashBlade.ComboSequence combo = ItemSlashBlade.ComboSequence.None;
        int color = 0x3333FF;
        if(stack.hasTagCompound()){
            NBTTagCompound tag = stack.getTagCompound();

            combo = itemBlade.getComboSequence(tag);

            if(adjust){
                ax = tag.getFloat(ItemSlashBlade.adjustXStr)/10.0f;
                ay = -tag.getFloat(ItemSlashBlade.adjustYStr)/10.0f;
                az = -tag.getFloat(ItemSlashBlade.adjustZStr)/10.0f;
            }

            if (ItemSlashBlade.SummonedSwordColor.exists(tag)) {
                color = ItemSlashBlade.SummonedSwordColor.get(tag);
                if(color < 0)
                    color = -color;
            }
        }

        WavefrontObject offhandModel = null;
        ResourceLocationRaw offHandResourceTexture = null;
        boolean offhandIsBroken = false;
        int offhandColor = 0x3333FF;
        if(!offhand.isEmpty() && (offhand.getItem() instanceof ItemSlashBlade)){

            renderBack(offhand,entity,true, combo.mainHandCombo != null);

            ItemSlashBlade offhandItemBlade = ((ItemSlashBlade) offhand.getItem());
            offhandModel = BladeModelManager.getInstance().getModel(ItemSlashBlade.getModelLocation(offhand));
            offHandResourceTexture = ItemSlashBlade.getModelTexture(offhand);

            EnumSet<ItemSlashBlade.SwordType> offHandSwordType = offhandItemBlade.getSwordType(offhand);
            offhandIsBroken = offHandSwordType.contains(ItemSlashBlade.SwordType.Broken);

            if (offhand.hasTagCompound()) {
                NBTTagCompound tag = offhand.getTagCompound();
                if (ItemSlashBlade.SummonedSwordColor.exists(tag)) {
                    offhandColor = ItemSlashBlade.SummonedSwordColor.get(tag);
                    if (offhandColor < 0)
                        offhandColor = -offhandColor;
                }
            }
            this.render.bindTexture(textureLocation);
        }





        float progress = entity.getSwingProgress(partialTicks);

        if((!combo.equals(ItemSlashBlade.ComboSequence.None)) && entity.swingProgress == 0.0f)
            progress = 1.0f;

        progress *= 1.2;
        if(1.0f < progress)
            progress = 1.0f;

        //progress = (entity.ticksExisted % 10) / 10.0f;

        switch(combo){
            case SIai:
            case Iai:
                progress = 1.0f - (Math.abs(progress-0.5f) * 2.0f);

                break;

            case HiraTuki:
            case Stinger:
                progress = 1.0f;

                break;

            default :
                progress = 1.0f - progress;
                progress = 1.0f - (float)Math.pow(progress,2.0);

                break;
        }

        /*
		if(!isBroken && isEnchanted && ItemSlashBlade.RequiredChargeTick < charge){
			progress = 0.0f;
			combo = ComboSequence.None;
		}
        */




        String renderTarget;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);

            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            GL11.glColor3f(1.0F, 1.0F, 1.0F);

            //�̊i�␳ config���
            GL11.glTranslatef(ax,ay,az);

            //���ʒu��
            GL11.glTranslatef(0.25f,0.4f,-0.5f);


            {
                //�S�̃X�P�[���␳
                float scale = (float)(0.075f);
                GL11.glScalef(scale, scale, scale);
            }

            //�������
            GL11.glRotatef(60.0f, 1, 0, 0);

            //����O��
            GL11.glRotatef(-20.0f, 0, 0, 1);

            //�n�����Ɍ�����i��������
            GL11.glRotatef(90.0f, 0, 1.0f, 0);


            float xoffset = 10.0f;
            float yoffset = 8.0f;

            //-----------------------------------------------------------------------------------------------------------------------
            GL11.glPushMatrix();


            boolean doOffhandRender = false;
            int handsLoop = 1;
            if(combo.mainHandCombo != null){
                handsLoop = 2;
                if(!offhand.isEmpty() && offhand.getItem() instanceof ItemSlashBlade)
                    doOffhandRender = true;
            }

            float handsLoopProgressTmp = progress;
            ItemSlashBlade.ComboSequence comboSequenceTmp = combo;

            boolean tmpIsBroken = isBroken;
            ResourceLocationRaw tmpResourceTexture = resourceTexture;
            WavefrontObject tmpModel = model;
            int tmpColor = color;
            ItemStack tmpStack = stack;

            for(int handsLoopIdx = 0; handsLoopIdx < handsLoop; handsLoopIdx++) {
                GL11.glPushMatrix();


                if (doOffhandRender && handsLoopIdx == 0) {
                    isBroken = offhandIsBroken;
                    resourceTexture = offHandResourceTexture != null ? offHandResourceTexture : tmpResourceTexture;
                    model = offhandModel != null ? offhandModel : tmpModel;
                    color = offhandColor;
                    stack = offhand;

                }else if (handsLoopIdx == 1) {
                    combo = comboSequenceTmp.mainHandCombo;
                    progress = 1.0f;

                    isBroken = tmpIsBroken;
                    resourceTexture = tmpResourceTexture;
                    model = tmpModel;
                    color = tmpColor;
                    stack = tmpStack;
                }

                float progressTmp = progress;
                for (int blurLoop = 0; blurLoop < 3; blurLoop++) {
                    GL11.glPushMatrix();
                    if ((progressTmp == 1.0f || combo.useScabbard) && blurLoop != 0) {
                        GL11.glPopMatrix();
                        break;
                    }

                    if (0 < blurLoop) {
                        progress *= 0.8f;
                    }

                    if (!combo.equals(ItemSlashBlade.ComboSequence.None)) {
                        float tmp = progress;

                        if (combo.swingAmplitude < 0) {
                            progress = 1.0f - progress;
                        }
                        //GL11.glRotatef(-90, 0.0f, 1.0f, 0.0f);

                        if (combo.equals(ItemSlashBlade.ComboSequence.Stinger)
                                || combo.equals(ItemSlashBlade.ComboSequence.HiraTuki)) {
                            GL11.glTranslatef(0.0f, 0.0f, -26.0f);
                        }

                        if (combo.equals(ItemSlashBlade.ComboSequence.Kiriorosi)) {


                            GL11.glRotatef(20.0f, -1.0f, 0, 0);
                            GL11.glRotatef(-30.0f, 0, 0, -1.0f);


                            GL11.glTranslatef(0.0f, 0.0f, -8.0f);
                            //GL11.glRotatef(-30.0f,1,0,0);


                            GL11.glRotatef((90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);

                            GL11.glRotatef((1.0f - progress) * -90.0f, 0.0f, 0.0f, -1.0f);
                            GL11.glTranslatef(0.0f, (1.0f - progress) * -5.0f, 0.0f);
                            GL11.glTranslatef((1.0f - progress) * 10.0f, 0.0f, 0.0f);

                            GL11.glTranslatef(-xoffset, 0.0f, 0.0f);
                            GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                            progress = 1.0f;

                            if (0 < combo.swingAmplitude) {
                                GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                            } else {
                                GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                            }

                            GL11.glTranslatef(0.0f, yoffset, 0.0f);
                            GL11.glTranslatef(xoffset, 0.0f, 0.0f);
                            GL11.glRotatef(180.0f, 0, 1, 0);
                        } else if (combo.swingDirection < 0) {


                            GL11.glRotatef(20.0f, -1.0f, 0, 0);
                            GL11.glRotatef(-30.0f, 0, 0, -1.0f);


                            GL11.glTranslatef(0.0f, 0.0f, -12.0f);
                            //GL11.glRotatef(-30.0f,1,0,0);


                            GL11.glRotatef((90 + combo.swingDirection), 0.0f, -1.0f, 0.0f);


                            GL11.glRotatef((1.0f - progress) * -(180.0f + 60.0f), 0.0f, 0.0f, -1.0f);
                        /*
                        GL11.glTranslatef(0.0f, (1.0f-progress) * -5.0f, 0.0f);
                        GL11.glTranslatef((1.0f-progress) * 10.0f, 0.0f, 0.0f);
                        */

                            GL11.glTranslatef(-xoffset, 0.0f, 0.0f);
                            GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                            float rotate = progress * Math.abs(combo.swingAmplitude);
                            GL11.glRotatef(rotate, 0.0f, 0.0f, -1.0f);

                            GL11.glTranslatef(0.0f, yoffset, 0.0f);
                            GL11.glTranslatef(xoffset, 0.0f, 0.0f);
                            //GL11.glRotatef(180.0f, 0, 1, 0);
                        } else {

                            GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
                            GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


                            GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


                            GL11.glTranslatef(-xoffset, 0.0f, 0.0f);


                            GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                            if (0 < combo.swingAmplitude) {
                                GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                            } else {
                                GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                            }

                            GL11.glTranslatef(0.0f, yoffset, 0.0f);
                            GL11.glTranslatef(xoffset, 0.0f, 0.0f);
                        }


                        progress = tmp;
                    } else {
                        if (doProjectileBarrier) {

                            GL11.glRotatef(90.0f, 0, -1, 0);
                            GL11.glRotatef(-20.0f, 0, 0, -1);
                            GL11.glRotatef(60.0f, -1, 0, 0);

                            if (entity.isSneaking())
                                GL11.glRotatef(30.0f, -1, 0, 0);

                            GL11.glTranslatef(-7.0f, 0.0f, -4.0f);

                            final int span = 7;
                            float rotParTicks = 360.0f / (float) span;
                            rotParTicks *= (entity.ticksExisted % span) + partialTicks;
                            GL11.glRotatef(rotParTicks, 0, 0, -1);

                            GL11.glTranslatef(0.0f, -3.0f, 0.0f);

                            progress = 0.5f;
                        }
                    }


                    if (isBroken)
                        renderTarget = "blade_damaged";
                    else
                        renderTarget = "blade";


                    float scaleLocal = 0.095f;
                    GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                    GL11.glRotatef(-90.0f, 0, 0, 1);
                    this.render.bindTexture(resourceTexture);

                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                    GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.005f);

                    if (0 < blurLoop) {
                        Face.setColor(new Color(1.0f, 1.0f, 1.0f, (float) Math.pow(0.5, blurLoop)).getRGB());
                    }

                    model.renderPart(renderTarget);

                    if (!combo.useScabbard) {
                        model.renderPart(renderTarget + "_unsheathe");
                    }

                    if (0 < blurLoop) {
                        Face.resetColor();
                    }

                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                    float lastx = OpenGlHelper.lastBrightnessX;
                    float lasty = OpenGlHelper.lastBrightnessY;
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                    model.renderPart(renderTarget + "_luminous");
                    if (!combo.useScabbard) {
                        model.renderPart(renderTarget + "_unsheathe_luminous");
                    }

        /**/
                    if (!combo.useScabbard
                            && (combo != ItemSlashBlade.ComboSequence.Noutou)
                            && (combo != ItemSlashBlade.ComboSequence.HiraTuki)
                            && (combo != ItemSlashBlade.ComboSequence.Stinger)
                            || doProjectileBarrier) {
                        GlStateManager.pushMatrix();
                        GlStateManager.depthMask(false);
                        this.render.bindTexture(textureLocation);
                        double alpha = Math.sin(progress * Math.PI);
                        if (doProjectileBarrier)
                            GlStateManager.scale(1, 0.8, 1);
                        else if (isBroken)
                            GlStateManager.scale(0.4, 0.5, 1);
                        else
                            GlStateManager.scale(1, alpha * 2.0, 1);

                        GlStateManager.rotate((float) (10.0 * (1.0 - alpha)), 0, 0, 1);

                        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);

                        GlStateManager.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);

                        float transparent = 1.0f;
                        if (0 < blurLoop)
                            transparent = (float) Math.pow(0.5, blurLoop);

                        Face.setColor((0xFFFFFF - color) | (0xFF000000 & ((int) (0x44 * alpha * transparent) << 24)));
                        trailModel.renderAll();

                        GlStateManager.glBlendEquation(GL14.GL_FUNC_ADD);

                        Face.setColor((color) | (0xFF000000 & ((int) (0x66 * alpha * transparent) << 24)));
                        trailModel.renderAll();

                        Face.resetColor();

                        GlStateManager.depthMask(true);

                        this.render.bindTexture(resourceTexture);
                        GlStateManager.popMatrix();
                    }
        /**/

                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                    GL11.glEnable(GL11.GL_LIGHTING);
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

                    ringStates.renderRing();

                    if (stack.hasEffect() && SlashBlade.RenderEnchantEffect) {
                        GL11.glDepthFunc(GL11.GL_EQUAL);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.render.bindTexture(RES_ITEM_GLINT);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                        float f7 = 0.76F;

                        Face.setColor(0xFF8040CC);

                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glPushMatrix();
                        float f8 = 0.125F;
                        GL11.glScalef(f8, f8, f8);
                        float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                        GL11.glTranslatef(f9, 0.0F, 0.0F);
                        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                        model.renderPart(renderTarget);
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        GL11.glScalef(f8, f8, f8);
                        f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                        GL11.glTranslatef(-f9, 0.0F, 0.0F);
                        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                        model.renderPart(renderTarget);
                        GL11.glPopMatrix();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);

                        Face.resetColor();

                        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDepthFunc(GL11.GL_LEQUAL);
                    }

                    GL11.glPopMatrix();
                }
                progress = progressTmp;
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
            //restore
            {
                progress = handsLoopProgressTmp;
                combo = comboSequenceTmp;
                isBroken = tmpIsBroken;
                resourceTexture = tmpResourceTexture;
                model = tmpModel;
                color = tmpColor;
                stack = tmpStack;
            }
            //-----------------------------------------------------------------------------------------------------------------------

            GL11.glPushMatrix();{


            if((!combo.equals(ItemSlashBlade.ComboSequence.None)) && combo.useScabbard){


                if(combo.swingAmplitude < 0){
                    progress = 1.0f - progress;
                }

                GL11.glRotatef(progress * 20.0f, -1.0f, 0, 0);
                GL11.glRotatef(progress * -30.0f, 0, 0, -1.0f);


                GL11.glRotatef(progress * (90 - combo.swingDirection), 0.0f, -1.0f, 0.0f);


                GL11.glTranslatef(-xoffset , 0.0f, 0.0f );


                GL11.glTranslatef(0.0f, -yoffset, 0.0f);

                if(0 < combo.swingAmplitude){
                    GL11.glRotatef(progress * (combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                }else{
                    GL11.glRotatef(progress * (-combo.swingAmplitude), 0.0f, 0.0f, -1.0f);
                }

                GL11.glTranslatef(0.0f, yoffset, 0.0f);
                GL11.glTranslatef(xoffset , 0.0f, 0.0f );

            }


            GL11.glPushMatrix();

            float scaleLocal = 0.095f;
            GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
            GL11.glRotatef(-90.0f, 0, 0, 1);
            this.render.bindTexture(resourceTexture);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

            renderTarget = "sheath";
            model.renderPart(renderTarget);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            float lastx = OpenGlHelper.lastBrightnessX;
            float lasty = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            model.renderPart(renderTarget + "_luminous");

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

            GL11.glEnable(GL11.GL_LIGHTING);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            if (stack.hasEffect() && SlashBlade.RenderEnchantEffect)
            {

                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                render.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f7 = 0.76F;

                Face.setColor(0xFF8040CC);

                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                model.renderPart(renderTarget);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);

                Face.resetColor();

                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }

            GL11.glPopMatrix();

            if(!isBroken && isEnchanted && (ItemSlashBlade.RequiredChargeTick < charge || combo.isCharged)){
                GL11.glPushMatrix();

                GL11.glPushMatrix();

                GL11.glEnable(GL11.GL_BLEND);
                float f4 = 3.0F;
                GL11.glColor4f(f4, f4, f4, 3.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);


                GL11.glPushMatrix();
                GL11.glScalef(scaleLocal, scaleLocal, scaleLocal);
                GL11.glRotatef(-90.0f, 0, 0, 1);
                model.renderPart("sheath");

                GL11.glPopMatrix();

                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();

                float ff1 = (float)entity.ticksExisted + partialTicks;
                render.bindTexture(armoredCreeperTextures);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                float f2 = ff1 * 0.03F;
                float f3 = ff1 * 0.02F;
                GL11.glTranslatef(f2, -f3, 0.0F);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glEnable(GL11.GL_BLEND);
                f4 = 1.0F;
                GL11.glColor4f(f4, f4, f4, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

                //GL11.glTranslatef(-1f, 0.0f, -0.5f);

                GL11.glPushMatrix();
                GL11.glScalef(scaleLocal,scaleLocal,scaleLocal);
                GL11.glRotatef(-90.0f,0,0,1);
                model.renderPart("effect");

                GL11.glPopMatrix();

                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glColor4f(1, 1, 1, 1.0F);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);


                GL11.glPopMatrix();
            }
        }GL11.glPopMatrix();

            //-----------------------------------------------------------------------------------------------------------------------
            GL11.glShadeModel(GL11.GL_FLAT);
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public void doHeldItemRenderLayer(EntityLivingBase entitylivingbaseIn, EnumSet<ItemSlashBlade.SwordType> types, WavefrontObject model)
    {
        if(!(render.getMainModel() instanceof ModelBiped))
            return;

        ItemStack itemstack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);

        if (!itemstack.isEmpty())
        {
            GlStateManager.pushMatrix();

            if (render.getMainModel().isChild)
            {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }


            ((ModelBiped)render.getMainModel()).postRenderArm(0.0625F, entitylivingbaseIn.getPrimaryHand());
            GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);


            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            if (entitylivingbaseIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.203125F, 0.0F);
            }

            {
                ResourceLocationRaw resourceTexture = ItemSlashBlade.getModelTexture(itemstack);
                this.render.bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

                GL11.glTranslatef(0.0f, 0.15f, 0.0f);
                float scale = 0.008f;
                GL11.glScalef(scale,scale,scale);
                GL11.glRotatef(-90, 0, 1, 0);

                String renderTargets[];
                if(/*data[1] instanceof EntityPlayer
                    || */types.contains(ItemSlashBlade.SwordType.NoScabbard)){

                    if(types.contains(ItemSlashBlade.SwordType.Broken)){
                        renderTargets = new String[]{"blade_damaged"};
                    }else{
                        renderTargets = new String[]{"blade"};
                    }
                }else{
                    renderTargets = new String[]{"sheath", "blade"};
                }

                model.renderOnly(renderTargets);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);

                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

                float lastx = OpenGlHelper.lastBrightnessX;
                float lasty = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

                for(String renderTarget : renderTargets)
                    model.renderPart(renderTarget + "_luminous");

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);

                GL11.glEnable(GL11.GL_LIGHTING);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            }

            GlStateManager.popMatrix();
        }
    }
}
