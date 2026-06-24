package scalinghealthissues.mixin.infernalmobs.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.Wrapper.InfernalMobsWrapper;

import javax.annotation.Nullable;

@Mixin(EntityDamageSourceIndirect.class)
public abstract class EntityDamageSourceIndirect_DeathMessageMixin extends EntityDamageSource {

    @Shadow @Nullable public abstract Entity getTrueSource();

    public EntityDamageSourceIndirect_DeathMessageMixin(String damageTypeIn, @Nullable Entity damageSourceEntityIn) {
        super(damageTypeIn, damageSourceEntityIn);
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageInfernal(Entity entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (InfernalMobsWrapper.isModified(entity)) {
            entityName = InfernalMobsWrapper.prependInfernalText(entityName, entity);
        }

        return entityName;
    }
}
