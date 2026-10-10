package scalinghealthissues.mixin.modcompat.champions.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.compat.ChampionsUtil;

@Mixin(value = EntityDamageSource.class, priority = 1002)
public abstract class EntityDamageSource_ChampionDeathMessageMixin {

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSource_getDeathMessageVictimChampion(EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (ChampionsUtil.isEntityChampion(entity)) {
            entityName = ChampionsUtil.prependChampionText(entityName, entity);
        }

        return entityName;
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSource_getDeathMessageKillerChampion(Entity entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        if (ChampionsUtil.isEntityChampion(entity)) {
            entityName = ChampionsUtil.prependChampionText(entityName, entity);
        }

        return entityName;
    }
}
