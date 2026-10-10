package scalinghealthissues.mixin.modcompat.infernalmobs.vanilla;

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
import scalinghealthissues.compat.InfernalMobsUtil;

import javax.annotation.Nullable;

@Mixin(value = EntityDamageSourceIndirect.class, priority = 1003)
public abstract class EntityDamageSourceIndirect_InfernalDeathMessageMixin extends EntityDamageSource {

    @Shadow @Nullable public abstract Entity getTrueSource();

    public EntityDamageSourceIndirect_InfernalDeathMessageMixin(String damageTypeIn, @Nullable Entity damageSourceEntityIn) {
        super(damageTypeIn, damageSourceEntityIn);
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageKillerInfernal(Entity entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (entity instanceof EntityLivingBase && InfernalMobsUtil.isModified((EntityLivingBase) entity)) {
            entityName = InfernalMobsUtil.prependInfernalText(entityName, entity);
        }

        return entityName;
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageVictimInfernal(EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (InfernalMobsUtil.isModified(entity)) {
            entityName = InfernalMobsUtil.prependInfernalText(entityName, entity);
        }

        return entityName;
    }
}
