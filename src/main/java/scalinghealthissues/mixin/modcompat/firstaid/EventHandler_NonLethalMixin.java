package scalinghealthissues.mixin.modcompat.firstaid;

import com.llamalad7.mixinextras.sugar.Local;
import ichttt.mods.firstaid.common.EventHandler;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

@Mixin(EventHandler.class)
public abstract class EventHandler_NonLethalMixin {

    // One part of handling Non-Lethal Damage
    // This handles overflowing to other parts, which the default handler can force lethality
    @ModifyArg(
            method = "onLivingHurt",
            at = @At(value = "INVOKE", target = "Lichttt/mods/firstaid/common/damagesystem/distribution/DamageDistribution;handleDamageTaken(Lichttt/mods/firstaid/api/IDamageDistribution;Lichttt/mods/firstaid/api/damagesystem/AbstractPlayerDamageModel;FLnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/DamageSource;ZZ)F"),
            index = 6,
            remap = false
    )
    private static boolean scalingHealthIssues_firstAidFirstAidEventHandler_onLivingHurtNonLethal(boolean redistributeIfLeft, @Local DamageSource source){
        if(source instanceof IDamageSources_NonLethalMixin && ((IDamageSources_NonLethalMixin) source).scalingHealthIssues$isNonLethal())
            return false;

        return redistributeIfLeft;
    }
}
