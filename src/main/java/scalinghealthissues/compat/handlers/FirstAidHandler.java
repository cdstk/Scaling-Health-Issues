package scalinghealthissues.compat.handlers;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

import java.util.Iterator;

public class FirstAidHandler {

    // One part of handling Non-Lethal Damage
    // This handles Vanilla damage animation/sfx
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if(event.getEntityLiving().world.isRemote) return;
        if(!ConfigHandler.compat.firstAidPoisonFix) return;
        if(!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer victim = (EntityPlayer) event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        if(damageSource instanceof IDamageSources_NonLethalMixin && ((IDamageSources_NonLethalMixin) damageSource).scalingHealthIssues$isNonLethal()) {
            AbstractPlayerDamageModel playerDamageModel = victim.getCapability(CapabilityExtendedHealthSystem.INSTANCE, null);
            if(playerDamageModel != null) {
                boolean stopDamaging = true;

                Iterator<AbstractDamageablePart> partIterator = playerDamageModel.iterator();
                while (stopDamaging && partIterator.hasNext()) {
                    AbstractDamageablePart part = partIterator.next();
                    if(part.canCauseDeath) {
                        if(part.currentHealth > 1.0F) {
                            stopDamaging = false;
                        }
                    }
                    else {
                        if(part.currentHealth > 0) {
                            stopDamaging = false;
                        }
                    }
                }

                if(stopDamaging)
                    event.setCanceled(true);
            }
        }
    }
}
