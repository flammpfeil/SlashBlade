package mods.flammpfeil.slashblade.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

/**
 * Created by Furia on 14/05/24.
 */
public abstract class KeyBindingEx extends KeyBinding {
    private void register(){
        if(!Arrays.asList(Minecraft.getMinecraft().gameSettings.keyBindings).contains(this)){
            ClientRegistry.registerKeyBinding(this);
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Override
    public boolean conflicts(KeyBinding other) {
        return false;
    }

    private static final int dumyKeyCode = -200;

    public int overrideKeyCode;
    final public int overrideKeyCodeDefault;

    public KeyBindingEx(String Description, int keyCode, String Category) {
        super(Description, dumyKeyCode, Category);
        this.overrideKeyCode = keyCode;
        this.overrideKeyCodeDefault = keyCode;

        register();
    }

    @Override
    public int getKeyCodeDefault() {
        return overrideKeyCodeDefault;
    }

    @Override
    public int getKeyCode() {
        return overrideKeyCode;
    }

    @Override
    public void setKeyCode(int keyCode) {
        this.overrideKeyCode = keyCode;
    }

    public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, int keyCode)
    {
        this.overrideKeyCode = keyCode;
    }

    @Override
    public boolean isKeyDown()
    {
        return lastPressd;
    }

    @Override
    public boolean isPressed()
    {
        if(0 < pressCount){
            pressCount--;
            return true;
        }else{
            return false;
        }
    }

    int pressCount;
    int pressTicks;
    boolean lastPressd = false;
    @SubscribeEvent
    public void ClientTickEvent(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            boolean currentPressd;
            if(overrideKeyCode < 0)
                currentPressd = Mouse.isButtonDown(overrideKeyCode + 100);
            else
                currentPressd = Keyboard.isKeyDown(overrideKeyCode);

            if (currentPressd)
            {
                if(lastPressd == false){
                    downkey();
                    pressCount++;
                }
                presskey(pressTicks);
                pressTicks++;
            }else{
                if(lastPressd == true){
                    upkey(pressTicks);
                    pressTicks = 0;
                    pressCount = 0;
                }
            }
            lastPressd = currentPressd;
        }
    }

    public void presskey(int count){
        //System.out.println("Press" + count);
    }
    public void upkey(int count){
        //System.out.println("Up");
    }
    public void downkey(){
        //System.out.println("Down");

    }
}
