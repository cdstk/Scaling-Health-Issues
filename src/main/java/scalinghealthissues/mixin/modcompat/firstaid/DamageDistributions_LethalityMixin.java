package scalinghealthissues.mixin.modcompat.firstaid;

import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.RandomDamageDistribution;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import scalinghealthissues.util.IAbstractDamageablePart_NonLethalMixin;

import javax.annotation.Nonnull;

@Mixin(value = {
        DamageDistribution.class,
        RandomDamageDistribution.class
})
public abstract class DamageDistributions_LethalityMixin {

    // Extends the No Kill behavior from RandomDamageDistribution to the above (certain Damage Distributions will still kill)
    @ModifyConstant(
            method = "minHealth",
            constant = @Constant(floatValue = 0F),
            remap = false
    )
    private float scalingHealthIssues_firstAidDamageDistributions_minHealthNonLethal(float constant, @Nonnull EntityPlayer player, @Nonnull AbstractDamageablePart part){
        if(IAbstractDamageablePart_NonLethalMixin.isNonLethal(part)) {
            return 1.0F;
        }
        return constant;
    }
}
