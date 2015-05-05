package mods.flammpfeil.slashblade.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.flammpfeil.slashblade.ItemSlashBlade;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.UUID;

/**
 * Created by Furia on 15/04/27.
 */
public class SneakMove {
    private static final UUID skillSneakMoveSpeedBoostModifierUUID = UUID.fromString("f58db5f5-c38d-482b-a1cb-f8effad0b3e8");
    private static final AttributeModifier skillSneakMoveSpeedBoostModifier = (new AttributeModifier(skillSneakMoveSpeedBoostModifierUUID, "SneakMove speed boost", 2.30000001192092896D, 2)).setSaved(false);

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase entity = event.entityLiving;
        if(entity == null) return;

        IAttributeInstance iattributeinstance = entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (iattributeinstance.getModifier(skillSneakMoveSpeedBoostModifierUUID) != null)
        {
            iattributeinstance.removeModifier(skillSneakMoveSpeedBoostModifier);
        }

        if(!entity.isSneaking()) return;

        ItemStack heldItem = entity.getHeldItem();
        if(heldItem == null) return;
        if(!(heldItem.getItem() != null && heldItem.getItem() instanceof ItemSlashBlade)) return;

        iattributeinstance.applyModifier(skillSneakMoveSpeedBoostModifier);
    }

    @SubscribeEvent
    public void onFOVUpdateEvent(FOVUpdateEvent event){
        if(event.entity == null) return;
        IAttributeInstance iattributeinstance = event.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (iattributeinstance.getModifier(skillSneakMoveSpeedBoostModifierUUID) != null)
        {
            event.newfov = 1.0f;
        }
    }
}
