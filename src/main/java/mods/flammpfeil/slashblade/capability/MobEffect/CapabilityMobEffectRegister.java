package mods.flammpfeil.slashblade.capability.MobEffect;

import com.google.common.collect.Sets;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public class CapabilityMobEffectRegister {
        static public ResourceLocation MOB_EFFECT_PROVIDER = new ResourceLocation(SlashBlade.modid, "mobeffect");

        @SubscribeEvent
        public void AttachCapabilitiesEvent_Entity(AttachCapabilitiesEvent<Entity> event) {

            if(!(event.getObject() instanceof EntityLivingBase)) return;

            event.addCapability(MOB_EFFECT_PROVIDER, new MobEffectCapabilityProvider());
        }
}
