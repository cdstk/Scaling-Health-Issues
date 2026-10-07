package scalinghealthissues.mixin.modcompat.firstaid;

import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import scalinghealthissues.util.IAbstractDamageablePart_NonLethalMixin;

@Mixin(AbstractDamageablePart.class)
public abstract class AbstractDamageablePart_LethalityMixin implements IAbstractDamageablePart_NonLethalMixin {

    @Unique
    private boolean scalingHealthIssues$applyNonLethal = false;
    @Unique
    private boolean scalingHealthIssues$ignoreRemainder = false;

    @Unique
    @Override
    public void scalingHealthIssues$setNonLethal(boolean nonLethal) {
        this.scalingHealthIssues$applyNonLethal = nonLethal;
    }

    @Unique
    @Override
    public boolean scalingHealthIssues$isNonLethal() {
        return this.scalingHealthIssues$applyNonLethal;
    }

    @Unique
    @Override
    public void scalingHealthIssues$setIgnoreRemainder(boolean redistribute) {
        this.scalingHealthIssues$ignoreRemainder = redistribute;
    }

    @Unique
    @Override
    public boolean scalingHealthIssues$shouldIgnoreRemainder() {
        return this.scalingHealthIssues$ignoreRemainder;
    }
}
