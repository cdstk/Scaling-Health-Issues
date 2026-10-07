package scalinghealthissues.mixin.modcompat.firstaid;

import com.llamalad7.mixinextras.sugar.Local;
import ichttt.mods.firstaid.common.EventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import scalinghealthissues.compat.handlers.FirstAidHandler;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

@Mixin(EventHandler.class)
public abstract class EventHandler_LethalityMixin {

    // One part of handling Non-Lethal Damage
    // This handles general overflowing to other part groupings/armor slots, which allowing can kill the player in some cases
    @ModifyArg(
            method = "onLivingHurt",
            at = @At(value = "INVOKE", target = "Lichttt/mods/firstaid/common/damagesystem/distribution/DamageDistribution;handleDamageTaken(Lichttt/mods/firstaid/api/IDamageDistribution;Lichttt/mods/firstaid/api/damagesystem/AbstractPlayerDamageModel;FLnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/DamageSource;ZZ)F"),
            index = 6,
            remap = false
    )
    private static boolean scalingHealthIssues_firstAidFirstAidEventHandler_onLivingHurtLethalityRedistribute(boolean redistributeIfLeft, @Local EntityPlayer player, @Local DamageSource source){
        if(IDamageSources_NonLethalMixin.isNonLethal(source)) {
            return false;
        }

        if(FirstAidHandler.morphineBuffActive(player)) {
            return false;
        }

        return redistributeIfLeft;
    }
}
