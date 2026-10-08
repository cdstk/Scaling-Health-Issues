package scalinghealthissues.mixin.modcompat.lycanitesmobs;

import com.lycanitesmobs.PotionEffects;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import scalinghealthissues.mixininterface.IDamageSources_NonLethalMixin;
import scalinghealthissues.util.DamageSources;

@Mixin(PotionEffects.class)
public abstract class PotionEffects_RuntimeRefMixin {

    @ModifyArg(
            method = "onEntityUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z")
    )
    private DamageSource scalingHealthIssues_lycanitesMobsPotionEffects_onEntityUpdateBleedSourceRef(DamageSource source){
        if(DamageSources.LYCANITES_BLEED == null) {
            if(source.equals(DamageSource.MAGIC)) {
                // Do not modify the static Vanilla Magic
                DamageSources.LYCANITES_BLEED = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
            }
            else {
                // Other mod replaced
                DamageSources.LYCANITES_BLEED = source;
            }
            if(DamageSources.LYCANITES_BLEED instanceof IDamageSources_NonLethalMixin)
                ((IDamageSources_NonLethalMixin) DamageSources.LYCANITES_BLEED).scalingHealthIssues$setNonLethal();
        }
        return DamageSources.LYCANITES_BLEED;
    }
}
