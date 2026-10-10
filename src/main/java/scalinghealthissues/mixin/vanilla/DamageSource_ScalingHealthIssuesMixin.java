package scalinghealthissues.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.handlers.BetterDifficultyHandler;
import scalinghealthissues.mixininterface.IDamageSources_NonLethalMixin;
import scalinghealthissues.wrapper.ScalingHealthWrapper;


@Mixin(value = DamageSource.class, priority = 1004)
public abstract class DamageSource_ScalingHealthIssuesMixin implements IDamageSources_NonLethalMixin {

    @Unique
    private boolean scalingHealthIssues$isNonLethal = false;

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaDamageSourceIndirect_getDeathMessageBlight(EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if(ScalingHealthWrapper.isBlight(entity)) {
            entityName = BetterDifficultyHandler.prependBlightText(entityName, entity);
        }

        return entityName;
    }

    @ModifyReturnValue(
            method = "getDeathMessage",
            at = @At("RETURN")
    )
    private ITextComponent scalingHealthIssues_vanillaDamageSourceIndirect_getDeathMessageDifficulty(ITextComponent original, EntityLivingBase victim){
        return BetterDifficultyHandler.wrapDifficultyHoverText(original, victim, victim.getAttackingEntity());
    }

    @Override
    public boolean scalingHealthIssues$isNonLethal() {
        return this.scalingHealthIssues$isNonLethal;
    }

    @Override
    public DamageSource scalingHealthIssues$setNonLethal(){
        this.scalingHealthIssues$isNonLethal = true;
        return (DamageSource) (Object) this;
    }
}
