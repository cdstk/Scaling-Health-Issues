package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ScalingHealthIssuesConfigHandler;
import scalinghealthissues.handlers.BetterBlightHandler;
import scalinghealthissues.handlers.BetterDifficultyHandler;
import scalinghealthissues.network.PacketEntityDifficulty;
import scalinghealthissues.network.PacketHandler;

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

    @WrapMethod(
            method = "process",
            remap = false
    )
    private boolean scalingHealthIssues_shDifficultyHandler_processDifficultyFinish(EntityLivingBase entity, Operation<Boolean> original){
        boolean result = original.call(entity);
        if(result)
            entity.getEntityData().setBoolean(BetterDifficultyHandler.NBT_DIFFICULTY_PROCESSED, true);

        return result;
    }

    @WrapOperation(
            method = "process",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setShort(Ljava/lang/String;S)V")
    )
    private void scalingHealthIssues_shDifficultyHandler_processDifficultySync(NBTTagCompound instance, String key, short value, Operation<Void> original, EntityLivingBase entity){
        original.call(instance, key, value);
        PacketHandler.instance.sendToAllTracking(new PacketEntityDifficulty(entity), entity);
    }

    @WrapOperation(
            method = "increaseEntityHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setShort(Ljava/lang/String;S)V")
    )
    private void scalingHealthIssues_shDifficultyHandler_increaseEntityHealthDifficultySync(NBTTagCompound instance, String key, short value, Operation<Void> original, EntityLivingBase entity){
        original.call(instance, key, value);
        PacketHandler.instance.sendToAllTracking(new PacketEntityDifficulty(entity), entity);
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
        if(ScalingHealthIssuesConfigHandler.debug.logBlights && BetterBlightHandler.isFakeBlight(entityLiving)) {
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
