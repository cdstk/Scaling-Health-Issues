package scalinghealthissues.mixin.modcompat.defiledlands;

import lykrast.defiledlands.common.potion.PotionGeneric;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import scalinghealthissues.mixininterface.IDamageSources_NonLethalMixin;
import scalinghealthissues.util.DamageSources;

@Mixin(PotionGeneric.class)
public abstract class PotionGeneric_RuntimeRefMixin {

    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z")
    )
    private DamageSource scalingHealthIssues_defiledLandsPotionGeneric_performEffectBleedSourceRef(DamageSource source){
        if(DamageSources.DEFILEDLANDS_BLEED == null) {
            if(source.equals(DamageSource.MAGIC)) {
                // Do not modify the static Vanilla Magic
                DamageSources.DEFILEDLANDS_BLEED = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
            }
            else {
                // Other mod replaced
                DamageSources.DEFILEDLANDS_BLEED = source;
            }
            if(DamageSources.DEFILEDLANDS_BLEED instanceof IDamageSources_NonLethalMixin)
                ((IDamageSources_NonLethalMixin) DamageSources.DEFILEDLANDS_BLEED).scalingHealthIssues$setNonLethal();
        }
        return DamageSources.DEFILEDLANDS_BLEED;
    }
}
