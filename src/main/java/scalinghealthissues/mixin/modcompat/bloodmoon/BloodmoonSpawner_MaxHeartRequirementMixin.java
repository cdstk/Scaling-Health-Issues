package scalinghealthissues.mixin.modcompat.bloodmoon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import lumien.bloodmoon.server.BloodmoonSpawner;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.compat.BloodmoonUtil;

@Mixin(BloodmoonSpawner.class)
public class BloodmoonSpawner_MaxHeartRequirementMixin {

    @ModifyExpressionValue(
            method = "findChunksForSpawning",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSpectator()Z")
    )
    private boolean scalingHealthIssues_bloodmoonBloodmoonSpawner_findChunksForSpawningAllowed(boolean isSpectator, @Local EntityPlayer entityplayer){
        return isSpectator || BloodmoonUtil.isSafeFromActive(entityplayer);
    }
}
