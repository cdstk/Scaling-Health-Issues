package scalinghealthissues.mixin.modcompat.srp;

import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import scalinghealthissues.mixininterface.IDamageSources_NonLethalMixin;
import scalinghealthissues.util.DamageSources;

@Mixin(SRPEffectBase.class)
public abstract class SRPEffectBase_RuntimeRefMixin {

    @ModifyArg(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z")
    )
    private DamageSource scalingHealthIssues_srpSRPEffectBase_performEffectBleedSourceRef(DamageSource source){
        if(DamageSources.SRP_BLEED == null) {
            if(source.equals(DamageSource.MAGIC)) {
                // Do not modify the static Vanilla Magic
                DamageSources.SRP_BLEED = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
            }
            else {
                // Other mod replaced
                DamageSources.SRP_BLEED = source;
            }
            if(DamageSources.SRP_BLEED instanceof IDamageSources_NonLethalMixin)
                ((IDamageSources_NonLethalMixin) DamageSources.SRP_BLEED).scalingHealthIssues$setNonLethal();
        }
        return DamageSources.SRP_BLEED;
    }
}
