package mods.flammpfeil.slashblade.capability.MobEffect;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityMobEffectRegister {
        static public ResourceLocation MOB_EFFECT_PROVIDER = new ResourceLocation(SlashBlade.modid, "mobeffect");

        @SubscribeEvent
        public void AttachCapabilitiesEvent_Entity(AttachCapabilitiesEvent<Entity> event) {

            if(!(event.getObject() instanceof EntityLivingBase)) return;

            event.addCapability(MOB_EFFECT_PROVIDER, new MobEffectCapabilityProvider());
        }
}
