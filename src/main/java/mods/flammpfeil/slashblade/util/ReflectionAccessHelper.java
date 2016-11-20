package mods.flammpfeil.slashblade.util;

import mods.flammpfeil.slashblade.ability.IllegalActionEnabler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Furia on 2016/02/03.
 */
public class ReflectionAccessHelper {

    @SideOnly(Side.CLIENT)
    public static Timer timer;
    @SideOnly(Side.CLIENT)
    public static float getPartialTicks(){

        if(timer == null)
            timer = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");

        return timer.renderPartialTicks;
    }

    public static void setFire(Entity entity, int ticks) {
        ReflectionHelper.setPrivateValue(Entity.class, entity, ticks, "fire", "field_70151_c", "field_190534_ay");
    }

    public static void setItem(ItemStack stack , Item item){
        ReflectionHelper.setPrivateValue(ItemStack.class, stack, item, "item", "field_151002_e");
        Method forgeInit = ReflectionHelper.findMethod(ItemStack.class, stack, new String[]{"forgeInit"});
        if(forgeInit != null) try {
            forgeInit.invoke(stack);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void setVelocity(Entity entity, double x, double y, double z){
        entity.motionX = x;
        entity.motionY = y;
        entity.motionZ = z;
    }
}
