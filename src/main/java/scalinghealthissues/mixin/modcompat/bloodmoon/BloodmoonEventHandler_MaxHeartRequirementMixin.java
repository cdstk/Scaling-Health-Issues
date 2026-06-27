package scalinghealthissues.mixin.modcompat.bloodmoon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lumien.bloodmoon.handler.BloodmoonEventHandler;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.compat.BloodmoonUtil;

@Mixin(BloodmoonEventHandler.class)
public abstract class BloodmoonEventHandler_MaxHeartRequirementMixin {

    @ModifyExpressionValue(
            method = "sleepInBed",
            at = @At(value = "INVOKE", target = "Llumien/bloodmoon/proxy/CommonProxy;isBloodmoon()Z"),
            remap = false
    )
    private boolean scalingHealthIssues_bloodmoonBloodmoonEventHandler_sleepInBedAllowed(boolean isBloodmoon, PlayerSleepInBedEvent event){
        if(ScalingHealthIssues.PROXY.isSinglePlayer() && !BloodmoonUtil.canExperienceBloodmoon(event.getEntityPlayer()))
            return false;

        return isBloodmoon;
    }
}
