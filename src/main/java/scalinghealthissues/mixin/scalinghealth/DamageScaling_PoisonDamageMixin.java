package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.silentchaos512.scalinghealth.event.DamageScaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

@Mixin(DamageScaling.class)
public abstract class DamageScaling_PoisonDamageMixin {

    @ModifyVariable(
            method = "onPlayerHurt",
            at = @At(value = "STORE", ordinal = 0),
            name = "newAmount",
            remap = false
    )
    private float scalingHealthIssues_shDamageScaling_onPlayerHurtFixFatalPoison(float value, @Local EntityLivingBase entity, @Local DamageSource source){
        if(IDamageSources_NonLethalMixin.isNonLethal(source))
            return Math.min(value, entity.getHealth() - 1F);

        return value;
    }
}
