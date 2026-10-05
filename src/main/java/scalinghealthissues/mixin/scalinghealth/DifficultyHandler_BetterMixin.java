package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.handlers.BetterBlightHandler;

import java.util.Random;

@Mixin(DifficultyHandler.class)
public abstract class DifficultyHandler_BetterMixin {

    @ModifyExpressionValue(
            method = "process",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/scalinghealth/event/BlightHandler;isBlight(Lnet/minecraft/entity/EntityLivingBase;)Z"),
            remap = false
    )
    private boolean scalingHealthIssues_shDifficultyHandler_processFakeBlight(boolean isNaturalBlight, EntityLivingBase entity){
        if(BetterBlightHandler.isFakeBlight(entity))
            return false;

        return isNaturalBlight;
    }

    @ModifyReturnValue(
            method = "isAlwaysBlight",
            at = @At("RETURN"),
            remap = false
    )
    private static boolean scalingHealthIssues_shDifficultyHandler_isAlwaysBlightFakeBlight(boolean original, EntityLivingBase entity){
        if(BetterBlightHandler.isFakeBlight(entity)) { // Allow recalculating fake
            return true;
        }
        return original;
    }

    @Inject(
            method = "makeEntityBlight",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/scalinghealth/event/BlightHandler;markBlight(Lnet/minecraft/entity/EntityLivingBase;Z)V"),
            remap = false
    )
    private void scalingHealthIssues_shDifficultyHandler_makeEntityBlightFakeFixed(EntityLiving entityLiving, Random rand, CallbackInfo ci){
        if(ConfigHandler.debug.logBlights && BetterBlightHandler.isFakeBlight(entityLiving)) {
            ScalingHealthIssues.LOGGER.log(Level.INFO, "Recalculated a fake blight: {}", entityLiving);
        }
        entityLiving.getEntityData().setBoolean(BetterBlightHandler.NBT_BLIGHT_PROCESSED, true);
    }

    @ModifyExpressionValue(
            method = "entityBlacklistedFromBecomingBlight",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/scalinghealth/event/BlightHandler;isBlight(Lnet/minecraft/entity/EntityLivingBase;)Z"),
            remap = false
    )
    private static boolean scalingHealthIssues_shDifficultyHandler_entityBlacklistedFromBecomingBlightExceptFake(boolean canBeNaturalBlight, EntityLivingBase entityLiving){
        if(BetterBlightHandler.isFakeBlight(entityLiving)) { // Likely Blight set by NBT
            return false;
        }
        return canBeNaturalBlight;
    }
}
