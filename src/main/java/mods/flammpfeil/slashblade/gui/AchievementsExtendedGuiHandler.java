package mods.flammpfeil.slashblade.gui;

import com.google.common.collect.Sets;
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
import java.util.Set;

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

    private Set<String> targetGui = Sets.newHashSet("GuiBetterAchievements");

    @SubscribeEvent
    public void guiRenderHandler(GuiScreenEvent.DrawScreenEvent.Post event){
        if(!(event.getGui() instanceof GuiAchievements) && !targetGui.contains(event.getGui().getClass().getSimpleName()) ){
            visible = false;
            AchievementList.currentMouseOver = null;
            return;
        }

        /*
        int currentPage = (Integer)ReflectionHelper.getPrivateValue(GuiAchievements.class,guiAchievements,"currentPage");
        if(0 < currentPage && AchievementPage.getAchievementPage(currentPage).getName().equals(I18n.translateToLocal("flammpfeil.slashblade")))
        */
        {

            if(currentRecipe == null){
                currentRecipe = new GuiSlashBladeRecipe();
                currentRecipe.setWorldAndResolution(Minecraft.getMinecraft(), event.getGui().width, event.getGui().height);
            }else{
                if(event.getGui().width != currentRecipe.width || event.getGui().height != currentRecipe.height){
                    currentRecipe.setWorldAndResolution(Minecraft.getMinecraft(), event.getGui().width, event.getGui().height);
                }
            }

            if(doClose && !Mouse.isButtonDown(0)){
                visible = false;
                doClose = false;
                AchievementList.currentMouseOver = null;
            }

            if(doOpen && !Mouse.isButtonDown(0)){
                if(!doClose && AchievementList.currentMouseOver != null)
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
                currentRecipe.drawScreen(event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());


        }
    }
}
