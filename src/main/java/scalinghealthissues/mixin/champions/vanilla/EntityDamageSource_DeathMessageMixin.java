package scalinghealthissues.mixin.champions.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.Wrapper.ChampionsWrapper;

import javax.annotation.Nullable;

@Mixin(EntityDamageSource.class)
public abstract class EntityDamageSource_DeathMessageMixin {

    @Shadow @Nullable public abstract Entity getTrueSource();

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSource_getDeathMessageInfernal(Entity entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (ChampionsWrapper.isEntityChampion(entity)) {
            entityName = ChampionsWrapper.prependChampionText(entityName, entity);
        }

        return entityName;
    }
}
