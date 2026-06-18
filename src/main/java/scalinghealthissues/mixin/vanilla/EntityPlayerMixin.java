package scalinghealthissues.mixin.vanilla;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import scalinghealthissues.ScalingHealthIssues;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Inject(
            method = "attackEntityFrom",
            at = @At("HEAD")
    )
    public void scalinghealthissues_vanillaEntityPlayer_attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ScalingHealthIssues.LOGGER.log(Level.INFO, "Player attacked");
    }
}