package scalinghealthissues.mixin.lycanitesmobs.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.lycanitesmobs.core.mobevent.MobEventPlayerClient;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.compat.LycanitesMobsUtil;

@Mixin(MobEventPlayerClient.class)
public abstract class MobEventPlayerClient_MaxHeartRequirementMixin {

    @WrapWithCondition(
            method = "onStart",
            at = @At(value = "INVOKE", target = "Lcom/lycanitesmobs/core/mobevent/MobEventPlayerClient;playSound()V"),
            remap = false
    )
    private boolean scalingHealthIssues_lycanitesMobsMobEventPlayerClient_onStartPlayerHearts(MobEventPlayerClient instance, EntityPlayer player){
        return LycanitesMobsUtil.canExperienceMobEvent(player);
    }
}
