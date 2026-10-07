package scalinghealthissues.compat.handlers;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.common.EventHandler;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.damagesystem.distribution.RandomDamageDistribution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.util.DamageSources;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

import java.util.Iterator;

public class FirstAidHandler {

    // When First Aid or command modifies
    public static boolean morphineAppliedInternally = false;
    public static boolean morphineRemovedInternally = false;

    public static boolean morphineBuffActive(EntityLivingBase entityLivingBase) {
        return ConfigHandler.compat.firstAid.morphineRework && entityLivingBase.isPotionActive(EventHandler.MORPHINE);
    }

    public static boolean morphineApplyRulesEnabled() {
        return ConfigHandler.compat.firstAid.morphineRework && ConfigHandler.compat.firstAid.morphineApplyRules;
    }

    public static int getRandMorphineDuration(int min, int max, int stepSize) {
        int randBound = Math.max(1, (max - min) / stepSize);
        return 20 * ((EventHandler.rand.nextInt(randBound) * stepSize) + min);
    }

    public static int getRandMorphineDuration() {
        return getRandMorphineDuration(
                ConfigHandler.compat.firstAid.morphineDurationMin,
                ConfigHandler.compat.firstAid.morphineDurationMax,
                ConfigHandler.compat.firstAid.morphineDurationStep
        );
    }

    public static void registerDefaults() {
        FirstAidRegistryImpl.INSTANCE.registerDistribution(new DamageSource[] { DamageSources.POISON }, RandomDamageDistribution.ANY_NOKILL);
    }

    // One part of handling Non-Lethal Damage
    // This handles Vanilla damage animation/sfx
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if(event.getEntityLiving().world.isRemote) return;
        if(!ConfigHandler.compat.firstAid.poisonIFramesFix) return;
        if(!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer victim = (EntityPlayer) event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        if(IDamageSources_NonLethalMixin.isNonLethal(damageSource)) {
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

    @SubscribeEvent
    public static void onPotionRemove(PotionEvent.PotionRemoveEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if(effect == null) return;
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if(!morphineApplyRulesEnabled()) return;
        if(!event.getPotionEffect().getPotion().equals(EventHandler.MORPHINE)) return;
        if(event.isCanceled()) return;

        // Prevent removal from general cleanse, can still time out
        if(!morphineRemovedInternally && entity.isPotionActive(EventHandler.MORPHINE)) {
            event.setCanceled(true);
        }
        morphineRemovedInternally = false;
    }

    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if(!morphineApplyRulesEnabled()) return;
        if(!event.getPotionEffect().getPotion().equals(EventHandler.MORPHINE)) return;

        // Prevent extensions outside the Morphine item, it can still be applied randomly if not already active.
        if(event.getResult() == Event.Result.DEFAULT) {
            if(!morphineAppliedInternally && entity.isPotionActive(EventHandler.MORPHINE)) {
                event.setResult(Event.Result.DENY);
            }
        }
        morphineAppliedInternally = false;
    }
}
