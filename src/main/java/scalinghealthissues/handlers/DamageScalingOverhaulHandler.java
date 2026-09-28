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
import scalinghealthissues.config.ForgeConfigHandler;
import scalinghealthissues.config.folders.DamageScalingConfig;
import scalinghealthissues.mixin.scalinghealth.DamageScaling_InvokerMixin;
import scalinghealthissues.util.DamageSources;

public abstract class DamageScalingOverhaulHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote) return;

        DamageScaling_InvokerMixin config = (DamageScaling_InvokerMixin) (Object) DamageScaling.INSTANCE;

        if(!(entity instanceof EntityPlayer)) {
            Boolean shouldScale;
            shouldScale = DamageScalingConfig.isHostileScalable(entity);

            if (shouldScale == null)
                shouldScale = DamageScalingConfig.isPassiveScalable(entity);

            if (shouldScale == null)
                shouldScale = DamageScalingConfig.isEntityScalable(entity);

            if (!shouldScale)
                return;
        }

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
            // TODO More Non Lethal conditions? Test First Aid more Thoroughly as Health Boost 255 possible fatal but very over 128 part hp cap
            if(source == DamageSources.POISON)
                newAmount = Math.min(newAmount, entity.getHealth() - 1F);

            event.setAmount(newAmount);

            if (ForgeConfigHandler.debug.logDamageScale) {
                ScalingHealthIssues.LOGGER.log(Level.DEBUG,
                        "{} on {} from {}: {} -> {} (scale={}, affected={}, change={})",
                        source.damageType, entity.getName(), source.getTrueSource() == null ? "NULL" : source.getTrueSource().getName(),
                        original, newAmount, scale, affectedAmount, change
                );
            }
        }
    }
}
