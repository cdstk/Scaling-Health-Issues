package scalinghealthissues.mixin.scalinghealth.overhaul;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.silentchaos512.scalinghealth.event.DamageScaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageScaling.class)
public abstract class DamageScaling_OverhaulMixin {

    @Inject(
            method = "onPlayerHurt",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false
    )
    private void scalingHealthIssues_shDamageScaling_onPlayerHurtOverhaul(LivingAttackEvent event, CallbackInfo ci){
        ci.cancel();
    }
}
