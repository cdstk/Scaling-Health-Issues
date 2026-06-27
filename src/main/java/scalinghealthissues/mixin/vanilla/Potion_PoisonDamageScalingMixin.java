package scalinghealthissues.mixin.vanilla;

import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import scalinghealthissues.util.DamageSources;

@Mixin(Potion.class)
public abstract class Potion_PoisonDamageScalingMixin {

    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 0),
            index = 0
    )
    private DamageSource scalingHealthIssues_vanillaPotion_performEffectScaledPoisonDamage(DamageSource source){
        return DamageSources.POISON;
    }
}
