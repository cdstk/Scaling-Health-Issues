package scalinghealthissues.mixin.modcompat.bloodmoon.client;

import lumien.bloodmoon.handler.BloodmoonEventHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BloodmoonEventHandler.class)
public abstract class BloodmoonEventHandler_MaxHeartRequirementMixin {

    // This approach would need syncing extra data, so instead don't send Bloodmoon packet to client

//    @ModifyExpressionValue(
//            method = "fogColor",
//            at = @At(value = "INVOKE", target = "Llumien/bloodmoon/client/ClientBloodmoonHandler;isBloodmoonActive()Z"),
//            remap = false
//    )
//    private boolean scalingHealthIssues_bloodmoonBloodmoonEventHandler_fogColorAllowed(boolean isBloodmoonActive){
//        return isBloodmoonActive && BloodmoonUtil.canExperienceBloodmoon(Minecraft.getMinecraft().player);
//    }
}
