package mods.flammpfeil.slashblade.util;

import mods.flammpfeil.slashblade.ability.IllegalActionEnabler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.relauncher.ObfuscationReflectionHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Furia on 2016/02/03.
 */
public class ReflectionAccessHelper {

    @OnlyIn(Dist.CLIENT)
    public static Timer timer;
    @OnlyIn(Dist.CLIENT)
    public static float getPartialTicks(){

        if(timer == null)
            timer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getInstance(), "timer", "field_71428_T");

        return timer.renderPartialTicks;
    }

    public static void setFire(Entity entity, int ticks) {
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, entity, ticks, "fire", "field_70151_c", "field_190534_ay");
    }

    public static void setItem(ItemStack stack , Item item){
        ObfuscationReflectionHelper.setPrivateValue(ItemStack.class, stack, item, "item", "field_151002_e");
        Method forgeInit = ObfuscationReflectionHelper.findMethod(ItemStack.class, "forgeInit","forgeInit");
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
