package scalinghealthissues.mixin.modcompat.firstaid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.damagesystem.distribution.RandomDamageDistribution;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

@Mixin(FirstAidRegistryImpl.class)
public abstract class FirstAidRegistryImpl_Mixin {

    @ModifyReturnValue(
            method = "getDamageDistributionForSource",
            at = @At("RETURN"),
            remap = false
    )
    private IDamageDistribution scalingHealthIssues_firstAidFirstAidRegistryImpl_getDamageDistributionForSourcePoisonDamage(IDamageDistribution original, DamageSource source){
        if(source instanceof IDamageSources_NonLethalMixin && ((IDamageSources_NonLethalMixin) source).scalingHealthIssues$isNonLethal())
            return RandomDamageDistribution.ANY_NOKILL;

        return original;
    }
}
