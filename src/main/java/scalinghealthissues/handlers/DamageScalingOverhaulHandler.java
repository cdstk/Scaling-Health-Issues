package scalinghealthissues.handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.scalinghealth.event.DamageScaling;
import org.apache.logging.log4j.Level;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.folders.DamageScalingConfig;
import scalinghealthissues.mixin.scalinghealth.DamageScaling_InvokerMixin;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

public abstract class DamageScalingOverhaulHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote) return;

        if(!(entity instanceof EntityPlayer)) {
            if (!DamageScalingConfig.getVictimReceivesScaledDamage(entity))
                return;
        }

        DamageScaling_InvokerMixin config = (DamageScaling_InvokerMixin) (Object) DamageScaling.INSTANCE;
        DamageSource source = event.getSource();

        // Get scaling factor from map, if it exists. Otherwise, use the generic scale.
        float scale = DamageScalingConfig.getScaleForSource(
                source,
                config.scalingHealthIssues$accessScalingMap(),
                config.scalingHealthIssues$accessGenericScale()
        );

        // Get the amount of the damage to affect. Can be many times the base value.
        float affectedAmount = config.scalingHealthIssues$invokeGetEffectScale(entity);

        // Calculate damage to add to the original.
        float original = event.getAmount();
        float change = scale * affectedAmount * original;
        if (change != 0) {
            float newAmount = config.scalingHealthIssues$invokeMakeSane(event.getAmount() + change);

            if(IDamageSources_NonLethalMixin.isNonLethal(source))
                newAmount = Math.min(newAmount, entity.getHealth() - 1F);

            event.setAmount(newAmount);

            if (ConfigHandler.debug.logDamageScale) {
                ScalingHealthIssues.LOGGER.log(Level.DEBUG,
                        "{} on {} from {}: {} -> {} (scale={}, affected={}, change={})",
                        source.damageType, entity.getName(), source.getTrueSource() == null ? "NULL" : source.getTrueSource().getName(),
                        original, newAmount, scale, affectedAmount, change
                );
            }
        }
    }
}
