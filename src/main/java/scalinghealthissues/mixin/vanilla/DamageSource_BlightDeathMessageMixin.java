package scalinghealthissues.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.Wrapper.ScalingHealthWrapper;


@Mixin(DamageSource.class)
public abstract class DamageSource_BlightDeathMessageMixin {

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaDamageSourceIndirect_getDeathMessageBlight(EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if(ScalingHealthWrapper.isBlight(entity)) {
            entityName = ScalingHealthWrapper.prependBlightText(entityName, entity);
        }

        return entityName;
    }

    @ModifyReturnValue(
            method = "getDeathMessage",
            at = @At("RETURN")
    )
    private ITextComponent scalingHealthIssues_vanillaDamageSourceIndirect_getDeathMessageDifficulty(ITextComponent original, EntityLivingBase victim){
        return ScalingHealthWrapper.wrapDifficultyHoverText(original, victim, victim.getAttackingEntity());
    }
}
