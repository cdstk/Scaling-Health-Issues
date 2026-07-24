package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DifficultyHandler.class)
public abstract class DifficultyHandler_FixConfigMixin {

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "FIELD", target = "Lnet/silentchaos512/scalinghealth/config/Config;BLIGHT_EQUIPMENT_ARMOR_PIECE_CHANCE:F"),
            remap = false
    )
    private float scalingHealthIssues_shDifficultyHandler_makeEntityBlightFixArmorConfig(float original){
        return 1F - original;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "FIELD", target = "Lnet/silentchaos512/scalinghealth/config/Config;BLIGHT_EQUIPMENT_HAND_PIECE_CHANCE:F"),
            remap = false
    )
    private float scalingHealthIssues_shDifficultyHandler_makeEntityBlightFixHandConfig(float original){
        return 1F - original;
    }
}
