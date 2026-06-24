package scalinghealthissues.mixin.lycanitesmobs;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import scalinghealthissues.compat.LycanitesMobsUtil;

@Mixin(Spawner.class)
public abstract class Spawner_MobEventRequirementMixin {

    @Inject(
            method = "isEnabled",
            at = @At(value = "INVOKE", target = "Lcom/lycanitesmobs/ExtendedWorld;getForWorld(Lnet/minecraft/world/World;)Lcom/lycanitesmobs/ExtendedWorld;"),
            cancellable = true,
            remap = false
    )
    private void scalingHealthIssues_lycanitesMobsSpawner_isEnabledPlayerHearts(World world, EntityPlayer player, CallbackInfoReturnable<Boolean> cir){
        if(!LycanitesMobsUtil.canExperienceMobEvent(player))
            cir.setReturnValue(false);
    }
}
