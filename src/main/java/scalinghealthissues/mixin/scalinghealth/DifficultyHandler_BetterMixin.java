package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.handlers.BetterBlightHandler;
import scalinghealthissues.handlers.BetterDifficultyHandler;

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

    @WrapOperation(
            method = "process",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/scalinghealth/event/DifficultyHandler;makeEntityBlight(Lnet/minecraft/entity/EntityLiving;Ljava/util/Random;)V"),
            remap = false
    )
    private void scalingHealthIssues_shDifficultyHandler_processMarkFinalizeBlight(DifficultyHandler instance, EntityLiving entityLiving, Random rand, Operation<Void> original){
        if(entityLiving.ticksExisted == 0) {
            entityLiving.getEntityData().setBoolean(BetterDifficultyHandler.FINALIZE_BLIGHT_SPAWN, true);
        }
        else {
            entityLiving.getEntityData().setBoolean(BetterDifficultyHandler.FINALIZE_BLIGHT_SPAWN, false);
            original.call(instance, entityLiving, rand);
            if(ConfigHandler.debug.logBlights) {
                ScalingHealthIssues.LOGGER.log(Level.INFO, "An existing entity was Blight processed: {}", entityLiving);
            }
        }
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

    @ModifyArg(
            method = "increaseEntityHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setShort(Ljava/lang/String;S)V"),
            index = 1
    )
    private short scalingHealthIssues_shDifficultyHandler_increaseEntityHealthCalcZeroLoop(short difficulty, @Local(argsOnly = true) EntityLivingBase entityLiving){
        if(difficulty == 0) {
            if(ConfigHandler.debug.logDifficulty) {
                ScalingHealthIssues.LOGGER.log(Level.INFO, "An entity calculated 0.0 Difficulty, a processing loop was prevented for: {}", entityLiving);
            }
            return -1;
        }
        return difficulty;
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

    @ModifyConstant(
            method = "canIncreaseEntityHealth",
            constant = @Constant(intValue = 1),
            remap = false
    )
    private static int scalingHealthIssues_shDifficultyHandler_canIncreaseEntityHealthOnSpawn(int constant){
        return -1; // Move to onSpawn Events
    }

    @ModifyReturnValue(
            method = "entityBlacklistedFromBecomingBlight",
            at = @At("RETURN"),
            remap = false
    )
    private static boolean scalingHealthIssues_shDifficultyHandler_entityBlacklistedFromBecomingBlightExceptFake(boolean canBeNaturalBlight, EntityLivingBase entityLiving){
        if(BetterBlightHandler.isFakeBlight(entityLiving)) { // Likely Blight set by NBT
            return false;
        }
        return canBeNaturalBlight;
    }
}
