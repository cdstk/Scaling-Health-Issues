package scalinghealthissues.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.handlers.BetterDifficultyHandler;
import scalinghealthissues.wrapper.ScalingHealthWrapper;

import javax.annotation.Nullable;

@Mixin(value = EntityDamageSourceIndirect.class, priority = 1004)
public abstract class EntityDamageSourceIndirect_DeathMessageMixin extends EntityDamageSource {

    @Shadow @Nullable public abstract Entity getTrueSource();

    public EntityDamageSourceIndirect_DeathMessageMixin(String damageTypeIn, @Nullable Entity damageSourceEntityIn) {
        super(damageTypeIn, damageSourceEntityIn);
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageKillerBlight(Entity entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (ScalingHealthWrapper.isBlight(entity)) {
            entityName = BetterDifficultyHandler.prependBlightText(entityName, entity);
        }

        return entityName;
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageVictimBlight(EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (ScalingHealthWrapper.isBlight(entity)) {
            entityName = BetterDifficultyHandler.prependBlightText(entityName, entity);
        }

        return entityName;
    }

    @ModifyReturnValue(
            method = "getDeathMessage",
            at = @At("RETURN")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageDifficulty(ITextComponent original, EntityLivingBase victim){
        return BetterDifficultyHandler.wrapDifficultyHoverText(original, victim, this.getTrueSource());
    }
}
