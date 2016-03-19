package mods.flammpfeil.slashblade.gui;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

/**
 * Created by Furia on 15/02/12.
 */

public class AchievementsExtendedGuiHandler {

    public AchievementsExtendedGuiHandler(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    static public GuiSlashBladeRecipe currentRecipe = null;

    static public boolean visible = false;
    static public boolean doClose = false;
    static public boolean doOpen = false;

    @SubscribeEvent
    public void guiRenderHandler(GuiScreenEvent.DrawScreenEvent.Post event){
        if(!(event.gui instanceof GuiAchievements)){
            visible = false;
            return;
        }

        GuiAchievements guiAchievements = (GuiAchievements)event.gui;

        /*
        int currentPage = (Integer)ReflectionHelper.getPrivateValue(GuiAchievements.class,guiAchievements,"currentPage");
        if(0 < currentPage && AchievementPage.getAchievementPage(currentPage).getName().equals(I18n.translateToLocal("flammpfeil.slashblade")))
        */
        {

            if(currentRecipe == null){
                currentRecipe = new GuiSlashBladeRecipe();
                currentRecipe.setWorldAndResolution(Minecraft.getMinecraft(), event.gui.width, event.gui.height);
            }else{
                if(event.gui.width != currentRecipe.width || event.gui.height != currentRecipe.height){
                    currentRecipe.setWorldAndResolution(Minecraft.getMinecraft(), event.gui.width, event.gui.height);
                }
            }

            if(doClose || Keyboard.isKeyDown(Keyboard.KEY_BACK)){
                visible = false;
                doClose = false;
            }

            if(doOpen && !Mouse.isButtonDown(0)){
                visible = true;
                doOpen = false;
            }

            if(visible==false && AchievementList.currentMouseOver != null){

                if(Mouse.isButtonDown(0)){
                    Object content = AchievementList.currentMouseOver.content;

                    if(content == null)
                        return;

                    if(content instanceof List){
                        currentRecipe.recipe = (List<IRecipe>)content;
                        doOpen = true;
                    }
                }
            }

            if(visible)
                currentRecipe.drawScreen(event.mouseX, event.mouseY, event.renderPartialTicks);


        }
    }
}
