package scalinghealthissues.mixin.bloodmoon.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import lumien.bloodmoon.handler.BloodmoonEventHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.compat.BloodmoonUtil;

@Mixin(BloodmoonEventHandler.class)
public abstract class BloodmoonEventHandler_MaxHeartRequirementMixin {

    @ModifyExpressionValue(
            method = "fogColor",
            at = @At(value = "INVOKE", target = "Llumien/bloodmoon/client/ClientBloodmoonHandler;isBloodmoonActive()Z"),
            remap = false
    )
    private boolean scalingHealthIssues_bloodmoonBloodmoonEventHandler_fogColorAllowed(boolean isBloodmoonActive){
        return isBloodmoonActive && BloodmoonUtil.canExperienceBloodmoon(Minecraft.getMinecraft().player);
    }
}
