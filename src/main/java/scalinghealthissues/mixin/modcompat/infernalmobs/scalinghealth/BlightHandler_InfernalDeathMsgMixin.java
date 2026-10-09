package scalinghealthissues.mixin.modcompat.infernalmobs.scalinghealth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.silentchaos512.scalinghealth.event.BlightHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.compat.InfernalMobsUtil;

@Mixin(BlightHandler.class)
public abstract class BlightHandler_InfernalDeathMsgMixin {

    @ModifyExpressionValue(
            method = "onBlightKilled",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/scalinghealth/event/BlightHandler;isBlight(Lnet/minecraft/entity/EntityLivingBase;)Z"),
            remap = false
    )
    private boolean scalingHealthIssues_shBlightHandler_onBlightKilledInfernalMobsKilled(boolean isBlight, LivingDeathEvent event){
        return isBlight || InfernalMobsUtil.shouldAnnouceKill(event.getEntityLiving());
    }

    @ModifyExpressionValue(
            method = "onBlightKilled",
            at = @At(value = "FIELD", target = "Lnet/silentchaos512/scalinghealth/config/Config$Mob$Blight;notifyOnDeath:Z"),
            remap = false
    )
    private boolean scalingHealthIssues_shBlightHandler_onBlightKilledInfernalMobsMessage(boolean notifyOnDeath, LivingDeathEvent event){
        return notifyOnDeath || InfernalMobsUtil.shouldAnnouceKill(event.getEntityLiving());
    }
}
