package scalinghealthissues.mixin.modcompat.firstaid;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ichttt.mods.firstaid.common.potion.PotionPoisonPatched;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.config.ConfigHandler;

@Mixin(PotionPoisonPatched.class)
public abstract class PotionPoisonPatched_Mixin extends Potion {

    protected PotionPoisonPatched_Mixin(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    @WrapMethod(
            method = "performEffect"
    )
    private void scalingHealthIssues_firstAidPotionPoisonPatched_performEffectPoisonFix(EntityLivingBase entity, int amplifier, Operation<Void> original){
        if(ConfigHandler.compat.firstAidPoisonFix) { // Fully use Vanilla Poison
            super.performEffect(entity, amplifier);
        }
        else {
            original.call(entity, amplifier);
        }
    }

    @WrapOperation(
            method = "performEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Potion;performEffect(Lnet/minecraft/entity/EntityLivingBase;I)V")
    )
    private void scalingHealthIssues_firstAidPotionPoisonPatched_performEffectPoisonUnFix(PotionPoisonPatched instance, EntityLivingBase entityLivingBaseIn, int amplifier, Operation<Void> original){
        // This only runs if First Aid Poison is called
        int hurtResistantTimeBefore = entityLivingBaseIn.hurtResistantTime;
        original.call(instance, entityLivingBaseIn, amplifier);
        if(ConfigHandler.compat.firstAidPoisonUnfixAll)
            entityLivingBaseIn.hurtResistantTime = hurtResistantTimeBefore;
    }
}
