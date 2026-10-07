package scalinghealthissues.mixin.modcompat.firstaid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ichttt.mods.firstaid.common.damagesystem.PlayerDamageModel;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.compat.handlers.FirstAidHandler;
import scalinghealthissues.config.ConfigHandler;

@Mixin(PlayerDamageModel.class)
public abstract class PlayerDamageModel_MorphineMixin {

    @ModifyReturnValue(
            method = "getRandMorphineDuration",
            at = @At("RETURN"),
            remap = false
    )
    private static int scalingHealthIssues_firstAidPlayerDamageModel_getRandMorphineDurationConfig(int randDuration){
        return ConfigHandler.compat.firstAid.morphineRework ? FirstAidHandler.getRandMorphineDuration() : randDuration;
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"),
            remap = false
    )
    private void scalingHealthIssues_firstAidPlayerDamageModel_tickInternal(CallbackInfo ci){
        FirstAidHandler.morphineAppliedInternally = true;
    }

    @Inject(
            method = "applyMorphine(Lnet/minecraft/entity/player/EntityPlayer;)V",
            at = @At("HEAD"),
            remap = false
    )
    private void scalingHealthIssues_firstAidPlayerDamageModel_applyMorphineInternal(EntityPlayer player, CallbackInfo ci){
        FirstAidHandler.morphineAppliedInternally = true;
    }
}
