package scalinghealthissues.mixin.modcompat.lycanitesmobs.scalinghealth;

import com.llamalad7.mixinextras.sugar.Local;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.silentchaos512.scalinghealth.event.PetEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import scalinghealthissues.config.ScalingHealthIssuesConfigHandler;

@Mixin(PetEventHandler.class)
public abstract class PetEventHandler_LycanitesMixin {

    @ModifyVariable(
            method = "onLivingUpdate",
            at = @At(value = "STORE", ordinal = 0),
            name = "isTamed",
            remap = false
    )
    private boolean scalingHealthIssues_shPetEventHandler_onLivingUpdateLycanites(boolean isTamed, @Local EntityLivingBase entity){
        if(entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity creature = (BaseCreatureEntity) entity;
            if(creature.isTamed() && !creature.isTemporary)
                if(!creature.isBoundPet() || ScalingHealthIssuesConfigHandler.compat.lycanitesPetRegenSoulbind)
                    return true;

        }
        return isTamed;
    }
}
