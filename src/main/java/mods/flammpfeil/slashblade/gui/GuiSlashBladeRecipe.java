package mods.flammpfeil.slashblade.gui;

import mods.flammpfeil.slashblade.util.DummyAnvilRecipe;
import mods.flammpfeil.slashblade.util.DummyPotionRecipe;
import mods.flammpfeil.slashblade.util.DummyRecipeBase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.util.DummySmeltingRecipe;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Furia on 15/02/11.
 */
public class GuiSlashBladeRecipe extends GuiScreen {
    protected int xSize = 176;
    protected int ySize = 85;

    int gridLeft = 30;
    int gridTop = 17;

    int slotSize = 16+2;

    int resultLeft = 120+4;
    int resultTop = 31+4;

    protected int guiLeft;
    protected int guiTop;

    public String title;
    public List<IRecipe> recipe;

    public GuiSlashBladeRecipe()
    {
        this.recipe = null;

        this.title = "";
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    DummyRecipeBase.RecipeType recipeType = null;

    protected void drawGuiContainerBackgroundLayer()
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        int shiftTop;
        ResourceLocation texture;

        if(recipeType != null) {
            shiftTop = recipeType.yOffset;
            texture = recipeType.backGround;
        }else{
            shiftTop = 0;
            texture = DummyRecipeBase.BackGroundResource;
        }

        this.mc.getTextureManager().bindTexture(texture);//BackGroundResource);

        this.drawTexturedModalRect(k, l, 0, shiftTop, this.xSize, this.ySize);
    }

    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRendererObj.drawString(this.title,
                this.guiLeft + this.xSize / 2 - this.fontRendererObj.getStringWidth(this.title) / 2,
                this.guiTop + 6,
                0x000000);//4210752);

        this.fontRendererObj.drawString(this.title,
                this.guiLeft + this.xSize / 2 - this.fontRendererObj.getStringWidth(this.title) / 2,
                this.guiTop + 6,
                0x000000);//4210752);



        this.fontRendererObj.drawString("x",
                this.guiLeft + this.xSize - 10,
                this.guiTop + 5,
                0x000000);//4210752);
    }

    ItemStack getWildCardStack(ItemStack stack){
        if(stack.isItemStackDamageable()){
            if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
                stack = stack.copy();

                stack.setItemDamage((int)(System.currentTimeMillis() % 1000 / 1000.0 * stack.getMaxDamage()));
            }
        }else{
            if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE){
                stack = stack.copy();
                stack.setItemDamage(0);
            }
        }

        return stack;
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_)
    {
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();


        exit:{
            if(this.recipe == null) break exit;
            if(0 == this.recipe.size()) break exit;

            int recipeIndex = (int)(System.currentTimeMillis() / 3000) % this.recipe.size();
            IRecipe currentRecipe = this.recipe.get(recipeIndex);

            if(currentRecipe == null) break exit;

            if(currentRecipe instanceof DummyRecipeBase){
                recipeType = ((DummyRecipeBase) currentRecipe).getRecipeType();
            }else{
                recipeType = DummyRecipeBase.RecipeType.Crafting;
            }

            {
                ItemStack output = currentRecipe.getRecipeOutput();
                if(output != null){
                    this.title = output.getItem().getItemStackDisplayName(output);
                }
            }


            drawGuiContainerBackgroundLayer();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawGuiContainerForegroundLayer();
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            super.drawScreen(mouseX, mouseY, p_73863_3_);

            if(currentRecipe instanceof DummyPotionRecipe){

                DummyPotionRecipe dummyRecipe = (DummyPotionRecipe)currentRecipe;

                ItemStack targetItemStack;
                targetItemStack = dummyRecipe.top;
                if(targetItemStack != null){
                    int posX = 1;
                    int posY = 0;

                    targetItemStack = getWildCardStack(targetItemStack);

                    //
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack,
                            this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY, null);

                }
                targetItemStack = dummyRecipe.bottom;
                if(targetItemStack != null){
                    int posX = 1;
                    int posY = 2;

                    targetItemStack = getWildCardStack(targetItemStack);

                    //
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack,
                            this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY, null);

                }

                targetItemStack = dummyRecipe.getRecipeOutput();
                if(targetItemStack != null){
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack, this.guiLeft + resultLeft , this.guiTop + resultTop);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + resultLeft, this.guiTop + resultTop,null);

                    if (this.checkMouseOver(resultLeft, resultTop, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }

                targetItemStack = dummyRecipe.top;
                if(targetItemStack != null){
                    int posX = 1;
                    int posY = 0;
                    targetItemStack = getWildCardStack(targetItemStack);

                    if (this.checkMouseOver(gridLeft + slotSize * posX, gridTop + slotSize * posY, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
                targetItemStack = dummyRecipe.bottom;
                if(targetItemStack != null){
                    int posX = 1;
                    int posY = 2;
                    targetItemStack = getWildCardStack(targetItemStack);

                    if (this.checkMouseOver(gridLeft + slotSize * posX, gridTop + slotSize * posY, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
            }else if(currentRecipe instanceof DummyAnvilRecipe){

                DummyAnvilRecipe dummyRecipe = (DummyAnvilRecipe)currentRecipe;

                ItemStack targetItemStack;
                targetItemStack = dummyRecipe.left;
                if(targetItemStack != null){
                    int posX = 0;
                    int posY = 1;

                    targetItemStack = getWildCardStack(targetItemStack);

                    //
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack,
                            this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY, null);

                }
                targetItemStack = dummyRecipe.right;
                if(targetItemStack != null){
                    int posX = 2;
                    int posY = 1;

                    targetItemStack = getWildCardStack(targetItemStack);

                    //
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack,
                            this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + gridLeft + slotSize * posX, this.guiTop + gridTop + slotSize * posY, null);

                }

                targetItemStack = dummyRecipe.getRecipeOutput();
                if(targetItemStack != null){
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack, this.guiLeft + resultLeft , this.guiTop + resultTop);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + resultLeft, this.guiTop + resultTop,null);

                    if (this.checkMouseOver(resultLeft, resultTop, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }

                targetItemStack = dummyRecipe.left;
                if(targetItemStack != null){
                    int posX = 0;
                    int posY = 1;
                    targetItemStack = getWildCardStack(targetItemStack);

                    if (this.checkMouseOver(gridLeft + slotSize * posX, gridTop + slotSize * posY, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
                targetItemStack = dummyRecipe.right;
                if(targetItemStack != null){
                    int posX = 2;
                    int posY = 1;
                    targetItemStack = getWildCardStack(targetItemStack);

                    if (this.checkMouseOver(gridLeft + slotSize * posX, gridTop + slotSize * posY, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
            }else if(currentRecipe instanceof DummySmeltingRecipe){
                DummySmeltingRecipe dummySmeltingRecipe = (DummySmeltingRecipe)currentRecipe;

                ItemStack targetItemStack = dummySmeltingRecipe.input;
                if(targetItemStack != null){

                    targetItemStack = getWildCardStack(targetItemStack);

                    //
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack,
                            this.guiLeft + gridLeft + slotSize * 1, this.guiTop + gridTop + slotSize * 1);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + gridLeft + slotSize * 1, this.guiTop + gridTop + slotSize * 1, null);

                }

                targetItemStack = dummySmeltingRecipe.getRecipeOutput();
                if(targetItemStack != null){
                    itemRender.renderItemAndEffectIntoGUI(targetItemStack, this.guiLeft + resultLeft , this.guiTop + resultTop);
                    itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + resultLeft, this.guiTop + resultTop,null);

                    if (this.checkMouseOver(resultLeft, resultTop, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }

                targetItemStack = dummySmeltingRecipe.input;
                if(targetItemStack != null){
                    targetItemStack = getWildCardStack(targetItemStack);
                    
                    if (this.checkMouseOver(gridLeft + slotSize * 1, gridTop + slotSize * 1, 16, 16, mouseX, mouseY))
                    {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.renderToolTip(targetItemStack, mouseX, mouseY);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
            }else if(currentRecipe instanceof ShapedOreRecipe){

                ShapedOreRecipe shapedOreRecipe = (ShapedOreRecipe)currentRecipe;

                int gridWidth = (Integer)ReflectionHelper.getPrivateValue(ShapedOreRecipe.class,shapedOreRecipe,"width");
                int gridHeight = (Integer) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class,shapedOreRecipe,"height");

                Object[] input = shapedOreRecipe.getInput();

                GL11.glPushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                GL11.glEnable(GL11.GL_LIGHTING);
                itemRender.zLevel = 100.0F;

                for(int gy=0; gy < gridHeight; gy++){
                    for(int gx=0; gx < gridWidth; gx++){
                        int idx = gy*gridHeight + gx;
                        Object target = input[gy*gridHeight + gx];

                        ItemStack targetItemStack = null;

                        boolean isDict = false;

                        if (target instanceof ItemStack){
                            targetItemStack = (ItemStack)target;
                        }
                        else if (target instanceof List){

                            List<ItemStack> list = (List<ItemStack>)target;
                            if(list.size() != 0){
                                long index = (System.currentTimeMillis() / 1000) % list.size();
                                targetItemStack = list.get((int)index);
                            }
                            isDict = true;
                        }


                        if(targetItemStack != null){
                            targetItemStack = getWildCardStack(targetItemStack);

                            itemRender.renderItemAndEffectIntoGUI(targetItemStack, this.guiLeft + gridLeft + slotSize * gx, this.guiTop + gridTop + slotSize * gy);
                            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + gridLeft + slotSize * gx, this.guiTop + gridTop + slotSize * gy, null);

                        }

                        if(isDict){
                            GL11.glDisable(GL11.GL_DEPTH_TEST);
                            this.fontRendererObj.drawString("★",
                                    this.guiLeft + gridLeft + slotSize * gx + 1,
                                    this.guiTop + gridTop + slotSize * gy - 1,
                                    0xFFD400);
                            GL11.glEnable(GL11.GL_DEPTH_TEST);
                        }

                    }
                }

                {
                    ItemStack targetItemStack = shapedOreRecipe.getRecipeOutput();

                    if(targetItemStack != null){
                        itemRender.renderItemAndEffectIntoGUI(targetItemStack, this.guiLeft + resultLeft , this.guiTop + resultTop);
                        itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, targetItemStack, this.guiLeft + resultLeft, this.guiTop + resultTop, null);


                        if (this.checkMouseOver(resultLeft, resultTop, 16, 16, mouseX, mouseY))
                        {
                            GL11.glDisable(GL11.GL_LIGHTING);
                            this.renderToolTip(targetItemStack, mouseX, mouseY);
                            GL11.glEnable(GL11.GL_LIGHTING);
                        }
                    }
                }


                for(int gy=0; gy < gridHeight; gy++){
                    for(int gx=0; gx < gridWidth; gx++){
                        int idx = gy*gridHeight + gx;
                        Object target = input[gy*gridHeight + gx];

                        ItemStack targetItemStack = null;
                        if (target instanceof ItemStack){
                            targetItemStack = (ItemStack)target;
                        }
                        else if (target instanceof List){
                            List<ItemStack> list = (List<ItemStack>)target;
                            if(list.size() != 0){
                                long index = (System.currentTimeMillis() / 1000) % list.size();
                                targetItemStack = list.get((int)index);
                            }
                        }

                        if(targetItemStack != null){
                        /*
                        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), targetItemStack, this.guiLeft + 16 * gx, this.guiTop + 16 * gy);
                        itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), targetItemStack, this.guiLeft + 16 * gx, this.guiTop + 16 * gy)
                        */
                            if (this.checkMouseOver(gridLeft + slotSize * gx, gridTop + slotSize * gy, 16, 16, mouseX, mouseY))
                            {
                                targetItemStack = getWildCardStack(targetItemStack);

                                GL11.glDisable(GL11.GL_LIGHTING);
                                this.renderToolTip(targetItemStack, mouseX, mouseY);
                                GL11.glEnable(GL11.GL_LIGHTING);
                            }
                        }
                    }
                }

                itemRender.zLevel = 0.0F;

                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                RenderHelper.enableStandardItemLighting();
            }




        }

        if (!this.checkMouseOver(0, 0, xSize, ySize, mouseX, mouseY)
                || this.checkMouseOver(xSize - 10, 5, 5, 5, mouseX, mouseY))
        {
            if(Mouse.isButtonDown(0) || Keyboard.isKeyDown(Keyboard.KEY_BACK))
                AchievementsExtendedGuiHandler.doClose = true;
        }

        GL11.glPopMatrix();
    }

    boolean checkMouseOver(int left, int top, int width, int height, int mouseX, int mouseY)
    {
        int k1 = this.guiLeft;
        int l1 = this.guiTop;
        mouseX -= k1;
        mouseY -= l1;
        return mouseX >= left - 1 && mouseX < left + width + 1 && mouseY >= top - 1 && mouseY < top + height + 1;
    }
}
